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

package com.griefcraft.io;

import com.griefcraft.cache.BlockCache;
import com.griefcraft.lwc.LWC;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class RestorableBlock implements Restorable {

    private int id;

    private String world;

    private int x;

    private int y;

    private int z;

    private final Map<Integer, ItemStack> items = new HashMap<>();

    @Override
    public int getType() {
        return getBackupType().getType();
    }
    
    @Override
    public BackupType getBackupType(){
        return BackupType.BLOCK;
    }
    
    public void restore() {
        LWC lwc = LWC.getInstance();

        lwc.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(lwc.getPlugin(), new Runnable() {
            public void run() {
                Server server = Bukkit.getServer();

                // Get the world
                World bworld = server.getWorld(world);

                // Not found :-(
                if (bworld == null) {
                    return;
                }

                // Get the block we want
                Block block = bworld.getBlockAt(x, y, z);

                // Begin screwing with shit :p
                BlockCache blockCache = BlockCache.getInstance();
                block.setType(blockCache.getBlockType(id));

                if (items.size() > 0) {
                    if (!(block.getState() instanceof InventoryHolder)) {
                        lwc.log(String.format("The block at [%d, %d, %d] has backed up items but no longer supports them. Why? %s", x, y, z, block));
                    }

                    // Get the block's inventory
                    Inventory inventory = ((InventoryHolder) block.getState()).getInventory();

                    // Set all of the items to it
                    for (Map.Entry<Integer, ItemStack> entry : items.entrySet()) {
                        int slot = entry.getKey();
                        ItemStack stack = entry.getValue();

                        if (stack == null) {
                            continue;
                        }

                        // Add it to the inventory
                        inventory.setItem(slot, stack);
                    }
                }
            }
        });
    }

    /**
     * Wrap a block in a restorableblock object<br>
     * This may return null if the provided block is null
     *
     * @param block The block to turn into a RestorableBlock
     * @return The RestorableBlock instance or null
     */
    public static RestorableBlock wrapBlock(Block block) {
        if (block == null) {
            return null;
        }

        BlockCache blockCache = BlockCache.getInstance();
        RestorableBlock rblock = new RestorableBlock();
        rblock.id = blockCache.getBlockId(block);
        rblock.world = block.getWorld().getName();
        rblock.x = block.getX();
        rblock.y = block.getY();
        rblock.z = block.getZ();

        BlockState state = block.getState();

        // Does it have an inventory? ^^
        if (state instanceof InventoryHolder) {
            Inventory inventory = ((InventoryHolder) state).getInventory();
            ItemStack[] stacks = inventory.getContents();

            for (int slot = 0; slot < stacks.length; slot++) {
                ItemStack stack = stacks[slot];

                if (stack == null) {
                    continue; // don't waste space!
                }

                rblock.setSlot(slot, stack);
            }
        }

        return rblock;
    }

    /**
     * Set a slot in the inventory
     *
     * @param slot The slot to set the ItemStack into
     * @param stack The ItemStack to set
     */
    public void setSlot(int slot, ItemStack stack) {
        items.put(slot, stack);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public Map<Integer, ItemStack> getItems() {
        return items;
    }
}
