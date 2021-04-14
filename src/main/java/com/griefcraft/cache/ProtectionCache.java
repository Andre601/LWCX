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

package com.griefcraft.cache;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;

public class ProtectionCache {

    private final static int ADAPTIVE_CACHE_TICK = 10;

    private final static int ADAPTIVE_CACHE_MAX = 100000;

    private final LWC lwc;

    private final LRUCache<Protection, Object> references;

    private final WeakLRUCache<String, Protection> byCacheKey;

    private final WeakLRUCache<Integer, Protection> byId;

    private final WeakLRUCache<String, Protection> byKnownBlock;

    private final LRUCache<String, Object> byKnownNulls;

    private final int capacity;

    private int adaptiveCapacity = 0;

    private final MethodCounter counter = new MethodCounter();

    private final static Object FAKE_VALUE = new Object();

    public ProtectionCache(LWC lwc) {
        this.lwc = lwc;
        this.capacity = lwc.getConfiguration().getInt("core.cacheSize", 10000);

        this.references = new LRUCache<>(capacity);
        this.byCacheKey = new WeakLRUCache<>(capacity);
        this.byId = new WeakLRUCache<>(capacity);
        this.byKnownBlock = new WeakLRUCache<>(capacity);
        this.byKnownNulls = new LRUCache<>(Math.min(10000,
                capacity)); // enforce a min size so we have a known buffer
    }

    /**
     * Called from specific potentially high-intensity access areas. These areas
     * preferably need(!) free space in the cache and otherwise could cause
     * "lag" or other oddities.
     */
    public void increaseIfNecessary() {
        if (isFull() && adaptiveCapacity < ADAPTIVE_CACHE_MAX) {
            adaptiveCapacity += ADAPTIVE_CACHE_TICK;
            adjustCacheSizes();
        }
    }

    /**
     * Gets the direct reference of the references cache
     *
     * @return Instance of the LRUCache
     */
    public LRUCache<Protection, Object> getReferences() {
        return references;
    }

    /**
     * Get the method counter for this class
     *
     * @return Instance of the MethodCounter
     */
    public MethodCounter getMethodCounter() {
        return counter;
    }

    /**
     * Gets the default capacity of the cache
     *
     * @return int representing the current capacity of the cache
     */
    public int capacity() {
        return capacity;
    }

    /**
     * Gets the adaptive capacity of the cache
     *
     * @return int representing the current capacity of the adaptive cache
     */
    public int adaptiveCapacity() {
        return adaptiveCapacity;
    }

    /**
     * Gets the total capacity (default + adaptive) of the cache
     *
     * @return int representing the current total capacity of default and adaptive cache combined
     */
    public int totalCapacity() {
        return capacity + adaptiveCapacity;
    }

    /**
     * Clears the entire protection cache
     */
    public void clear() {
        // remove hard refs
        references.clear();

        // remove weak refs
        byCacheKey.clear();
        byId.clear();
        byKnownBlock.clear();
        byKnownNulls.clear();
    }

    /**
     * Check if the cache is full
     *
     * @return True if the cache is full
     */
    public boolean isFull() {
        return references.size() >= totalCapacity();
    }

    /**
     * Gets the amount of protections that are cached
     *
     * @return int representing the current size of the cache
     */
    public int size() {
        return references.size();
    }

    /**
     * Cache a protection<br>
     * This method does nothing if the provided Protection instance is null
     *
     * @param protection The protection to cache
     */
    public void addProtection(Protection protection) {
        if (protection == null) {
            return;
        }

        counter.increment("addProtection");

        // Add the hard reference
        references.put(protection, null);

        // Add weak references which are used to lookup protections
        byCacheKey.put(protection.getCacheKey(), protection);
        byId.put(protection.getId(), protection);

        // get the protection's finder if it was found via that
        if (protection.getProtectionFinder() != null) {
            Block protectedBlock = protection.getBlock();

            for (BlockState state : protection.getProtectionFinder()
                    .getBlocks()) {
                if (!protectedBlock.equals(state.getBlock())) {
                    String cacheKey = cacheKey(state.getLocation());
                    byKnownBlock.put(cacheKey, protection);
                }
            }
        }
    }

    /**
     * Remove the protection from the cache
     *
     * @param protection The protection instance to remove
     */
    public void removeProtection(Protection protection) {
        counter.increment("removeProtection");

        references.remove(protection);
        byId.remove(protection.getId());

        if (protection.getProtectionFinder() != null) {
            for (BlockState state : protection.getProtectionFinder()
                    .getBlocks()) {
                remove(cacheKey(state.getLocation()));
            }
        }
    }

    public Protection getProtection(BlockState block) {
        return getProtection(cacheKey(block.getWorld().getName(), block.getX(),
                block.getY(), block.getZ()));
    }

    /**
     * Remove the given cache key from any caches
     *
     * @param cacheKey The key of the cache entry to remove from all caches
     */
    public void remove(String cacheKey) {
        byCacheKey.remove(cacheKey);
        byKnownBlock.remove(cacheKey);
        byKnownNulls.remove(cacheKey);
    }

    /**
     * Make a cache key known as null in the cache<br>
     * This will add the provided key with a default fake value to the list of known nulls
     *
     * @param cacheKey The key to be added to the list of known nulls
     */
    public void addKnownNull(String cacheKey) {
        counter.increment("addKnownNull");
        byKnownNulls.put(cacheKey, FAKE_VALUE);
    }

    /**
     * Check if a cache key is known to not exist in the database
     *
     * @param cacheKey The key to check
     * @return True if the provided key is in the list of known nulls
     */
    public boolean isKnownNull(String cacheKey) {
        counter.increment("isKnownNull");
        return byKnownNulls.containsKey(cacheKey);
    }

    /**
     * Get a protection in the cache via its cache key
     *
     * @param cacheKey The key to get the Protection instance from
     * @return Protection instance from the provided key
     */
    public Protection getProtection(String cacheKey) {
        counter.increment("getProtection");

        Protection protection;

        // Check the direct cache first
        if ((protection = byCacheKey.get(cacheKey)) != null) {
            return protection;
        }

        // now use the 'others' cache
        return byKnownBlock.get(cacheKey);
    }

    /**
     * Get a protection in the cache located on the given block
     *
     * @param block The block to get the Protection from
     * @return Protection instance of the provided block
     */
    public Protection getProtection(Block block) {
        return getProtection(cacheKey(block.getWorld().getName(), block.getX(),
                block.getY(), block.getZ()));
    }

    /**
     * Check if the known block protection cache contains the given key
     *
     * @param block Block to check protection for
     * @return True if the checked block has a known Protection
     */
    public boolean isKnownBlock(Block block) {
        counter.increment("isKnownBlock");
        return byKnownBlock.containsKey(cacheKey(block.getWorld().getName(),
                block.getX(), block.getY(), block.getZ()));
    }

    /**
     * Get a protection in the cache via its id
     *
     * @param id The id to get the protection for
     * @return Protection instance of the provided id
     */
    public Protection getProtectionById(int id) {
        counter.increment("getProtectionById");
        return byId.get(id);
    }

    /**
     * Gets the cache key for the given location
     *
     * @param location Location to get the key for
     * @return String representing the key for the provided location
     */
    public String cacheKey(Location location) {
        return cacheKey(location.getWorld().getName(), location.getBlockX(),
                location.getBlockY(), location.getBlockZ());
    }

    /**
     * Generate a cache key using the given data
     *
     * @param world The world name to use for the key
     * @param x The X coordinate to use for the key
     * @param y The Y coordinate to use for the key
     * @param z The Z coordinate to use for the key
     * @return String in the format {@literal <world>:<x>:<y>:<z>}
     */
    public String cacheKey(String world, int x, int y, int z) {
        return world + ":" + x + ":" + y + ":" + z;
    }

    private void adjustCacheSizes() {
        references.maxCapacity = totalCapacity();
        byCacheKey.maxCapacity = totalCapacity();
        byId.maxCapacity = totalCapacity();
        byKnownBlock.maxCapacity = totalCapacity();
        byKnownNulls.maxCapacity = totalCapacity();
    }

    public LWC getLwc() {
        return lwc;
    }

}
