package ru.model;

import java.io.Serializable;

public abstract class Identity<ID> implements Serializable {

    protected ID id;

    public ID getId() {
        return id;
    }

    public void setId(ID id) {
        this.id = id;
    }
}
