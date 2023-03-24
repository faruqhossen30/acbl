package com.shopnolive.shopnolive.ppal.util;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    private final SharedPreferences sharedPreferences;

    public PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(Common.PREF_MANAGER, Context.MODE_PRIVATE);
    }

    //set int val
    public void set_val(String key, String val) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, val);
        editor.apply();
    }

    //get int val
    public String get_val(String key) {
        return sharedPreferences.getString(key, "");
    }
}
