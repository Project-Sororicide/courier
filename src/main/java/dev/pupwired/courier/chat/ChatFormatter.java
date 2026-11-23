package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

/** Builds "[<acronym>] <speaker>: <message>", leaving out whatever is not set. */
public final class ChatFormatter {

    private ChatFormatter() {}

    public static Component format(Channel channel, String characterName, Component message) {
        return format(channel.acronym(), channel.color(), Component.text(characterName), message);
    }

    public static Component format(@Nullable String acronym, TextColor color,
                                   @Nullable Component speaker, Component message) {
        TextComponent.Builder line = Component.text();
        if (acronym != null && !acronym.isEmpty())
            line.append(Component.text("[" + acronym + "] ", color));
        if (speaker != null) {
            line.append(speaker.colorIfAbsent(NamedTextColor.WHITE));
            line.append(Component.text(": ", NamedTextColor.GRAY));
        }
        return line.append(message.colorIfAbsent(NamedTextColor.WHITE)).build();
    }
}