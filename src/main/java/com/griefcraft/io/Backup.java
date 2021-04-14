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
import org.bukkit.inventory.ItemStack;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Backup {
    
    private final OperationMode operationMode;
    
    private int revision;
    
    private long created;
    
    private DataInputStream inputStream;
    
    private DataOutputStream outputStream;

    /**
     * The backup file's current revision
     */
    public static final int CURRENT_REVISION = 1;

    public Backup(File file, OperationMode operationMode, EnumSet<BackupManager.Flag> flags) throws IOException {
        this.operationMode = operationMode;
        if (!file.exists()) {
            if (operationMode == OperationMode.READ) {
                throw new UnsupportedOperationException("The backup could not be read");
            } else {
                if(!file.createNewFile())
                    throw new IllegalStateException("Couldn't create file!");
            }
        }

        // Set some base data if we're writing
        if (operationMode == OperationMode.WRITE) {
            revision = CURRENT_REVISION;
            created = System.currentTimeMillis() / 1000;
        }

        // Are we using compression?
        boolean compression = flags.contains(BackupManager.Flag.COMPRESSION);

        // create the stream we need
        if (operationMode == OperationMode.READ) {
            FileInputStream fis = new FileInputStream(file);
            inputStream = new DataInputStream(compression ? new GZIPInputStream(fis) : fis);
        } else if (operationMode == OperationMode.WRITE) {
            FileOutputStream fos = new FileOutputStream(file);
            outputStream = new DataOutputStream(compression ? new GZIPOutputStream(fos) : fos);
        }
    }

    /**
     * Read an entity from the backup file
     *
     * @return Restorable instance or null
     * @throws IOException When the provided InputStream cannot be read
     * @throws UnsupportedOperationException when the 
     */
    protected Restorable readRestorable() throws IOException {
        if (operationMode != OperationMode.READ) {
            throw new UnsupportedOperationException("READ is not allowed on this backup.");
        }
        
        int type = inputStream.read();
        switch (BackupType.getByInt(type)) {
            case EOF:
                return null;
            
            case PROTECTION:
                RestorableProtection rprotection = new RestorableProtection();
                rprotection.setId(inputStream.readInt());
                rprotection.setProtectionType(inputStream.readByte());
                rprotection.setBlockId(inputStream.readShort());
                rprotection.setOwner(inputStream.readUTF());
                rprotection.setWorld(inputStream.readUTF());
                rprotection.setX(inputStream.readInt());
                rprotection.setY(inputStream.readShort());
                rprotection.setZ(inputStream.readInt());
                rprotection.setCreated(inputStream.readLong());
                rprotection.setUpdated(inputStream.readLong());
    
                return rprotection;
            
            case BLOCK:
                RestorableBlock rblock = new RestorableBlock();
                rblock.setId(inputStream.readShort());
                rblock.setWorld(inputStream.readUTF());
                rblock.setX(inputStream.readInt());
                rblock.setY(inputStream.readShort());
                rblock.setZ(inputStream.readInt());
                int itemCount = inputStream.readShort();
    
                for (int i = 0; i < itemCount; i++) {
                    // Read in us some RestorableItems
                    int slot = inputStream.readShort();
                    int itemId = inputStream.readShort();
                    int amount = inputStream.readShort();
                    short damage = inputStream.readShort();
        
                    // Create the stack
                    BlockCache blockCache = BlockCache.getInstance();
                    ItemStack itemStack = new ItemStack(blockCache.getBlockType(itemId), amount, damage);
        
                    // add it to the block
                    rblock.setSlot(slot, itemStack);
                }
    
                // Woo!
                return rblock;
            
            case INVALID:
            default:
                throw new UnsupportedOperationException("Read unknown type: " + type);
        }
    }

    /**
     * Write an entity to the backup file
     *
     * @param restorable The Restorable to use
     */
    protected void writeRestorable(Restorable restorable) throws IOException {
        if (operationMode != OperationMode.WRITE) {
            throw new UnsupportedOperationException("WRITE is not allowed on this backup.");
        }

        // write the id
        outputStream.write(restorable.getType());

        switch (restorable.getBackupType()) {
            case PROTECTION:
                RestorableProtection rprotection = (RestorableProtection) restorable;
    
                outputStream.writeInt(rprotection.getId());
                outputStream.writeByte(rprotection.getType());
                outputStream.writeShort(rprotection.getBlockId());
                outputStream.writeUTF(rprotection.getOwner());
                outputStream.writeUTF(rprotection.getWorld());
                outputStream.writeInt(rprotection.getX());
                outputStream.writeShort(rprotection.getY());
                outputStream.writeInt(rprotection.getZ());
                outputStream.writeLong(rprotection.getCreated());
                outputStream.writeLong(rprotection.getUpdated());
                break;
            
            case BLOCK:
                RestorableBlock rblock = (RestorableBlock) restorable;
    
                outputStream.writeShort(rblock.getId());
                outputStream.writeUTF(rblock.getWorld());
                outputStream.writeInt(rblock.getX());
                outputStream.writeShort(rblock.getY());
                outputStream.writeInt(rblock.getZ());
                outputStream.writeShort(rblock.getItems().size());
    
                // Write the items if there are any
                for (Map.Entry<Integer, ItemStack> entry : rblock.getItems().entrySet()) {
                    int slot = entry.getKey();
                    ItemStack stack = entry.getValue();
        
                    BlockCache blockCache = BlockCache.getInstance();
                    outputStream.writeShort(slot);
                    outputStream.writeShort(blockCache.getBlockId(stack.getType()));
                    outputStream.writeShort(stack.getAmount());
                    outputStream.writeShort(stack.getDurability());
                }
                break;
        }

        outputStream.flush();
    }

    /**
     * Read the backup's header
     *
     * @throws IOException When InputStream cannot be read
     */
    protected void readHeader() throws IOException {
        revision = inputStream.readShort();
        created = inputStream.readLong();
        inputStream.read(new byte[10]); // reserved space
    }

    /**
     * Write the backup's header
     *
     * @throws IOException When the OutputStream cannot write
     */
    protected void writeHeader() throws IOException {
        outputStream.writeShort(revision);
        outputStream.writeLong(created);
        outputStream.write(new byte[10]); // reserved space
        outputStream.flush();
    }

    /**
     * Close the backup file
     *
     * @throws IOException When either InputStream or OutputStream cannot be closed
     */
    protected void close() throws IOException {
        if (operationMode == OperationMode.READ) {
            inputStream.close();
        } else if (operationMode == OperationMode.WRITE) {
            outputStream.close();
        }
    }
    
    /**
     * The operations the backup is allowed to perform
     */
    public enum OperationMode {
        READ,
        WRITE
    }

}
