package dev.pupwired.courier;

import dev.pupwired.courier.chat.ChatListener;
import org.bukkit.plugin.java.JavaPlugin;

public class CourierPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new ChatListener(), this);
    }
}