package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import dev.pupwired.courier.message.Message;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class ChatListener implements Listener {

    private final JavaPlugin plugin;
    private final PlayerChannels channels;
    private final ChatDispatcher dispatcher;

    public ChatListener(JavaPlugin plugin, PlayerChannels channels, ChatDispatcher dispatcher) {
        this.plugin = plugin;
        this.channels = channels;
        this.dispatcher = dispatcher;
    }

    // HIGH + ignoreCancelled: a mute or anti-spam plugin that cancelled the message earlier is respected
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onChat(AsyncChatEvent event) {
        event.setCancelled(true);

        Player player = event.getPlayer();
        Channel channel = channels.get(player);
        Component message = event.message();
        plugin.getServer().getScheduler().runTask(plugin, () -> speak(player, channel, message));
    }

    /** Main thread: permissions and positions are safe to read here. */
    private void speak(Player player, Channel channel, Component message) {
        if (!player.isOnline()) return;

        if (!player.hasPermission(channel.writePermission())) {
            Message.CHANNEL_WRITE_DENIED.sendMessage(player, channel.name());
            channels.unload(player); // back to the default channel; the saved choice stays in their PDC
            return;
        }
        Component line = ChatFormatter.format(channel, CharacterNames.of(player), message);
        dispatcher.deliver(channel, player.getLocation(), line, player);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        channels.load(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        channels.unload(event.getPlayer());
    }
}