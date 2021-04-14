package com.griefcraft.io;

/**
 * Enum for the different Backup Types LWC can have.
 */
// Hope you're happy about this one Hidendra ;)
public enum BackupType{
    EOF(-1),
    
    PROTECTION(0),
    BLOCK(1),
    INVALID(2);
    
    private final int type;
    
    BackupType(int type) {
        this.type = type;
    }
    
    public static BackupType getByInt(int integer) {
        for (BackupType type : values()) {
            if (type.getType() == integer) {
                return type;
            }
        }
        
        return INVALID;
    }
    
    public int getType(){
        return type;
    }
}
