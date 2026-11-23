package dev.pupwired.courier.channel;

import net.kyori.adventure.text.format.TextColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** A chat channel. A radius of {@link #UNLIMITED} means everyone hears it. */
public record Channel(String id, String name, String acronym, int radius, TextColor color,
                      String readPermission, String writePermission) {

    public static final int UNLIMITED = -1;

    public Channel {
        id = id.toLowerCase(Locale.ROOT);
    }

    /** A channel with the default permissions {@code courier.channel.<id>.read} and {@code .write}. */
    public static Channel of(String id, String name, String acronym, int radius, TextColor color) {
        String base = "courier.channel." + id.toLowerCase(Locale.ROOT);
        return new Channel(id, name, acronym, radius, color, base + ".read", base + ".write");
    }

    public boolean isGlobal() {
        return radius == UNLIMITED;
    }

    /** Every command label that selects this channel: its id, plus its radius if it has one. */
    public List<String> commandLabels() {
        List<String> labels = new ArrayList<>();
        labels.add(id);
        if (!isGlobal()) labels.add(String.valueOf(radius));
        return labels;
    }
}