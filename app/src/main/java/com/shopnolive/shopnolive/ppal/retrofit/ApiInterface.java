package com.shopnolive.shopnolive.ppal.retrofit;

import com.shopnolive.shopnolive.ppal.model.FcmModel;
import com.shopnolive.shopnolive.ppal.model.FollowListModel;
import com.shopnolive.shopnolive.statusCheck.StatusModel;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    //check server status famous live
    @GET("serverSwitch.php")
    Call<StatusModel> check_status();

    //update fcm token
    @FormUrlEncoded
    @POST("intialData/storeFirebaseData.php")
    Call<FcmModel> save_fcm_token(
            @Field("userId") String userId,
            @Field("fcmToken") String fcmToken,
            @Field("firebaseUserId") String firebaseUserId
    );
    //get follower
    @FormUrlEncoded
    @POST("intialData/appInitializeData.php")
    Call<FollowListModel> get_my_follower_list(
            @Field("userId") String userId
    );
}
