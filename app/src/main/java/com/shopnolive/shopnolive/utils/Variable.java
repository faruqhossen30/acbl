package com.shopnolive.shopnolive.utils;

import android.content.SharedPreferences;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.shopnolive.shopnolive.model.BlockItem;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.IsLiveUser;
import com.shopnolive.shopnolive.model.profile.ProfileData;

import java.util.ArrayList;
import java.util.List;

import io.agora.rtc.video.VideoEncoderConfiguration;

public class Variable {

    public static VideoEncoderConfiguration.VideoDimensions[] VIDEO_DIMENSIONS = new VideoEncoderConfiguration.VideoDimensions[]{
            VideoEncoderConfiguration.VD_240x180,
            VideoEncoderConfiguration.VD_320x180,
            VideoEncoderConfiguration.VD_320x240,
            VideoEncoderConfiguration.VD_480x360,
            VideoEncoderConfiguration.VD_640x360,
            VideoEncoderConfiguration.VD_640x480,
            new VideoEncoderConfiguration.VideoDimensions(960, 540),
            VideoEncoderConfiguration.VD_1280x720
    };

    public static int[] VIDEO_MIRROR_MODES = new int[]{
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_AUTO,
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_ENABLED,
            io.agora.rtc.Constants.VIDEO_MIRROR_MODE_DISABLED,
    };

    public static SharedPreferences pref;
    // = getApplicationContext().getSharedPreferences("Login", 0); // 0 - for private mode
    public static SharedPreferences.Editor editor;
    // = pref.edit();

    // Write a message to the database
    public static FirebaseDatabase database = FirebaseDatabase.getInstance();
    public static DatabaseReference myRef = database.getReference("Live");

    public static String userLoginToken = null;
    public static String accessToken = null;
    public static String joinUserId = null;
    public static String playId = null;

    public static String RTMP_BASE_URL = "ws://35.225.172.98:5080/LiveApp/websocket";


    public static ProfileData userInfo = null;
    public static ProfileData joinUserInfo = null;
    public static ProfileData hostInfo = null;

    public static int userPosition;
    public static String selectedUserId;
    public static String selectedUserName;
    public static int liveUserPosition;
    public static int global_Position;
    public static String myId = "";
    public static String myName = "";
    public static String myImage = "";

    public static String waitingInfo;

    public static List<ProfileData> allUserInfo = new ArrayList<>();
    public static List<ProfileData> viewUserList = new ArrayList<>();
    public static List<IsLiveUser> allLiveUserInfo = new ArrayList<>();
    public static ArrayList<BlockItem> blockedUserList = new ArrayList<>();
    public static ArrayList<BlockItem> conversationBlockedList = new ArrayList<>();
    public static ArrayList<String> blockedUserIdList = new ArrayList<>();
    public static ArrayList<String> conversationBlockedIdList = new ArrayList<>();


    //   public static Response userProfileInformation;

    public static List<Follower> userProfileFollow = new ArrayList<>();

    public static int[] levelUpNumber = {999, 1999, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000, 100000, 200000, 300000, 400000, 500000, 600000, 700000, 800000, 900000, 1000000,};
}