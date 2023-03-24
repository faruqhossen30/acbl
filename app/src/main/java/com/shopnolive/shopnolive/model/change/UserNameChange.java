package com.shopnolive.shopnolive.model.change;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserNameChange {

    @SerializedName("newName")
    @Expose
    private String newName;

    public UserNameChange(String newName) {
        this.newName = newName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

}
