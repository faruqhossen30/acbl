
package com.shopnolive.shopnolive.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IsLiveUser {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("image")
    @Expose
    private Object image;
    @SerializedName("role")
    @Expose
    private Object role;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isLive")
    @Expose
    private String isLive;

    @SerializedName("userLevel")
    @Expose
    private int userLevel;

    @SerializedName("mainCoinBalance")
    @Expose
    private String mainCoinBalance;
    @SerializedName("presentCoinBalance")
    @Expose
    private String presentCoinBalance;
    @SerializedName("accessToken")
    @Expose
    private Object accessToken;
    @SerializedName("entryAt")
    @Expose
    private String entryAt;
    @SerializedName("myId")
    @Expose
    private String myId;
    @SerializedName("followersId")
    @Expose
    private String followersId;

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

    public Object getImage() {
        return image;
    }

    public void setImage(Object image) {
        this.image = image;
    }

    public Object getRole() {
        return role;
    }

    public void setRole(Object role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getMainCoinBalance() {
        return mainCoinBalance;
    }

    public void setMainCoinBalance(String mainCoinBalance) {
        this.mainCoinBalance = mainCoinBalance;
    }

    public String getPresentCoinBalance() {
        return presentCoinBalance;
    }

    public void setPresentCoinBalance(String presentCoinBalance) {
        this.presentCoinBalance = presentCoinBalance;
    }

    public Object getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(Object accessToken) {
        this.accessToken = accessToken;
    }

    public String getEntryAt() {
        return entryAt;
    }

    public void setEntryAt(String entryAt) {
        this.entryAt = entryAt;
    }

    public String getMyId() {
        return myId;
    }

    public void setMyId(String myId) {
        this.myId = myId;
    }

    public String getFollowersId() {
        return followersId;
    }

    public void setFollowersId(String followersId) {
        this.followersId = followersId;
    }
}