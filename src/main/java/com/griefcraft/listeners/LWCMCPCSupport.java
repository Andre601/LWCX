package com.griefcraft.listeners;

import com.griefcraft.lwc.LWC;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCProtectionDestroyEvent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LWCMCPCSupport extends JavaModule {

    
    private final LWC lwc;

    private final Set<String> blacklistedPlayers = new HashSet<>();

    public LWCMCPCSupport(LWC lwc) {
        this.lwc = lwc;
        loadAndProcessConfig();
    }

    /**
     * Load and process the configuration
     */
    public void loadAndProcessConfig() {
        blacklistedPlayers.clear();

        for (String player : lwc.getConfiguration().getStringList("optional.blacklistedPlayers", new ArrayList<String>())) {
            blacklistedPlayers.add(player.toLowerCase());
        }
    }

    /**
     * Called when a protection is destroyed
     *
     * @param event The Event instance
     */
    public void onDestroyProtection(LWCProtectionDestroyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        String lowerPlayerName = player.getName().toLowerCase();

        if (blacklistedPlayers.contains(lowerPlayerName)) {
            event.setCancelled(true);
            lwc.sendLocale(player, "protection.accessdenied");
        }
    }

}
