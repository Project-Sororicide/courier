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

/** Works out who hears a line in a channel from an origin, and delivers it. Main thread only. */
public final class ChatDispatcher {

    /** Everyone online who may read the channel and is in range of the origin (any distance for global). */
    public Set<Player> listeners(Channel channel, Location origin) {
        Set<Player> result = new LinkedHashSet<>();
        World world = origin.getWorld();
        if (!channel.isGlobal() && world == null) return result;

        double radiusSq = (double) channel.radius() * channel.radius();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(channel.readPermission())) continue;
            if (!channel.isGlobal()) {
                if (!player.getWorld().equals(world)) continue;
                if (player.getLocation().distanceSquared(origin) > radiusSq) continue;
            }
            result.add(player);
        }
        return result;
    }

    /**
     * Sends the line to every listener, to the console as a log, and to the sender (if any) even when they
     * could not otherwise hear it.
     */
    public void deliver(Channel channel, Location origin, Component line, @Nullable Player sender) {
        Set<Player> heard = listeners(channel, origin);
        if (sender != null) heard.add(sender);

        Bukkit.getConsoleSender().sendMessage(line);
        for (Player player : heard) player.sendMessage(line);
    }
}