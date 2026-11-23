package dev.pupwired.courier.channel;

import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * All known channels, from config.yml. A broken entry is logged and skipped; the contents are swapped only
 * after a full load, and if nothing valid loads the previous channels stay.
 */
public final class ChannelRegistry {

    private static final Pattern ID = Pattern.compile("[a-z0-9_-]{1,32}");

    private static final Channel FALLBACK =
            Channel.of("global", "Global", "G", Channel.UNLIMITED, NamedTextColor.AQUA);

    private record State(Map<String, Channel> channels, Channel defaultChannel) {}

    private volatile State state = new State(Map.of(FALLBACK.id(), FALLBACK), FALLBACK);

    public void load(ConfigurationSection config, Logger log) {
        Map<String, Channel> loaded = new LinkedHashMap<>();
        ConfigurationSection root = config.getConfigurationSection("channels");
        if (root != null) {
            for (String key : root.getKeys(false)) {
                ConfigurationSection section = root.getConfigurationSection(key);
                if (section == null) {
                    log.warning("Skipping channel '" + key + "': not a section");
                    continue;
                }
                try {
                    Channel channel = parse(key, section);
                    if (loaded.putIfAbsent(channel.id(), channel) != null)
                        throw new IllegalArgumentException("duplicate id");
                } catch (IllegalArgumentException e) {
                    log.warning("Skipping channel '" + key + "': " + e.getMessage());
                }
            }
        }
        if (loaded.isEmpty()) {
            log.severe("config.yml defines no valid channel; keeping the previous channels.");
            return;
        }

        String defaultId = config.getString("default-channel", "global").toLowerCase(Locale.ROOT);
        Channel defaultChannel = loaded.get(defaultId);
        if (defaultChannel == null) {
            defaultChannel = loaded.values().iterator().next();
            log.warning("default-channel '" + defaultId + "' does not exist; using '" + defaultChannel.id() + "'.");
        }
        state = new State(Collections.unmodifiableMap(loaded), defaultChannel);
    }

    private static Channel parse(String key, ConfigurationSection s) {
        String id = key.toLowerCase(Locale.ROOT);
        if (!ID.matcher(id).matches())
            throw new IllegalArgumentException("ids may only use a-z, 0-9, '_' and '-' (max 32)");

        int radius = Channel.UNLIMITED;
        if (s.isSet("radius")) {
            if (!s.isInt("radius") || s.getInt("radius") < 1)
                throw new IllegalArgumentException("'radius' must be a whole number of 1 or more (leave it out for an unlimited channel)");
            radius = s.getInt("radius");
        }

        String base = "courier.channel." + id;
        return new Channel(id,
                s.getString("name", key),
                s.getString("acronym", id.substring(0, 1).toUpperCase(Locale.ROOT)),
                radius,
                parseColor(s.getString("color", "white")),
                s.getString("read-permission", base + ".read"),
                s.getString("write-permission", base + ".write"));
    }

    private static TextColor parseColor(String text) {
        TextColor color = text.startsWith("#")
                ? TextColor.fromHexString(text)
                : NamedTextColor.NAMES.value(text.toLowerCase(Locale.ROOT));
        if (color == null)
            throw new IllegalArgumentException("unknown color '" + text + "' (use a name like 'gold' or a hex like '#fb2e01')");
        return color;
    }

    public Channel defaultChannel() { return state.defaultChannel(); }

    public Optional<Channel> find(String id) {
        return Optional.ofNullable(state.channels().get(id.toLowerCase(Locale.ROOT)));
    }

    public Collection<Channel> all() { return state.channels().values(); }
}