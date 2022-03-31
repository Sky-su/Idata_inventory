package com.ives.idata_inventory.entity;

public class SerialEntity {
    private String serialIdentifier;
    private String typeName;

    public SerialEntity() {
    }

    public String getSerialIdentifier() {
        return serialIdentifier;
    }

    public void setSerialIdentifier(String serialIdentifier) {
        this.serialIdentifier = serialIdentifier;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }
}
