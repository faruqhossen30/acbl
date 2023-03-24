package com.shopnolive.shopnolive.liveVideoPlayer;

import static com.shopnolive.shopnolive.api.client.ApiClient.BASE_URL;
import static com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity.api;
import static com.shopnolive.shopnolive.utils.Variable.RTMP_BASE_URL;
import static com.shopnolive.shopnolive.utils.Variable.allUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.joinUserId;
import static com.shopnolive.shopnolive.utils.Variable.joinUserInfo;
import static com.shopnolive.shopnolive.utils.Variable.myRef;
import static com.shopnolive.shopnolive.utils.Variable.playId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserId;
import static com.shopnolive.shopnolive.utils.Variable.selectedUserName;
import static com.shopnolive.shopnolive.utils.Variable.userInfo;
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
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Rational;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;
import com.shopnolive.shopnolive.R;
import com.shopnolive.shopnolive.activity.main.LiveUserViewModel;
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel;
import com.shopnolive.shopnolive.adapter.GiftHistoryAdapter;
import com.shopnolive.shopnolive.adapter.HistoryViewAdapter;
import com.shopnolive.shopnolive.adapter.MessageSendAdapter;
import com.shopnolive.shopnolive.adapter.ViewHeadAdapter;
import com.shopnolive.shopnolive.databinding.ActivityLiveVideoBroadcasterBinding;
import com.shopnolive.shopnolive.fragment.GiftHistoryFragment;
import com.shopnolive.shopnolive.fragment.LiveFragment;
import com.shopnolive.shopnolive.fragment.SendGiftFragment;
import com.shopnolive.shopnolive.fragment.StreamersViewFragment;
import com.shopnolive.shopnolive.fragment.WaitlistFragment;
import com.shopnolive.shopnolive.listener.UserItemClickListener;
import com.shopnolive.shopnolive.model.Comment;
import com.shopnolive.shopnolive.model.DeleteResponse;
import com.shopnolive.shopnolive.model.GiftHistory;
import com.shopnolive.shopnolive.model.LiveRequest;
import com.shopnolive.shopnolive.model.PlayerModel;
import com.shopnolive.shopnolive.model.gift.GiftHistoryItem;
import com.shopnolive.shopnolive.model.gift.GiftHistoryResponse;
import com.shopnolive.shopnolive.model.profile.ProfileData;
import com.squareup.picasso.Picasso;

import org.webrtc.DataChannel;
import org.webrtc.SurfaceViewRenderer;

import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import de.tavendo.autobahn.WebSocket;
import io.antmedia.webrtcandroidframework.ConferenceManager;
import io.antmedia.webrtcandroidframework.IDataChannelObserver;
import io.antmedia.webrtcandroidframework.IWebRTCClient;
import io.antmedia.webrtcandroidframework.IWebRTCListener;
import io.antmedia.webrtcandroidframework.StreamInfo;
import io.antmedia.webrtcandroidframework.WebRTCClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveVideoBroadcasterActivity extends AppCompatActivity implements IWebRTCListener, IDataChannelObserver, UserItemClickListener {


    private ProfileViewModel profileViewModel;
    private LiveUserViewModel viewModelLiveUser;

    private ActivityLiveVideoBroadcasterBinding binding;

    private RecyclerView messageShow;
    private MessageSendAdapter messageSendAdapter;
    private List<String> message;

    private GiftHistoryAdapter giftHistoryAdapter;
    boolean mStopHandler = false;

    private ImageButton likeFly, loveFly, hahaFly, wowFly, sadFly, angryFly;

    //Share
    //private
    private ImageView follow, share_video;
    private TextView countLove;

    private CircleImageView profileImage;
    private TextView userName, totalCoin, totlaViewer;

    private String userId;
    //TODO: End

    private static final String TAG = LiveVideoBroadcasterActivity.class.getSimpleName();

    private ViewGroup mRootView;
    boolean mIsRecording = false;

    private Timer mTimer;
    private long mElapsedTime;
    public TimerHandler mTimerHandler;
    private ImageButton mSettingsButton;
    private TextView joinBTN;


    private boolean shouldAutoPlay;
    private boolean needRetrySource;
    private int resumeWindow;
    private long resumePosition;
    private Handler mainHandler;
    protected String userAgent;

    private BottomSheetBehavior joinBottomSheetBehavior, giftBottomSheetBehavior;
    private StreamersViewFragment streamersViewFragment;

    private WaitlistFragment waitlistFragment;
    private LiveFragment liveFragment;

    private ViewPager viewPagerJoin;
    private TabLayout tabLayoutJoin;
    private String unicKey;

    private ConferenceManager conferenceManager;
    private int camFace = 1;
    private WebRTCClient webRTCClient;
    private final String webRTCMode = IWebRTCClient.MODE_PUBLISH;
    private final String[] livePeoples = new String[5];
    private ArrayList<SurfaceViewRenderer> playViewRenderers;
    private ArrayList<PlayerModel> playerModels;

    private static final CookieManager DEFAULT_COOKIE_MANAGER;

    static {
        DEFAULT_COOKIE_MANAGER = new CookieManager();
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    View joinBottomSheet, giftBottomSheet;
    private ContentLoadingProgressBar progressBar;
    private String hostName = "";

    private boolean reJoin = false;

    private boolean isMuted = true;
    private boolean isVideoOff = true;


    // Get All View User Information Data from Database
    public void getViewUser() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                LiveVideoBroadcasterActivity.this, LinearLayoutManager.HORIZONTAL, false
        );

        binding.topBar.recyclerVewViewers.setLayoutManager(linearLayoutManager);


        myRef.child(userInfo.getId()).child("view").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                List<ProfileData> list = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    //    List<Comment> list = new ArrayList<>();
                    ProfileData value = d.getValue(ProfileData.class);
                    //   Log.d("firebase", "Value is: " + value.getId());
                    list.add(value);

                }

                ViewHeadAdapter viewHeadAdapter = new ViewHeadAdapter(LiveVideoBroadcasterActivity.this, list, LiveVideoBroadcasterActivity.this);
                binding.topBar.recyclerVewViewers.setAdapter(viewHeadAdapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());
            }
        });

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hide title
        //requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);


        waitingInfo = "own";


        //selectedUserId = userInfo.getId();
        playId = userInfo.getId();

        binding = DataBindingUtil.setContentView(this, R.layout.activity_live_video_broadcaster);

        profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        viewModelLiveUser = ViewModelProviders.of(this).get(LiveUserViewModel.class);


        try {
            playInitiation();
            initFunction();
            initListener();
            initJoin();
            viewListSize();
            getReactCount();
            getComment();
            getViewUser();
            giftMethod();
            getChangeObserver();
            initCam();

            mTimerHandler = new TimerHandler();

            mRootView = findViewById(R.id.root_layout);

        } catch (Exception e) {
            Toast.makeText(this, "error : " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        showGiftAnim();

        Call<DeleteResponse> responseCall = api.isGuest(0);
        responseCall.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                //Toast.makeText(LiveVideoBroadcasterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                Toast.makeText(LiveVideoBroadcasterActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void initConference() {

        binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(true);
        binding.playBroadcast.joinCameraPreview.setMirror(true);

        playViewRenderers = new ArrayList<>();
        playerModels = new ArrayList<>();


        playerModels.add(new PlayerModel(binding.playBroadcast.other,
                binding.playBroadcast.userCoin1stUser,
                binding.playBroadcast.userName1stUser));

        playerModels.add(new PlayerModel(binding.playBroadcast.player2layout,
                binding.playBroadcast.player2Coin,
                binding.playBroadcast.player2Name));

        playerModels.add(new PlayerModel(binding.playBroadcast.player3layout,
                binding.playBroadcast.player3Coin,
                binding.playBroadcast.player3Name));

        playerModels.add(new PlayerModel(binding.playBroadcast.player4layout,
                binding.playBroadcast.player4Coin,
                binding.playBroadcast.player4Name));

        playerModels.add(new PlayerModel(binding.playBroadcast.player5layout,
                binding.playBroadcast.player5Coin,
                binding.playBroadcast.player5Name));


        playViewRenderers.add(binding.playBroadcast.playerView);
        playViewRenderers.add(binding.playBroadcast.playerView1);
        playViewRenderers.add(binding.playBroadcast.playerView2);
        playViewRenderers.add(binding.playBroadcast.playerView3);
        playViewRenderers.add(binding.playBroadcast.playerView4);

        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//        this.getIntent().putExtra(EXTRA_VIDEO_FPS, 24);
        this.getIntent().putExtra(EXTRA_VIDEO_BITRATE, 300);
        this.getIntent().putExtra(EXTRA_VIDEO_HEIGHT, 640);
        this.getIntent().putExtra(EXTRA_VIDEO_WIDTH, 360);
        this.getIntent().putExtra(EXTRA_AUDIO_BITRATE, 64);


        String streamId = userInfo.getId();
        String roomId = userInfo.getId();


        conferenceManager = new ConferenceManager(
                this,
                this,
                this.getIntent(),
                RTMP_BASE_URL,
                roomId,
                binding.playBroadcast.cameraPreviewSurfaceViewPlay,
                playViewRenderers,
                streamId,
                this
        );

        conferenceManager.setPlayOnlyMode(false);
        conferenceManager.setOpenFrontCamera(true);
    }

    private void initCam() {
        webRTCClient = new WebRTCClient(new IWebRTCListener() {
            @Override
            public void onDisconnected(String streamId) {

            }

            @Override
            public void onPublishFinished(String streamId) {

            }

            @Override
            public void onPlayFinished(String streamId) {

            }

            @Override
            public void onPublishStarted(String streamId) {

            }

            @Override
            public void onPlayStarted(String streamId) {

            }

            @Override
            public void noStreamExistsToPlay(String streamId) {

            }

            @Override
            public void onError(String description, String streamId) {

            }

            @Override
            public void onSignalChannelClosed(WebSocket.WebSocketConnectionObserver.WebSocketCloseNotification code, String streamId) {

            }

            @Override
            public void streamIdInUse(String streamId) {

            }

            @Override
            public void onIceConnected(String streamId) {

            }

            @Override
            public void onIceDisconnected(String streamId) {

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
        }, LiveVideoBroadcasterActivity.this);


        this.getIntent().putExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, true);

        webRTCClient.setOpenFrontCamera(true);

        webRTCClient.setVideoRenderers(null, binding.playBroadcast.joinCameraPreview);

        webRTCClient.init(RTMP_BASE_URL, userInfo.getId(), webRTCMode, "", this.getIntent());
        initConference();
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


    private void getChangeObserver() {


        profileViewModel.getProfile().observe(this, myProfile -> {
            try {
                userInfo = myProfile.getProfileData();
                userProfileFollow = myProfile.getFollowers();
                totalCoin.setText(userInfo.getPresentCoinBalance());

                binding.playBroadcast.hostBroadcastCoin.setText(getResources().getString(R.string.diamond_emoji) + userInfo.getPresentCoinBalance());
                binding.playBroadcast.hostBroadcastName.setText(userInfo.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }


    private void initJoin() {

        joinBottomSheet = findViewById(R.id.bottom_sheet_join);
        joinBottomSheetBehavior = BottomSheetBehavior.from(joinBottomSheet);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        viewPagerAdapter.addFragments(streamersViewFragment, "VIEW");
        viewPagerAdapter.addFragments(waitlistFragment, "WATCH LIST");
        viewPagerAdapter.addFragments(liveFragment, "LIVE");
        //viewPagerJoin.setAdapter(viewPagerAdapter);

        Button joinB = findViewById(R.id.joinPlay);
        joinB.setVisibility(View.GONE);

        viewPagerJoin = findViewById(R.id.view_pager_join);
        tabLayoutJoin = findViewById(R.id.tab_layout_join);

        viewPagerJoin.setAdapter(viewPagerAdapter);
        tabLayoutJoin.setupWithViewPager(viewPagerJoin);
    }

    private void getReactCount() {

        myRef.child("liveUser").child(userInfo.getId()).child("Emoji").addValueEventListener(new ValueEventListener() {
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

    private void initListener() {


        binding.userHistory.setOnClickListener(view -> userHistory());

        joinBTN.setOnClickListener(v -> {
            // play();
            Toast.makeText(this,"Pressed LiveVideoBroadCast",Toast.LENGTH_LONG).show();

            joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            //        bottomSheetDialog();

        });

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

        binding.bottomBar.btnLike.setOnClickListener(v -> {

            likeAnimation();


            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("like");

            // anim.start();
            //countEmotion();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "like", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);
        });


        binding.bottomBar.btnLove.setOnClickListener(v -> {

            loveAnimation();

            unicKey = myRef.push().getKey();

            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("love");
            //countEmotion();
            // anim.start();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "love", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);

        });

        binding.bottomBar.btnHaha.setOnClickListener(v -> {

            hahaAnimation();
            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("haha");

            //countEmotion();
            // anim.start();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "haha", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);

        });


        binding.bottomBar.btnWow.setOnClickListener(v -> {

            wowAnimation();

            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("wow");
            //countEmotion();
            // anim.start();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "wow", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);

        });

        binding.bottomBar.btnSad.setOnClickListener(v -> {

            sadAnimation();

            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("sad");
            //countEmotion();
            // anim.start();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "sad", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);

        });

        binding.bottomBar.btnAngry.setOnClickListener(v -> {
            angryAnimation();

            unicKey = myRef.push().getKey();
            myRef.child("liveUser").child(userInfo.getId()).child("Emoji").child(unicKey).setValue("angry");
            //countEmotion();
            // anim.start();

            Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), "angry", String.valueOf(userInfo.getUserLevel()), "reaction");
            myRef.child(playId).child("commend").child(unicKey).setValue(comment);

        });


        messageShow = findViewById(R.id.messageShowBroadcaster);
        message = new ArrayList<>();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                LiveVideoBroadcasterActivity.this, LinearLayoutManager.VERTICAL, false
        );
        linearLayoutManager.setStackFromEnd(true);


        messageShow.setLayoutManager(linearLayoutManager);
        messageSendAdapter = new MessageSendAdapter(LiveVideoBroadcasterActivity.this, 1, LiveVideoBroadcasterActivity.this);

        binding.bottomBar.sendButtonComment.setOnClickListener(v -> {

            String messages = binding.bottomBar.editTextComment.getText().toString();

            if (!messages.isEmpty()) {

                binding.bottomBar.editTextComment.setText("");
                message.add(messages);
                unicKey = myRef.push().getKey();

                Comment comment = new Comment(userInfo.getId(), userInfo.getName(), userInfo.getImage(), messages, String.valueOf(userInfo.getUserLevel()), "comment");

                myRef.child(userInfo.getId()).child("commend").child(unicKey).setValue(comment);
                getComment();
            } else {
                Toast.makeText(this, "Enter message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void create_andShare() {
        Log.e("main", "create link ");

        DynamicLink dynamicLink = FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse("https://www.aponseba.xyz/live.com/"))
                //.setDynamicLinkDomain("bdliveapp.page.link")
                .setDomainUriPrefix("bdliveapp.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildDynamicLink();

        Uri dynamicLinkUri = dynamicLink.getUri();

        Log.e("main", "  Long refer " + dynamicLink.getUri());


    }

    public void createShareBaseUser(String userid) {
        // manuall link
        String sharelinktext = "https://bdliveapp.page.link/?" +
                "link=http://www.aponseba.xyz/live.com/myshare.php?userid=" + userid + "-" +
                "&apn=" + getPackageName() +
                "&st=" + "BD Live" +
                "&sd=" + userInfo.getName() + " Live Room" +
                // "&si="+userInfo.getImage().toString()/*"https://domain/logo-1.png"*/;
                "&si=" + "https://dyl80ryjxr1ke.cloudfront.net/external_assets/hero_examples/hair_beach_v1785392215/original.jpeg";

        Log.e("mainactivity", "sharelink - " + sharelinktext);
        // shorten the link
        Task<ShortDynamicLink> shortLinkTask = FirebaseDynamicLinks.getInstance().createDynamicLink()
                //.setLongLink(dynamicLink.getUri())    // enable it if using firebase method dynamicLink
                .setLongLink(Uri.parse(sharelinktext))  // manually
                .buildShortDynamicLink()
                .addOnCompleteListener(this, new OnCompleteListener<ShortDynamicLink>() {
                    @Override
                    public void onComplete(@NonNull Task<ShortDynamicLink> task) {
                        if (task.isSuccessful()) {
                            // Short link created
                            Uri shortLink = task.getResult().getShortLink();
                            Log.d("main ", "short link " + shortLink.toString());
                            // share app dialog
                            Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_SEND);
                            intent.putExtra(Intent.EXTRA_TEXT, shortLink.toString());
                            intent.setType("text/plain");
                            startActivity(intent);


                        } else {
                            // Error
                            // ...
                            Log.d("main", " error " + task.getException());

                        }
                    }
                });

    }


    private void angryAnimation() {

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        angryFly.setVisibility(View.VISIBLE);
        angryFly.startAnimation(anim);
        angryFly.setVisibility(View.INVISIBLE);
    }

    private void sadAnimation() {

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        sadFly.setVisibility(View.VISIBLE);
        sadFly.startAnimation(anim);
        sadFly.setVisibility(View.INVISIBLE);

    }

    private void wowAnimation() {

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        wowFly.setVisibility(View.VISIBLE);
        wowFly.startAnimation(anim);
        wowFly.setVisibility(View.INVISIBLE);

    }

    private void hahaAnimation() {

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        hahaFly.setVisibility(View.VISIBLE);
        hahaFly.startAnimation(anim);
        hahaFly.setVisibility(View.INVISIBLE);
    }

    private void loveAnimation() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        loveFly.setVisibility(View.VISIBLE);
        loveFly.startAnimation(anim);
        loveFly.setVisibility(View.INVISIBLE);
    }

    private void likeAnimation() {
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.button_flay);
        likeFly.setVisibility(View.VISIBLE);
        likeFly.startAnimation(anim);
        likeFly.setVisibility(View.INVISIBLE);
    }

    private void initFunction() {

        streamersViewFragment = new StreamersViewFragment();
        waitlistFragment = new WaitlistFragment();
        liveFragment = new LiveFragment();


        Intent intent = getIntent();
        int image = intent.getExtras().getInt("story");
        String openStoryName = intent.getExtras().getString("name");
        userId = intent.getExtras().getString("id");
        joinBTN = findViewById(R.id.joinLive);

        userName = findViewById(R.id.liveUserProfileName);
        profileImage = findViewById(R.id.liveUserProfileImage);

        totalCoin = findViewById(R.id.userDiamondLive);
        totlaViewer = findViewById(R.id.userViewLive);
        binding.topBar.liveUserProfileName.setText(userInfo.getName());

        Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1)
                .into(binding.topBar.liveUserProfileImage);

        //  if(userInfo.)
        totalCoin.setText(String.valueOf(userInfo.getPresentCoinBalance()));


        try {

            if (userProfileFollow != null)
                totlaViewer.setText(String.valueOf(userProfileFollow.size()));
        } catch (Exception e) {
        }

        countLove = findViewById(R.id.countLoveLive);
        //follow = findViewById(R.id.followIV);
        share_video = findViewById(R.id.shareVideoLive);

        // Like Fly menu
        likeFly = findViewById(R.id.imgButtonOneFlyLive);
        loveFly = findViewById(R.id.imgButtonTwoFlyLive);
        hahaFly = findViewById(R.id.imgButtonThreeFlyLive);
        wowFly = findViewById(R.id.imgButtonFourFlyLive);
        sadFly = findViewById(R.id.imgButtonFiveFlyLive);
        angryFly = findViewById(R.id.imgButtonSixFlyLive);


        binding.topBar.btnEndBroadcast.setOnClickListener(v -> {
            toggleBroadcasting();
        });

        binding.btnMuteCall.setOnClickListener(v -> {
            isMuted = !isMuted;
            if (isMuted) {
                if (conferenceManager.isJoined()) {
                    conferenceManager.getPeers().get(userInfo.getId()).enableAudio();
                } else {
                    webRTCClient.enableAudio();
                }
                binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, R.color.white));
            } else {
                if (conferenceManager.isJoined()) {
                    conferenceManager.getPeers().get(userInfo.getId()).disableAudio();
                } else {
                    webRTCClient.disableAudio();
                }
                binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, R.color.red));
            }
        });

        binding.btnVideoOff.setOnClickListener(v -> {
            isVideoOff = !isVideoOff;
            if (isVideoOff) {
                if (conferenceManager.isJoined()) {
                    conferenceManager.getPeers().get(userInfo.getId()).enableVideo();
                } else {
                    webRTCClient.enableVideo();
                }
                binding.btnVideoOff.setColorFilter(ContextCompat.getColor(this, R.color.white));
            } else {
                if (conferenceManager.isJoined()) {
                    conferenceManager.getPeers().get(userInfo.getId()).disableVideo();
                } else {
                    webRTCClient.disableVideo();
                }
                binding.btnVideoOff.setColorFilter(ContextCompat.getColor(this, R.color.red));
            }
        });

        binding.btnStartLive.setOnClickListener(v -> {
            if (!conferenceManager.isJoined()) {
                reJoin = true;
                webRTCClient.onCallHangUp();
                //mBroadcastControlButton.setBackgroundTintList(getResources().getColorStateList(R.color.leave));
                startLive();
            }
            progressBar = new ContentLoadingProgressBar(LiveVideoBroadcasterActivity.this);
            progressBar.show();

            binding.btnStartLive.setVisibility(View.GONE);
            binding.tvLiveHint.setVisibility(View.GONE);

            new CountDownTimer(3000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    //Toast.makeText(LiveVideoBroadcasterActivity.this, String.valueOf(millisUntilFinished / 1000), Toast.LENGTH_SHORT).show();
                    binding.tvLiveTimer.setText(String.valueOf((millisUntilFinished / 1000) + 1));
                }

                @Override
                public void onFinish() {
                    binding.layoutStartLive.setVisibility(View.GONE);
                    binding.messageShowBroadcaster.setVisibility(View.VISIBLE);
                }
            }.start();
        });
    }

    public void changeCamera(View v) {

        if (camFace == 0) {
            camFace++;
            conferenceManager.setOpenFrontCamera(true);
            binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(true);
            binding.playBroadcast.joinCameraPreview.setMirror(true);
        } else {
            camFace = 0;
            conferenceManager.setOpenFrontCamera(false);
            binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(false);
            binding.playBroadcast.joinCameraPreview.setMirror(false);
        }

        if (conferenceManager.isJoined()) {
            conferenceManager.getPeers().get(userInfo.getId()).switchCamera();
        } else {
            webRTCClient.switchCamera();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        // Play live
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //   play();
        } else {
            showToast(R.string.storage_permission_denied);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        deleteLive();
    }


    public void toggleBroadcasting() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle("Leave");
        builder1.setMessage("Are you sure to leave this live?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                (dialog, id) -> {
                    dialog.cancel();
                    deleteLive();
                    mIsRecording = false;
                    finish();
                });

        builder1.setNegativeButton(
                "No",
                (dialog, id) -> dialog.cancel());

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private void startLive() {
        try {
            binding.playBroadcast.cameraPreviewSurfaceViewPlay.release();
            binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(true);

            conferenceManager = new ConferenceManager(
                    this,
                    this,
                    this.getIntent(),
                    RTMP_BASE_URL,
                    userInfo.getId(),
                    binding.playBroadcast.cameraPreviewSurfaceViewPlay,
                    playViewRenderers,
                    userInfo.getId(),
                    this
            );

            conferenceManager.setPlayOnlyMode(false);

            if (camFace == 1) {
                conferenceManager.setOpenFrontCamera(true);
                binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(true);
            } else {
                conferenceManager.setOpenFrontCamera(false);
                binding.playBroadcast.cameraPreviewSurfaceViewPlay.setMirror(false);
            }

            conferenceManager.joinTheConference();
        } catch (Exception e) {
            startLive();
        }
    }


    public void triggerStopRecording() {
        onBackPressed();
    }

    //This method starts a mTimer and updates the textview to show elapsed time for recording
    public void startTimer() {

        if (mTimer == null) {
            mTimer = new Timer();
        }

        mElapsedTime = 0;
        mTimer.scheduleAtFixedRate(new TimerTask() {

            public void run() {
                mElapsedTime += 1; //increase every sec
                mTimerHandler.obtainMessage(TimerHandler.INCREASE_TIMER).sendToTarget();

            }
        }, 0, 1000);
    }


    public void stopTimer() {
        if (mTimer != null) {
            this.mTimer.cancel();
        }
        this.mTimer = null;
        this.mElapsedTime = 0;
    }


    public void setLivePlay(List<LiveRequest> liveUser) {
        if (liveUser != null) {

            Log.i("ysawijaaw", "setLivePlay: ");

            try {

                List<String> list = Arrays.asList(livePeoples);


                for (LiveRequest user : liveUser) {

                    if (!list.contains(user.getId())) {
                        int i = 0;
                        while (i < livePeoples.length) {
                            if (livePeoples[i] == null) {
                                livePeoples[i] = user.getId();
                                break;
                            }
                            i++;
                        }
                    }
                }


                if (livePeoples[0] != null) {
                    joinUserId = livePeoples[0];
                }

                for (int i = 0; i < livePeoples.length; i++) {
                    boolean isAvailable = false;

                    for (LiveRequest id : liveUser) {
                        if (id.getId().equals(livePeoples[i])) {
                            isAvailable = true;
                            break;
                        }
                    }

                    if (!isAvailable) {
                        livePeoples[i] = null;
                    }
                }


//                binding.playBroadcast.playerView.setVisibility(View.VISIBLE);
//                binding.playBroadcast.liveActive2User.setVisibility(View.VISIBLE);
//                binding.playBroadcast.liveActiveLastUser.setVisibility(View.VISIBLE);


                //Todo : This System to improve the assign Coin
                //binding.playBroadcast.userCoin1stUser.setText(getResources().getString(R.string.diamond_emoji) + liveRequest.getCoin());
                //binding.playBroadcast.userName1stUser.setText(liveRequest.getName());
                getChangeObserver();
                getChangeObserverJoinUser();
//                binding.playBroadcast.hostBroadcastCoin.setVisibility(View.VISIBLE);
//                binding.playBroadcast.hostBroadcastName.setVisibility(View.VISIBLE);


                play(joinUserId);

            } catch (Exception e) {
            }
        } else {
//            binding.playBroadcast.hostBroadcastCoin.setVisibility(View.GONE);
//            binding.playBroadcast.hostBroadcastName.setVisibility(View.GONE);
//            binding.playBroadcast.playerView.setVisibility(View.GONE);
//            binding.playBroadcast.liveActive2User.setVisibility(View.GONE);
//            binding.playBroadcast.liveActiveLastUser.setVisibility(View.GONE);

            binding.recyclerViewGiftAnim.setVisibility(View.VISIBLE);

            Arrays.fill(livePeoples, null);

//            for (SurfaceViewRenderer sr : playViewRenderers) {
//                sr.setVisibility(View.INVISIBLE);
//            }
//
//            for (PlayerModel pm : playerModels) {
//                pm.getConstraintLayout().setVisibility(View.INVISIBLE);
//            }
//
//
//            binding.playBroadcast.other.setVisibility(View.GONE);

        }

//        viewChecker();

        refreshLayouts();
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
            setMargins(binding.playBroadcast.getRoot(), 0, 56, 0, 100);
        } else {
            setMargins(binding.playBroadcast.getRoot(), 0, 0, 0, 0);
        }*/

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {

            boolean isSinglePlay = true;

            for (int i = 0; i < livePeoples.length; i++) {
                if (livePeoples[i] != null && conferenceManager.getPeers().get(livePeoples[i]) != null) {
                    Log.i("ysawijaaw", "viewChecker: ok " + livePeoples[i]);
                    isSinglePlay = false;
                    playViewRenderers.get(i).setVisibility(View.VISIBLE);
                    playerModels.get(i).getConstraintLayout().setVisibility(View.VISIBLE);
                } else {
                    Log.i("ysawijaaw", "viewChecker: not " + livePeoples[i]);
                    playViewRenderers.get(i).setVisibility(View.INVISIBLE);
                    playerModels.get(i).getConstraintLayout().setVisibility(View.INVISIBLE);
                }
            }

            if (isSinglePlay) {
                //setMargins(binding.playBroadcast.getRoot(), 0, 0, 0, 0);
                binding.playBroadcast.other.setVisibility(View.GONE);
                binding.playBroadcast.liveActive2User.setVisibility(View.GONE);
                binding.playBroadcast.liveActiveLastUser.setVisibility(View.GONE);
                binding.playBroadcast.hostBroadcastCoin.setVisibility(View.GONE);
                binding.playBroadcast.hostBroadcastName.setVisibility(View.GONE);
            } else {
                binding.playBroadcast.liveActive2User.setVisibility(View.VISIBLE);
                binding.playBroadcast.liveActiveLastUser.setVisibility(View.VISIBLE);
                binding.playBroadcast.hostBroadcastCoin.setVisibility(View.VISIBLE);
                binding.playBroadcast.hostBroadcastName.setVisibility(View.VISIBLE);
                //setMargins(binding.playBroadcast.getRoot(), 0, 56, 0, 100);
            }

            refreshLayouts();

            viewChecker();

        }, 2000);
    }

    private void refreshLayouts() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(() -> {
            binding.rootLayout.setVisibility(View.GONE);
            binding.rootLayout.setVisibility(View.VISIBLE);
        }, 1000);
    }


    private void getChangeObserverJoinUser() {


        profileViewModel.getProfileById(joinUserId).observe(this, new Observer<ProfileData>() {
            @Override
            public void onChanged(ProfileData profileData) {
                try {
                    joinUserInfo = profileData;

                } catch (Exception e) {
                }
            }
        });

        viewModelLiveUser.getAllUsers().observe(this, allUserResponses -> {


            if (allUserResponses != null) {
                List<ProfileData> userDetails = allUserResponses.getResponse();

                allUserInfo = userDetails;

                for (int i = 0; i < livePeoples.length; i++) {
                    if (livePeoples[i] != null) {
                        for (int j = 0; j < userDetails.size(); j++) {
                            if (userDetails.get(j).getId().equals(livePeoples[i])) {
                                playerModels.get(i).getNameText().setText(userDetails.get(j).getName());
                                playerModels.get(i).getDiamondText().setText(getResources().getString(R.string.diamond_emoji) + userDetails.get(j).getPresentCoinBalance());
                            }

                        }
                    }
                }
            }
        });


    }

    public void totalCalls(int size) {
        if (size == 0) {
            binding.badge.setVisibility(View.GONE);
        } else {
            binding.badge.setNumber(size);
            binding.badge.setVisibility(View.VISIBLE);
        }
    }

    public void hideGift() {
        if (giftBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    @Override
    public void onUserItemClicked(@NonNull String userId) {

    }


    private class TimerHandler extends Handler {
        static final int CONNECTION_LOST = 2;
        static final int INCREASE_TIMER = 1;

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case INCREASE_TIMER:
                    //    mStreamLiveStatus.setText(getString(R.string.live_indicator) + " - " + getDurationString((int) mElapsedTime));
                    break;
                case CONNECTION_LOST:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        triggerStopRecording();
                    }
                    new AlertDialog.Builder(LiveVideoBroadcasterActivity.this)
                            .setMessage(R.string.broadcast_connection_lost)
                            .setPositiveButton(android.R.string.yes, null)
                            .show();

                    break;
            }
        }
    }

    public static String getDurationString(int seconds) {

        if (seconds < 0 || seconds > 2000000)//there is an codec problem and duration is not set correctly,so display meaningfull string
            seconds = 0;
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        if (hours == 0)
            return twoDigitString(minutes) + " : " + twoDigitString(seconds);
        else
            return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    public static String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }
        return String.valueOf(number);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

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


    private void countEmotion() {
        int count = 1 + Integer.parseInt(countLove.getText().toString());
        countLove.setText(String.valueOf(count));
    }


    public void getComment() {

        // Read from the database
        myRef.child(userInfo.getId()).child("commend").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                getChangeObserver();
                getChangeObserverJoinUser();


                List<Comment> list = new ArrayList<>();
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Comment value = d.getValue(Comment.class);
                    Log.d("firebase", "Value is: " + value.getComment());
                    list.add(value);
                }

                messageSendAdapter = new MessageSendAdapter(LiveVideoBroadcasterActivity.this, 1, LiveVideoBroadcasterActivity.this);
                messageShow.smoothScrollToPosition(list.size());
                messageShow.setAdapter(messageSendAdapter);

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
        } else if (conferenceManager.isJoined()) {
            minimizeApp();
        } else {
            super.onBackPressed();
        }

    }

    public void minimizeApp() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private void deleteLive() {
        if (conferenceManager.isJoined()) {
            conferenceManager.leaveFromConference();
        }
        myRef.child("liveUser").child(userInfo.getId()).removeValue();
        myRef.child(userInfo.getId()).removeValue();
        myRef.child(playId).removeValue();

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
    }

    private void deleteComment() {
        // myRef.child(userInfo.getId()).child("commend").removeValue();
        myRef.child(userInfo.getId()).removeValue();

    }


    private void viewListSize() {

        myRef.child(userInfo.getId()).child("view").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long count = dataSnapshot.getChildrenCount();

                /*if(count*/

                totlaViewer.setText(String.valueOf(count));
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException());

            }
        });

        //myRef.child(allUserInfo.get(userPosition).getId()).child("view").addChildEventListener(new ValueEventListener())
    }

    private void userHistory() {

        View view = getLayoutInflater().inflate(R.layout.history_show, null);
        TextView userName = view.findViewById(R.id.userNameForHistory);
        TextView userLevel = view.findViewById(R.id.userLevelForHistory);

        CircleImageView userImage = view.findViewById(R.id.historyShowProfileImage);
        RecyclerView userHistory = view.findViewById(R.id.userHistoryShowRV);

        userHistory.setLayoutManager(new LinearLayoutManager(this));

        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(LiveVideoBroadcasterActivity.this, R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);

        //  mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        //mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        //mBottomSheetDialog.dismiss();
        Picasso.get().load(BASE_URL + userInfo.getImage()).placeholder(R.drawable.user1)
                .into(userImage);

        userName.setText(userInfo.getName());
        userLevel.setText(String.valueOf(userInfo.getUserLevel()));

        //api;
        Call<GiftHistoryResponse> calls = api.getGiftHistory(playId);


        calls.enqueue(new Callback<GiftHistoryResponse>() {
            @Override
            public void onResponse(Call<GiftHistoryResponse> call, Response<GiftHistoryResponse> response) {
                if (response.body() != null && response.body().isSuccess()) {
                    List<GiftHistoryItem> userDetails = response.body().getData();

                    HistoryViewAdapter userStoryAdapter = new HistoryViewAdapter(LiveVideoBroadcasterActivity.this, userDetails);
                    userHistory.setAdapter(userStoryAdapter);
                }
            }

            @Override
            public void onFailure(Call<GiftHistoryResponse> call, Throwable t) {

            }
        });

    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        shouldAutoPlay = true;
        setIntent(intent);
    }


    private void playInitiation() {

        playInitialProduct();

        shouldAutoPlay = true;
        mainHandler = new Handler();


        binding.playBroadcast.cameraPreviewSurfaceViewPlay.setOnClickListener(view -> Toast.makeText(LiveVideoBroadcasterActivity.this, "This is your !! Please try to another person !! ", Toast.LENGTH_SHORT).show());

        binding.playBroadcast.other.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[0];
            hostName = playerModels.get(0).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        binding.playBroadcast.player2layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[1];
            hostName = playerModels.get(1).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.playBroadcast.player3layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[2];
            hostName = playerModels.get(2).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.playBroadcast.player4layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[3];
            hostName = playerModels.get(3).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        binding.playBroadcast.player5layout.setOnClickListener(v -> {
            if (v.getVisibility() != View.VISIBLE)
                return;
            selectedUserId = livePeoples[4];
            hostName = playerModels.get(4).getNameText().getText().toString();
            giftMethod();
            giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });


        if (CookieHandler.getDefault() != DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER);
        }

        sendGift = new SendGiftFragment();
        profile = new GiftHistoryFragment();

        binding.playBroadcast.playerView.requestFocus();
    }

    private void playInitialProduct() {
//        binding.playBroadcast.playerView.setVisibility(View.GONE);
        binding.playBroadcast.liveActive2User.setVisibility(View.GONE);

        binding.playBroadcast.hostBroadcastCoin.setVisibility(View.GONE);
        binding.playBroadcast.hostBroadcastName.setVisibility(View.GONE);
    }


    private void showToast(int messageId) {
        showToast(getString(messageId));
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    public void play(String id) {
        String URL = RTMP_BASE_URL + id /*userId*/;
    }


    private void giftMethod() {

        selectedUserName = hostName;


        // tabLayoutGift = findViewById(R.id.tab_layout_gift);

        viewPagerGift = findViewById(R.id.view_pager_gift);
        tabLayoutGift = findViewById(R.id.tab_layout_gift);


        giftBottomSheet = findViewById(R.id.bottom_sheet_gift);
        giftBottomSheetBehavior = BottomSheetBehavior.from(giftBottomSheet);


        joinBottomSheet = findViewById(R.id.bottom_sheet_join);
        joinBottomSheetBehavior = BottomSheetBehavior.from(joinBottomSheet);


        tabLayoutGift.setupWithViewPager(viewPagerGift);
        //  tabLayoutJoin.setupWithViewPager(viewPagerJoin);


        ViewPagerAdapter2 viewPagerAdapter2 = new ViewPagerAdapter2(getSupportFragmentManager(), 0);

        viewPagerAdapter2.addFragmentsGift(sendGift, "Send Gifts");
        viewPagerAdapter2.addFragmentsGift(profile, hostName);
        viewPagerGift.setAdapter(viewPagerAdapter2);

        tabLayoutGift.getTabAt(0).setIcon(R.drawable.gift);
        tabLayoutGift.getTabAt(1).setIcon(R.drawable.user1);

        tabLayoutGift.setTabIconTint(null);
    }


    private ViewPager viewPagerGift;
    private TabLayout tabLayoutGift;
    private SendGiftFragment sendGift;
    private GiftHistoryFragment profile;


    public void setDiamond(int diamond, int coin) {
     /*   if (binding.playBroadcast.userCoin1stUser != null && binding.playBroadcast.userName1stUser != null) {
            binding.playBroadcast.userCoin1stUser.setText(String.valueOf(diamond));
        }*/
        getComment();

        // ImageView iv_5, iv_10, iv_20, iv_50;
      /*  iv_5 = findViewById(R.id.gift_5_diamond_animation);
        iv_10 = findViewById(R.id.gift_10_diamond_animation);
        iv_20 = findViewById(R.id.gift_20_diamond_animation);
        iv_50 = findViewById(R.id.gift_50_diamond_animation);
*/

        Log.d("coin_test", "setDiamond: " + coin);
        switch (coin) {
            case 5:
                binding.gift5DiamondAnimation.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.SlideInUp)
                        .duration(1000)
                        .repeat(2)
                        .playOn(binding.gift5DiamondAnimation);


                Handler handler = new Handler();

                final Runnable r = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        binding.gift5DiamondAnimation.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler.postDelayed(r, 1200);
                //

                break;

            case 10:
                binding.gift10DiamondAnimation.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.Swing)
                        .duration(1000)
                        .repeat(2)
                        .playOn(binding.gift10DiamondAnimation);
                //  iv_10.setVisibility(View.GONE);
                Handler handlers = new Handler();

                final Runnable rs = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        binding.gift10DiamondAnimation.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handlers.postDelayed(rs, 1200);
                break;
            case 20:
                binding.gift20DiamondAnimation.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.SlideInRight)
                        .duration(1000)
                        .repeat(2)
                        .playOn(binding.gift20DiamondAnimation);
                //  iv_10.setVisibility(View.GONE);
                Handler handler20 = new Handler();

                final Runnable r20 = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        binding.gift20DiamondAnimation.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler20.postDelayed(r20, 1200);
                break;

            case 50:
                binding.gift50DiamondAnimation.setVisibility(View.VISIBLE);

                YoYo.with(Techniques.BounceIn)
                        .duration(1000)
                        .repeat(2)
                        .playOn(binding.gift50DiamondAnimation);
                //  iv_10.setVisibility(View.GONE);
                Handler handler50 = new Handler();

                final Runnable r50 = new Runnable() {
                    public void run() {
                        //   tv.append("Hello World");

                        binding.gift50DiamondAnimation.setVisibility(View.GONE);
                        //handler.postDelayed(this, 1000);
                    }
                };

                handler50.postDelayed(r50, 1200);
                break;

        }
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

//        binding.playBroadcast.mainPlay.setVisibility(View.GONE);

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

        reJoin = false;

        unicKey = myRef.push().getKey();

        myRef.child("liveUser").child(userInfo.getId()).child(unicKey).setValue("online");

        viewChecker();


        if (progressBar.isShown())
            progressBar.hide();
    }

    @Override
    public void onPlayStarted(String streamId) {
        Log.i("ysawijaaw", "onPlayStarted");
        //Toast.makeText(this, "Play started", Toast.LENGTH_LONG).show();

        binding.recyclerViewGiftAnim.setVisibility(View.GONE);

//        int i = 0;
//
//        while (i < livePeoples.length) {
//            if (livePeoples[i] != null) {
//                Log.i("ysawijaaw", "openViews: main");
//                playViewRenderers.get(i).setVisibility(View.VISIBLE);
//                playerModels.get(i).getConstraintLayout().setVisibility(View.VISIBLE);
//            }
//            i++;
//        }

//        viewChecker();

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
        binding.playBroadcast.joinCameraPreview.setVisibility(View.GONE);
    }

    @Override
    public void onIceDisconnected(String streamId) {
        Log.i("ysawijaaw", "onIceDisconnected: ");
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
//            JSONObject json = new JSONObject(strDataJson);
//            String eventType = json.getString("eventType");
//            String streamId = json.getString("streamId");
//            Toast.makeText(this, eventType + " : " + streamId, Toast.LENGTH_LONG).show();
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
                Toast.makeText(LiveVideoBroadcasterActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}

