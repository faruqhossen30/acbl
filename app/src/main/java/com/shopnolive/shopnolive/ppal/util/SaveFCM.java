package com.shopnolive.shopnolive.ppal.util;

import android.app.Activity;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;
import com.shopnolive.shopnolive.ppal.model.FcmModel;
import com.shopnolive.shopnolive.ppal.retrofit.ApiClient;
import com.shopnolive.shopnolive.ppal.retrofit.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SaveFCM {
    private static SaveFCM instance = null;

    public SaveFCM() {

    }

    public static SaveFCM getInstance() {
        if (instance == null){
            instance = new SaveFCM();
        }
        return instance;

    }

    public void storeFcmToken(Activity activity,String userId){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                //get fcm token
                String token = task.getResult();
                //get firebase uid
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                assert firebaseUser != null;
                String firebaseUid = firebaseUser.getUid();
                //save token
                ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
                Call<FcmModel> call = apiInterface.save_fcm_token(userId,token,firebaseUid);
                call.enqueue(new Callback<FcmModel>() {
                    @Override
                    public void onResponse(Call<FcmModel> call, Response<FcmModel> response) {
                        if (response.isSuccessful() && response.body()!=null){
                            PrefManager prefManager = new PrefManager(activity);
                            prefManager.set_val(Common.USER_ID,userId);
                            prefManager.set_val(Common.MY_FCM_TOKEN,token);
                            prefManager.set_val(Common.MY_FIREBASE_ID,firebaseUid);
                            //Toast.makeText(activity, "ok save", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(activity, "cant save fcm", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FcmModel> call, Throwable t) {
                        Toast.makeText(activity, ""+t.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });



            }
        });



    }
}
