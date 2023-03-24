package com.shopnolive.shopnolive.model;

public class StoryModel {

    int img;
    int userId;
    String name;

    public StoryModel() {

    }

    public StoryModel(int img, int userId, String name) {
        this.img = img;
        this.userId = userId;
        this.name = name;
    }

    public int getImg() {
        return img;
    }

    public int getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
