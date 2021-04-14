package com.griefcraft.cache;

import com.griefcraft.lwc.LWC;
import com.griefcraft.sql.PhysDB;
import com.griefcraft.util.MaterialUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loads and stores block mappings from the database, as well as caching them during use for performance.
 */
public class BlockCache {

    private static BlockCache blockCache = new BlockCache();

    private final LWC lwc;

    private final Map<Integer, Material> intBlockCache;
    private final Map<Material, Integer> materialBlockCache;

    private int nextId = 1;

    private BlockCache() {
        lwc = LWC.getInstance();
        intBlockCache = new HashMap<>();
        materialBlockCache = new HashMap<>();
    }

    /**
     * Gets the block cache.
     *
     * @return The instance of this BlockCache
     */
    public static BlockCache getInstance() {
        return blockCache;
    }

    /**
     * Clean up the singleton instance when disabling.
     */
    public static void destruct() {
        blockCache = null;
    }

    /**
     * Add a mapping to both maps. Only to be used internally.
     *
     * @param integer The integer to keep track of
     * @param material The Material to keep track of
     */
    private void addMapping(int integer, Material material) {
        intBlockCache.put(integer, material);
        materialBlockCache.put(material, integer);
    }

    /**
     * Remove a mapping from both maps. Only to be used internally.
     *
     * @param integer The integer to remove
     * @param material The Material to remove
     */
    private void removeMapping(int integer, Material material) {
        intBlockCache.remove(integer);
        materialBlockCache.remove(material);
    }

    /**
     * Loads all block mappings from the database.
     */
    public void loadBlocks() {
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement blocksStatement = database.prepare("SELECT id, name FROM " + prefix + "blocks");
            ResultSet blocksSet = blocksStatement.executeQuery();
            while (blocksSet.next()) {
                int materialId = blocksSet.getInt("id");
                String materialName = blocksSet.getString("name");
                Material material = Material.matchMaterial(materialName);
                if (material != null) {
                    addMapping(materialId, material);
                } else {
                    lwc.log("Unable to load " + materialName + " from " + prefix + "blocks!");
                }
            }
            blocksSet.close();
            PreparedStatement idStatement = database.prepare("SELECT MAX(id) FROM " + prefix + "blocks");
            ResultSet idSet = idStatement.executeQuery();
            if (idSet.next()) {
                nextId = idSet.getInt(1) + 1;
            }
            idSet.close();
        } catch (SQLException e) {
            lwc.log("Unable to load " + prefix + "blocks!");
            e.printStackTrace();
        }
    }

    /**
     * Adds a block the block cache by its Material type, and tries to add it if it doesn't exist.
     *
     * @param blockMaterial The material to add
     * @return int representing the new ID or -1 if not successful.
     */
    public int addBlock(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            return materialBlockCache.get(blockMaterial);
        }
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement statement = database.prepare("INSERT INTO " + prefix
                    + "blocks (id, name) VALUES(?, ?)");
            statement.setInt(1, nextId);
            statement.setString(2, blockMaterial.name());
            statement.executeUpdate();
            int newId = nextId;
            addMapping(newId, blockMaterial);
            ++nextId;
            return newId;
        } catch (SQLException e) {
            lwc.log("Unable to add " + blockMaterial.name() + " to " + prefix + "blocks!");
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Adds a block to the block cache by its Block name, and tries to add it if it doesn't exist.
     *
     * @param block The block to add
     * @return int representing the new ID or -1 if not successful.
     */
    public int addBlock(String block) {
        Material material = Material.matchMaterial(block);
        if (material != null) {
            return addBlock(material);
        }
        return -1;
    }

    /**
     * Adds a block to the block cache by its Block type, and tries to add it if it doesn't exist.
     *
     * @param block the block to add
     * @return int representing the new ID or -1 if not successful.
     */
    public int addBlock(Block block) {
        return addBlock(block.getType());
    }

    /**
     * Adds a block to the block cache by its pre-1.13 ID, and tries to add it if it doesn't exist.
     *
     * @param blockId The block to add by its ID.
     * @return int representing the new ID or -1 if not successful.
     */
    public int addBlock(int blockId) {
        return addBlock(MaterialUtil.getMaterialById(blockId));
    }

    /**
     * Removes a block the block cache by its Material type, if it exists.
     *
     * @param blockMaterial The Block to remove by its Material type
     */
    public void removeBlock(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            removeBlock(materialBlockCache.get(blockMaterial));
        }
    }

    public void removeBlock(String block) {
        Material material = Material.matchMaterial(block);
        if (material != null) {
            removeBlock(material);
        }
    }

    /**
     * Removes a block to the block cache by its Block type, if it exists.
     *
     * @param block The block to remove
     */
    public void removeBlock(Block block) {
        removeBlock(block.getType());
    }

    /**
     * Removes a block by its ID in the database, if it exists.
     *
     * @param blockId The block to remove by its ID
     */
    public void removeBlock(int blockId) {
        if (!intBlockCache.containsKey(blockId)) {
            return;
        }
        PhysDB database = lwc.getPhysicalDatabase();
        String prefix = database.getPrefix();
        try {
            PreparedStatement statement = database.prepare("DELETE FROM " + prefix + "blocks WHERE id = ?");
            statement.setInt(1, blockId);
            statement.executeUpdate();
            removeMapping(blockId, intBlockCache.get(blockId));
        } catch (SQLException e) {
            lwc.log("Unable to remove block from " + prefix + "blocks!");
            e.printStackTrace();
        }
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockMaterial The block ID to get by its Material type
     * @return int representing the ID or -1 if not successful.
     */
    public int getBlockId(Material blockMaterial) {
        Integer id = materialBlockCache.get(blockMaterial);
        if (id != null) {
            return id;
        }
        return addBlock(blockMaterial);
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockName The block ID to get by its name
     * @return int representing the ID or -1 if not successful.
     */
    public int getBlockId(String blockName) {
        Material material = Material.matchMaterial(blockName);
        if (material != null) {
            return getBlockId(material);
        }
        return addBlock(blockName);
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param block The block ID to get by the Block type
     * @return int representing the ID or -1 if not successful.
     */
    public int getBlockId(Block block) {
        return getBlockId(block.getType());
    }

    /**
     * Get a block's id, or try to add it if it doesn't exist.
     *
     * @param blockId The block ID to get by the pre-1.13 block id
     * @return int representing the ID or -1 if not successful.
     */
    public int getBlockId(int blockId) {
        if (intBlockCache.containsKey(blockId)) {
            return blockId;
        }
        return addBlock(blockId);
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockMaterial The Material to get by the Material type
     * @return Material of the block or null if not successful.
     */
    public Material getBlockType(Material blockMaterial) {
        if (materialBlockCache.containsKey(blockMaterial)) {
            return blockMaterial;
        }
        int id = addBlock(blockMaterial);
        if (intBlockCache.containsKey(id)) {
            return intBlockCache.get(id);
        }
        return null;
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockName The Material to get by the block name
     * @return Material of the block or null if not successful.
     */
    public Material getBlockType(String blockName) {
        Material material = Material.matchMaterial(blockName);
        if (material != null) {
            return getBlockType(material);
        }
        return null;
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param block The Material to get by the Block type
     * @return Material of the block or null if not successful.
     */
    public Material getBlockType(Block block) {
        return getBlockType(block.getType());
    }

    /**
     * Get a block's type, or try to add it if it doesn't exist.
     *
     * @param blockId The Material to get by the block ID
     * @return Material of the block or null if not successful.
     */
    public Material getBlockType(int blockId) {
        if (intBlockCache.containsKey(blockId)) {
            return intBlockCache.get(blockId);
        }
        Material material = MaterialUtil.getMaterialById(blockId);
        if (material != null) {
            int id = addBlock(material);
            if (intBlockCache.containsKey(id)) {
                return intBlockCache.get(id);
            }
        }
        return null;
    }
    
    /**
     * Values that LWC will expect to be constant across databases.
     */
    public enum Constants {
        AIR(0),
        ENTITY(1);
        
        private final int id;
        
        Constants(int id) {
            this.id = id;
        }
        
        public int getValue() {
            return id;
        }
    }

}
