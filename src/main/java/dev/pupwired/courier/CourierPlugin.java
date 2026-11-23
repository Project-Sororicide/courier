package dev.pupwired.courier;

import dev.pupwired.courier.api.Courier;
import dev.pupwired.courier.api.CourierService;
import dev.pupwired.courier.channel.ChannelPermissions;
import dev.pupwired.courier.channel.ChannelRegistry;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.chat.ChatDispatcher;
import dev.pupwired.courier.chat.ChatListener;
import dev.pupwired.courier.command.ChannelCommands;
import dev.pupwired.courier.command.CourierCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class CourierPlugin extends JavaPlugin {

    private ChannelRegistry channels;
    private ChannelPermissions permissions;
    private PlayerChannels playerChannels;
    private ChannelCommands channelCommands;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        ChatDispatcher dispatcher = new ChatDispatcher();
        channels = new ChannelRegistry();
        permissions = new ChannelPermissions(getServer().getPluginManager());
        playerChannels = new PlayerChannels(this, channels);
        channelCommands = new ChannelCommands(this, playerChannels, dispatcher);

        getServer().getPluginManager().registerEvents(new ChatListener(this, playerChannels, dispatcher), this);
        getServer().getServicesManager().register(Courier.class, new CourierService(channels, dispatcher), this, ServicePriority.Normal);

        var command = getCommand("courier");
        if (command != null) {
            var handler = new CourierCommand(this);
            command.setExecutor(handler);
            command.setTabCompleter(handler);
        }

        reload();
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregisterAll(this);
        if (channelCommands != null) channelCommands.unregister();
        if (permissions != null) permissions.unregister();
    }

    /** Re-reads config.yml and rebuilds the channels, their permissions and their commands. */
    public void reload() {
        reloadConfig();
        channels.load(getConfig(), getLogger());
        permissions.register(channels.all());
        channelCommands.register(channels.all());
        playerChannels.loadOnline(); // re-resolves each online player's channel against the new definitions
        getServer().getOnlinePlayers().forEach(Player::updateCommands);
        getLogger().info("Loaded " + channels.all().size() + " channel(s).");
    }

    public ChannelRegistry channels() { return channels; }

    public PlayerChannels playerChannels() { return playerChannels; }
}