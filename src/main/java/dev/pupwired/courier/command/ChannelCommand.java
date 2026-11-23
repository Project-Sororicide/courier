package dev.pupwired.courier.command;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/** /<channel id> or /<channel radius>: makes that channel the player's active one. */
final class ChannelCommand extends Command {

    private final Channel channel;
    private final PlayerChannels players;

    ChannelCommand(Channel channel, PlayerChannels players) {
        super(channel.id(), "Speak in the " + channel.name() + " channel", "/" + channel.id(), channel.commandAliases());
        this.channel = channel;
        this.players = players;
    }

    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            Message.COMMAND_PLAYER_ONLY.sendMessage(sender);
            return true;
        }
        if (players.get(player).equals(channel)) {
            Message.CHANNEL_ALREADY.sendMessage(player, channel.name());
            return true;
        }
        players.set(player, channel);
        Message.CHANNEL_SWITCHED.sendMessage(player, channel.name());
        return true;
    }
}