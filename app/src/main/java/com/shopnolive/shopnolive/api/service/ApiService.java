package com.shopnolive.shopnolive.api.service;

import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.GiftListResponse;
import com.shopnolive.shopnolive.model.GiftResponse;
import com.shopnolive.shopnolive.model.GiftSendModel;
import com.shopnolive.shopnolive.model.ReportModel;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.TestUser;
import com.shopnolive.shopnolive.model.UserListResponse;
import com.shopnolive.shopnolive.model.coin.ShareCoin;
import com.shopnolive.shopnolive.model.coin_history.UserHistory;
import com.shopnolive.shopnolive.model.gift.GiftHistoryResponse;
import com.shopnolive.shopnolive.model.login.LoginRespons;
import com.shopnolive.shopnolive.model.profile.IsLiveUserResponse;
import com.shopnolive.shopnolive.model.registration.RegistrationModel;
import com.shopnolive.shopnolive.model.registration.Response;
import com.shopnolive.shopnolive.model.token.AgoraTokenModel;
import com.shopnolive.shopnolive.model.token.Token;
import com.shopnolive.shopnolive.model.user.BlockResponse;
import com.shopnolive.shopnolive.model.user.MyProfile;
import com.shopnolive.shopnolive.model.user.UserDetails;
import com.shopnolive.shopnolive.statusCheck.StatusModel;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryName;

public interface ApiService {

//@Headers({"Accept: application/json"})
    //@Headers("Content-Type: application/json")

    //1
    @FormUrlEncoded
    @POST("registerApi.php")
    Call<RegistrationModel> registrationUser(@Field("name") String name, @Field("phone") String phone,@Field("device_id") String device_id);

    // Call<ResultMsg> registrationUser(@Body RegistrationModel cartList);

    //2
    @FormUrlEncoded
    @POST("loginApi.php")
    Call<LoginRespons> loginUser(@Field("phone") String number,@Field("device_id") String device_id);


    //2
    @FormUrlEncoded
    @POST("userInfoApi.php")
    Call<RegistrationModel> userPersonalData(@Field("id") String id);


    //3
    @FormUrlEncoded
    @POST("helpApi.php")
    Call<ResultMsg> helpPost(@Field("id") String id, @Field("msg") String msg);

    //4
    @FormUrlEncoded
    @POST("followApi.php")
    Call<ResultMsg> followUser(@Field("myid") String myId, @Field("followersid") String followOther);

    //5
    @FormUrlEncoded
    @POST("unfollowApi.php")
    Call<ResultMsg> unFollowUser(@Field("myid") String myId, @Field("followersid") String followOther);

    //6
    @POST("user/delete.php")
    Call<DeleteResponse> deleteUser();

    //7
    @FormUrlEncoded
    @POST("nameChangeApi.php")
    Call<ResultMsg> nameChange(@Field("myid") String myId, @Field("name") String changeName);

    //8
    @FormUrlEncoded
    @POST("numberChangeApi.php")
    Call<ResultMsg> numberChange(@Field("myid") String myId, @Field("phone") String changeName);

    //9
    @FormUrlEncoded
    @POST("blockApi.php")
    Call<ResultMsg> blockUser(@Field("myid") String myId, @Field("blockid") String block);

    //10
    @FormUrlEncoded
    @POST("unBlockApi.php")
    Call<ResultMsg> unBlockUser(@Field("myid") String myId, @Field("unblockid") String unblock);

    //11
    @FormUrlEncoded
    @POST("shareCoinApi.php")
    Call<String> shareCoin(@Field("myid") String myId, @Field("shareid") String shareid, @Field("coin") int coin);

    //12
    @FormUrlEncoded
    @POST("liveApi.php")
    Call<IsLiveUserResponse> isLive(@Field("myid") String myId);

    //13
    @FormUrlEncoded
    @POST("historyInsertApi.php")
    Call<ResultMsg> historyAdded(@Field("senderId") String senderId, @Field("receiverId") String receiverId, @Field("coin") int coin);

    //14
    @FormUrlEncoded
    @POST("historyViewApi.php")
    Call<List<UserHistory>> historyView(@Field("receiverId") String receiverId);

    //15
    @FormUrlEncoded
    @POST("shareCoinApi.php")
    Call<String> levelUpdate(@Field("id") String id, @Field("level") int level);

    //16
    //@Part MultipartBody.Part file
    @FormUrlEncoded
    @Multipart
    @POST("imageUploadApi.php")
    Call<String> imageUpload(@Field("id") RequestBody id, @Field("image") RequestBody file);

    //@Part MultipartBody.Part  file
    //16
    //@Part MultipartBody.Part file

    @Multipart
    @POST("imageUploadApi.php")
    Call<String> imageUploadFile(@Part("id") RequestBody id, @Part MultipartBody.Part file);

    //1

    @GET("allUsersApi.php?")
    Call<UserDetails> allUser(@Query("Id") String id);

    @FormUrlEncoded
    @Multipart
    @PUT("user/update-profile-image")
    Call<Response> uploadProfile(@Header("Authorization") String authorization, @Body MultipartBody.Part image);

    // TODO: user/share-coin
    @POST("user/share-coin")
    Call<Response> shareCoinOrDiamond(@Header("Authorization") String authorization, @Body ShareCoin coin);

    @GET("all/3252465795")
    Call<List<TestUser>> tesUser();

    @GET("api.php?")
    Call<String> getDivision(@QueryName String options);

    @FormUrlEncoded
    @POST("gift/send.php")
    Call<GiftResponse> giftCoin(@Field("sender_id") int senderId, @Field("receiver_id") int receiverId, @Field("coin") int coin, @Field("comission") int comission);

    @POST("gift/send.php")
    Call<GiftResponse> giftCoinToUser(@Body GiftSendModel giftSendModel);

    @GET("gift/list.php")
        //@Headers("Authorization: Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwOlwvXC9mYW1vdXNsaXZlYXBwLmNvbSIsImlhdCI6MTYzMTYyMDU4NSwibmJmIjoxNjMxNjIwNTg1LCJ1c2VyIjoiNjM0In0.ZXM4Jj1jR_5IAG-U55vf2zkmTGwuP9Xf5NvNrS9_tRY")
    Call<GiftListResponse> getAllGiftList();

    @POST("user/get-token.php")
    Call<Token> getToken();

    @GET("user/streaming.php")
    Call<UserListResponse> getStreamingList();
//block............................................................................................
    @POST("user/block.php")
    @FormUrlEncoded
    Call<DeleteResponse> blockSpecificUser(
            @Field("blockId") int userId,
            @Field("duration") int duration
    );

    @FormUrlEncoded
    @POST("report/userReport.php")
    Call<ReportModel> reportUser(
            @Field("reported_user") String reportedId,
            @Field("reporter") String reporterId
    );

    @GET("user/me.php")
    Call<MyProfile> getMyProfile();

    @POST("user/join.php")
    @FormUrlEncoded
    Call<DeleteResponse> isGuest(@Field("is_guest") int is_guest);

    @DELETE("history/delete.php")
    Call<DeleteResponse> deleteMyHistory();


    @GET("history/list.php")
    Call<GiftHistoryResponse> getGiftHistory(@Query("user_id") String userId);

    @GET("block/check.php")
    Call<BlockResponse> isBlocked(@Query("user_id") String userId);

    @GET("serverSwitch.php")
    Call<StatusModel> check_status();


    @GET("settings.php")
    Call<AgoraTokenModel> getAgora_token();


}