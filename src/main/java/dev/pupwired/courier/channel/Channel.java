package dev.pupwired.courier.channel;

import net.kyori.adventure.text.format.TextColor;

import java.util.List;
import java.util.Locale;

/** A chat channel. A radius of {@link #UNLIMITED} means everyone hears it. */
public record Channel(String id, String name, String acronym, int radius, TextColor color) {

    public static final int UNLIMITED = -1;

    public Channel {
        id = id.toLowerCase(Locale.ROOT);
    }

    public boolean isGlobal() {
        return radius == UNLIMITED;
    }

    /** Extra command labels besides the id: the radius, for channels that have one. */
    public List<String> commandAliases() {
        return isGlobal() ? List.of() : List.of(String.valueOf(radius));
    }
}