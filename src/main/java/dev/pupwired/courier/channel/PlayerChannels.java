package dev.pupwired.courier.channel;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Which channel each online player is currently speaking in. Concurrent because chat events are async. */
public final class PlayerChannels {

    private final Channel defaultChannel;
    private final Map<UUID, Channel> active = new ConcurrentHashMap<>();

    public PlayerChannels(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Channel get(Player player) {
        return active.getOrDefault(player.getUniqueId(), defaultChannel);
    }

    public void set(Player player, Channel channel) {
        active.put(player.getUniqueId(), channel);
    }

    /** Back to the default channel. */
    public void clear(Player player) {
        active.remove(player.getUniqueId());
    }
}