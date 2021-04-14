/*
 * Copyright 2011 Tyler Blair. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE AUTHOR OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * The views and conclusions contained in the software and documentation are those of the
 * authors and contributors and should not be interpreted as representing official policies,
 * either expressed or implied, of anybody else.
 */

package com.griefcraft.model;

import com.griefcraft.lwc.LWC;
import com.griefcraft.modules.history.HistoryModule;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class LWCPlayer implements CommandSender {

    private final LWC lwc;

    private final Player player;

    private final static Map<Player, LWCPlayer> playerCache = new HashMap<>();

    private final Map<String, Action> actions = new HashMap<>();

    private final Set<Mode> modes = new HashSet<>();

    private final Set<Protection> accessibleProtections = new HashSet<>();

    public LWCPlayer(LWC lwc, Player player) {
        this.lwc = lwc;
        this.player = player;
    }

    /**
     * Get the LWCPlayer object from a Player object
     *
     * @param player The Player to get a LWCPlayer instance of
     * @return LWCPlayer instance of the provided Player
     */
    public static LWCPlayer getPlayer(Player player) {
        if (!playerCache.containsKey(player)) {
            playerCache.put(player, new LWCPlayer(LWC.getInstance(), player));
        }

        return playerCache.get(player);
    }

    /**
     * Remove a player from the player cache
     *
     * @param player The Player to remove
     */
    public static void removePlayer(Player player) {
        getPlayer(player);

        // uncache them
        playerCache.remove(player);
    }

    /**
     * @return the Bukkit Player object
     */
    public Player getBukkitPlayer() {
        return player;
    }

    /**
     * Get the player's UUID
     *
     * @return player's UUID
     */
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    /**
     * @return the player's name
     */
    @Nonnull
    public String getName() {
        return player.getName();
    }

    /**
     * Required by Spigot for plugin compilation.
     *
     * @return The Spigot instance
     */
    @Override
    @Nonnull
    public Spigot spigot() {
        return player.spigot();
    }

    /**
     * Enable a mode on the player
     *
     * @param mode The mode to enable
     * @return True if the mode was added successfully
     */
    public boolean enableMode(Mode mode) {
        return modes.add(mode);
    }

    /**
     * Disable a mode on the player
     *
     * @param mode The mode to disable
     * @return True if the mode was removed successfully
     */
    public boolean disableMode(Mode mode) {
        return modes.remove(mode);
    }

    /**
     * Disable all modes enabled by the player
     */
    public void disableAllModes() {
        modes.clear();
    }

    /**
     * Check if the player has an action
     *
     * @param name The action to check for
     * @return True if the Player has this action
     */
    public boolean hasAction(String name) {
        return actions.containsKey(name);
    }

    /**
     * Get the action represented by the name
     *
     * @param name The name of the action to get
     * @return The Action instance from the provided name
     */
    public Action getAction(String name) {
        return actions.get(name);
    }

    /**
     * Add an action
     *
     * @param action The Action to add
     * @return True
     */
    public boolean addAction(Action action) {
        actions.put(action.getName(), action);
        return true;
    }

    /**
     * Remove an action
     *
     * @param action The Action to remove
     * @return True
     */
    public boolean removeAction(Action action) {
        actions.remove(action.getName());
        return true;
    }

    /**
     * Remove all actions
     */
    public void removeAllActions() {
        actions.clear();
    }

    /**
     * Retrieve a Mode object for a player
     *
     * @param name The name of the mode to get
     * @return The Mode if found or null
     */
    public Mode getMode(String name) {
        for (Mode mode : modes) {
            if (mode.getName().equals(name)) {
                return mode;
            }
        }

        return null;
    }

    /**
     * Check if the player has the given mode
     *
     * @param name The Mode to check
     * @return True if the player has the mode
     */
    public boolean hasMode(String name) {
        return getMode(name) != null;
    }

    /**
     * @return the Set of modes the player has activated
     */
    public Set<Mode> getModes() {
        return new HashSet<>(modes);
    }

    /**
     * @return the Set of actions the player has
     */
    public Map<String, Action> getActions() {
        return new HashMap<>(actions);
    }

    /**
     * @return a Set containing all of the action names
     */
    public Set<String> getActionNames() {
        return new HashSet<>(actions.keySet());
    }

    /**
     * @return the set of protections the player can temporarily access
     */
    public Set<Protection> getAccessibleProtections() {
        return new HashSet<>(accessibleProtections);
    }

    /**
     * Add an accessible protection for the player
     *
     * @param protection The Protection to add
     * @return True if the Protection was added successfully
     */
    public boolean addAccessibleProtection(Protection protection) {
        return accessibleProtections.add(protection);
    }

    /**
     * Remove an accessible protection from the player
     *
     * @param protection The Protection to remove
     * @return True if the Protection was removed successfully
     */
    public boolean removeAccessibleProtection(Protection protection) {
        return accessibleProtections.remove(protection);
    }

    /**
     * Remove all accessible protections
     */
    public void removeAllAccessibleProtections() {
        accessibleProtections.clear();
    }

    /**
     * Create a History object that is attached to this protection
     *
     * @return The created History Object
     */
    public History createHistoryObject() {
        History history = new History();

        history.setPlayer(player.getName());
        history.setStatus(History.Status.INACTIVE);

        return history;
    }

    /**
     * Send a locale to the player
     *
     * @param key The key to get the language String from
     * @param args Key-value pair to parse
     */
    public void sendLocale(String key, Object... args) {
        lwc.sendLocale(player, key, args);
    }

    /**
     * Get the player's history
     *
     * @return List of History instances for this Player
     */
    public List<History> getRelatedHistory() {
        return lwc.getPhysicalDatabase().loadHistory(player);
    }

    /**
     * Get the player's history for a given page
     *
     * @param page The page to get History instances for
     * @return List of History instances for this player and for the set page
     */
    public List<History> getRelatedHistory(int page) {
        return lwc.getPhysicalDatabase().loadHistory(player, (page - 1) * HistoryModule.ITEMS_PER_PAGE, HistoryModule.ITEMS_PER_PAGE);
    }

    /**
     * Get the player's history pertaining to the type
     *
     * @param type The type to get History instances for
     * @return List of History instances for this player and the provided History Type
     */
    public List<History> getRelatedHistory(History.Type type) {
        List<History> related = new ArrayList<>();

        for (History history : getRelatedHistory()) {
            if (history.getType() == type) {
                related.add(history);
            }
        }

        return related;
    }

    public void sendMessage(@Nonnull String s) {
        player.sendMessage(s);
    }

    public void sendMessage(String[] s) {
        for (String _s : s) {
            sendMessage(_s);
        }
    }

    @Override
    public void sendMessage(UUID uuid, @Nonnull String s) {
        player.sendMessage(player.getUniqueId(), s);
    }

    @Override
    public void sendMessage(UUID uuid, @Nonnull String[] strings) {
        player.sendMessage(player.getUniqueId(), strings);
    }
    
    @Nonnull
    public Server getServer() {
        return player.getServer();
    }

    public boolean isPermissionSet(@Nonnull String s) {
        return player.isPermissionSet(s);
    }

    public boolean isPermissionSet(@Nonnull Permission permission) {
        return player.isPermissionSet(permission);
    }

    public boolean hasPermission(@Nonnull String s) {
        return player.hasPermission(s);
    }

    public boolean hasPermission(@Nonnull Permission permission) {
        return player.hasPermission(permission);
    }
    
    @Nonnull
    public PermissionAttachment addAttachment(@Nonnull Plugin plugin, @Nonnull String s, boolean b) {
        return player.addAttachment(plugin, s, b);
    }
    
    @Nonnull
    public PermissionAttachment addAttachment(@Nonnull Plugin plugin) {
        return player.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(@Nonnull Plugin plugin, @Nonnull String s, boolean b, int i) {
        return player.addAttachment(plugin, s, b, i);
    }

    public PermissionAttachment addAttachment(@Nonnull Plugin plugin, int i) {
        return player.addAttachment(plugin, i);
    }

    public void removeAttachment(@Nonnull PermissionAttachment permissionAttachment) {
        player.removeAttachment(permissionAttachment);
    }

    public void recalculatePermissions() {
        player.recalculatePermissions();
    }
    
    @Nonnull
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return player.getEffectivePermissions();
    }

    public boolean isOp() {
        return player.isOp();
    }

    public void setOp(boolean b) {
        player.setOp(b);
    }
}
