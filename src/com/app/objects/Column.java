package com.app.objects;

/**
 * Created by alicanb on 07.06.2018.
 */
public class Column implements Cloneable{
    public String name;
    public String type;

    public Column() {
    }

    public Column(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public Column clone() throws CloneNotSupportedException {
        return (Column)super.clone();
    }
}