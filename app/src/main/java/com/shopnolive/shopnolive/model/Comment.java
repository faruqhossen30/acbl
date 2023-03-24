package com.shopnolive.shopnolive.model;

public class Comment {
    public String id;
    public String name;
    public String image;
    public String type = "comment";
    public String comment;
    public String like;
    public String love;
    public String angry;
    public String sad;
    public String wow;
    public String haha;
    public String level = "1";

    public Comment() {

    }

    public Comment(String id, String name, String image, String comment, String like, String love, String angry, String sad, String wow, String haha, String level, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.comment = comment;
        this.like = like;
        this.love = love;
        this.angry = angry;
        this.sad = sad;
        this.wow = wow;
        this.haha = haha;
        this.level = level;
        this.type = type;
    }

    public Comment(String id, String name, String image, String comment, String level, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.comment = comment;
        this.level = level;
        this.type = type;
    }

    public Comment(String like) {
        this.like = like;
    }
   /*public Comment(String love) {
        this.like = like;
    }*/


    public Comment(String like, String love, String angry, String sad, String wow, String haha, String level, String type) {
        this.like = like;
        this.love = love;
        this.angry = angry;
        this.sad = sad;
        this.wow = wow;
        this.haha = haha;
        this.level = level;
        this.type = type;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getLove() {
        return love;
    }

    public void setLove(String love) {
        this.love = love;
    }

    public String getAngry() {
        return angry;
    }

    public void setAngry(String angry) {
        this.angry = angry;
    }

    public String getSad() {
        return sad;
    }

    public void setSad(String sad) {
        this.sad = sad;
    }

    public String getWow() {
        return wow;
    }

    public void setWow(String wow) {
        this.wow = wow;
    }

    public String getHaha() {
        return haha;
    }

    public void setHaha(String haha) {
        this.haha = haha;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}
