package dev.pupwired.courier;

import dev.pupwired.courier.channel.ChannelRegistry;
import dev.pupwired.courier.channel.PlayerChannels;
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
        playerChannels = new PlayerChannels(channels.defaultChannel());
        channelCommands = new ChannelCommands(this, playerChannels);

        getServer().getPluginManager().registerEvents(new ChatListener(playerChannels), this);
        channelCommands.register(channels.all());
    }

    @Override
    public void onDisable() {
        if (channelCommands != null) channelCommands.unregister();
    }

    public ChannelRegistry channels() { return channels; }

    public PlayerChannels playerChannels() { return playerChannels; }
}