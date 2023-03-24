package com.shopnolive.shopnolive.ppal.util;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import com.shopnolive.shopnolive.R;

public class Common {
    //pref manager
    public static final String PREF_MANAGER = "myPref";
    public static final String MY_ID = "my_id";
    public static final String BASE_URL = "https://vid-mates.com/prohor/testingProject/";
    public static final String BASE_URL_MAIN = "http://194.233.93.200/Api/";


    public static final String USER_PHONE = "my_phone";
    public static final String USER_ID = "my_id";
    public static final String MY_FCM_TOKEN = "fcm_token";
    public static final String MY_FIREBASE_ID = "my_firebase_uid";


    //change status bar color
    public static void changeStatusBarColor(Activity activity){
        Window window = activity.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
    }
}
