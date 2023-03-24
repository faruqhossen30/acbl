package com.shopnolive.shopnolive.ppal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FollowListModel {
    @SerializedName("success")
    @Expose
    private Boolean success;
    @SerializedName("data")
    @Expose
    private Data data;
    @SerializedName("userId")
    @Expose
    private String userId;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }


    public String getUserId() {
        return userId;
    }

    public class BlockId {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("fcm_token")
        @Expose
        private Object fcmToken;
        @SerializedName("firebaseUserId")
        @Expose
        private Object firebaseUserId;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(Object fcmToken) {
            this.fcmToken = fcmToken;
        }

        public Object getFirebaseUserId() {
            return firebaseUserId;
        }

        public void setFirebaseUserId(Object firebaseUserId) {
            this.firebaseUserId = firebaseUserId;
        }

    }

    public class BlockedById {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("fcm_token")
        @Expose
        private Object fcmToken;
        @SerializedName("firebaseUserId")
        @Expose
        private Object firebaseUserId;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(Object fcmToken) {
            this.fcmToken = fcmToken;
        }

        public Object getFirebaseUserId() {
            return firebaseUserId;
        }

        public void setFirebaseUserId(Object firebaseUserId) {
            this.firebaseUserId = firebaseUserId;
        }

    }

    public class Data {

        @SerializedName("myFollowerList")
        @Expose
        private List<MyFollower> myFollowerList = null;
        @SerializedName("myFollowingList")
        @Expose
        private List<MyFollowing> myFollowingList = null;
        @SerializedName("blockIds")
        @Expose
        private List<BlockId> blockIds = null;
        @SerializedName("blockedByIds")
        @Expose
        private List<BlockedById> blockedByIds = null;

        public List<MyFollower> getMyFollowerList() {
            return myFollowerList;
        }

        public void setMyFollowerList(List<MyFollower> myFollowerList) {
            this.myFollowerList = myFollowerList;
        }

        public List<MyFollowing> getMyFollowingList() {
            return myFollowingList;
        }

        public void setMyFollowingList(List<MyFollowing> myFollowingList) {
            this.myFollowingList = myFollowingList;
        }

        public List<BlockId> getBlockIds() {
            return blockIds;
        }

        public void setBlockIds(List<BlockId> blockIds) {
            this.blockIds = blockIds;
        }

        public List<BlockedById> getBlockedByIds() {
            return blockedByIds;
        }

        public void setBlockedByIds(List<BlockedById> blockedByIds) {
            this.blockedByIds = blockedByIds;
        }

    }


    public class MyFollower {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("fcm_token")
        @Expose
        private Object fcmToken;
        @SerializedName("firebaseUserId")
        @Expose
        private Object firebaseUserId;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(Object fcmToken) {
            this.fcmToken = fcmToken;
        }

        public Object getFirebaseUserId() {
            return firebaseUserId;
        }

        public void setFirebaseUserId(Object firebaseUserId) {
            this.firebaseUserId = firebaseUserId;
        }

    }

    public class MyFollowing {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("image")
        @Expose
        private String image;
        @SerializedName("fcm_token")
        @Expose
        private Object fcmToken;
        @SerializedName("firebaseUserId")
        @Expose
        private Object firebaseUserId;

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

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public Object getFcmToken() {
            return fcmToken;
        }

        public void setFcmToken(Object fcmToken) {
            this.fcmToken = fcmToken;
        }

        public Object getFirebaseUserId() {
            return firebaseUserId;
        }

        public void setFirebaseUserId(Object firebaseUserId) {
            this.firebaseUserId = firebaseUserId;
        }

    }
}
