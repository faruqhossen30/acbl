
package com.shopnolive.shopnolive.model.coin_history;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserHistory {

    @SerializedName("history")
    @Expose
    private History history;

    @SerializedName("info")
    @Expose
    private Info info;

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

}
