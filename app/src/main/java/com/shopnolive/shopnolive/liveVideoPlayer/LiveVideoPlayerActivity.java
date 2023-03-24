package com.shopnolive.shopnolive.liveVideoPlayer;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.RTMP_BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.allLiveUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.allUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.joinUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.liveUserPosition;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserName;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
import static com.shopnolive.shopnolive.utils.Variable.userPosition;
import static com.shopnolive.shopnolive.utils.Variable.userProfileFollow;
import static com.shopnolive.shopnolive.utils.Variable.waitingInfo;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_AUDIO_BITRATE;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_VIDEO_BITRATE;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_VIDEO_HEIGHT;
import static io.antmedia.webrtcandroidframework.apprtc.CallActivity.EXTRA_VIDEO_WIDTH;

import android.app.AlertDialog;
import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.main.LiveUserViewModel;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.adapter.GiftHistoryAdapter;
import com.shopnolive.shopnolive.adapter.HistoryViewAdapter;
import com.shopnolive.shopnolive.adapter.MessageSendAdapter;
import com.shopnolive.shopnolive.adapter.ViewHeadAdapter;
import com.shopnolive.shopnolive.databinding.ActivityLiveVideoPlayerBinding;
import com.shopnolive.shopnolive.fragment.GiftHistoryFragment;
import com.shopnolive.shopnolive.fragment.SendGiftFragment;
import com.shopnolive.shopnolive.fragment.StreamersViewFragment;
import com.shopnolive.shopnolive.fragment.WaitlistFragment;
import com.shopnolive.shopnolive.listener.UserItemClickListener;
import com.shopnolive.shopnolive.model.Comment;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.GiftHistory;
import com.shopnolive.shopnolive.model.LiveRequest;
import com.shopnolive.shopnolive.model.PlayerModel;
import com.shopnolive.shopnolive.model.ResultMsg;
import com.shopnolive.shopnolive.model.gift.GiftHistoryItem;
import com.shopnolive.shopnolive.model.gift.GiftHistoryResponse;
import com.shopnolive.shopnolive.model.profile.Follower;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.shopnolive.shopnolive.model.user.BlockResponse;
import com.shopnolive.shopnolive.model.user.UserDetails;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.webrtc.DataChannel;
import org.webrtc.SurfaceViewRenderer;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import de.tavendo.autobahn.WebSocket;
import io.antmedia.webrtcandroidframework.ConferenceManager;
import io.antmedia.webrtcandroidframework.IDataChannelObserver;
import io.antmedia.webrtcandroidframework.IWebRTCListener;
import io.antmedia.webrtcandroidframework.StreamInfo;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveVideoPlayerActivity extends AppCompatActivity implements IWebRTCListener, IDataChannelObserver, UserItemClickListener {


    private ActivityLiveVideoPlayerBinding binding;

    boolean followResult = false;
    boolean joinRequest = true;
    private SendGiftFragment sendGift;
    private GiftHistoryFragment profile;

    //TODO : gift
    private ImageView gift;
    private View joinBottomSheet, giftBottomSheet;
    private ViewPager viewPagerJoin, viewPagerGift;
    private TabLayout tabLayoutJoin, tabLayoutGift;

    private BottomSheetBehavior joinBottomSheetBehavior, giftBottomSheetBehavior;

    private StreamersViewFragment streamersViewFragment;
    private WaitlistFragment waitlistFragment;
    //LiveFragment liveFragment;

    private RecyclerView messageShow;
    private MessageSendAdapter messageSendAdapter;
    private GiftHistoryAdapter giftHistoryAdapter;
    private List<String> message;

    private String unicKey;
    public int showLayouts = 0;
    private ArrayList<SurfaceViewRenderer> playViewRenderers;
    private ArrayList<PlayerModel> playerModels;
    public String[] livePeoples = new String[5];

    //    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {

        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    private Handler mainHandler;

    private boolean needRetrySource;

    private boolean shouldAutoPlay;
    private int resumeWindow;
    private long resumePosition;

    //    private RtmpDataSource.RtmpDataSourceFactory rtmpDataSourceFactory;
    protected String userAgent;

    // Activity lifecycle
    private String userId;
    private TextView joinBTN;

    private ImageButton likeFly, loveFly, hahaFly, wowFly, sadFly, angryFly;

    //Share
    //private
    private ImageView share_video;
    private TextView countLove;

    // Live User Info
    private TextView liveUserDiamond, liveUserView;
    private String waiting;
    private LinearLayout linearLayout;
    boolean mStopHandler = false;

    private LiveUserViewModel viewModelLiveUser;
    //private ProfileViewModel viewModel;


    private String followIDForPlay = null;


    String hostName;
    private ConferenceManager conferenceManager;
    private ContentLoadingProgressBar progressBar;

    private boolean isJoining = false;

    private int camFace = 1;

    private boolean reJoin = false;

    private boolean isMuted = true;
    private boolean isVideoOff = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        waiting = intent.getStringExtra("waiting");
        waitingInfo = "other";


        followIDForPlay = intent.getStringExtra("id");

        shouldAutoPlay = true;
        if (followIDForPlay != null) {
            playId = followIDForPlay;
        } else {
            playId = allUserInfo.get(userPosition).getId();
        }

        livePeoples[0] = playId;

        // This is the play id for the get Live user information. This is the most importent

        hostName = intent.getStringExtra("name");


        mainHandler = new Handler();

        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        // Activity
        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_player);
        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModelLiveUser = ViewModelProviders.of(this).get(LiveUserViewModel.class);

        // Todo : this section start for live broadcast data
        getChangeObserverHost();
        observerAnyChange();

        //Todo : End
        ///     viewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);

        binding.livePlayView.cameraHide.setVisibility(View.GONE);
        binding.livePlayView.liveActive2User.setVisibility(View.GONE);

        View rootView = findViewById(R.id.root);

        linearLayout = findViewById(R.id.layout);


        playViewRenderers = new ArrayList<>();
        playerModels = new ArrayList<>();

        playerModels.add(new PlayerModel(binding.livePlayView.hostGift,
                binding.livePlayView.hostPlayCoin,
                binding.livePlayView.hostPlayName));

        playerModels.add(new PlayerModel(binding.livePlayView.player1layout,
                binding.livePlayView.player1Coin,
                binding.livePlayView.player1Name));

        playerModels.add(new PlayerModel(binding.livePlayView.player2layout,
                binding.livePlayView.player2Coin,
                binding.livePlayView.player2Name));

        playerModels.add(new PlayerModel(binding.livePlayView.player3layout,
                binding.livePlayView.player3Coin,
                binding.livePlayView.player3Name));

        playerModels.add(new PlayerModel(binding.livePlayView.player4layout,
                binding.livePlayView.player4Coin,
                binding.livePlayView.player4Name));


        playViewRenderers.add(binding.livePlayView.playerView);
        playViewRenderers.add(binding.livePlayView.playerView1);
        playViewRenderers.add(binding.livePlayView.playerView2);
        playViewRenderers.add(binding.livePlayView.playerView3);
        playViewRenderers.add(binding.livePlayView.playerView4);


        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//        this.getIntent().putExtra(EXTRA_VIDEO_FPS, 24);
        this.getIntent().putExtra(EXTRA_VIDEO_BITRATE, 300);
        this.getIntent().putExtra(EXTRA_VIDEO_HEIGHT, 426);
        this.getIntent().putExtra(EXTRA_VIDEO_WIDTH, 240);
        this.getIntent().putExtra(EXTRA_AUDIO_BITRATE, 64);


        String streamId = userInfo.getId();
        String roomId = playId;

        binding.livePlayView.cameraPreviewSurfaceViewPlay.setMirror(true);

        conferenceManager = new ConferenceManager(
                this,
                this,
                this.getIntent(),
                RTMP_BASE_URL,
                roomId,
                null,
                playViewRenderers,
                streamId,
                this
        );

        conferenceManager.setPlayOnlyMode(true);
        conferenceManager.setOpenFrontCamera(true);

        conferenceManager.joinTheConference();


        initLiveView();
        initFunction();

        initListener();
        getReactCount();
        getViewUser();
        //ToDo:if otp link recieve mnot working
        recieveLink();
        // setData();
        giftMethod();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(LiveVideoPlayerActivity.this, LinearLayoutManager.VERTICAL, false);
        linearLayoutManager.setStackFromEnd(true);

        messageShow.setLayoutManager(linearLayoutManager);
        //messageSendAdapter = new MessageSendAdapter(LiveVideoPlayerActivity.this, new ArrayList<>());

        //show gift anim
        showGiftAnim();
        checkIsBlockedOrNot();

        showLiveEndedStatus();


        Call<DeleteResponse> responseCall = api.isGuest(1);
        responseCall.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                //Toast.makeText(LiveVideoPlayerActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(LiveVideoPlayerActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void checkIsBlockedOrNot() {
        Call<BlockResponse> blockResponseCall = api.isBlocked(String.valueOf(playId));
        blockResponseCall.enqueue(new Callback<BlockResponse>() {
            @Override
            public void onResponse(Call<BlockResponse> call, Response<BlockResponse> response) {
                BlockResponse blockResponse = response.body();
                if (blockResponse != null && blockResponse.isSuccess()) {
                    if (blockResponse.isIs_blocked()) {
                        Toast.makeText(LiveVideoPlayerActivity.this, "This host blocked you", Toast.LENGTH_SHORT).show();
                        mIsRecording = false;

                        leaveToLive();
                        removeLiveRequest();
                    }
                }
            }

            @Override
            public void onFailure(Call<BlockResponse> call, Throwable t) {
                Toast.makeText(LiveVideoPlayerActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showLiveEndedStatus() {
        myRef.child(playId).addValueEventListener(valueEventListener);
    }

    private void recieveLink() {
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();

                            Log.e("TAG", "  sharelink " + deepLink.toString());
                            String sharelink = deepLink.toString();
                            try {

                                sharelink = sharelink.substring(sharelink.lastIndexOf("=") + 1);
                                Log.e("TAG", " substring " + sharelink);

                                String userid = sharelink.substring(0, sharelink.indexOf("-"));


                            } catch (Exception e) {
                                Log.e("TAG", " error " + e.toString());
                            }


                        }


                    }
                })
                .addOnFailureListener(this, e -> Log.e("TAG", "getDynamicLink:onFailure", e));
    }


    //Minimize window
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onUserLeaveHint() {
        Display d = getWindowManager()
                .getDefaultDisplay();
        Point p = new Point();
        d.getSize(p);
        int width = p.x;
        int height = p.y;

        Rational ratio
                = new Rational(width, height);
        PictureInPictureParams.Builder
                pip_Builder
                = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            pip_Builder = new PictureInPictureParams
                    .Builder();
        }
        pip_Builder.setAspectRatio(ratio).build();
        enterPictureInPictureMode(pip_Builder.build());
        super.onUserLeaveHint();
    }


    public void getViewUser() {

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                LiveVideoPlayerActivity.this, LinearLayoutManager.HORIZONTAL, false
        );

        binding.topBar.recyclerVewViewers.setLayoutManager(linearLayoutManager);


        myRef.child(playId).child("view").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                List<ProfileData> list = new ArrayList<>();
                list.clear();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    ProfileData value = d.getValue(ProfileData.class);
                    //   Log.d("firebase", "Value is: " + value.getId());
                    list.add(value);
                }
                //  viewUserList = list;
                binding.liveUserView.setText(String.valueOf(list.size()));

                ViewHeadAdapter viewHeadAdapter = new ViewHeadAdapter(LiveVideoPlayerActivity.this, list, LiveVideoPlayerActivity.this);
                binding.topBar.recyclerVewViewers.setAdapter(viewHeadAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });

    }

    private void initLiveView() {

        binding.livePlayView.userPlayCoin.setText(getResources().getString(R.string.diamond_emoji) + userInfo.getPresentCoinBalance());
        binding.livePlayView.userPlayName.setText(userInfo.getName());

        myRef.child(playId).child("view").child(userInfo.getId()).setValue(userInfo);

        unicKey = myRef.push().getKey();

        Comment comment = setCommentorLive("is coming");
        myRef.child(playId).child("commend").child(unicKey).setValue(comment);
        getComment();

        checkMyStatus();

    }

    private void checkMyStatus() {
        myRef.child(playId).child("view").child(userInfo.getId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    ProfileData myInfo = snapshot.getValue(ProfileData.class);
                    assert myInfo != null;
                    if (myInfo.getStatus().equals("blocked")) {
                        Toast.makeText(LiveVideoPlayerActivity.this, "You removed by host", Toast.LENGTH_SHORT).show();
                        leaveToLive();
                        removeLiveRequest();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LiveVideoPlayerActivity.this, "", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Comment setCommentorLive(String is_coming) {
        return new Comment(userInfo.getId(), userInfo.getName() + " " + is_coming, (String) userInfo.getImage(),/*userInfo.getName()+*/"", String.valueOf(userInfo.getUserLevel()), "comment");
    }

    private void initListener() {
        //Share Coin by User
        binding.userHistoryView.setOnClickListener(view -> userHistory());

        share_video.setOnClickListener(v -> {
            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "BD Live");
                String shareMessage = "\nYou Can Join With Us for Fun....... \n\n";
                shareMessage = shareMessage + "https://developer.android.com/training/sharing/send#java\n\n";
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                startActivity(Intent.createChooser(shareIntent, "Share BD Live"));
            } catch (Exception e) {
                //e.toString();
            }
        });
        binding.bottomBar.btnLike.setOnClickListener(v -> {

            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("like");
            likeAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "like", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();

        });
        binding.bottomBar.btnLove.setOnClickListener(v -> {

            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("love");

            loveAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "love", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();

        });
        binding.bottomBar.btnHaha.setOnClickListener(v -> {
            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("haha");
            hahaAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "haha", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();

        });
        binding.bottomBar.btnWow.setOnClickListener(v -> {
            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("wow");
            wowAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "wow", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();
        });
        binding.bottomBar.btnSad.setOnClickListener(v -> {
            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("sad");
            sadAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "sad", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();

        });
        binding.bottomBar.btnAngry.setOnClickListener(v -> {
            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(playId).child("Emoji").child(unicKey).setValue("angry");
            angryAnimation();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), "angry", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
            getComment();

        });
        binding.topBar.btnEndBroadcast.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setTitle("Leave");
            builder1.setMessage("Are you sure to leave this live?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    (dialog, id) -> {
                        dialog.cancel();
                        mIsRecording = false;

                        leaveToLive();
                        removeLiveRequest();
                    });

            builder1.setNegativeButton(
                    "No",
                    (dialog, id) -> dialog.cancel());

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });
        binding.bottomBar.sendButtonComment.setOnClickListener(v -> {
            String messages = binding.bottomBar.editTextComment.getText().toString();
            if (!messages.isEmpty()) {
                binding.bottomBar.editTextComment.setText("");
                message.add(messages);
                unicKey = myRef.push().getKey();
                // Live user id, comment user id, comment user Image,
                Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), messages, String.valueOf(userInfo.getUserLevel()), "comment");
                myRef.child(playId).child("commend").child(unicKey).setValue(comment);
                getComment();
                // messageShow.onScrollStateChanged(message.size());
            } else
                Toast.makeText(LiveVideoPlayerActivity.this, "Enter message", Toast.LENGTH_SHORT).show();

        });
        binding.topBar.btnFollow.setOnClickListener(v -> {
            if (followResult) {
                Call<ResultMsg> unFollow = api.unFollowUser(userInfo.getId(), playId);
                unFollow.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {

                        if (response.body() != null) {
                            followResult = false;
                            setUnFollow();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {

                    }
                });
            } else {
                Call<ResultMsg> follow = api.followUser(userInfo.getId(), playId);
                follow.enqueue(new Callback<ResultMsg>() {
                    @Override
                    public void onResponse(Call<ResultMsg> call, Response<ResultMsg> response) {

                        if (response.body() != null) {
                            followResult = true;
                            setFollow();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResultMsg> call, Throwable t) {

                    }
                });
            }
        });

    }



    private void initFunction() {

        liveUserDiamond = findViewById(R.id.liveUserDiamond);
        liveUserView = findViewById(R.id.liveUserView);


        gift = findViewById(R.id.giftLiveUser);

        joinBTN = findViewById(R.id.join_playLive);

        viewPagerGift = findViewById(R.id.view_pager_gift);
        tabLayoutGift = findViewById(R.id.tab_layout_gift);

        viewPagerJoin = findViewById(R.id.view_pager_join);
        tabLayoutJoin = findViewById(R.id.tab_layout_join);

        streamersViewFragment = new StreamersViewFragment();
        waitlistFragment = new WaitlistFragment();
//        liveFragment = new LiveFragment();

        sendGift = new SendGiftFragment();
        profile = new GiftHistoryFragment();


        tabLayoutGift.setupWithViewPager(viewPagerGift);
        tabLayoutJoin.setupWithViewPager(viewPagerJoin);

        countLove = findViewById(R.id.countLove);
        share_video = findViewById(R.id.share_video);

        likeFly = findViewById(R.id.imgButtonOneFly);
        loveFly = findViewById(R.id.imgButtonTwoFly);
        hahaFly = findViewById(R.id.imgButtonThreeFly);
        wowFly = findViewById(R.id.imgButtonFourFly);
        sadFly = findViewById(R.id.imgButtonFiveFly);
        angryFly = findViewById(R.id.imgButtonSixFly);

        messageShow = findViewById(R.id.messageShow);
        message = new ArrayList<>();

        binding.topBar.btnFollow.setVisibility(View.VISIBLE);

        gift.setOnClickListener(v -> {
            if (livePeoples[0] != null) {
                selectedUserId = livePeoples[0];
                hostName = playerModels.get(0).getNameText().getText().toString();
            } else {
                selectedUserId = playId;
            }

            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        /// Gift or Share Coin Any User Host or Live

        binding.livePlayView.hostGift.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            if (livePeoples[0] != null) {
                selectedUserId = livePeoples[0];
                hostName = playerModels.get(0).getNameText().getText().toString();
            } else {
                selectedUserId = playId;
            }

            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.livePlayView.player1layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[1];
            hostName = playerModels.get(1).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.livePlayView.player2layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[2];
            hostName = playerModels.get(2).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.livePlayView.player3layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[3];
            hostName = playerModels.get(3).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.livePlayView.player4layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[4];
            hostName = playerModels.get(4).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        binding.livePlayView.cameraHide.setOnClickListener(v -> {

            View view = getLayoutInflater().inflate(R.layout.history_show, null);
            TextView userName = view.findViewById(R.id.userNameForHistory);
            TextView userLevel = view.findViewById(R.id.userLevelForHistory);

            CircleImageView userImage = view.findViewById(R.id.historyShowProfileImage);
            RecyclerView userHistory = view.findViewById(R.id.userHistoryShowRV);

            userHistory.setLayoutManager(new LinearLayoutManager(this));

            BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(LiveVideoPlayerActivity.this, R.style.MaterialDialogSheet);
            mBottomSheetDialog.setContentView(view);
            mBottomSheetDialog.setCancelable(true);
            mBottomSheetDialog.show();

            Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(userImage);
            userName.setText(userInfo.getName());
            userLevel.setText(String.valueOf(userInfo.getUserLevel()));

            /*if (followIDForPlay != null) {
                Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1).into(userImage);
                userName.setText(userInfo.getName());
                userLevel.setText(String.valueOf(userInfo.getUserLevel()));
            } else {
                Picasso.get().load(BASE_URL + joinUserInfo.getImage()).placeholder(R.drawable.user1).into(userImage);
                userName.setText(joinUserInfo.getName());
                userLevel.setText(String.valueOf(joinUserInfo.getUserLevel()));
            }*/


            //api;
            Call<GiftHistoryResponse> calls = api.getGiftHistory(userInfo.getId());


            calls.enqueue(new Callback<GiftHistoryResponse>() {
                @Override
                public void onResponse(Call<GiftHistoryResponse> call, Response<GiftHistoryResponse> response) {
                    if (response.body() != null && response.body().isSuccess()) {
                        List<GiftHistoryItem> userDetails = response.body().getData();

                        HistoryViewAdapter userStoryAdapter = new HistoryViewAdapter(LiveVideoPlayerActivity.this, userDetails);
                        userHistory.setAdapter(userStoryAdapter);
                    }
                }

                @Override
                public void onFailure(Call<GiftHistoryResponse> call, Throwable t) {

                }
            });

        });

        Button joinB = findViewById(R.id.joinPlay);

        joinB.setOnClickListener(v -> {

            if (joinRequest) {
                LiveRequest liveRequest = new LiveRequest(userInfo.getId(), userInfo.getId(), userInfo.getName(), userInfo.getImage(), "waiting", userInfo.getPresentCoinBalance());
                //Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), messages);

                myRef.child(playId).child("join").child(userInfo.getId()).setValue(liveRequest);

                unicKey = myRef.push().getKey();

                myRef.child(playId).child("commend").child(unicKey).setValue(setCommentorLive("Request for Join the Group!"));
                getComment();

                joinB.setText("Cancel");
                joinRequest = false;

            } else {
                joinB.setText("Add");

                unicKey = myRef.push().getKey();

                myRef.child(playId).child("commend").child(unicKey).setValue(setCommentorLive("cancel the joining request!"));
                getComment();

                removeLiveRequest();
                joinRequest = true;
            }
        });

        joinBTN.setOnClickListener(v -> joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED));


        binding.bottomBar.editTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s != null) {
                    binding.bottomBar.sendButtonComment.setVisibility(View.VISIBLE);
                } else {
                    binding.bottomBar.sendButtonComment.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.btnMuteCall.setOnClickListener(v -> {
            if (conferenceManager.isJoined()) {
                isMuted = !isMuted;
                if (isMuted) {
                    conferenceManager.getPeers().get(userInfo.getId()).enableAudio();
                    binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, R.color.white));
                } else {
                    conferenceManager.getPeers().get(userInfo.getId()).disableAudio();
                    binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, R.color.red));
                }

            } else {
                showToast("You are not live yet");
            }
        });

        binding.btnVideoOff.setOnClickListener(v -> {

            if (conferenceManager.isJoined()) {
                isVideoOff = !isVideoOff;
                if (isVideoOff) {
                    conferenceManager.getPeers().get(userInfo.getId()).enableVideo();
                    binding.btnVideoOff.setColorFilter(ContextCompat.getColor(this, R.color.white));
                } else {
                    conferenceManager.getPeers().get(userInfo.getId()).disableVideo();
                    binding.btnVideoOff.setColorFilter(ContextCompat.getColor(this, R.color.red));
                }

            } else {
                showToast("You are not live yet");
            }
        });

        //binding.bottomBar.editTextComment.requestFocus();
    }

    private void giftMethod() {

        selectedUserName = hostName;

        giftBottomSheet = findViewById(R.id.bottom_sheet_gift);
        giftBottomSheetBehavior = BottomSheetBehavior.from(giftBottomSheet);

        joinBottomSheet = findViewById(R.id.bottom_sheet_join);
        joinBottomSheetBehavior = BottomSheetBehavior.from(joinBottomSheet);


        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragments(streamersViewFragment, "VIEW");
        //viewPagerAdapter.addFragments(sendGift, "STREAMERS");
        viewPagerAdapter.addFragments(waitlistFragment, "REQUEST");

        //viewPagerAdapter.addFragments(liveFragment, "VIEW");
        viewPagerJoin.setAdapter(viewPagerAdapter);


        ViewPagerAdapter2 viewPagerAdapter2 = new ViewPagerAdapter2(getSupportFragmentManager(), 0);
        viewPagerAdapter2.addFragmentsGift(sendGift, "Send Gifts");
        viewPagerAdapter2.addFragmentsGift(profile, hostName);
        viewPagerGift.setAdapter(viewPagerAdapter2);

        tabLayoutGift.getTabAt(0).setIcon(R.drawable.gift);
        tabLayoutGift.getTabAt(1).setIcon(R.drawable.user1);

        tabLayoutGift.setTabIconTint(null);

        viewPagerGift.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    profile.updateHistory();
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void removeLiveRequest() {
        myRef.child(playId).child("join").child(userInfo.getId()).removeValue();
    }

    private void countEmotion() {
        int count = 1 + Integer.parseInt(countLove.getText().toString());
        countLove.setText(String.valueOf(count));
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shouldAutoPlay = true;
        setIntent(intent);
    }

    @Override
    public void onPause() {
        super.onPause();

    }


    @Override
    public void onStop() {
        super.onStop();

        mIsRecording = false;
        leaveToLive();
        removeLiveRequest();
    }


    private void leaveToLive() {

        if (conferenceManager.isJoined()) {
            conferenceManager.leaveFromConference();
        }

        myRef.child(playId).child("view").child(userInfo.getId()).removeValue();

        Call<DeleteResponse> deleteResponseCall = api.deleteMyHistory();
        deleteResponseCall.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                //Toast.makeText(LiveVideoBroadcasterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {

            }
        });

        finish();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // Play live
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            play();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }


    }


    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void play() {

        String URL = RTMP_BASE_URL + playId/*userId*/;

    }

    @Override
    protected void onStart() {
        super.onStart();

        followUser();
        getComment();
        play();
        viewChecker();
    }

    private void followUser() {
        //Log.d("followUser", "followUser: "+userProfileFollow.getId());

        int checkFollow = 0;
        try {

            if (userProfileFollow != null)
                for (Follower f : userProfileFollow) {
                    Log.d("followUser", "followUser: " + f.getFollowersId());
                    Log.d("followUser", "followUser: " + playId);

                    if (f.getFollowersId().equals(playId)) {
                        checkFollow++;
                        break;
                    }
                }

        } catch (Exception e) {

        }
        if (checkFollow != 0) {
            followResult = true;
            setFollow();
        } else {
            setUnFollow();
        }

    }

    private void setUnFollow() {
        binding.topBar.btnFollow.setImageDrawable(getResources().getDrawable(R.drawable.plus));
    }

    private void setFollow() {
        binding.topBar.btnFollow.setImageDrawable(getResources().getDrawable(R.drawable.ic_min));
        //follow.setBackground(getResources().getDrawable(R.drawable.ic_min));
    }


    public void startLive() {


        unicKey = myRef.push().getKey();

        try {
            getChangeObserverHost();
            observerAnyChange();

        } catch (Exception e) {
        }


        if (conferenceManager.isJoined() && conferenceManager.playOnlyMode && !isJoining) {

            isJoining = true;
            reJoin = true;

            joinConference();
            progressBar = new ContentLoadingProgressBar(LiveVideoPlayerActivity.this);
            progressBar.show();

            Log.i("ysawijaaw", "startLive: join");
        } else {
            Log.i("ysawijaaw", "startLive: add");

        }

    }

    private void joinConference() {
        try {
            conferenceManager.leaveFromConference();

            binding.livePlayView.cameraPreviewSurfaceViewPlay.release();

            binding.livePlayView.cameraPreviewSurfaceViewPlay.setMirror(true);

            conferenceManager = new ConferenceManager(
                    this,
                    this,
                    this.getIntent(),
                    RTMP_BASE_URL,
                    playId,
                    binding.livePlayView.cameraPreviewSurfaceViewPlay,
                    playViewRenderers,
                    userInfo.getId(),
                    this
            );

            conferenceManager.setOpenFrontCamera(true);

            conferenceManager.setPlayOnlyMode(false);

            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(() -> {
                conferenceManager.joinTheConference();
            }, 500);

        } catch (Exception e) {
            joinConference();
        }
    }

    public void openViews(boolean isLive) {
        binding.root.setVisibility(View.GONE);


        showLayouts = 2;

        if (isLive) {
            binding.livePlayView.cameraPreviewSurfaceViewPlay.setVisibility(View.VISIBLE);
        } else {

            binding.recyclerViewGiftAnim.setVisibility(View.GONE);

//            int i = 0;
//
//            while (i < livePeoples.length) {
//                if (livePeoples[i] != null) {
//                    Log.i("ysawijaaw", "openViews: main");
//                    playerModels.get(i).getConstraintLayout().setVisibility(View.VISIBLE);
//                }
//                i++;
//            }

//            viewChecker();
        }

        binding.livePlayView.hostPlayCoin.setVisibility(View.VISIBLE);
        binding.livePlayView.hostPlayName.setVisibility(View.VISIBLE);

        binding.livePlayView.cameraHide.setVisibility(View.VISIBLE);
        binding.livePlayView.liveActive2User.setVisibility(View.VISIBLE);
        binding.livePlayView.liveActiveLastUser.setVisibility(View.VISIBLE);
        binding.root.setVisibility(View.VISIBLE);


        refreshLayouts();
    }

    public void changeAudienceCamera(View view) {

        if (conferenceManager.isJoined()) {
            conferenceManager.getPeers().get(userInfo.getId()).switchCamera();

            if (camFace == 0) {
                camFace++;
                binding.livePlayView.cameraPreviewSurfaceViewPlay.setMirror(true);
            } else {
                camFace = 0;
                binding.livePlayView.cameraPreviewSurfaceViewPlay.setMirror(false);
            }

        } else {
            showToast("You are not live yet");
        }
    }

    public void hideLayouts() {

        myRef.child(playId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Log.i("ysawijaaw", "hideLayouts: Firebase");
                    binding.root.setVisibility(View.GONE);
                    binding.livePlayView.hostPlayCoin.setVisibility(View.GONE);
                    binding.livePlayView.hostPlayName.setVisibility(View.GONE);
                    binding.livePlayView.cameraHide.setVisibility(View.GONE);
                    binding.livePlayView.liveActive2User.setVisibility(View.GONE);
                    binding.livePlayView.liveActiveLastUser.setVisibility(View.GONE);
                    binding.recyclerViewGiftAnim.setVisibility(View.VISIBLE);

                    boolean b = true;

//                    for (PlayerModel sr : playerModels) {
//                        if (b) {
//                            b = false;
//                        } else {
//                            sr.getConstraintLayout().setVisibility(View.INVISIBLE);
////                            sr.setVisibility(View.INVISIBLE);
//                        }
//                    }

                    for (int i = 0; i < playerModels.size(); i++) {
                        if (b) {
                            b = false;
                        } else {
                            livePeoples[i] = null;
                            playViewRenderers.get(i).setVisibility(View.INVISIBLE);
                            playerModels.get(i).getConstraintLayout().setVisibility(View.INVISIBLE);
                        }
                    }

                    binding.root.setVisibility(View.VISIBLE);

                    refreshLayouts();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void refreshLayouts() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            binding.root.setVisibility(View.GONE);
            binding.root.setVisibility(View.VISIBLE);
        }, 1000);
    }

    public void totalCalls(int size) {
        if (size == 0) {
            binding.badge.setVisibility(View.GONE);
        } else {
            binding.badge.setNumber(size);
            binding.badge.setVisibility(View.VISIBLE);
        }
    }

    public void updateDetails(LiveRequest user, int i) {

        playerModels.get(i).getDiamondText().setText(getResources().getString(R.string.diamond_emoji) + user.getCoin());
        playerModels.get(i).getNameText().setText(user.getName());
    }

    public void kickOut() {
        showToast("You have been removed by Host");
        leaveToLive();
        removeLiveRequest();
    }

    public void removeFromCall() {
        showToast("You have been removed by Host");
        if (conferenceManager.isJoined()) {
            conferenceManager.leaveFromConference();
        }
    }

    public void liveChecker(List<LiveRequest> liveUser) {

        for (int i = 1; i < livePeoples.length; i++) {
            boolean isAvailable = false;

            Log.i("ysawijaaw", "liveChecker: " + livePeoples[i]);

            for (LiveRequest id : liveUser) {
                if (id.getId().equals(livePeoples[i])) {
                    isAvailable = true;
                    break;
                }
            }

            if (!isAvailable) {
                livePeoples[i] = null;
                Log.i("ysawijaaw", "liveChecker: nulled " + livePeoples[i]);
            }
        }
    }

    @Override
    public void onUserItemClicked(@NonNull String userId) {

    }

    private class ViewPagerAdapter2 extends FragmentPagerAdapter {

        List<Fragment> fragmentsGift = new ArrayList<>();
        List<String> fragmentsTitleGift = new ArrayList<>();

        public ViewPagerAdapter2(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void addFragmentsGift(Fragment fragment, String title) {
            fragmentsGift.add(fragment);
            fragmentsTitleGift.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsGift.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsGift.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleGift.get(position);
        }
    }

    public void getComment() {

        //    Log.d("firebase", "getComment: " + playId);
        // Read from the database

        myRef.child(playId).child("commend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.


                List<Comment> list = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //    List<Comment> list = new ArrayList<>();
                    Comment value = d.getValue(Comment.class);
                    //          Log.d("firebase", "Value is: " + value.getComment());
                    list.add(value);
                }

                changeObserver();
                observerAnyChange();
                getChangeObserverHost();

                messageSendAdapter = new MessageSendAdapter(LiveVideoPlayerActivity.this, 0, LiveVideoPlayerActivity.this);
                messageShow.setAdapter(messageSendAdapter);
                messageShow.smoothScrollToPosition(list.size());
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (giftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else if (joinBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        } else {
            minimizeApp();
        }

    }

    private void deleteComment() {
        myRef.child(playId).child("commend").removeValue();
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        List<Fragment> fragmentsJoin = new ArrayList<>();
        List<String> fragmentsTitleJoin = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        void addFragments(Fragment fragment, String title) {
            fragmentsJoin.add(fragment);
            fragmentsTitleJoin.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragmentsJoin.get(position);
        }

        @Override
        public int getCount() {
            return fragmentsJoin.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentsTitleJoin.get(position);
        }
    }

    private void getReactCount() {

        myRef.child("liveUser").child(playId).child("Emoji").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                long count = snapshot.getChildrenCount();
                countLove.setText(String.valueOf(count));

                List<String> emojiList = new ArrayList<>();

                for (DataSnapshot d : snapshot.getChildren()) {
                    String emoji = d.getValue(String.class);
                    emojiList.add(emoji);
                }

                if (emojiList.size() > 0) {


                    int size = emojiList.size() - 1;

                    if (emojiList.get((size)).equals("like")) {
                        likeAnimation();

                    } else if (emojiList.get((size)).equals("love")) {
                        loveAnimation();

                    } else if (emojiList.get((size)).equals("haha")) {

                        hahaAnimation();

                    } else if (emojiList.get((size)).equals("wow")) {
                        wowAnimation();
                    } else if (emojiList.get((size)).equals("sad")) {
                        sadAnimation();
                    } else if (emojiList.get((size)).equals("angry")) {
                        angryAnimation();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    // Animation Fly Start section

    private void angryAnimation() {


        Handler mainHandler = new Handler();

        // This is your code
        Runnable myRunnable = () -> {

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            angryFly.setVisibility(View.VISIBLE);
            angryFly.startAnimation(anim);
            angryFly.setVisibility(View.INVISIBLE);

        };
        mainHandler.post(myRunnable);


    }

    private void sadAnimation() {

        Handler mainHandler = new Handler(this.getMainLooper());

        // This is your code
        Runnable myRunnable = () -> {

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            sadFly.setVisibility(View.VISIBLE);
            sadFly.startAnimation(anim);
            sadFly.setVisibility(View.INVISIBLE);

        };
        mainHandler.post(myRunnable);


    }

    private void wowAnimation() {

        Handler mainHandler = new Handler();

        // This is your code
        Runnable myRunnable = () -> {

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            wowFly.setVisibility(View.VISIBLE);
            wowFly.startAnimation(anim);
            wowFly.setVisibility(View.INVISIBLE);

        };
        mainHandler.post(myRunnable);


    }

    private class AsyncTaskRunner extends AsyncTask<String, String, String> {


        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()
            try {

                // int time = Integer.parseInt(params[0])*1000;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation

        }


        @Override
        protected void onPreExecute() {

        }


        @Override
        protected void onProgressUpdate(String... text) {
            //   finalResult.setText(text[0]);

        }
    }

    private void hahaAnimation() {


        Handler mainHandler = new Handler();

        // This is your code
        Runnable myRunnable = () -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            hahaFly.setVisibility(View.VISIBLE);
            hahaFly.startAnimation(anim);
            hahaFly.setVisibility(View.INVISIBLE);


        };

        mainHandler.post(myRunnable);

    }

    private void loveAnimation() {


        Handler mainHandler = new Handler(this.getMainLooper());

        // This is your code
        Runnable myRunnable = () -> {
            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            loveFly.setVisibility(View.VISIBLE);
            loveFly.startAnimation(anim);
            loveFly.setVisibility(View.INVISIBLE);
        };
        mainHandler.post(myRunnable);

    }

    private void likeAnimation() {

        //AsyncTaskRunner
        Handler mainHandler = new Handler(this.getMainLooper());
        // This is your code
        Runnable myRunnable = () -> {

            Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
            likeFly.setVisibility(View.VISIBLE);
            likeFly.startAnimation(anim);
            likeFly.setVisibility(View.INVISIBLE);

        };
        mainHandler.post(myRunnable);

    }

    private void userHistory() {

        View view = getLayoutInflater().inflate(R.layout.history_show, null);
        TextView userName = view.findViewById(R.id.userNameForHistory);
        TextView userLevel = view.findViewById(R.id.userLevelForHistory);

        CircleImageView userImage = view.findViewById(R.id.historyShowProfileImage);
        RecyclerView userHistory = view.findViewById(R.id.userHistoryShowRV);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                LiveVideoPlayerActivity.this, LinearLayoutManager.VERTICAL, false
        );
        userHistory.setLayoutManager(linearLayoutManager);

        BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(LiveVideoPlayerActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.show();

        if (followIDForPlay != null) {
            Picasso.get().load(BASE_URL + allLiveUserInfo.get(liveUserPosition).getImage()).placeholder(R.drawable.user1).into(userImage);
            userName.setText(allLiveUserInfo.get(liveUserPosition).getName());
            userLevel.setText("" + allLiveUserInfo.get(liveUserPosition).getUserLevel());
        } else {
            Picasso.get().load(BASE_URL + joinUserInfo.getImage()).placeholder(R.drawable.user1).into(userImage);
            userName.setText(joinUserInfo.getName());
            userLevel.setText("" + joinUserInfo.getUserLevel());
        }


        //api;
        Call<GiftHistoryResponse> calls = api.getGiftHistory(playId);


        calls.enqueue(new Callback<GiftHistoryResponse>() {
            @Override
            public void onResponse(Call<GiftHistoryResponse> call, Response<GiftHistoryResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<GiftHistoryItem> userDetails = response.body().getData();

                    HistoryViewAdapter userStoryAdapter = new HistoryViewAdapter(LiveVideoPlayerActivity.this, userDetails);
                    userHistory.setAdapter(userStoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<GiftHistoryResponse> call, Throwable t) {

            }
        });


    }

    private void setData() {

        if (followIDForPlay != null) {
            liveUserDiamond.setText(String.valueOf(joinUserInfo.getPresentCoinBalance()));
            //liveUserView.setText(String.valueOf(0));
        } else {
            liveUserDiamond.setText(String.valueOf(joinUserInfo.getPresentCoinBalance()));
            //liveUserView.setText(String.valueOf(0));
        }

    }

    public void setDiamond(int diamond, int coin) {

        /*if (giftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }*/

        if (liveUserDiamond != null && liveUserView != null) {
            liveUserDiamond.setText(String.valueOf(diamond));
        }

        ImageView iv_5, iv_10, iv_20, iv_50;
        iv_5 = findViewById(R.id.gift_5_diamond_animation);
        iv_10 = findViewById(R.id.gift_10_diamond_animation);
        iv_20 = findViewById(R.id.gift_20_diamond_animation);
        iv_50 = findViewById(R.id.gift_50_diamond_animation);


        Log.d("coin_test", "setDiamond: " + coin);
        switch (coin) {
            case 5:
                iv_5.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .repeat(2)
                        .playOn(iv_5);


                Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        iv_5.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1200);
                //

                break;

            case 10:
                iv_10.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.Swing)
                        .duration(1000)
                        .repeat(2)
                        .playOn(iv_10);
                //  iv_10.setVisibility(View.GONE);
                Handler handlers = new Handler();

                final Runnable rs = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        iv_10.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handlers.postDelayed(rs, 1200);
                break;
            case 20:
                iv_20.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.SlideInRight)
                        .duration(1000)
                        .repeat(2)
                        .playOn(iv_20);
                //  iv_10.setVisibility(View.GONE);
                Handler handler20 = new Handler();

                final Runnable r20 = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        iv_20.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler20.postDelayed(r20, 1200);
                break;

            case 50:
                iv_50.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.BounceIn)
                        .duration(1000)
                        .repeat(2)
                        .playOn(iv_10);
                //  iv_10.setVisibility(View.GONE);
                Handler handler50 = new Handler();

                final Runnable r50 = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        iv_50.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler50.postDelayed(r50, 1200);
                break;

        }
    }

    //Boardcast
    private Intent mLiveVideoBroadcasterServiceIntent;
    //    private ILiveVideoBroadcaster mLiveVideoBroadcaster;
    private GLSurfaceView mGLView;
    boolean mIsRecording = false;

    //  private Button mBroadcastControlButton;
    private ViewGroup mRootView;

    public void triggerStopRecording() {

        onBackPressed();
    }

    private ProfileViewModel profileViewModel;

    private void observerAnyChange() {

        viewModelLiveUser.getAllUsers().observe(this, new Observer<UserDetails>() {
            @Override
            public void onChanged(UserDetails allUserResponses) {


                if (allUserResponses != null) {
                    List<ProfileData> userDetails = allUserResponses.getResponse();

                    //allUserInfo.clear();
                    //allUserInfo = userDetails;


//                    for (int i = 0; i < allUserResponses.getResponse().size(); i++) {
//                        Log.d("userid", "own: " + userInfo.getId());
//
//                        if (userInfo.getId().equals(userDetails.get(i).getId())) {
//                            userDetails.remove(i);
//                            global_Position = i;
//                        }
//
//                    }


                    for (int i = 0; i < livePeoples.length; i++) {
                        if (livePeoples[i] != null) {
                            for (int j = 0; j < userDetails.size(); j++) {
                                if (userDetails.get(j).getId().equals(livePeoples[i])) {
                                    Log.i("ysawijaaw", "onChanged: user " + userDetails.get(j).getName() + " coin " + userDetails.get(j).getPresentCoinBalance());
                                    playerModels.get(i).getNameText().setText(userDetails.get(j).getName());
                                    playerModels.get(i).getDiamondText().setText(getResources().getString(R.string.diamond_emoji) + userDetails.get(j).getPresentCoinBalance());
                                }

                            }
                        }
                    }

//                    try {
//                        binding.livePlayView.hostPlayCoin.setText(getResources().getString(R.string.diamond_emoji) + joinUserInfo.getPresentCoinBalance());
//                        binding.livePlayView.hostPlayName.setText(joinUserInfo.getName());
//
//                    } catch (Exception e) {
//                    }
                }

            }
        });


    }

    private void changeObserver() {

        profileViewModel.getProfile().observe(this, myProfile -> {
            try {
                userInfo = myProfile.getProfileData();
                userProfileFollow = myProfile.getFollowers();

                liveUserDiamond.setText(joinUserInfo.getPresentCoinBalance());

                binding.livePlayView.userPlayCoin.setText(getResources().getString(R.string.diamond_emoji) + userInfo.getPresentCoinBalance());
                binding.livePlayView.userPlayName.setText(userInfo.getName());

            } catch (Exception e) {
            }
        });

    }


    /// Host Change any type of data from the user
    private void getChangeObserverHost() {


        profileViewModel.getProfileById(playId).observe(this, new Observer<ProfileData>() {
            @Override
            public void onChanged(ProfileData profileData) {
                try {
                    joinUserInfo = profileData;
                    // userProfileFollow = loginRespons.getFollowers();
                    // totalCoin.setText( userInfo.getPresentCoinBalance() );

//                    binding.livePlayView.hostPlayCoin.setText(getResources().getString(R.string.diamond_emoji) + joinUserInfo.getPresentCoinBalance());
//                    binding.livePlayView.hostPlayName.setText(joinUserInfo.getName());

                    if (hostName == null) {
                        hostName = joinUserInfo.getName();
                    }

                    if (followIDForPlay != null) {

                        // liveUserPosition
                        binding.topBar.liveUserProfileName.setText(joinUserInfo.getName());

                        try {
                            Picasso.get().load(BASE_URL + joinUserInfo.getImage()).placeholder(R.drawable.user1).into(binding.topBar.liveUserProfileImage);
                        } catch (Exception e) {
                        }
                    } else {

                        binding.topBar.liveUserProfileName.setText(joinUserInfo.getName());

                        try {
                            Picasso.get().load(BASE_URL + joinUserInfo.getImage()).placeholder(R.drawable.user1).into(binding.topBar.liveUserProfileImage);
                        } catch (Exception e) {
                        }
                    }

                    setData();

                } catch (Exception e) {
                }
            }
        });

    }


    //WebRTC Listener
    @Override
    public void onDisconnected(String streamId) {

    }

    @Override
    public void onPublishFinished(String streamId) {
        Log.i("ysawijaaw", "onPublishFinished: ");

        if (reJoin) {
            Toast.makeText(this, "Reconnecting", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Publish finished", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onPlayFinished(String streamId) {
        Log.i("ysawijaaw", "onPlayFinished: ");
        //Toast.makeText(this, "Play finished", Toast.LENGTH_LONG).show();
    }


    @Override
    public void onPublishStarted(String streamId) {
        Log.i("ysawijaaw", "onPublishStarted");
        //Toast.makeText(this, "Publish started", Toast.LENGTH_LONG).show();

//        unicKey = myRef.push().getKey();
//
//        myRef.child("liveUser").child(userInfo.getId()).child(unicKey).setValue("online");

        openViews(true);

        reJoin = false;


        if (progressBar.isShown())
            progressBar.hide();
    }

    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private void viewChecker() {

        /*if (livePeoples.length > 0) {
            setMargins(binding.livePlayView.getRoot(), 0, 40, 0, 100);
        } else {
            setMargins(binding.livePlayView.getRoot(), 0, 0, 0, 0);
        }*/

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {

            for (int i = 0; i < livePeoples.length; i++) {
                if (livePeoples[i] != null && conferenceManager.getPeers().get(livePeoples[i]) != null) {
                    Log.i("ysawijaaw", "viewChecker: ok " + livePeoples[i]);
                    playViewRenderers.get(i).setVisibility(View.VISIBLE);
                    playerModels.get(i).getConstraintLayout().setVisibility(View.VISIBLE);
                } else {
                    Log.i("ysawijaaw", "viewChecker: not " + livePeoples[i]);
                    playViewRenderers.get(i).setVisibility(View.INVISIBLE);
                    playerModels.get(i).getConstraintLayout().setVisibility(View.INVISIBLE);
                }
            }

            refreshLayouts();
            viewChecker();

        }, 2000);

    }

    @Override
    public void onPlayStarted(String streamId) {
        Log.i("ysawijaaw", "onPlayStarted");
        //Toast.makeText(this, "Play started", Toast.LENGTH_LONG).show();

//        viewChecker();

        if (showLayouts == 1) {
            openViews(false);
        }

    }

    @Override
    public void noStreamExistsToPlay(String streamId) {
        Log.i("ysawijaaw", "noStreamExistsToPlay: ");
    }

    @Override
    public void onError(String description, String streamId) {
        Log.i("ysawijaaw", "onError: " + description);
    }

    @Override
    public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String streamId) {
        Log.i("ysawijaaw", "onSignalChannelClosed: " + code);
    }

    @Override
    public void streamIdInUse(String streamId) {
        Log.i("ysawijaaw", "streamIdInUse: ");

    }

    @Override
    public void onIceConnected(String streamId) {

        Log.i("ysawijaaw", "onIceConnected: ");


        refreshLayouts();
    }

    @Override
    public void onIceDisconnected(String streamId) {
        Log.i("ysawijaaw", "onIceDisconnected: ");
        refreshLayouts();
    }

    @Override
    public void onTrackList(String[] tracks) {

    }

    @Override
    public void onBitrateMeasurement(String streamId, int targetBitrate, int videoBitrate, int audioBitrate) {

    }

    @Override
    public void onStreamInfoList(String streamId, ArrayList<StreamInfo> streamInfoList) {

    }

    //Data Observer Callback


    @Override
    public void onBufferedAmountChange(long previousAmount, String dataChannelLabel) {

    }

    @Override
    public void onStateChange(DataChannel.State state, String dataChannelLabel) {

    }

    @Override
    public void onMessage(DataChannel.Buffer buffer, String dataChannelLabel) {
        ByteBuffer data = buffer.data;

        String strDataJson = new String(data.array(), StandardCharsets.UTF_8);

        try {
            JSONObject json = new JSONObject(strDataJson);
            String eventType = json.getString("eventType");
            String streamId = json.getString("streamId");
            Toast.makeText(this, eventType + " : " + streamId, Toast.LENGTH_LONG).show();
            Log.i("ysawijaaw", "onMessage: " + strDataJson);
        } catch (Exception e) {
            Log.i("ysawijaaw", "onMessage: " + e.getMessage());
        }
    }

    @Override
    public void onMessageSent(DataChannel.Buffer buffer, boolean successful) {
        ByteBuffer data = buffer.data;
        String strDataJson = new String(data.array(), StandardCharsets.UTF_8);

        Log.i("ysawijaaw", "onMessageSent: " + strDataJson + " status: " + successful);
    }

    private void showGiftAnim() {
        final Handler handler = new Handler();
        ArrayList<GiftHistory> giftHistories = new ArrayList<>();
        binding.recyclerViewGiftAnim.setLayoutManager(new LinearLayoutManager(this));

        Animation anim = AnimationUtils.loadAnimation(this,
                android.R.anim.fade_in);
        anim.setDuration(300);
        binding.recyclerViewGiftAnim.startAnimation(anim);

        myRef.child(playId).child("histories").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                giftHistories.clear();
                if (snapshot.exists()) {
                    binding.recyclerViewGiftAnim.setVisibility(View.VISIBLE);
                    mStopHandler = false;
                    handler.removeCallbacksAndMessages(null);
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        giftHistories.add(dataSnapshot.getValue(GiftHistory.class));
                    }

                    giftHistoryAdapter = new GiftHistoryAdapter(giftHistories);
                    binding.recyclerViewGiftAnim.setAdapter(giftHistoryAdapter);

                    handler.postDelayed(() -> {
                        if (giftHistories.size() == 0) {
                            mStopHandler = true;
                        }
                        if (!mStopHandler) {
                            myRef.child(playId).child("histories").child(giftHistories.get(0).getGiftId()).removeValue();
                        } else {
                            handler.removeCallbacksAndMessages(null);
                        }
                    }, 3000);
                } else {
                    binding.recyclerViewGiftAnim.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LiveVideoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private final ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            if (!snapshot.exists()) {
                binding.tvLiveEnded.setVisibility(View.VISIBLE);
                //Toast.makeText(LiveVideoPlayerActivity.this, "Live Ended", Toast.LENGTH_SHORT).show();
                //leaveToLive();
            } else {
                binding.tvLiveEnded.setVisibility(View.GONE);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(LiveVideoPlayerActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.child(playId).removeEventListener(valueEventListener);
    }
}