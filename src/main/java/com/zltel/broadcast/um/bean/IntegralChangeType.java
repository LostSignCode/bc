package com.zltel.broadcast.um.bean;

public class IntegralChangeType {
    private Integer ictId;

    private String type;

    private String name;

    private String describes;

    private Integer operation;

    public Integer getIctId() {
        return ictId;
    }

    public void setIctId(Integer ictId) {
        this.ictId = ictId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes == null ? null : describes.trim();
    }

    public Integer getOperation() {
        return operation;
    }

    public void setOperation(Integer operation) {
        this.operation = operation;
    }
}