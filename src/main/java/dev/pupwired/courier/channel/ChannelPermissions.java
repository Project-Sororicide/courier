package dev.pupwired.courier.channel;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/** Registers each channel's read and write permissions, open to everyone by default. */
public final class ChannelPermissions {

    private final PluginManager manager;
    private final Set<String> registered = new HashSet<>();

    public ChannelPermissions(PluginManager manager) {
        this.manager = manager;
    }

    /** Replaces whatever was registered before, so it is safe to call again after a reload. */
    public void register(Collection<Channel> channels) {
        unregister();
        for (Channel channel : channels) {
            add(channel.readPermission(), "Hear the " + channel.name() + " channel");
            add(channel.writePermission(), "Speak in the " + channel.name() + " channel");
        }
    }

    private void add(String name, String description) {
        // already defined: by another channel sharing the permission, or by another plugin; leave it as it is
        if (manager.getPermission(name) != null) return;
        manager.addPermission(new Permission(name, description, PermissionDefault.FALSE));
        registered.add(name);
    }

    public void unregister() {
        registered.forEach(manager::removePermission);
        registered.clear();
    }
}