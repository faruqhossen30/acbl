package com.shopnolive.shopnolive.activity.profile;

import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.model.registration.RegistrationModel;
import com.shopnolive.shopnolive.model.user.MyProfile;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileRepository {

    MutableLiveData<MyProfile> liveData;
    MutableLiveData<ProfileData> liveDataById;
    MutableLiveData<ProfileData> liveDataByMessId;


    public static ProfileRepository instance;
    // public static ProfileRepository instances;

    public static ProfileRepository getInstance() {

        if (instance == null) {

            synchronized (ProfileRepository.class) {
                if (instance == null) {
                    instance = new ProfileRepository();
                }
            }
        }

        return instance;
    }


    private ProfileRepository() {
        liveData = new MutableLiveData<>();
        liveDataById = new MutableLiveData<>();
        liveDataByMessId = new MutableLiveData<>();
    }


    /*public LiveData<LoginRespons> getProfiles() {
        Call<LoginRespons> call = null;
        try {
            call = api.loginUser(userLoginToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        call.enqueue(new Callback<LoginRespons>() {
            @Override
            public void onResponse(Call<LoginRespons> call, retrofit2.Response<LoginRespons> response) {

                LoginRespons respons = response.body();

                if (respons != null) {

                    try {
                        userInfo = respons.getResponse().get(0);
                        userProfileFollow = respons.getFollowers();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    liveData.postValue(respons);
                }
            }

            @Override
            public void onFailure(Call<LoginRespons> call, Throwable t) {

            }
        });

        return liveData;
    }*/

    public LiveData<MyProfile> getMyProfileData() {

        Log.d("API_Call", "Profile api calling");

        Call<MyProfile> call = api.getMyProfile();

        call.enqueue(new Callback<MyProfile>() {
            @Override
            public void onResponse(Call<MyProfile> call, Response<MyProfile> response) {

                MyProfile myProfile = response.body();

                if (myProfile != null && myProfile.isSuccess()) {
                    liveData.postValue(myProfile);
                }
            }

            @Override
            public void onFailure(Call<MyProfile> call, Throwable t) {

            }
        });

        return liveData;
    }


    public LiveData<ProfileData> getProfilesById(String id) {
        Call<RegistrationModel> call = api.userPersonalData(id);

        call.enqueue(new Callback<RegistrationModel>() {
            @Override
            public void onResponse(Call<RegistrationModel> call, Response<RegistrationModel> response) {
                RegistrationModel registrationModel = response.body();
                if (registrationModel != null && registrationModel.isSuccess()) {
                    liveDataById.postValue(registrationModel.getData());
                }
            }

            @Override
            public void onFailure(Call<RegistrationModel> call, Throwable t) {

            }
        });

        return liveDataById;
    }


    public LiveData<ProfileData> getProfileByMessIds(String id) {
        Call<RegistrationModel> call = api.userPersonalData(id);

        call.enqueue(new Callback<RegistrationModel>() {
            @Override
            public void onResponse(Call<RegistrationModel> call, Response<RegistrationModel> response) {
                RegistrationModel registrationModel = response.body();
                if (registrationModel != null && registrationModel.isSuccess()) {
                    liveDataByMessId.postValue(registrationModel.getData());
                }
            }

            @Override
            public void onFailure(Call<RegistrationModel> call, Throwable t) {

            }
        });

        return liveDataByMessId;
    }

}
