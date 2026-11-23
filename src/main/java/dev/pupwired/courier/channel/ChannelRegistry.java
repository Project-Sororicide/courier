package dev.pupwired.courier.channel;

import net.kyori.adventure.text.format.NamedTextColor;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/** All known channels. Hardcoded for now; bullet 5 replaces this with config loading. */
public final class ChannelRegistry {

    private final Map<String, Channel> channels = new LinkedHashMap<>();
    private final Channel defaultChannel;

    public ChannelRegistry() {
        defaultChannel = add(new Channel("global", "Global", "G", Channel.UNLIMITED, NamedTextColor.AQUA));
        add(new Channel("yell", "Yell", "YEL", 50, NamedTextColor.RED));
        add(new Channel("say", "Say", "SAY", 30, NamedTextColor.WHITE));
        add(new Channel("mumble", "Mumble", "MUM", 15, NamedTextColor.GRAY));
        add(new Channel("whisper", "Whisper", "WHI", 5, NamedTextColor.DARK_GRAY));
    }

    private Channel add(Channel channel) {
        channels.put(channel.id(), channel);
        return channel;
    }

    public Channel defaultChannel() { return defaultChannel; }

    public Optional<Channel> find(String id) {
        return Optional.ofNullable(channels.get(id.toLowerCase(Locale.ROOT)));
    }

    public Collection<Channel> all() {
        return Collections.unmodifiableCollection(channels.values());
    }
}