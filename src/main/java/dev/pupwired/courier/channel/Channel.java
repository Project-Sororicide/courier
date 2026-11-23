package dev.pupwired.courier.channel;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;

/** A chat channel. Radius, permissions and config loading are added in the next steps. */
public record Channel(String id, String acronym, TextColor color) {

    /** Placeholder until channels are loaded from config. */
    public static final Channel GLOBAL = new Channel("global", "G", NamedTextColor.GRAY);
}