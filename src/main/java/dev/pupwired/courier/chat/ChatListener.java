package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public final class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        event.renderer(ChatRenderer.viewerUnaware((source, sourceDisplayName, message) ->
                ChatFormatter.format(Channel.GLOBAL, CharacterNames.of(source), message)));
    }
}