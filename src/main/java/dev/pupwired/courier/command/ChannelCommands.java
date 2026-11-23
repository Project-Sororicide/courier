package dev.pupwired.courier.command;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.chat.ChatDispatcher;
import org.bukkit.command.CommandMap;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Registers one command per label at runtime, so channels do not need to be listed in plugin.yml. */
public final class ChannelCommands {

    private final JavaPlugin plugin;
    private final PlayerChannels players;
    private final ChatDispatcher dispatcher;
    private final List<ChannelCommand> registered = new ArrayList<>();

    public ChannelCommands(JavaPlugin plugin, PlayerChannels players, ChatDispatcher dispatcher) {
        this.plugin = plugin;
        this.players = players;
        this.dispatcher = dispatcher;
    }

    /** Replaces whatever was registered before, so it is safe to call again after a config reload. */
    public void register(Collection<Channel> channels) {
        unregister();

        Map<String, List<Channel>> byLabel = new LinkedHashMap<>();
        for (Channel channel : channels) {
            for (String label : channel.commandLabels()) {
                byLabel.computeIfAbsent(label.toLowerCase(Locale.ROOT), k -> new ArrayList<>()).add(channel);
            }
        }

        CommandMap map = plugin.getServer().getCommandMap();
        byLabel.forEach((label, candidates) -> {
            ChannelCommand command = new ChannelCommand(label, candidates, players, dispatcher);
            map.register(plugin.getName().toLowerCase(Locale.ROOT), command);
            // register() yields to an existing command with the same label (like vanilla /say); take the bare label over
            map.getKnownCommands().put(label, command);
            registered.add(command);
        });
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