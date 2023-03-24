package com.shopnolive.shopnolive.model.login;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.List;

public class LoginRespons {

    @SerializedName("response")
    @Expose
    private List<ProfileData> response = null;

    @SerializedName("followers")
    @Expose
    private List<Follower> followers = null;

    public List<ProfileData> getResponse() {
        return response;
    }

    public void setResponse(List<ProfileData> response) {
        this.response = response;
    }

    public List<Follower> getFollowers() {
        return followers;
    }

    public void setFollowers(List<Follower> followers) {
        this.followers = followers;
    }

}
