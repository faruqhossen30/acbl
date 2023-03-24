package com.shopnolive.shopnolive.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GiftSendModel {
    @SerializedName("sender_id")
    @Expose
    private int senderId;

    @SerializedName("receiver_id")
    @Expose
    private int receiverId;

    @SerializedName("coin")
    @Expose
    private int coin;

    @SerializedName("comission")
    @Expose
    private int comission;


    public GiftSendModel(int senderId, int receiverId, int coin, int comission) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.coin = coin;
        this.comission = comission;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public int getComission() {
        return comission;
    }

    public void setComission(int comission) {
        this.comission = comission;
    }
}
