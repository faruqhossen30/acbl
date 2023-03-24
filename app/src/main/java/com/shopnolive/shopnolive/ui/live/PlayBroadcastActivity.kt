package com.shopnolive.shopnolive.ui.live

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shopnolive.shopnolive.activity.profile.ProfileViewModel
import com.shopnolive.shopnolive.adapter.*
import com.shopnolive.shopnolive.api.client.ApiClient
import com.shopnolive.shopnolive.databinding.ActivityPlayBroadcastBinding
import com.shopnolive.shopnolive.fragment.*
import com.shopnolive.shopnolive.listener.BroadcasterItemClickListener
import com.shopnolive.shopnolive.listener.UserItemClickListener
import com.shopnolive.shopnolive.model.Comment
import com.shopnolive.shopnolive.model.DeleteResponse
import com.shopnolive.shopnolive.model.GiftHistory
import com.shopnolive.shopnolive.model.LiveRequest
import com.shopnolive.shopnolive.model.profile.ProfileData
import com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity
import com.shopnolive.shopnolive.ui.activities.BannedActivity
import com.shopnolive.shopnolive.ui.chat.MessageFragment
import com.shopnolive.shopnolive.ui.live.base.RtcBaseActivity
import com.shopnolive.shopnolive.utils.*
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.agora.rtc.EngineConfig
import com.shopnolive.shopnolive.utils.agora.stats.LocalStatsData
import com.shopnolive.shopnolive.utils.agora.stats.RemoteStatsData
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLink
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.ktx.Firebase
import de.hdodenhof.circleimageview.CircleImageView
import io.agora.rtc.Constants.CLIENT_ROLE_AUDIENCE
import io.agora.rtc.Constants.CLIENT_ROLE_BROADCASTER
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.video.VideoEncoderConfiguration
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PlayBroadcastActivity : RtcBaseActivity(), BroadcasterItemClickListener,
    UserItemClickListener {

    private lateinit var binding: ActivityPlayBroadcastBinding

    private lateinit var databaseReference: DatabaseReference
    private lateinit var commentReference: DatabaseReference

    private lateinit var commentList: ArrayList<Comment>

    private lateinit var handler: Handler

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

    private var mVideoDimension: VideoEncoderConfiguration.VideoDimensions? = null

    private var channelName = "xyz"
    private var hostName = ""
    private var followIDForPlay = ""


    private var joinRequest = true
    private var mStopHandler = false
    private var isMuted = true
    private var isCameraOff = true
    private var isBackCamera = false
    private var isScrolling = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        binding = ActivityPlayBroadcastBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Display always on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val sharedPreference = getSharedPreferences("login", MODE_PRIVATE)
        myId = sharedPreference.getString("myId", "") ?: ""
        myName = sharedPreference.getString("myName", "") ?: ""
        myImage = sharedPreference.getString("myImage", "") ?: ""

        //init
        followIDForPlay = intent.getStringExtra("id") ?: ""
        playId = intent.getStringExtra("id") ?: ""
        channelName = playId
        selectedUserId = playId
        initAll()

        binding.bottomBar.bottomBarLayout.visibility = View.VISIBLE

        binding.recyclerVewComments.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                val lastVisibleItemPosition: Int = mLayoutManager.findLastVisibleItemPosition()
                val totalItemCount = recyclerView.layoutManager!!.itemCount
                isScrolling = totalItemCount != lastVisibleItemPosition + 1
            }
        })

        binding.joinLive.setOnClickListener {
            joinBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED)
        }



        binding.layoutGiftHistory.setOnClickListener {
            showGiftHistory(playId)
        }

        binding.topBar.btnEndBroadcast.setOnClickListener {
            MaterialAlertDialogBuilder(this@PlayBroadcastActivity)
                .setTitle("Leave from Live!")
                .setMessage("Are you sure to leave from this live ?")
                .setCancelable(false)
                .setPositiveButton(
                    com.shopnolive.shopnolive.R.string.yes
                ) { _, _ ->
                    leaveToLive()
                }
                .setNegativeButton(
                    com.shopnolive.shopnolive.R.string.no
                ) { _, _ -> }
                .create().show()
        }
        //video off.....................................................................................


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

//        binding.btnShare.setOnClickListener {
////            Toast.makeText(this,"LiveBroadcast Working",Toast.LENGTH_LONG).show()
//
//            createFirebaseDynamicLink()
//        }

        binding.changeCameraButton.setOnClickListener {
            isBackCamera = !isBackCamera
            rtcEngine().switchCamera()
            if (isBackCamera)
                EngineConfig().mirrorLocalIndex = 1
            else
                EngineConfig().mirrorEncodeIndex = 0
        }



        binding.topBar.liveUserProfileName.setOnClickListener {
            MessageFragment(myId, playId, false).apply {
                show(supportFragmentManager, "MessageFragment")
            }
        }
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

    fun initAll() {
        //waiting = intent.getStringExtra("waiting")
        waitingInfo = "other"

        hostName = intent.getStringExtra("name") ?: ""
        selectedUserName = hostName

        databaseReference = FirebaseDatabase.getInstance().getReference("Live")
        commentReference = databaseReference.child(playId).child("comment")

        commentList = ArrayList()

        handler = Handler(Looper.getMainLooper())

        profileViewModel = ViewModelProviders.of(this)[ProfileViewModel::class.java]

        mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.reverseLayout = false
        mLayoutManager.stackFromEnd = true


        commentAdapter =
            MessageSendAdapter(this@PlayBroadcastActivity, 0, this)

        binding.recyclerVewComments.apply {
            layoutManager = mLayoutManager
            adapter = commentAdapter
        }
        //binding.btnVideoOff.visibility = View.VISIBLE

        //for agora
        initAgora()
        initData()
        playBroadCast()

        initJoin()
        giftMethod()
        initLiveView()
        sendComment()
        getAllComments()
        getReactionCount()
        handleReactionClick()
        showGiftAnim()
        observeHostInfo()
    }

    private fun initAgora() {
        rtcEngine().setBeautyEffectOptions(
            true,
            Constants.AgoraConst.DEFAULT_BEAUTY_OPTIONS
        )

        binding.playBroadcast.liveVideoGridLayout.setStatsManager(statsManager())
        binding.playBroadcast.liveVideoGridLayout.setListener(this)

        rtcEngine().setClientRole(CLIENT_ROLE_AUDIENCE)
    }

    private fun initData() {
        mVideoDimension = VIDEO_DIMENSIONS[config().videoDimenIndex]
    }

    private fun initLiveView() {

        //binding.userDiamondLive.text = userInfo.getPresentCoinBalance().toString()

        myRef.child(playId).child("view").child(userInfo.getId()).setValue(userInfo)
        val viewKey = myRef.push().key!!
        val comment: Comment = setCommenterLive("is coming")
        myRef.child(playId).child("comment").child(viewKey).setValue(comment)
        checkMyStatus()
        getViewUser()
    }

    private fun showGiftHistory(userId: String) {
        val view = layoutInflater.inflate(com.shopnolive.shopnolive.R.layout.history_show, null)
        val userName = view.findViewById<TextView>(com.shopnolive.shopnolive.R.id.userNameForHistory)
        val userLevel = view.findViewById<TextView>(com.shopnolive.shopnolive.R.id.userLevelForHistory)
        val userImage: CircleImageView = view.findViewById(com.shopnolive.shopnolive.R.id.historyShowProfileImage)
        val userHistory: RecyclerView = view.findViewById(com.shopnolive.shopnolive.R.id.userHistoryShowRV)
        val linearLayoutManager = LinearLayoutManager(
            this@PlayBroadcastActivity, LinearLayoutManager.VERTICAL, false
        )
        userHistory.layoutManager = linearLayoutManager
        val mBottomSheetDialog =
            BottomSheetDialog(this@PlayBroadcastActivity, com.shopnolive.shopnolive.R.style.MaterialDialogSheet)
        mBottomSheetDialog.setContentView(view)
        mBottomSheetDialog.setCancelable(true)
        mBottomSheetDialog.show()

        if (userId == playId) {
            userImage.loadImageFromUrl(ApiClient.BASE_URL + hostInfo.getImage())
            userName.text = hostInfo.getName()
            userLevel.text = hostInfo.getUserLevel().toString()
        } else {
            callApi(
                getRestApis().userPersonalData(userId),
                onApiSuccess = {
                    val data = it.data

                    userName.text = data.getName()
                    userLevel.text = data.getUserLevel().toString()
                    if (data.image != null)
                        userImage.loadImageFromUrl(data.getImage())

                })
        }


        callApi(getRestApis().getGiftHistory(userId),
            onApiSuccess = { historyList ->
                val userHistoryList = historyList.data
                userHistoryList.sortBy { it.coin }
                userHistoryList.reversed()
                val userHistoryAdapter =
                    HistoryViewAdapter(this@PlayBroadcastActivity, userHistoryList)
                userHistory.adapter = userHistoryAdapter
            })
    }

    private fun getViewUser() {
        val linearLayoutManager = LinearLayoutManager(
            this@PlayBroadcastActivity, LinearLayoutManager.HORIZONTAL, false
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
                binding.userViewLive.text = list.size.toString()
                val viewHeadAdapter =
                    ViewHeadAdapter(this@PlayBroadcastActivity, list, this@PlayBroadcastActivity)
                binding.topBar.recyclerVewViewers.adapter = viewHeadAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.w("firebase", "Failed to read value.", error.toException())
            }
        })
    }

    private fun checkMyStatus() {
        myRef.child(playId).child("view").child(userInfo.getId())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val myInfo = snapshot.getValue(
                            ProfileData::class.java
                        )!!
                        if (myInfo.getStatus() == "blocked") {
                            Toast.makeText(
                                this@PlayBroadcastActivity,
                                "You removed by host",
                                Toast.LENGTH_SHORT
                            ).show()
                            leaveToLive()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@PlayBroadcastActivity, "", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun playBroadCast() {
        //toast("Live starting")
        this.joinChannel(channelName, Integer.parseInt(userInfo.getId()))
        rtcEngine().setClientRole(CLIENT_ROLE_AUDIENCE)
        //val surface = prepareRtcVideo(1, true)
        //binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(1, surface, true)
        //mMuteAudioBtn.setActivated(true)
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

    private fun initJoin() {
        joinBottomSheetBehavior =
            BottomSheetBehavior.from(binding.bottomNavigationJoin.bottomSheetJoin)

        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)

        viewPagerAdapter.addFragment(streamersViewFragment, "VIEW")
        viewPagerAdapter.addFragment(waitListFragment, "WAITING")
        //viewPagerAdapter.addFragment(liveFragment, "LIVE")

        binding.bottomNavigationJoin.joinPlay.setOnClickListener {
            if (joinRequest) {
                val liveRequest = LiveRequest(
                    userInfo.getId(),
                    userInfo.getId(),
                    userInfo.getName(),
                    userInfo.getImage(),
                    "waiting",
                    userInfo.getPresentCoinBalance()
                )
                //Comment comment = new Comment(userInfo.getId(), userInfo.getName(), (String) userInfo.getImage(), messages);
                databaseReference.child(playId).child("join").child(userInfo.getId())
                    .setValue(liveRequest)
                val key = databaseReference.push().key
                databaseReference.child(playId).child("comment").child(key!!)
                    .setValue(setCommenterLive("Request for Join the Group!"))
                //getComment()
                binding.bottomNavigationJoin.joinPlay.text = getString(com.shopnolive.shopnolive.R.string.cancel)
                joinRequest = false
            } else {
                binding.bottomNavigationJoin.joinPlay.text = getString(com.shopnolive.shopnolive.R.string.join)
                val key = databaseReference.push().key
                databaseReference.child(playId).child("comment").child(key!!)
                    .setValue(setCommenterLive("cancel the joining request!"))
                //getComment()
                removeLiveRequest()
                joinRequest = true

                kickOut()
            }
        }

        binding.bottomNavigationJoin.viewPagerJoin.adapter = viewPagerAdapter
        binding.bottomNavigationJoin.tabLayoutJoin.setupWithViewPager(binding.bottomNavigationJoin.viewPagerJoin)
    }

    private fun removeLiveRequest() {
        databaseReference.child(playId).child("join").child(userInfo.getId()).removeValue()
    }

    private fun setCommenterLive(is_coming: String): Comment {
        return Comment(
            userInfo.getId(),
            userInfo.getName() + " " + is_coming,
            userInfo.getImage() as String,  /*userInfo.getName()+*/
            "",
            userInfo.getUserLevel().toString(),
            "comment"
        )
    }

    private fun handleReactionClick() {

        binding.bottomBar.btnShare.setOnClickListener {
            createFirebaseDynamicLink()

        }

        binding.bottomBar.btnCall.setOnClickListener {
            joinBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

        }

        binding.bottomBar.btnGift.setOnClickListener {
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

    private fun giftMethod() {
        //selectedUserName = hostName

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

    override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
        // Do nothing at the moment
    }

    override fun onUserJoined(uid: Int, elapsed: Int) {
        // Do nothing at the moment
    }

    override fun onUserOffline(uid: Int, reason: Int) {

        runOnUiThread {
            if (uid.toString() == playId && reason != 2) {
                toast("Live ended")
                finish()
            } else {
                removeRemoteUser(uid, reason)
            }
        }

    }

    override fun onFirstRemoteVideoDecoded(uid: Int, width: Int, height: Int, elapsed: Int) {
        runOnUiThread { renderRemoteUser(uid) }
    }

    private fun renderRemoteUser(uid: Int) {
        val surface = prepareRtcVideo(uid, false)
        binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(uid, surface, false, "$uid")
    }

    private fun removeRemoteUser(uid: Int, reason: Int) {
        runOnUiThread {
            removeRtcVideo(uid, false)
            binding.playBroadcast.liveVideoGridLayout.removeUserVideo(this, reason, uid, false)
            if (uid.toString() == playId && reason != 2) {
                toast("Live ended")
                finish()
            }
        }
    }

    override fun onLocalVideoStats(stats: IRtcEngineEventHandler.LocalVideoStats) {
        if (!statsManager().isEnabled) return
        val data: LocalStatsData = statsManager().getStatsData(0) as LocalStatsData
        data.width = mVideoDimension!!.width
        data.height = mVideoDimension!!.height
        data.framerate = stats.sentFrameRate
    }

    override fun onRtcStats(stats: IRtcEngineEventHandler.RtcStats) {
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

    override fun onRemoteVideoStats(stats: IRtcEngineEventHandler.RemoteVideoStats) {
        if (!statsManager().isEnabled) return
        val data: RemoteStatsData = statsManager().getStatsData(stats.uid) as RemoteStatsData
        data.width = stats.width
        data.height = stats.height
        data.framerate = stats.rendererOutputFrameRate
        data.videoDelay = stats.delay
    }

    override fun onRemoteAudioStats(stats: IRtcEngineEventHandler.RemoteAudioStats) {
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
            toast("This is your own layout")
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

    override fun onDestroy() {
        super.onDestroy()
        rtcEngine().leaveChannel()
        removeLiveRequest()
        removeViewStatus()
    }

    private fun removeViewStatus() {
        myRef.child(playId).child("view").child(userInfo.getId()).removeValue()
    }

    private fun leaveToLive() {
        rtcEngine().leaveChannel()
        removeLiveRequest()
        removeViewStatus()
        val deleteResponseCall = OTPPhoneNumberActivity.api.deleteMyHistory()
        deleteResponseCall.enqueue(object : Callback<DeleteResponse?> {
            override fun onResponse(
                call: Call<DeleteResponse?>,
                response: Response<DeleteResponse?>
            ) {
                //Toast.makeText(LiveVideoBroadcasterActivity.this, ""+response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            override fun onFailure(call: Call<DeleteResponse?>, t: Throwable) {}
        })
        finish()
    }

    fun kickOut() {
        rtcEngine().setClientRole(CLIENT_ROLE_AUDIENCE)
        binding.playBroadcast.liveVideoGridLayout.removeUserVideo(this, 0, 1, true)

        //hide some views
        binding.changeCameraButton.visibility = View.GONE
        binding.btnMuteCall.visibility = View.GONE
    }

    fun startLive() {
        rtcEngine().setClientRole(CLIENT_ROLE_BROADCASTER)
        val surface = prepareRtcVideo(0, true)
        binding.playBroadcast.liveVideoGridLayout.addUserVideoSurface(
            0,
            surface,
            false,
            userInfo.name
        )

        //show some views
        //binding.btnVideoOff.visibility = View.VISIBLE
        binding.changeCameraButton.visibility = View.VISIBLE
        binding.btnMuteCall.visibility = View.VISIBLE
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
                MaterialAlertDialogBuilder(this@PlayBroadcastActivity)
                    .setTitle("Leave from Live!")
                    .setMessage("Are you sure to leave from this live ?")
                    .setCancelable(false)
                    .setPositiveButton(
                        com.shopnolive.shopnolive.R.string.yes
                    ) { _, _ ->
                        leaveToLive()
                    }
                    .setNegativeButton(
                        com.shopnolive.shopnolive.R.string.no
                    ) { _, _ -> }
                    .create().show()
            }
        }
    }


    private fun minimizeApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
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
                    observeHostInfo()
                    giftHistories.clear()
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

    private fun observeHostInfo() {

        callApi(getRestApis().userPersonalData(playId), onApiSuccess = {
            hostInfo = it.data
            if (hostInfo.getImage().isNullOrEmpty()) {
                hostInfo.setImage("")
            }

            binding.userDiamondLive.text = hostInfo.getPresentCoinBalance().toString()
            binding.topBar.liveUserProfileName.text = hostInfo.getName()
            binding.topBar.liveUserProfileImage.loadImageFromUrl(ApiClient.BASE_URL + hostInfo.getImage())
        })

        profileViewModel.profile.observe(this) { myProfile ->
            if (userInfo != null) {
                userInfo = myProfile.profileData
                if (userInfo.getImage().isNullOrEmpty()) {
                    userInfo.setImage("")
                }

                if (userInfo.status != "active") {
                    val intent = Intent(this@PlayBroadcastActivity, BannedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    this@PlayBroadcastActivity.finish()
                }
            }
        }
    }

    fun totalCalls(size: Int) {
        if (size == 0) {
            binding.bottomBar.badge.visibility = View.GONE
        } else {

            try {
                binding.bottomBar.badge.setText(size.toString() + "")

            } catch (e: Exception) {
                Log.d("ERROR", e.toString())
            }

            binding.bottomBar.badge.visibility = View.VISIBLE
        }
    }

    override fun onUserItemClicked(userId: String) {
        MessageFragment(userInfo.getId(), userId).apply {
            show(supportFragmentManager, "MessageFragment")
        }
    }
}