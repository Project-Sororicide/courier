package dev.pupwired.courier.api;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.ChannelRegistry;
import dev.pupwired.courier.chat.ChatDispatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;

public final class CourierService implements Courier {

    private final ChannelRegistry channels;
    private final ChatDispatcher dispatcher;

    public CourierService(ChannelRegistry channels, ChatDispatcher dispatcher) {
        this.channels = channels;
        this.dispatcher = dispatcher;
    }

    @Override
    public LocalMessage at(Location origin) {
        requireMainThread();
        return new LocalMessage(channels, dispatcher, origin.clone(), null);
    }

    @Override
    public LocalMessage at(Entity origin) {
        requireMainThread();
        Player sender = origin instanceof Player player ? player : null;
        return new LocalMessage(channels, dispatcher, origin.getLocation(), sender);
    }

    @Override
    public boolean hasChannel(String id) {
        return channels.find(id).isPresent();
    }

    @Override
    public Collection<String> channelIds() {
        return channels.all().stream().map(Channel::id).toList();
    }

    private static void requireMainThread() {
        if (!Bukkit.isPrimaryThread())
            throw new IllegalStateException("Courier must be used from the main thread");
    }
}