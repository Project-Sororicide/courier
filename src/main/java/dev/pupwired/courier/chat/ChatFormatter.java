package dev.pupwired.courier.chat;

import dev.pupwired.courier.channel.Channel;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

/** Builds "[<acronym>] <name>: <message>". */
public final class ChatFormatter {

    private ChatFormatter() {}

    public static Component format(Channel channel, String characterName, Component message) {
        return Component.text()
                .append(Component.text("[" + channel.acronym() + "] ", channel.color()))
                .append(Component.text(characterName, NamedTextColor.WHITE))
                .append(Component.text(": ", NamedTextColor.GRAY))
                .append(message.colorIfAbsent(NamedTextColor.WHITE))
                .build();
    }
}