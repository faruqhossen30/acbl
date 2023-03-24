package com.shopnolive.shopnolive.model;

public class LiveRequest {

    private String id;
    private String un_id;
    private String name;
    private Object image;
    private String type;
    private String coin;

    public LiveRequest() {
    }


    public LiveRequest(String type) {
        this.type = type;
    }

    public LiveRequest(String id, String un_id, String name, Object image, String type, String coin) {
        this.id = id;
        this.un_id = un_id;
        this.name = name;
        this.image = image;
        this.type = type;
        this.coin = coin;
    }


    /*    public LiveRequest(String id, String un_id, String name, Object image, String type) {
        this.id = id;
        this.un_id = un_id;
        this.name = name;
        this.image = image;
        this.type = type;
    }*/

    /*    public LiveRequest(String id, String name, Object image, String type) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.type = type;
    }*/


    public String getCoin() {
        return coin;
    }

    public void setCoin(String coin) {
        this.coin = coin;
    }

    public String getUn_id() {
        return un_id;
    }

    public void setUn_id(String un_id) {
        this.un_id = un_id;
    }

    public void setImage(Object image) {
        this.image = image;
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

    public Object getImage() {
        return image;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
