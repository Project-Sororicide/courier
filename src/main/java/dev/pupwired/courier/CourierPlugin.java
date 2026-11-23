package dev.pupwired.courier;

import dev.pupwired.courier.channel.ChannelRegistry;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.chat.ChatDispatcher;
import dev.pupwired.courier.chat.ChatListener;
import dev.pupwired.courier.command.ChannelCommands;
import org.bukkit.plugin.java.JavaPlugin;

public class CourierPlugin extends JavaPlugin {

    private ChannelRegistry channels;
    private PlayerChannels playerChannels;
    private ChannelCommands channelCommands;

    @Override
    public void onEnable() {
        channels = new ChannelRegistry();
        playerChannels = new PlayerChannels(this, channels);
        channelCommands = new ChannelCommands(this, playerChannels);
        ChatDispatcher dispatcher = new ChatDispatcher();

        getServer().getPluginManager().registerEvents(new ChatListener(this, playerChannels, dispatcher), this);
        channelCommands.register(channels.all());
        playerChannels.loadOnline();
    }

    @Override
    public void onDisable() {
        if (channelCommands != null) channelCommands.unregister();
    }

    public ChannelRegistry channels() { return channels; }

    public PlayerChannels playerChannels() { return playerChannels; }
}