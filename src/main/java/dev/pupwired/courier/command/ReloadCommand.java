package dev.pupwired.courier.command;

import dev.pupwired.courier.CourierPlugin;
import dev.pupwired.courier.message.Message;
import dev.pupwired.luckylib.api.command.Subcommand;
import org.bukkit.command.CommandSender;

final class ReloadCommand implements Subcommand {

    private final CourierPlugin plugin;

    ReloadCommand(CourierPlugin plugin) {
        this.plugin = plugin;
    }

    @Override public String name() { return "reload"; }

    @Override
    public void execute(CommandSender sender, String label, String[] args) {
        plugin.reload();
        Message.COMMAND_RELOADED.sendMessage(sender, plugin.channels().all().size());
    }
}