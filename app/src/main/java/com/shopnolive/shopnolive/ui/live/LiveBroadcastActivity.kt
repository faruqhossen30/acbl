package com.shopnolive.shopnolive.ui.live

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.SurfaceView
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel
import com.shopnolive.shopnolive.adapter.*
import com.shopnolive.shopnolive.api.client.ApiClient
import com.shopnolive.shopnolive.api.client.RetrofitClientFactory.Companion.BASE_URL
import com.shopnolive.shopnolive.databinding.ActivityLiveBroadcastBinding
import com.shopnolive.shopnolive.fragment.*
import com.shopnolive.shopnolive.listener.BroadcasterItemClickListener
import com.shopnolive.shopnolive.listener.UserItemClickListener
import com.shopnolive.shopnolive.model.Comment
import com.shopnolive.shopnolive.model.GiftHistory
import com.shopnolive.shopnolive.model.gift.GiftHistoryItem
import com.shopnolive.shopnolive.model.notification.NotificationData
import com.shopnolive.shopnolive.model.notification.PushNotification
import com.shopnolive.shopnolive.model.profile.ProfileData
import com.shopnolive.shopnolive.ui.activities.BannedActivity
import com.shopnolive.shopnolive.ui.live.base.RtcBaseActivity
import com.shopnolive.shopnolive.ui.chat.MessageFragment
import com.shopnolive.shopnolive.utils.*
import com.shopnolive.shopnolive.utils.Constants.AgoraConst.DEFAULT_BEAUTY_OPTIONS
import com.shopnolive.shopnolive.utils.Constants.CAMERA_AND_AUDIO_PERMISSIONS
import com.shopnolive.shopnolive.utils.Constants.NOTIFICATION_KEY_LIVE
import com.shopnolive.shopnolive.utils.Tools.hasPermissions
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.agora.rtc.EngineConfig
import com.shopnolive.shopnolive.utils.agora.stats.LocalStatsData
import com.shopnolive.shopnolive.utils.agora.stats.RemoteStatsData
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER
import io.agora.rtc.IRtcEngineEventHandler.*
import io.agora.rtc.video.VideoEncoderConfiguration.VideoDimensions
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit


class LiveBroadcastActivity : RtcBaseActivity(), BroadcasterItemClickListener,
    UserItemClickListener {

    private lateinit var binding: ActivityLiveBroadcastBinding
    private lateinit var databaseReference: DatabaseReference
    private lateinit var commentReference: DatabaseReference
    private lateinit var liveReference: DatabaseReference

    private lateinit var commentList: ArrayList<Comment>

    private lateinit var handler: Handler
    private var shouldAutoPlay = false
    private var isMuted = true
    private var isCameraOff = true
    private var isBackCamera = false
    private var mStopHandler = false
    private var isScrolling = false
    private var isBroadcasting = true
    private lateinit var mHandler: Handler

    private val streamersViewFragment = StreamersViewFragment()
    private val waitListFragment = WaitlistFragment()
    private val liveFragment = LiveFragment()

    private val sendGiftFragment = SendGiftFragment()
    private val giftHistoryFragment = GiftHistoryFragment()

    private lateinit var joinBottomSheetBehavior: BottomSheetBehavior<*>
    private lateinit var giftBottomSheetBehavior: BottomSheetBehavior<*>

    private lateinit var commentAdapter: MessageSendAdapter
    private lateinit var mLayoutManager: LinearLayoutManager

    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var giftHistoryAdapter: GiftHistoryAdapter

    private var mVideoDimension: VideoDimensions? = null

    private var channelName = "xyz"
    private var liveDuration = 0
    private var durationCounter = 0

    private lateinit var surfaceLocal: SurfaceView

    private lateinit var DEFAULT_COOKIE_MANAGER: CookieManager

    private var TOPIC = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLiveBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Display always on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        waitingInfo = "own"
        playId = userInfo.getId().toString()
        channelName = playId
        TOPIC = "/topics/$playId"

        selectedUserName = ""

        //init
        databaseReference = FirebaseDatabase.getInstance().getReference("Live")
        commentReference = databaseReference.child(playId).child("comment")
        liveReference = FirebaseDatabase.getInstance().getReference("LiveUsers")

        profileViewModel = ViewModelProviders.of(this)[ProfileViewModel::class.java]

        commentList = ArrayList()

        handler = Handler(Looper.getMainLooper())

        DEFAULT_COOKIE_MANAGER = CookieManager()
        DEFAULT_COOKIE_MANAGER.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER)


        if (CookieHandler.getDefault() !== DEFAULT_COOKIE_MANAGER) {
            CookieHandler.setDefault(DEFAULT_COOKIE_MANAGER)
        }

        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = false
        mLayoutManager.stackFromEnd = true


        commentAdapter =
            MessageSendAdapter(this@LiveBroadcastActivity, 1, this)

        binding.recyclerVewComments.apply {
            layoutManager = mLayoutManager
            adapter = commentAdapter
        }

        //for agora
        initAgora()
        initData()

        setMyInfo()
        sendComment()
        getViewUser()
        getAllComments()
        getReactionCount()
        handleReactionClick()
        giftMethod()
        observeMyInfo()

        initJoin()
        showGiftAnim()

        if (!hasPermissions(this, CAMERA_AND_AUDIO_PERMISSIONS)) {
            finish()
        }

        liveReference.addValueEventListener(onlineValueEventListener)

        binding.recyclerVewComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastVisibleItemPosition: Int = mLayoutManager.findLastVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                isScrolling = totalItemCount != lastVisibleItemPosition + 1
            }
        })

        binding.layoutGiftHistory.setOnClickListener {
            showGiftHistory(playId)
        }

        binding.topBar.btnEndBroadcast.setOnClickListener {
            MaterialAlertDialogBuilder(this@LiveBroadcastActivity)
                .setTitle("Leave from Live!")
                .setMessage("Are you sure to leave from this live ?")
                .setCancelable(false)
                .setPositiveButton(
                    com.shopnolive.shopnolive.R.string.yes
                ) { _, _ ->
                    prepareLive()
                    Handler(Looper.getMainLooper()).postDelayed({
                        //Do something after 100ms
                        leaveFromLive()
                    }, 600)

                }
                .setNegativeButton(
                    com.shopnolive.shopnolive.R.string.no
                ) { _, _ -> }
                .create().show()
        }

        //start live#####################################################
        binding.btnStartLive.setOnClickListener {
            changeVisibilityForAfterStartLive()
            startBroadcast()

            binding.btnStartLive.visibility = View.GONE
            binding.tvLiveHint.visibility = View.GONE

            object : CountDownTimer(3000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    //Toast.makeText(LiveVideoBroadcasterActivity.this, String.valueOf(millisUntilFinished / 1000), Toast.LENGTH_SHORT).show();
                    binding.tvLiveTimer.text = (millisUntilFinished / 1000 + 1).toString()
                }

                override fun onFinish() {
                    binding.layoutStartLive.visibility = View.GONE
                    binding.recyclerVewComments.visibility = View.VISIBLE
                    startLiveCounter()
                    sendNotificationToFollowers()
                }
            }.start()
        }



//        binding.joinLive.setOnClickListener {
//            Toast.makeText(this,"Pressed LiveBroadCast",Toast.LENGTH_LONG).show()
//
//            joinBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }


//        binding.giftLive.setOnClickListener {
//            giftBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//        }

        //live mute button click

        binding.btnMuteCall.setOnClickListener {
            isMuted = !isMuted
            if (isMuted) {
                rtcEngine().muteLocalAudioStream(false)
                binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, com.shopnolive.shopnolive.R.color.white))
                binding.btnMuteCall.setImageResource(com.shopnolive.shopnolive.R.drawable.ic_mic_call_white_16)
            } else {
                rtcEngine().muteLocalAudioStream(true)
                binding.btnMuteCall.setColorFilter(ContextCompat.getColor(this, com.shopnolive.shopnolive.R.color.red))
                binding.btnMuteCall.setImageResource(com.shopnolive.shopnolive.R.drawable.ic_mic_call_red_16)

            }
        }

        binding.changeCameraButton.setOnClickListener {
            isBackCamera = !isBackCamera
            rtcEngine().switchCamera()
            if (isBackCamera)
                EngineConfig().mirrorLocalIndex = 1
            else
                EngineConfig().mirrorEncodeIndex = 0
        }

//        binding.btnShare.setOnClickListener {
////            Toast.makeText(this,"LiveBroadcast Working",Toast.LENGTH_LONG).show()
//
//            createFirebaseDynamicLink()
//        }
    }

    private fun changeVisibilityForAfterStartLive() {


        binding.floatingMenu.visibility = View.VISIBLE
//        binding.joinLive.visibility = View.VISIBLE
        binding.linearLayout6.visibility = View.VISIBLE
        binding.bottomBar.bottomBarLayout.visibility = View.VISIBLE


    }


    private fun testClick(){
        Toast.makeText(this,"Working",Toast.LENGTH_LONG).show()
    }
    private fun sendNotificationToFollowers() {
        val data = NotificationData(
            senderId = myId,
            senderName = userInfo.name,
            senderImage = userInfo.image,
            title = "New Live: ${userInfo.name}",
            description = "${userInfo.name} is now in live",
            type = NOTIFICATION_KEY_LIVE,
            extra = playId,
            icon = com.shopnolive.shopnolive.R.drawable.app_icon
        )

        PushNotification(data, TOPIC).also {
            NotificationSender.sendPushNotification(it)
        }
    }

    private fun startLiveCounter() {
        binding.topBar.tvTimer.visibility = View.VISIBLE
        mHandler = Handler(Looper.getMainLooper())
        Thread {
            while (isBroadcasting) {
                try {
                    Thread.sleep(1000)
                    mHandler.post {
                        liveDuration++
                        durationCounter++
                        binding.topBar.tvTimer.text = getFormattedStopWatchTime(liveDuration)
                    }
                } catch (ignored: java.lang.Exception) {
                }
                if (durationCounter == 40) {
                    durationCounter = 0
                    startBroadcast()
                }
            }
        }.start()
    }

    private fun getFormattedStopWatchTime(times: Int): String {
        var timeInString: String? = ""
        var timeInSec = times
        val hours = TimeUnit.SECONDS.toHours(timeInSec.toLong()).toInt()
        timeInSec -= TimeUnit.HOURS.toSeconds(hours.toLong()).toInt()
        val minutes = TimeUnit.SECONDS.toMinutes(timeInSec.toLong()).toInt()
        timeInSec -= TimeUnit.MINUTES.toSeconds(minutes.toLong()).toInt()
        val seconds = TimeUnit.SECONDS.toSeconds(timeInSec.toLong()).toInt()
        if (hours in 1..9) {
            timeInString += "$hours:"
        }
        timeInString += if (minutes < 10) {
            "0$minutes:"
        } else {
            "$minutes:"
        }
        if (seconds < 10) {
            timeInString += "0$seconds"
        } else {
            timeInString += seconds
        }
        return timeInString.toString()
    }

    private fun createFirebaseDynamicLink() {
        val dynamicLink = Firebase.dynamicLinks.dynamicLink {
            link = Uri.parse("http://shwapnolive.famousliveapp.com/live_share.php?userId=$playId")
            domainUriPrefix = "https://shopnoliveapp.page.link"
            // Open links with this app on Android
            androidParameters { }
            // Open links with com.example.ios on iOS
            //iosParameters("com.example.ios") { }
        }

        val dynamicLinkUri = dynamicLink.uri

        Log.d("Dynamic link", "dynamicLink : $dynamicLinkUri")

        Firebase.dynamicLinks.shortLinkAsync {
            longLink = Uri.parse(dynamicLinkUri.toString())
        }.addOnSuccessListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(com.shopnolive.shopnolive.R.string.app_name))
            intent.putExtra(Intent.EXTRA_TEXT, it.shortLink.toString())
            startActivity(Intent.createChooser(intent, "Choose one"))
        }.addOnFailureListener {
            toast(it.localizedMessage!!)
        }

    }

    private fun leaveFromLive() {
        isBroadcasting = false
        myRef.child("liveUser").child(userInfo.getId()).removeValue()
        liveReference.child(FirebaseAuth.getInstance().uid!!).removeValue()
        myRef.child(userInfo.getId()).removeValue()
        myRef.child(playId).removeValue()

        /*val deleteResponseCall = OTPPhoneNumberActivity.api.deleteMyHistory()
        deleteResponseCall.enqueue(object : Callback<DeleteResponse?> {
            override fun onResponse(
                call: Call<DeleteResponse?>,
                response: Response<DeleteResponse?>
            ) {
                //Toast.makeText(LiveVideoBroadcasterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            override fun onFailure(call: Call<DeleteResponse?>, t: Throwable) {}
        })*/

        rtcEngine().leaveChannel()
        finish()
    }

    private fun initAgora() {
        rtcEngine().setBeautyEffectOptions(
            true,
            DEFAULT_BEAUTY_OPTIONS
        )

        binding.playBroadcast.liveVideoGridLayout.setStatsManager(statsManager())
        binding.playBroadcast.liveVideoGridLayout.setListener(this)

        rtcEngine().setClientRole(CLIENT_ROLE_BROADCASTER)

        prepareLive()
    }

    private fun startBroadcast() {
        //toast("Live starting")
        liveReference.child(FirebaseAuth.getInstance().uid!!).setValue(
            userInfo
        ).addOnFailureListener {
            toast(it.localizedMessage!!)
        }
        /*this.joinChannel(channelName, Integer.parseInt(userInfo.getId()))
        rtcEngine().setClientRole(CLIENT_ROLE_BROADCASTER)
        surfaceLocal = prepareRtcVideo(0, true)
        binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(
            0,
            surfaceLocal,
            false,
            userInfo.name
        )*/

        //show views
//        binding.btnMuteCall.visibility = View.VISIBLE
//        binding.changeCameraButton.visibility = View.VISIBLE
//        binding.btnShare.visibility = View.VISIBLE
        //binding.btnVideoOff.visibility = View.VISIBLE
    }

    private fun prepareLive() {
        this.joinChannel(channelName, Integer.parseInt(userInfo.getId()))
        rtcEngine().setClientRole(CLIENT_ROLE_BROADCASTER)
        surfaceLocal = prepareRtcVideo(0, true)
        binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(
            0,
            surfaceLocal,
            false,
            userInfo.name
        )

        //show views
//        binding.changeCameraButton.visibility = View.VISIBLE
    }

//    private fun stopBroadcast() {
//        rtcEngine().setClientRole(Constants.CLIENT_ROLE_AUDIENCE)
//        removeRtcVideo(0, true)
//        binding.playBroadcast.liveVideoGridLayout.removeUserVideo(this, 0, 1, true)
//        //mMuteAudioBtn.setActivated(false)
//    }

    private fun initData() {
        mVideoDimension = VIDEO_DIMENSIONS[config().videoDimenIndex]
    }

    private fun initJoin() {
        joinBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomNavigationJoin.bottomSheetJoin)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(waitListFragment, "WAITING")
        viewPagerAdapter.addFragment(streamersViewFragment, "VIEW")
        viewPagerAdapter.addFragment(liveFragment, "LIVE")

        binding.bottomNavigationJoin.joinPlay.visibility = View.GONE


        binding.bottomNavigationJoin.viewPagerJoin.adapter = viewPagerAdapter
        binding.bottomNavigationJoin.tabLayoutJoin.setupWithViewPager(binding.bottomNavigationJoin.viewPagerJoin)
    }

    private fun getViewUser() {
        val linearLayoutManager = LinearLayoutManager(
            this@LiveBroadcastActivity, LinearLayoutManager.HORIZONTAL, false
        )
        binding.topBar.recyclerVewViewers.layoutManager = linearLayoutManager
        myRef.child(playId).child("view").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                val list: MutableList<ProfileData?> = java.util.ArrayList()
                list.clear()
                for (d in dataSnapshot.children) {
                    val value = d.getValue(
                        ProfileData::class.java
                    )
                    //   Log.d("firebase", "Value is: " + value.getId());
                    list.add(value)
                }
                //  viewUserList = list;
                //live viewer list...........................................................................................
                binding.userViewLive.text = list.size.toString()
                val viewHeadAdapter =
                    ViewHeadAdapter(this@LiveBroadcastActivity, list, this@LiveBroadcastActivity)
                binding.topBar.recyclerVewViewers.adapter = viewHeadAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun showGiftHistory(uid: String) {
        val view = layoutInflater.inflate(com.shopnolive.shopnolive.R.layout.history_show, null)
        val userName = view.findViewById<View>(com.shopnolive.shopnolive.R.id.userNameForHistory) as TextView
        val userLevel = view.findViewById<View>(com.shopnolive.shopnolive.R.id.userLevelForHistory) as TextView

        val userImage: CircleImageView = view.findViewById(com.shopnolive.shopnolive.R.id.historyShowProfileImage)
        val userHistory: RecyclerView = view.findViewById(com.shopnolive.shopnolive.R.id.userHistoryShowRV)

        userHistory.layoutManager = LinearLayoutManager(this)

        val mBottomSheetDialog =
            BottomSheetDialog(this, com.shopnolive.shopnolive.R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.show()

        if (uid == playId) {
            Picasso.get().load(ApiClient.BASE_URL + userInfo.getImage())
                .placeholder(com.shopnolive.shopnolive.R.drawable.user1)
                .into(userImage)

            userName.text = userInfo.getName()
            userLevel.text = userInfo.getUserLevel().toString()
        } else {
            callApi(getRestApis().userPersonalData(uid),
                onApiSuccess = {
                    val data = it.data

                    userName.text = data.getName()
                    userLevel.text = data.getUserLevel().toString()
                    if (data.image != null)
                        userImage.loadImageFromUrl(data.getImage())

                }, onApiError = {

                }, onNetworkError = {

                })
        }

        callApi(getRestApis().getGiftHistory(uid),
            onApiSuccess = { historyList ->
                val userHistoryList: ArrayList<GiftHistoryItem> = historyList.data as ArrayList
                userHistoryList.sortBy { it.coin }
                userHistoryList.reversed()
                val userHistoryAdapter =
                    HistoryViewAdapter(this@LiveBroadcastActivity, userHistoryList)
                userHistory.adapter = userHistoryAdapter
            },
            onApiError = {

            }, onNetworkError = {

            })
    }

    private fun getReactionCount() {
        val query = commentReference.orderByChild("type").equalTo("reaction")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val reactionList = ArrayList<Comment>()
                if (snapshot.exists()) {
                    binding.tvReactionCount.text = snapshot.childrenCount.toString()
                    for (data in snapshot.children) {
                        val comment = data.getValue(Comment::class.java)
                        reactionList.add(comment!!)
                    }

                    val comment = reactionList[reactionList.size - 1]
                    when (comment.comment) {
                        "like" -> {
                            animateReactionItem(binding.flyLike)
                        }
                        "love" -> {
                            animateReactionItem(binding.flyLove)
                        }
                        "haha" -> {
                            animateReactionItem(binding.flyHaha)
                        }
                        "angry" -> {
                            animateReactionItem(binding.flyAngry)
                        }
                        "sad" -> {
                            animateReactionItem(binding.flySad)
                        }
                        "wow" -> {
                            animateReactionItem(binding.flyWow)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }
        })
    }

    private fun handleReactionClick() {




        binding.bottomBar.btnShare.setOnClickListener{
            createFirebaseDynamicLink()

        }

        binding.bottomBar.btnCall.setOnClickListener{
            joinBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }

        binding.bottomBar.btnGift.setOnClickListener{
            giftBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }

        binding.bottomBar.btnLike.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "like",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flyLike)
        }

        binding.bottomBar.btnLove.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "love",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flyLove)
        }

        binding.bottomBar.btnHaha.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "haha",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flyHaha)
        }
        binding.bottomBar.btnWow.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "wow",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flyWow)
        }
        binding.bottomBar.btnSad.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "sad",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flySad)
        }

        binding.bottomBar.btnAngry.setOnClickListener {
            val commentId = commentReference.push().key.toString()

            val comment = Comment(
                userInfo.getId(),
                userInfo.getName(),
                userInfo.getImage().toString(),
                "angry",
                userInfo.getUserLevel().toString(),
                "reaction"
            )

            commentReference.child(commentId).setValue(comment)
                .addOnFailureListener {
                    toast(it.localizedMessage!!)
                }

            animateReactionItem(binding.flyAngry)
        }


    }

    private fun getAllComments() {

        commentReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()

                if (snapshot.exists()) {
                    for (dataSnapshot in snapshot.children) {
                        val comment = dataSnapshot.getValue(Comment::class.java)
                        commentList.add(comment!!)
                    }

                    commentAdapter.addAllComments(commentList)

                    if (!isScrolling)
                        binding.recyclerVewComments.smoothScrollToPosition(commentList.size - 1)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                toast(error.message)
            }
        })
    }

    private fun sendComment() {
        binding.bottomBar.editTextComment.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty())
                    binding.bottomBar.sendButtonComment.visibility = View.GONE
                else
                    binding.bottomBar.sendButtonComment.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        binding.bottomBar.sendButtonComment.setOnClickListener {
            val commentText = binding.bottomBar.editTextComment.text.toString().trim()
            // to check comment box is empty or not
            if (commentText.isEmpty()) {
                binding.bottomBar.editTextComment.error = "Enter your comment"
                binding.bottomBar.editTextComment.requestFocus()
            } else {
                //to clear comment box
                binding.bottomBar.editTextComment.setText("")

                val commentId = commentReference.push().key.toString()
                val comment = Comment(
                    userInfo.getId(),
                    userInfo.getName(),
                    userInfo.getImage().toString(),
                    commentText,
                    userInfo.getUserLevel().toString(),
                    "comment"
                )
                commentReference.child(commentId).setValue(comment)
                    .addOnFailureListener {
                        toast(it.localizedMessage!!)
                    }
            }
        }
    }

    private fun setMyInfo() {
        binding.topBar.liveUserProfileName.text = userInfo.getName()
        binding.userDiamondLive.text = userInfo.getPresentCoinBalance().toString()
        if (userInfo.getImage() != null) {
            binding.topBar.liveUserProfileImage.loadImageFromUrl(BASE_URL + userInfo.getImage())
        }
    }

    private fun animateReactionItem(view: View) {
        // This is your code
        val myRunnable = Runnable {
            val anim = AnimationUtils.loadAnimation(
                applicationContext,
                com.shopnolive.shopnolive.R.anim.button_flay
            )
            view.visibility = View.VISIBLE
            view.startAnimation(anim)
            view.visibility = View.INVISIBLE
        }
        handler.post(myRunnable)
    }

    private fun giftMethod() {
        giftBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomNavigationGift.bottomSheetGift)


        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(sendGiftFragment, "Send Gifts")
        viewPagerAdapter.addFragment(giftHistoryFragment, selectedUserName)

        binding.bottomNavigationGift.viewPagerGift.adapter = viewPagerAdapter
        binding.bottomNavigationGift.tabLayoutGift.setupWithViewPager(binding.bottomNavigationGift.viewPagerGift)

        binding.bottomNavigationGift.tabLayoutGift.getTabAt(0)?.setIcon(com.shopnolive.shopnolive.R.drawable.gift)
        binding.bottomNavigationGift.tabLayoutGift.getTabAt(1)?.setIcon(com.shopnolive.shopnolive.R.drawable.user1)

        binding.bottomNavigationGift.tabLayoutGift.tabIconTint = null
    }

    private fun minimizeApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        shouldAutoPlay = true
        setIntent(intent)
    }

    private fun showGiftAnim() {
        val handler = Handler(Looper.getMainLooper())
        val giftHistories = ArrayList<GiftHistory>()
        binding.recyclerViewGiftAnim.layoutManager = LinearLayoutManager(this)
        val anim = AnimationUtils.loadAnimation(
            this,
            android.R.anim.fade_in
        )
        anim.duration = 300
        binding.recyclerViewGiftAnim.startAnimation(anim)
        myRef.child(playId).child("histories")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    giftHistories.clear()
                    observeMyInfo()
                    if (snapshot.exists()) {
                        binding.recyclerViewGiftAnim.visibility = View.VISIBLE
                        mStopHandler = false
                        handler.removeCallbacksAndMessages(null)
                        for (dataSnapshot in snapshot.children) {
                            giftHistories.add(dataSnapshot.getValue(GiftHistory::class.java)!!)
                        }
                        giftHistoryAdapter = GiftHistoryAdapter(giftHistories)
                        binding.recyclerViewGiftAnim.adapter = giftHistoryAdapter
                        handler.postDelayed({
                            if (giftHistories.size == 0) {
                                mStopHandler = true
                            }
                            if (!mStopHandler) {
                                myRef.child(playId)
                                    .child("histories").child(
                                        giftHistories[0].giftId
                                    ).removeValue()
                            } else {
                                handler.removeCallbacksAndMessages(null)
                            }
                        }, 3000)
                    } else {
                        binding.recyclerViewGiftAnim.visibility = View.GONE
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast(error.message)
                }
            })
    }

    override fun onDestroy() {
        liveReference.removeEventListener(onlineValueEventListener)
        leaveFromLive()
        super.onDestroy()
    }

    private val onlineValueEventListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            liveReference.child(FirebaseAuth.getInstance().uid!!).onDisconnect().removeValue()
        }

        override fun onCancelled(error: DatabaseError) {
            toast(error.message)
        }
    }

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        // Do nothing at the moment
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        // Do nothing at the moment
        //toast("$uid")
    }

    override fun onUserOffline(uid: Int, reason: Int) {
        runOnUiThread { removeRemoteUser(uid, reason) }
    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        runOnUiThread { renderRemoteUser(uid) }
    }

    private fun renderRemoteUser(uid: Int) {
        val surface = prepareRtcVideo(uid, false)
        binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(uid, surface, false, "$uid")
        //toast("$uid")
    }

    private fun removeRemoteUser(uid: Int, reason: Int) {
        removeRtcVideo(uid, false)
        binding.playBroadcast.liveVideoGridLayout.removeUserVideo(this, reason, uid, false)
    }

    override fun onLocalVideoStats(stats: LocalVideoStats) {
        if (!statsManager().isEnabled) return
        val data: LocalStatsData = statsManager().getStatsData(0) as LocalStatsData
        data.width = mVideoDimension!!.width
        data.height = mVideoDimension!!.height
        data.framerate = stats.sentFrameRate
    }

    override fun onRtcStats(stats: RtcStats) {
        if (!statsManager().isEnabled) return
        val data: LocalStatsData = statsManager().getStatsData(0) as LocalStatsData
        data.lastMileDelay = stats.lastmileDelay
        data.videoSendBitrate = stats.txVideoKBitRate
        data.videoRecvBitrate = stats.rxVideoKBitRate
        data.audioSendBitrate = stats.txAudioKBitRate
        data.audioRecvBitrate = stats.rxAudioKBitRate
        data.cpuApp = stats.cpuAppUsage
        data.cpuTotal = stats.cpuAppUsage
        data.sendLoss = stats.txPacketLossRate
        data.recvLoss = stats.rxPacketLossRate
    }

    override fun onNetworkQuality(uid: Int, txQuality: Int, rxQuality: Int) {
        if (!statsManager().isEnabled) return
        val data = statsManager().getStatsData(uid) ?: return
        data.sendQuality = statsManager().qualityToString(txQuality)
        data.recvQuality = statsManager().qualityToString(rxQuality)
    }

    override fun onRemoteVideoStats(stats: RemoteVideoStats) {
        if (!statsManager().isEnabled) return
        val data: RemoteStatsData = statsManager().getStatsData(stats.uid) as RemoteStatsData
        data.width = stats.width
        data.height = stats.height
        data.framerate = stats.rendererOutputFrameRate
        data.videoDelay = stats.delay
    }

    override fun onRemoteAudioStats(stats: RemoteAudioStats) {
        if (!statsManager().isEnabled) return
        val data: RemoteStatsData = statsManager().getStatsData(stats.uid) as RemoteStatsData
        data.audioNetDelay = stats.networkTransportDelay
        data.audioNetJitter = stats.jitterBufferDelay
        data.audioLoss = stats.audioLossRate
        data.audioQuality = statsManager().qualityToString(stats.quality)
    }

    override fun finish() {
        super.finish()
        statsManager().clearAllData()
    }

    override fun onClick(userId: Int, name: String) {
        if (userId.toString() == userInfo.id.toString() || userId == 1 || userId == 0) {
            toast("This is your own layout 1")
        } else {
            selectedUserId = userId.toString()
            selectedUserName = name
            giftMethod()
            giftBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onInfoClicked(userId: Int, name: String) {
        showGiftHistory(userId.toString())
    }

    override fun onBackPressed() {
        when {
            giftBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                giftBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
            joinBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED -> {
                joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN)
            }
            else -> {
                //minimizeApp()
                MaterialAlertDialogBuilder(this@LiveBroadcastActivity)
                    .setTitle("Leave from Live!")
                    .setMessage("Are you sure to leave from this live ?")
                    .setCancelable(false)
                    .setPositiveButton(
                        com.shopnolive.shopnolive.R.string.yes
                    ) { _, _ ->
                        leaveFromLive()
                    }
                    .setNegativeButton(
                        com.shopnolive.shopnolive.R.string.no
                    ) { _, _ -> }
                    .create().show()
            }
        }
    }

    private fun observeMyInfo() {
        profileViewModel.profile.observe(this) { myProfile ->
            if (userInfo != null) {
                userInfo = myProfile.profileData

                if (userInfo.getImage().isNullOrEmpty()) {
                    userInfo.setImage("")
                }

                if (userInfo.status != "active") {
                    val intent = Intent(this@LiveBroadcastActivity, BannedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    this@LiveBroadcastActivity.finish()
                }

                setMyInfo()
            }
        }
    }

    fun totalCalls(size: Int) {
        if (size == 0) {
            binding.bottomBar.badge.visibility = View.GONE
        } else {

            try{
                binding.bottomBar.badge.setText(size.toString()+"")

            }
            catch (e: Exception){
                Log.d("ERROR",e.toString())
            }





            binding.bottomBar.badge.visibility = View.VISIBLE
        }
    }

    override fun onUserItemClicked(userId: String) {
        MessageFragment(userInfo.getId(), userId, true).apply {
            show(supportFragmentManager, "MessageFragment")
        }
    }
}