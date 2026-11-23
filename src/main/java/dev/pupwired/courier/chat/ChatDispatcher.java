package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Set;

/** Works out who hears a line from an origin, and delivers it. Main thread only. */
public final class ChatDispatcher {

    /**
     * Everyone online who may read it and is in range of the origin.
     * @param radius blocks (3D, same world); negative means unlimited
     * @param readPermission required to hear it; null means no requirement
     */
    public Set<Player> listeners(Location origin, double radius, @Nullable String readPermission) {
        Set<Player> result = new LinkedHashSet<>();
        boolean unlimited = radius < 0;
        World world = origin.getWorld();
        if (!unlimited && world == null) return result;

        double radiusSq = radius * radius;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (readPermission != null && !player.hasPermission(readPermission)) continue;
            if (!unlimited) {
                if (!player.getWorld().equals(world)) continue;
                if (player.getLocation().distanceSquared(origin) > radiusSq) continue;
            }
            result.add(player);
        }
        return result;
    }

    public Set<Player> listeners(Channel channel, Location origin) {
        return listeners(origin, channel.radius(), channel.readPermission());
    }

    /**
     * Sends the line to every listener, to the console as a log, and to the sender (if any) even when they
     * could not otherwise hear it.
     * @return how many players received it
     */
    public int deliver(Location origin, double radius, @Nullable String readPermission,
                       Component line, @Nullable Player sender, boolean log) {
        Set<Player> heard = listeners(origin, radius, readPermission);
        if (sender != null) heard.add(sender);

        if (log) Bukkit.getConsoleSender().sendMessage(line);
        for (Player player : heard) player.sendMessage(line);
        return heard.size();
    }

    public int deliver(Channel channel, Location origin, Component line, @Nullable Player sender) {
        return deliver(origin, channel.radius(), channel.readPermission(), line, sender, true);
    }
}