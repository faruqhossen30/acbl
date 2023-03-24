package com.shopnolive.shopnolive.model;

public class UserStory {


    private String id;
    private String name;
    private String view;
    private int story_img;
    private int user_image;

    public UserStory()
    {

    }

    public UserStory(String id, String name, String view, int story_img, int user_image) {
        this.id = id;
        this.name = name;
        this.view = view;
        this.story_img = story_img;
        this.user_image = user_image;
    }

    public UserStory(String view, String userName, Integer userImage, Integer integer) {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setStory_img(int story_img) {
        this.story_img = story_img;
    }

    public void setUser_image(int user_image) {
        this.user_image = user_image;
    }

    public String getView() {
        return view;
    }

    public int getStory_img() {
        return story_img;
    }

    public int getUser_image() {
        return user_image;
    }

    public String getName() {
        return name;
    }

}
