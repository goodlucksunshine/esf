package com.laile.esf.common.model;

import java.util.List;

public class Hierarchy<T> extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private T id;

    private T parentId;

    private List<Hierarchy<T>> children;

    private Hierarchy<T> parent;

    public T getId() {
        return (T) this.id;
    }

    public void setId(T id) {
        this.id = id;
    }

    public T getParentId() {
        return (T) this.parentId;
    }

    public void setParentId(T id) {
        this.parentId = id;
    }

    public List<Hierarchy<T>> getChildren() {
        return this.children;
    }

    public void setChildren(List<Hierarchy<T>> children) {
        this.children = children;
    }

    public Hierarchy<T> getParent() {
        return this.parent;
    }

    public void setParent(Hierarchy<T> parent) {
        this.parent = parent;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = 31 * result + (this.id == null ? 0 : this.id.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Hierarchy<T> other = (Hierarchy) obj;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        return true;
    }
}