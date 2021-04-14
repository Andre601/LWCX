package com.griefcraft.bukkit;

import org.bukkit.Chunk;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.PistonMoveReaction;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class EntityBlock implements Block {
    private static Entity entity;
    public static final int ENTITY_BLOCK_ID = 5000;
    public static String ENTITY_BLOCK_NAME = "Entity";
    public static final int POSITION_OFFSET = 50000;

    public EntityBlock(Entity entity) {
        EntityBlock.entity = entity;
        ENTITY_BLOCK_NAME = entity.getType().name();
    }

    @Override
    public int getX() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    @Override
    public int getY() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    @Override
    public int getZ() {
        return 50000 + EntityBlock.entity.getUniqueId().hashCode();
    }

    public int getTypeId() {
        return ENTITY_BLOCK_ID;
    }

    @Override
    @Nonnull
    public World getWorld() {
        return EntityBlock.entity.getWorld();
    }

    public static Entity getEntity() {
        return EntityBlock.entity;
    }

    public static Block getEntityBlock(Entity entity) {
        return new EntityBlock(entity);
    }

    @Override
    @Nonnull
    public List<MetadataValue> getMetadata(@Nonnull String s) {
        return entity.getMetadata(s);
    }

    @Override
    public boolean hasMetadata(@Nonnull String s) {
        return entity.hasMetadata(s);
    }

    @Override
    public void removeMetadata(@Nonnull String s, @Nonnull Plugin p) {
        entity.removeMetadata(s, p);
    }

    @Override
    public void setMetadata(@Nonnull String s, @Nonnull MetadataValue mv) {
        entity.setMetadata(s, mv);
    }

    @Override
    public boolean breakNaturally() {
        return false;
    }

    @Override
    public boolean breakNaturally(ItemStack itemStack) {
        return false;
    }

    @Override
    public boolean applyBoneMeal(@Nonnull BlockFace blockFace) {
        return false;
    }

    @Override
    @Nonnull
    public Biome getBiome() {
        return Biome.PLAINS; // Biome doesn't matter at all, so just return plains
    }

    @Override
    public int getBlockPower() {
        return 0;
    }

    @Override
    public int getBlockPower(@Nonnull BlockFace arg0) {
        return 0;
    }

    @Override
    @Nonnull
    public Chunk getChunk() {
        return EntityBlock.entity.getLocation().getChunk();
    }

    @Override
    public byte getData() {
        throw new IllegalStateException("getData should not be called.");
    }

    @Override
    @Nonnull
    public Collection<ItemStack> getDrops() {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public Collection<ItemStack> getDrops(ItemStack arg0) {
        return Collections.emptyList();
    }

    @Override
    @Nonnull
    public Collection<ItemStack> getDrops(@Nonnull ItemStack itemStack, Entity entity) {
        return Collections.emptyList();
    }

    @Override
    public boolean isPassable() {
        return false;
    }

    @Override
    public RayTraceResult rayTrace(@Nonnull Location location, @Nonnull Vector vector, double v, @Nonnull FluidCollisionMode fluidCollisionMode) {
        return null;
    }

    @Override
    @Nonnull
    public BoundingBox getBoundingBox() {
        return EntityBlock.entity.getBoundingBox();
    }

    @Override
    public BlockFace getFace(@Nonnull Block arg0) {
        return null;
    }

    @Override
    public double getHumidity() {
        return 0;
    }

    @Override
    public byte getLightFromBlocks() {
        return 0;
    }

    @Override
    public byte getLightFromSky() {
        return 0;
    }

    @Override
    public byte getLightLevel() {
        return 0;
    }

    @Override
    @Nonnull
    public Location getLocation() {
        return entity.getLocation();
    }

    @Override
    public Location getLocation(Location arg0) {
        return entity.getLocation(arg0);
    }

    @Override
    @Nonnull
    public PistonMoveReaction getPistonMoveReaction() {
        return EntityBlock.entity.getPistonMoveReaction();
    }

    @Override
    @Nonnull
    public Block getRelative(@Nonnull BlockFace arg0) {
        return this;
    }

    @Override
    @Nonnull
    public Block getRelative(@Nonnull BlockFace arg0, int arg1) {
        return this;
    }

    @Override
    @Nonnull
    public Block getRelative(int arg0, int arg1, int arg2) {
        return this;
    }

    @Override
    @Nonnull
    public BlockState getState() {
        return new EntityBlockState(this);
    }

    @Override
    public double getTemperature() {
        return 0;
    }

    @Override
    @Nonnull
    public Material getType() {
        // Temporary fix to avoid null pointer exceptions when using /lock on entities.
        // Entity protections are still locked under ENTITY_BLOCK_ID, not AIR.
        return Material.AIR;
    }

    @Override
    public boolean isBlockFaceIndirectlyPowered(@Nonnull BlockFace arg0) {
        return false;
    }

    @Override
    public boolean isBlockFacePowered(@Nonnull BlockFace arg0) {
        return false;
    }

    @Override
    public boolean isBlockIndirectlyPowered() {
        return false;
    }

    @Override
    public boolean isBlockPowered() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean isLiquid() {
        return false;
    }

    @Override
    public void setBiome(@Nonnull Biome arg0) {}

    @Override
    public void setType(@Nonnull Material arg0) {}

    public void setType(@Nonnull Material arg0, boolean arg1) {}


    @Override
    @Nonnull
    public BlockData getBlockData() {
        return Material.AIR.createBlockData();
    }

    @Override
    public void setBlockData(@Nonnull BlockData arg0) {}

    @Override
    public void setBlockData(@Nonnull BlockData arg0, boolean arg1) {}
}
