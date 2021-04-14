package com.griefcraft.bukkit;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class EntityBlockState implements BlockState {
    private static EntityBlock entityBlock;

    public EntityBlockState(EntityBlock entityBlock) {
        EntityBlockState.entityBlock = entityBlock; // TODO: Should do deep copy.
    }

    public static EntityBlock getEntityBlock() {
        return EntityBlockState.entityBlock;
    }

    public void setEntityBlock(EntityBlock entityBlock) {
        EntityBlockState.entityBlock = entityBlock;
    }

    @Override
    @Nonnull
    public Block getBlock() {
        return entityBlock;
    }

    @Override
    @Nonnull
    public Material getType() {
        return entityBlock.getType();
    }

    @Override
    public byte getLightLevel() {
        return entityBlock.getLightLevel();
    }

    @Override
    @Nonnull
    public World getWorld() {
        return entityBlock.getWorld();
    }

    @Override
    public int getX() {
        return entityBlock.getX();
    }

    @Override
    public int getY() {
        return entityBlock.getY();
    }

    @Override
    public int getZ() {
        return entityBlock.getZ();
    }

    @Override
    @Nonnull
    public Location getLocation() {
        return entityBlock.getLocation();
    }

    @Override
    public Location getLocation(Location location) {
        return entityBlock.getLocation(location); // TODO: What to do with param location
    }

    @Override
    @Nonnull
    public Chunk getChunk() {
        return entityBlock.getChunk();
    }

    @Override
    public void setData(@Nonnull MaterialData materialData) {
        // Yeah, this does not work: entityBlock.setData(materialData.getData());
        // TODO: What to do with it, deprecated?
    }

    @Override
    public void setType(@Nonnull Material material) {
        entityBlock.setType(material);
    }

    @Override
    public boolean update() {
        return false;
    }

    @Override
    public boolean update(boolean b) {
        return false;
    }

    @Override
    public boolean update(boolean b, boolean b1) {
        return false;
    }

    @Override
    public byte getRawData() {
        throw new IllegalStateException("getRawData should not be called.");
    }

    @Override
    public void setRawData(byte b) {
        throw new IllegalStateException("setRawData should not be called.");
    }

    @Override
    public boolean isPlaced() {
        return false;
    }

    @Override
    public void setMetadata(@Nonnull String s, @Nonnull MetadataValue metadataValue) {

    }

    @Override
    @Nonnull
    public List<MetadataValue> getMetadata(String s) {
        return new ArrayList<>();
    }

    @Override
    public boolean hasMetadata(String s) {
        return false;
    }

    @Override
    public void removeMetadata(@Nonnull String s, @Nonnull Plugin plugin) {

    }

    @Override
    @Nonnull
    public BlockData getBlockData() {
        return entityBlock.getBlockData();
    }

    @Override
    public void setBlockData(@Nonnull BlockData arg0) {

    }

    @Override
    @Nonnull
    public MaterialData getData() {
        return new MaterialData(Material.AIR);
    }
}
