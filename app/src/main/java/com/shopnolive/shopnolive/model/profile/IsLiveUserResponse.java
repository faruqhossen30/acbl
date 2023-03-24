package com.shopnolive.shopnolive.model.profile;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IsLiveUserResponse {

    @SerializedName("IsLiveUser")
    @Expose
    private List<IsLiveUser> isLiveUser = null;

    public List<IsLiveUser> getIsLiveUser() {
        return isLiveUser;
    }

    public void setIsLiveUser(List<IsLiveUser> isLiveUser) {
        this.isLiveUser = isLiveUser;
    }

}