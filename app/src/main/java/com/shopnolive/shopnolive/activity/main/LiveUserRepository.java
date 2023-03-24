package com.shopnolive.shopnolive.activity.main;

import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.shopnolive.shopnolive.model.GiftListData;
import com.shopnolive.shopnolive.model.GiftListResponse;
import com.shopnolive.shopnolive.model.UserListResponse;
import com.shopnolive.shopnolive.model.profile.IsLiveUser;
import com.shopnolive.shopnolive.model.profile.IsLiveUserResponse;
import com.shopnolive.shopnolive.model.user.UserDetails;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveUserRepository {

    MutableLiveData<List<IsLiveUser>> liveData;// = new MutableLiveData<>();
    MutableLiveData<UserDetails> allUser;// = new MutableLiveData<>();
    MutableLiveData<List<GiftListData>> giftList;// = new MutableLiveData<>();
    MutableLiveData<UserListResponse> streamingNow;


    public static LiveUserRepository instance;

    // SingleTon
    //LiveData
    public static LiveUserRepository getInstance() {
        if (instance == null) {
            synchronized (LiveUserRepository.class) {
                if (instance == null) {
                    instance = new LiveUserRepository();

                }
            }

        }
        return instance;
    }


    public MutableLiveData<List<IsLiveUser>> getLiveUser() {


        Call<IsLiveUserResponse> responseCall = api.isLive(userInfo.getId());

        responseCall.enqueue(new Callback<IsLiveUserResponse>() {
            @Override
            public void onResponse(Call<IsLiveUserResponse> call, Response<IsLiveUserResponse> response) {
                if (response.body() != null) {
                    liveData.postValue(response.body().getIsLiveUser());

                }
            }

            @Override
            public void onFailure(Call<IsLiveUserResponse> call, Throwable t) {

            }
        });


        return liveData;
    }


    private LiveUserRepository() {
        allUser = new MutableLiveData<>();
        liveData = new MutableLiveData<>();
        giftList = new MutableLiveData<>();
        streamingNow = new MutableLiveData<>();
    }

    public MutableLiveData<List<GiftListData>> getAllGiftList() {
        Call<GiftListResponse> call = api.getAllGiftList();
        call.enqueue(new Callback<GiftListResponse>() {
            @Override
            public void onResponse(Call<GiftListResponse> call, Response<GiftListResponse> response) {
                GiftListResponse giftListResponse = response.body();
                if (giftListResponse != null && giftListResponse.isSuccess()) {
                    giftList.postValue(giftListResponse.getData());
                }
            }

            @Override
            public void onFailure(Call<GiftListResponse> call, Throwable t) {
            }
        });

        return giftList;
    }

    public MutableLiveData<UserDetails> getAllUsers() {

        Log.d("userinfo", "getAllUsers: " + userInfo.getId());
        //api;
        Call<UserDetails> calls = api.allUser(userInfo.getId());

        calls.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(Call<UserDetails> call, Response<UserDetails> response) {

                if (response.body() != null) {
                    allUser.postValue(response.body());
                }

            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {

            }
        });


        return allUser;
    }

    public MutableLiveData<UserListResponse> getStreamingList() {
        Log.d("API_Call", "Streaming now calling");
        api.getStreamingList().enqueue(new Callback<UserListResponse>() {
            @Override
            public void onResponse(Call<UserListResponse> call, Response<UserListResponse> response) {
                UserListResponse userListResponse = response.body();
                if (userListResponse != null) {
                    streamingNow.postValue(userListResponse);
                }
            }

            @Override
            public void onFailure(Call<UserListResponse> call, Throwable t) {
                streamingNow.postValue(new UserListResponse(false, t.getMessage(), new ArrayList<>()));
            }
        });

        return streamingNow;
    }
}
