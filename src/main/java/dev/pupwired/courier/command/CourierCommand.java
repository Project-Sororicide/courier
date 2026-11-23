package dev.pupwired.courier.command;

import dev.pupwired.courier.CourierPlugin;
import dev.pupwired.courier.message.Message;
import dev.pupwired.luckylib.api.command.SubcommandRouter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public final class CourierCommand implements CommandExecutor, TabCompleter {

    private final SubcommandRouter router;

    public CourierCommand(CourierPlugin plugin) {
        this.router = new SubcommandRouter(new ReloadCommand(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!router.dispatch(sender, label, args)) Message.COMMAND_USAGE.sendMessage(sender, label);
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return router.complete(sender, args);
    }
}