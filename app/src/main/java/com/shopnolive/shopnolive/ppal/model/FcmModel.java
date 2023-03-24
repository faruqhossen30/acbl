package com.shopnolive.shopnolive.ppal.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FcmModel {
    @SerializedName("userId")
    @Expose
    private String UserId;
    @SerializedName("fcmToken")
    @Expose
    private String FcmToken;
    @SerializedName("firebaseUserId")
    @Expose
    private String FirebaseUserId;

    public String getUserId() {
        return UserId;
    }

    public String getFcmToken() {
        return FcmToken;
    }

    public String getFirebaseUserId() {
        return FirebaseUserId;
    }
}
