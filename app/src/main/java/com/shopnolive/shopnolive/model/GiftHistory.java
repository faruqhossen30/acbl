package com.shopnolive.shopnolive.model;

public class GiftHistory {

    private String username;
    private String userImageUrl;
    private String userLevel;
    private String giftCoin;
    private String giftStickerUrl;
    private String giftId;

    public GiftHistory() {
    }

    public GiftHistory(String username, String userImageUrl, String userLevel, String giftCoin, String giftStickerUrl, String giftId) {
        this.username = username;
        this.userImageUrl = userImageUrl;
        this.userLevel = userLevel;
        this.giftCoin = giftCoin;
        this.giftStickerUrl = giftStickerUrl;
        this.giftId = giftId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserImageUrl() {
        return userImageUrl;
    }

    public void setUserImageUrl(String userImageUrl) {
        this.userImageUrl = userImageUrl;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getGiftCoin() {
        return giftCoin;
    }

    public void setGiftCoin(String giftCoin) {
        this.giftCoin = giftCoin;
    }

    public String getGiftStickerUrl() {
        return giftStickerUrl;
    }

    public void setGiftStickerUrl(String giftStickerUrl) {
        this.giftStickerUrl = giftStickerUrl;
    }

    public String getGiftId() {
        return giftId;
    }

    public void setGiftId(String giftId) {
        this.giftId = giftId;
    }
}
