package com.shopnolive.shopnolive.model.user_follow;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnFollowUser {
    @SerializedName("unfollowId")
    @Expose
    private String unfollowId;

    public String getUnfollowId() {
        return unfollowId;
    }

    public void setUnfollowId(String unfollowId) {
        this.unfollowId = unfollowId;
    }

}
