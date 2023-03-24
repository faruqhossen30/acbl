package com.shopnolive.shopnolive.model;

public class Profile
{
       private String id;
       private String name;
       private String picture;
       private String number;
       private int coin;
       private String follow;
       private String level;

    public Profile(String fullNumber, String sabbir, String picture, String number, int coin, Follow follow, String level)
    {
    }

    public Profile(String id, String name, String picture, String number, int coin, String follow, String level) {
        this.id = id;
        this.name = name;
        this.picture = picture;
        this.number = number;
        this.coin = coin;
        this.follow = follow;
        this.level = level;
    }


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

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCoin() {
        return coin;
    }

    public void setCoin(int coin) {
        this.coin = coin;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
