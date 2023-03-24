package com.shopnolive.shopnolive.model;

public class MessageListModel {
    String user_name;
    int user_id;
    String desc;
    int img;

    public MessageListModel() {

    }

    public MessageListModel(String user_name, int user_id, String desc,int img) {
        this.user_name = user_name;
        this.user_id = user_id;
        this.desc = desc;
        this.img=img;
    }

    public String getUser_name() {
        return user_name;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getDesc() {
        return desc;
    }

    public int getImg() {
        return img;
    }
}
