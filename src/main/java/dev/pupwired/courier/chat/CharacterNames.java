package dev.pupwired.courier.chat;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import org.bukkit.entity.Player;

/** Reads a player's character name from their LuckPerms meta. */
public final class CharacterNames {

    public static final String META_KEY = "character_name";

    private CharacterNames() {}

    /** The character name, or the username if none is set. Safe to call from async threads. */
    public static String of(Player player) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        String name = luckPerms.getPlayerAdapter(Player.class)
                .getMetaData(player)
                .getMetaValue(META_KEY);
        return name == null || name.isBlank() ? player.getName() : name;
    }
}