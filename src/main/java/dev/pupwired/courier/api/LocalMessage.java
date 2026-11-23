package dev.pupwired.courier.api;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.ChannelRegistry;
import dev.pupwired.courier.chat.ChatDispatcher;
import dev.pupwired.courier.chat.ChatFormatter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/**
 * A localized message being built. Pick who hears it with {@link #channel}, {@link #radius} or {@link #global},
 * optionally style it, then send. Later calls override earlier ones. Main thread only.
 */
public final class LocalMessage {

    private final ChannelRegistry channels;
    private final ChatDispatcher dispatcher;
    private final Location origin;
    private final @Nullable Player sender;

    private @Nullable Double radius;            // null: not chosen yet; negative: unlimited
    private @Nullable String acronym;
    private TextColor color = NamedTextColor.WHITE;
    private @Nullable Component speaker;
    private @Nullable String readPermission;

    LocalMessage(ChannelRegistry channels, ChatDispatcher dispatcher, Location origin, @Nullable Player sender) {
        this.channels = channels;
        this.dispatcher = dispatcher;
        this.origin = origin;
        this.sender = sender;
    }

    /** Uses an existing channel's radius, acronym, color and read permission. */
    public LocalMessage channel(String id) {
        Channel channel = channels.find(id)
                .orElseThrow(() -> new IllegalArgumentException("Unknown channel '" + id + "'"));
        this.radius = channel.isGlobal() ? -1.0 : channel.radius();
        this.acronym = channel.acronym();
        this.color = channel.color();
        this.readPermission = channel.readPermission();
        return this;
    }

    /** Everyone within this many blocks hears it (3D distance, same world). */
    public LocalMessage radius(double blocks) {
        if (!Double.isFinite(blocks) || blocks <= 0)
            throw new IllegalArgumentException("radius must be above 0, got " + blocks);
        this.radius = blocks;
        return this;
    }

    /** Everyone online hears it, in any world. */
    public LocalMessage global() {
        this.radius = -1.0;
        return this;
    }

    /** The name shown before the message in the default format. */
    public LocalMessage speaker(@Nullable Component speaker) {
        this.speaker = speaker;
        return this;
    }

    public LocalMessage speaker(String plainName) {
        return speaker(Component.text(plainName));
    }

    /** The bracketed tag of the default format; {@code null} drops it. */
    public LocalMessage acronym(@Nullable String acronym) {
        this.acronym = acronym;
        return this;
    }

    /** Color of the tag in the default format. */
    public LocalMessage color(TextColor color) {
        this.color = color;
        return this;
    }

    /** Only players with this permission hear it; {@code null} removes the requirement. */
    public LocalMessage requirePermission(@Nullable String permission) {
        this.readPermission = permission;
        return this;
    }

    /** Sends with the default format: {@code [acronym] speaker: message}. @return how many players heard it */
    public int send(Component message) {
        return sendRaw(ChatFormatter.format(acronym, color, speaker, message));
    }

    public int send(String plainText) {
        return send(Component.text(plainText));
    }

    /** Sends the line exactly as given, with no prefix or speaker added. @return how many players heard it */
    public int sendRaw(Component line) {
        if (!Bukkit.isPrimaryThread())
            throw new IllegalStateException("Courier messages must be sent from the main thread");
        if (radius == null)
            throw new IllegalStateException("Choose who hears this first: channel(id), radius(blocks) or global()");
        return dispatcher.deliver(origin, radius, readPermission, line, sender);
    }
}