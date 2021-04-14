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

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class RestorableProtection implements Restorable {

    private int id;

    private int protectionType;

    private int blockId;

    private String owner;

    private String world;

    private int x;

    private int y;

    private int z;

    private String data;

    private long created;

    private long updated;

    @Override
    public int getType() {
        return getBackupType().getType();
    }
    
    @Override
    public BackupType getBackupType() {
        return BackupType.PROTECTION;
    }

    public void restore() {
        LWC lwc = LWC.getInstance();
        Protection protection = lwc.getPhysicalDatabase().registerProtection(blockId, Protection.Type.values()[protectionType],
                world, owner, data, x, y, z);
        // TODO fix the ID?
    }

    /**
     * Wrap a protection object around a RestorableProtection object<br>
     * This may return null if the provided Protection instance is null or the protection couldn't be parsed
     *
     * @param protection The Protection instance to create a RestorableProtection from
     * @return The RestorableProtection instance or null
     */
    public static RestorableProtection wrapProtection(Protection protection) {
        if (protection == null) {
            return null;
        }

        try {
            RestorableProtection rprotection = new RestorableProtection();
            rprotection.id = protection.getId();
            rprotection.protectionType = protection.getType().ordinal();
            rprotection.blockId = protection.getBlockId();
            rprotection.owner = protection.getOwner();
            rprotection.world = protection.getWorld();
            rprotection.x = protection.getX();
            rprotection.y = protection.getY();
            rprotection.z = protection.getZ();
            rprotection.data = protection.getData().toJSONString();
            rprotection.created = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(protection.getCreation()).getTime() / 1000;
            rprotection.updated = protection.getLastAccessed();

            return rprotection;
        } catch (ParseException e) {
            LWC.getInstance().getPlugin().getLogger().warning("Failed to wrap Protection: " + protection + " " + e.getMessage());
            return null;
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getProtectionType() {
        return protectionType;
    }

    public void setProtectionType(int protectionType) {
        this.protectionType = protectionType;
    }

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
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

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }
}
