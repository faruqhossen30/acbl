
package com.shopnolive.shopnolive.model.profile;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyInfo_ {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isLive")
    @Expose
    private Boolean isLive;
    @SerializedName("mainCoinBalane")
    @Expose
    private Integer mainCoinBalane;

    @SerializedName("presentCoinBalance")
    @Expose
    private Integer presentCoinBalance;
    @SerializedName("userLevel")
    @Expose
    private Integer userLevel;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("followers")
    @Expose
    private List<Object> followers = null;
    @SerializedName("following")
    @Expose
    private List<Following> following = null;
    @SerializedName("isLiveUsers")
    @Expose
    private List<IsLiveUser> isLiveUsers = null;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public Integer getMainCoinBalane() {
        return mainCoinBalane;
    }

    public void setMainCoinBalane(Integer mainCoinBalane) {
        this.mainCoinBalane = mainCoinBalane;
    }

    public Integer getPresentCoinBalance() {
        return presentCoinBalance;
    }

    public void setPresentCoinBalance(Integer presentCoinBalance) {
        this.presentCoinBalance = presentCoinBalance;
    }

    public Integer getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(Integer userLevel) {
        this.userLevel = userLevel;
    }

    public String getImage() {
        return image;
    }


    public void setImage(String image) {
        this.image = image;
    }

    public List<Object> getFollowers()



    {
        return followers;
    }

    public void setFollowers(List<Object> followers) {
        this.followers = followers;
    }

    public List<Following> getFollowing() {
        return following;
    }

    public void setFollowing(List<Following> following) {
        this.following = following;
    }

    public List<IsLiveUser> getIsLiveUsers() {
        return isLiveUsers;
    }

    public void setIsLiveUsers(List<IsLiveUser> isLiveUsers) {
        this.isLiveUsers = isLiveUsers;
    }

}
