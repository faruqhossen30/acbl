package com.shopnolive.shopnolive.activity.main;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.allLiveUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.allUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.global_Position;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userLoginToken;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;

import android.Manifest;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.UserOptionsActivity;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.adapter.ChatHeadAdapter;
import com.shopnolive.shopnolive.adapter.UserStoryAdapter;
import com.shopnolive.shopnolive.databinding.ActivityMainBinding;
import com.shopnolive.shopnolive.model.ChatHeadModel;
import com.shopnolive.shopnolive.model.UserStory;
import com.shopnolive.shopnolive.model.login.LoginRespons;
import com.shopnolive.shopnolive.model.profile.IsLiveUser;
import com.shopnolive.shopnolive.model.profile.IsLiveUserResponse;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.model.user.UserDetails;
import com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity;
import com.shopnolive.shopnolive.ui.live.LiveBroadcastActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks,
        EasyPermissions.RationaleCallbacks {

    private ArrayList<ChatHeadModel> chatHeadModel;
    private ChatHeadAdapter chatHeadAdapter;
    private ArrayList<UserStory> userStories;
    private UserStoryAdapter userStoryAdapter;
    private CircleImageView user;
    private ImageView goLive;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView1;

    // Try
    private int allUserTry = 0;
    private final int profileTry = 0;

    private LiveUserViewModel viewModels;
    private ProfileViewModel profileViewModel;
    private ActivityMainBinding binding;

    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        viewModels = new ViewModelProvider(this).get(LiveUserViewModel.class);

        Log.d("userinfo", "getAllUsers:  Check user start");
        Log.d("userinfo", "getAllUsers: 1");


        //getProfile();

        initFunction();

        initListener();

        observeAllUsers();
        observeAnyChange();
    }


    //Observer
    private void observeAnyChange() {

        profileViewModel.getProfile().observe(this, myProfile -> {
            try {
                userInfo = myProfile.getProfileData();

                observeAllUsers();
                userProfileFollow = myProfile.getFollowers();

                //Toast.makeText(this, ""+userInfo.getUserLevel(), Toast.LENGTH_SHORT).show();

                binding.tvUsername.setText(userInfo.getName());
                if (userInfo.getImage() != null)
                    Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(user);
                //getLiveUser();
            } catch (Exception e) {
                e.printStackTrace();
            }

            recyclerView.setVisibility(View.GONE);
            //getAllUser();

            try {
                // ObserverLiveUser();
                getLiveUser();
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

    }

    private void observeAllUsers() {
        //Log.d("userinfo", "getAllUsers: 2 " + userInfo.getId());

        /*viewModels.getAllUsers().observe(this, allUserResponses -> {
            //Log.d("userinfo", "getAllUsers: " + allUserResponses.getResponse().get(0).getId());

            List<ProfileData> userDetails = allUserResponses.getResponse();

            //allUserInfo.clear();
            allUserInfo = userDetails;
            Log.d("userinfo", "getAllUsers: " + userInfo.getId());

            Log.d("userinfo", "user size: " + allUserInfo.size());

            for (int i = 0; i < allUserResponses.getResponse().size(); i++) {
                //Log.d("userid", "own: " + userInfo.getId());

                if (userInfo.getId().equals(userDetails.get(i).getId())) {
                    userDetails.remove(i);
                    global_Position = i;
                }
            }
            userStoryAdapter = new UserStoryAdapter(MainActivity.this, userDetails);
            recyclerView1.setAdapter(userStoryAdapter);
            binding.shimmerLayoutHome.setVisibility(View.GONE);

        });*/

        viewModels.getStreamingNow().observe(this, userListResponse -> {
            if (userListResponse.isSuccess()) {
                List<ProfileData> userDetails = userListResponse.getData();

                //allUserInfo.clear();
                allUserInfo = userDetails;
                //Log.d("userinfo", "getAllUsers: " + userInfo.getId());

                userStoryAdapter = new UserStoryAdapter(MainActivity.this, userDetails);
                recyclerView1.setAdapter(userStoryAdapter);
                binding.shimmerLayoutHome.setVisibility(View.GONE);
            } else {
                binding.shimmerLayoutHome.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, userListResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void initListener() {

        binding.mainRefresh.setOnRefreshListener(() -> binding.mainRefresh.postDelayed(() -> {
            observeAnyChange();
            binding.mainRefresh.setRefreshing(false);
        }, 2000));


        user.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, UserOptionsActivity.class);
            startActivity(intent);
        });

        goLive.setOnClickListener(v -> {

            Intent i = new Intent(MainActivity.this, LiveBroadcastActivity.class);
            //   i.putExtra("story", userImages[0]);

            i.putExtra("name", "Sabbir");
            i.putExtra("waiting", "own");

            if (userInfo.getId() != null) {
                i.putExtra("id", userInfo.getId());
            } else {
                i.putExtra("id", "random");
            }

            startActivity(i);

        });
    }

    private void initFunction() {
        recyclerView = findViewById(R.id.chat_head);
        recyclerView1 = findViewById(R.id.user_story);
        user = findViewById(R.id.user);
        goLive = findViewById(R.id.goLive);

        chatHeadModel = new ArrayList<>();
        userStories = new ArrayList<>();

        //   Log.d("response", "onCreate: user Token "+ String.valueOf(userLoginToken));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                MainActivity.this, LinearLayoutManager.HORIZONTAL, false
        );

        int numberOfColumn;
        if (MainActivity.this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            numberOfColumn = 2;
        } else {
            numberOfColumn = 3;
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(
                MainActivity.this, numberOfColumn
        );

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView1.setLayoutManager(gridLayoutManager);
        recyclerView1.setItemAnimator(new DefaultItemAnimator());

        chatHeadAdapter = new ChatHeadAdapter(MainActivity.this, new ArrayList<>());
        recyclerView.setAdapter(chatHeadAdapter);

        userStoryAdapter = new UserStoryAdapter(MainActivity.this, new ArrayList<>());
        recyclerView1.setAdapter(userStoryAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        methodRequiresTwoPermission();
        observeAnyChange();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        //  ObserveAnyChange();
    }

    private void getLiveUser() {

        Call<IsLiveUserResponse> responseCall = api.isLive(userInfo.getId());

        responseCall.enqueue(new Callback<IsLiveUserResponse>() {
            @Override
            public void onResponse(Call<IsLiveUserResponse> call, Response<IsLiveUserResponse> response) {
                if (response.body() != null) {

                    List<IsLiveUser> liveUser = response.body().getIsLiveUser();

                    if (liveUser != null) {

                        myRef.child("liveUser").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {


                                List<String> id = new ArrayList<>();
                                List<IsLiveUser> live = new ArrayList<>();

                                for (DataSnapshot d : snapshot.getChildren()) {
                                    String data = d.getKey();

                                    //id.add(emoji);
                                    Log.d("LiveDataTest", "onDataChange: size " + liveUser.size());
                                    for (int i = 0; i < liveUser.size(); i++) {


                                        if (data.equals(liveUser.get(i).getFollowersId())) {
                                            Log.d("LiveDataTest", "onDataChange: Final " + liveUser.get(i).getId());


                                            //   IsLiveUser isLiveUser =
                                            live.add(liveUser.get(i));
                                        }
                                        //liveUser.size();
                                    }
                                    // if(userInfo.get)
                                }


                                if (live.size() > 0) {
                                    // Log.d("LiveDataTest", "onDataChange: not null  ");


                                    allLiveUserInfo = live;
                                    chatHeadAdapter = new ChatHeadAdapter(MainActivity.this, live);
                                    recyclerView.setAdapter(chatHeadAdapter);
                                    recyclerView.setVisibility(View.VISIBLE);
                                } else {
                                    //   Log.d("LiveDataTest", "onDataChange: not null  ");

                                    recyclerView.setVisibility(View.GONE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    } else {
                        recyclerView.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onFailure(Call<IsLiveUserResponse> call, Throwable t) {

            }
        });

    }





    private void goToLogin() {
        Intent intent = new Intent(MainActivity.this, OTPPhoneNumberActivity.class);
        startActivity(intent);
        finish();
    }


    private void getAllUser() {
        //api;
        Call<UserDetails> calls = api.allUser("110");

        calls.enqueue(new Callback<UserDetails>() {
            @Override
            public void onResponse(@NonNull Call<UserDetails> call, @NonNull Response<UserDetails> response) {

                if (response.body() != null) {
                    List<ProfileData> userDetails = response.body().getResponse();

                    allUserInfo.clear();
                    allUserInfo = userDetails;

                    Log.d("userid", "user size: " + allUserInfo.size());
                    for (int i = 0; i < response.body().getResponse().size(); i++) {
                        Log.d("userid", "own: " + userInfo.getId());

                        if (userInfo.getId().equals(userDetails.get(i).getId())) {
                            userDetails.remove(i);
                            global_Position = i;
                        }

                    }
                    userStoryAdapter = new UserStoryAdapter(MainActivity.this, userDetails);
                    recyclerView1.setAdapter(userStoryAdapter);


                } else {
                    if (allUserTry == 0) {
                        allUserTry++;
                        getAllUser();
                    }
                }

                binding.shimmerLayoutHome.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<UserDetails> call, Throwable t) {

            }
        });


    }

    private void methodRequiresTwoPermission() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET, Manifest.permission.RECORD_AUDIO};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            EasyPermissions.requestPermissions(this, "Please Allow Permission !!", 1, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    private void getProfile() {

        Log.d("TAG_userLoginToken", "getProfile: " + userLoginToken);

        Call<LoginRespons> call = null;
        try {
            call = api.loginUser(userLoginToken,"");
        } catch (Exception e) {
            e.printStackTrace();
        }

        call.enqueue(new Callback<LoginRespons>() {
            @Override
            public void onResponse(Call<LoginRespons> call, retrofit2.Response<LoginRespons> response) {

                LoginRespons loginResponse = response.body();

                if (loginResponse != null) {
                    getAllUser();
                    try {
                        userInfo = loginResponse.getResponse().get(0);
                        userProfileFollow = loginResponse.getFollowers();
                        if (userInfo.getImage() != null)
                            Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(user);
                        //getLiveUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    recyclerView.setVisibility(View.GONE);
                    //getAllUser();

                    try {
                        getLiveUser();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            }

            @Override
            public void onFailure(Call<LoginRespons> call, Throwable t) {

            }
        });
    }


    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }


    @Override
    public void onRationaleAccepted(int requestCode) {

    }

    @Override
    public void onRationaleDenied(int requestCode) {

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }
}
