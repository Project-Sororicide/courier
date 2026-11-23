package dev.pupwired.courier.channel;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Each player's active channel. The choice is saved in the player's PDC; an in-memory copy serves the async
 * chat thread, since PDC is not safe to read off the main thread. {@link #set}, {@link #load} and
 * {@link #unload} are main thread only.
 */
public final class PlayerChannels {

    private final NamespacedKey key;
    private final ChannelRegistry registry;
    private final Map<UUID, Channel> active = new ConcurrentHashMap<>();

    public PlayerChannels(JavaPlugin plugin, ChannelRegistry registry) {
        this.key = new NamespacedKey(plugin, "active_channel");
        this.registry = registry;
    }

    /** Safe from any thread. */
    public Channel get(Player player) {
        return active.getOrDefault(player.getUniqueId(), registry.defaultChannel());
    }

    public void set(Player player, Channel channel) {
        active.put(player.getUniqueId(), channel);
        player.getPersistentDataContainer().set(key, PersistentDataType.STRING, channel.id());
    }

    /**
     * Restores the saved channel on join. If it no longer exists or the player can no longer write to it, they
     * start in the default channel, but the saved value is kept in case access comes back.
     */
    public void load(Player player) {
        String id = player.getPersistentDataContainer().get(key, PersistentDataType.STRING);
        Channel channel = id == null ? null : registry.find(id)
                .filter(c -> player.hasPermission(c.writePermission()))
                .orElse(null);
        if (channel == null) active.remove(player.getUniqueId());
        else active.put(player.getUniqueId(), channel);
    }

    public void unload(Player player) {
        active.remove(player.getUniqueId());
    }

    /** For when the plugin is enabled with players already online (e.g. /reload). */
    public void loadOnline() {
        for (Player player : Bukkit.getOnlinePlayers()) load(player);
    }
}