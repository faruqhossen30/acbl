package com.shopnolive.shopnolive.utils.agora;

import static com.shopnolive.shopnolive.utils.Constants.AgoraConst.PREF_NAME;

import android.content.Context;
import android.content.SharedPreferences;


public class PrefManager {
    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
