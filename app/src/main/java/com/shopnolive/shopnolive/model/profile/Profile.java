
package com.shopnolive.shopnolive.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Profile {

    @SerializedName("my_info")
    @Expose
    private MyInfo myInfo;

    public MyInfo getMyInfo() {
        return myInfo;
    }

}
