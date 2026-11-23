package dev.pupwired.courier.command;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * /<label>, where the label is a channel id or radius. Several channels can share a label (equal radii): the
 * ones the player may write to are used; none is a no-access message, more than one is a collision error.
 */
final class ChannelCommand extends Command {

    private final List<Channel> candidates;
    private final PlayerChannels players;

    ChannelCommand(String label, List<Channel> candidates, PlayerChannels players) {
        super(label, "Speak in a chat channel", "/" + label, List.of());
        this.candidates = List.copyOf(candidates);
        this.players = players;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Message.COMMAND_PLAYER_ONLY.sendMessage(sender);
            return true;
        }
        List<Channel> usable = candidates.stream()
                .filter(c -> player.hasPermission(c.writePermission()))
                .toList();

        if (usable.isEmpty()) {
            Message.CHANNEL_NO_ACCESS.sendMessage(player, getName());
            return true;
        }
        if (usable.size() > 1) {
            Message.CHANNEL_RADIUS_COLLISION.sendMessage(player, getName(),
                    usable.stream().map(Channel::name).collect(Collectors.joining(", ")));
            return true;
        }
        Channel channel = usable.get(0);
        if (players.get(player).equals(channel)) {
            Message.CHANNEL_ALREADY.sendMessage(player, channel.name());
            return true;
        }
        players.set(player, channel);
        Message.CHANNEL_SWITCHED.sendMessage(player, channel.name());
        return true;
    }
}