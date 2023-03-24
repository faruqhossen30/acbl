
package com.shopnolive.shopnolive.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyInfo {

    @SerializedName("my_info")
    @Expose
    private MyInfo_ myInfo;

    public MyInfo_ getMyInfo() {
        return myInfo;
    }

    public void setMyInfo(MyInfo_ myInfo) {
        this.myInfo = myInfo;
    }

}
