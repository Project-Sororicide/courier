package dev.pupwired.courier.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Collection;

/** Entry point for other plugins: {@code Courier.get().at(entity).channel("say").send("hello")}. */
public interface Courier {

    /** @throws IllegalStateException if Courier is not enabled */
    static Courier get() {
        Courier courier = Bukkit.getServicesManager().load(Courier.class);
        if (courier == null) throw new IllegalStateException("Courier is not enabled");
        return courier;
    }

    /** Starts a message from a fixed point. Main thread only. */
    LocalMessage at(Location origin);

    /** Starts a message from where an entity is right now. A player origin always hears their own message. Main thread only. */
    LocalMessage at(Entity origin);

    boolean hasChannel(String id);

    Collection<String> channelIds();
}