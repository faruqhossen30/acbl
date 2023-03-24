package com.shopnolive.shopnolive.model;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowRequest {

    @SerializedName("myid")
    @Expose
    private String myid;
    @SerializedName("followid")
    @Expose
    private String followid;

    public String getMyid() {
        return myid;
    }

    public void setMyid(String myid) {
        this.myid = myid;
    }

    public String getFollowid() {
        return followid;
    }

    public void setFollowid(String followid) {
        this.followid = followid;
    }

}