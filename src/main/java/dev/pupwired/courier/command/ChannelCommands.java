package dev.pupwired.courier.command;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/** Registers one command per channel at runtime, so channels do not need to be listed in plugin.yml. */
public final class ChannelCommands {

    private final JavaPlugin plugin;
    private final PlayerChannels players;
    private final List<ChannelCommand> registered = new ArrayList<>();

    public ChannelCommands(JavaPlugin plugin, PlayerChannels players) {
        this.plugin = plugin;
        this.players = players;
    }

    /** Replaces whatever was registered before, so it is safe to call again after a config reload. */
    public void register(Collection<Channel> channels) {
        unregister();
        CommandMap map = plugin.getServer().getCommandMap();
        for (Channel channel : channels) {
            ChannelCommand command = new ChannelCommand(channel, players);
            map.register(plugin.getName().toLowerCase(), command);
            // register() yields to an existing command with the same label (like vanilla /say); take the bare labels over
            map.getKnownCommands().put(command.getName(), command);
            for (String alias : command.getAliases()) map.getKnownCommands().put(alias, command);
            registered.add(command);
        }
    }

    public void unregister() {
        CommandMap map = plugin.getServer().getCommandMap();
        for (ChannelCommand command : registered) {
            command.unregister(map);
            map.getKnownCommands().values().removeIf(c -> c == command);
        }
        registered.clear();
    }
}