//this is just e model class for the ChatHeadAdapter
//this helps ChatHeadAdapter class to retrieve name and image of the user

package com.shopnolive.shopnolive.model;

public class ChatHeadModel {
    Integer userImage;
    String userName;

    public ChatHeadModel() {
    }

    public ChatHeadModel(Integer image, String name) {
        userImage = image;
        userName = name;

    }

    public Integer getUserImage() {
        return userImage;
    }

    public String getUserName() {
        return userName;
    }
}
