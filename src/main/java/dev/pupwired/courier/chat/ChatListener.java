package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import dev.pupwired.courier.channel.PlayerChannels;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public final class ChatListener implements Listener {

    private final PlayerChannels channels;

    public ChatListener(PlayerChannels channels) {
        this.channels = channels;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        Channel channel = channels.get(event.getPlayer());
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                ChatFormatter.format(channel, CharacterNames.of(source), message)));
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