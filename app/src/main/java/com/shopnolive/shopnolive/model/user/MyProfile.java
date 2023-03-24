package com.shopnolive.shopnolive.model.user;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.List;

public class MyProfile {
    public boolean success = false;
    public String message;

    @SerializedName("data")
    @Expose
    private ProfileData profileData = null;

    @SerializedName("followers")
    @Expose
    private List<Follower> followers = null;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ProfileData getProfileData() {
        return profileData;
    }

    public void setProfileData(ProfileData profileData) {
        this.profileData = profileData;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }
}
