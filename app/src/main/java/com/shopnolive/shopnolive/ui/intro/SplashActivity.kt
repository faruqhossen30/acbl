package com.shopnolive.shopnolive.ui.intro

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.shopnolive.shopnolive.databinding.ActivitySplashBinding
import com.shopnolive.shopnolive.model.UpdateInfo
import com.shopnolive.shopnolive.otp.OTPPhoneNumberActivity
import com.shopnolive.shopnolive.statusCheck.StatusCheckActivity
import com.shopnolive.shopnolive.ui.activities.BannedActivity
import com.shopnolive.shopnolive.ui.live.PlayBroadcastActivity
import com.shopnolive.shopnolive.ui.main.HomeActivity
import com.shopnolive.shopnolive.utils.PreferenceUtils
import com.shopnolive.shopnolive.utils.Tools.isInternetConnected
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.callApi
import com.shopnolive.shopnolive.utils.getRestApis
import com.shopnolive.shopnolive.utils.toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.*
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private lateinit var updateRef: DatabaseReference
    private lateinit var userReference: DatabaseReference

    private lateinit var preferenceUtils: PreferenceUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playId = intent.getStringExtra("playId")
        if (!playId.isNullOrEmpty())
            selectedUserId = playId

        //init
        updateRef = FirebaseDatabase.getInstance().getReference("Update")
        userReference = FirebaseDatabase.getInstance().getReference("LiveUsers")
        preferenceUtils = PreferenceUtils(this)

        pref = applicationContext.getSharedPreferences("login", 0)
        accessToken = pref.getString("token", "")

        val blinkAnim = AnimationUtils.loadAnimation(this, com.shopnolive.shopnolive.R.anim.blink_animation)

        binding.imageSplash.animation = blinkAnim

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                var deepLink: Uri? = null
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link
                    Log.d("Dynamiclink", "Play Broadcast: ${deepLink!!.query}")
                    //init
                    try {
                        playId = deepLink.getQueryParameter("userId")
                        selectedUserId = playId
                    } catch (e: Exception) {

                    }
                    checkForUpdate()

                } else {
                    Log.d("Dynamiclink", "Play Broadcast: null")
                    //init
                    //playId = intent.getStringExtra("id") ?: ""
                    //selectedUserId = playId
                    checkForUpdate()
                }
            }.addOnFailureListener(this) { e ->
                Log.w(
                    "Dynamiclink",
                    "getDynamicLink:onFailure",
                    e
                )
                checkForUpdate()
            }


        /*if (accessToken.isNotEmpty()){
            goToHomeActivity()
        } else {
            startActivity(Intent(this, OTPPhoneNumberActivity::class.java))
            finish()
        }*/

    }

    private fun goToHomeActivity() {
        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
        finish()
    }

    private fun checkForUpdate() {
        if (isInternetConnected(this)) {
            if (accessToken.isNotEmpty()) {
                updateRef.addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (!snapshot.exists()) {
                            goToHomeActivity()
                            return
                        }

                        val updateInfo =
                            snapshot.getValue(UpdateInfo::class.java)

                        updateInfo?.let {
                            if (it.versionCode != BuildConfig.VERSION_NAME) {
                                if (it.updating) {
                                    startActivity(
                                        Intent(
                                            this@SplashActivity,
                                            StatusCheckActivity::class.java
                                        )
                                    )
                                    finish()
                                } else if (it.forceUpdate) {
                                    preferenceUtils.setUpdateSkipped(false)
                                    MaterialAlertDialogBuilder(this@SplashActivity)
                                        .setTitle("New Update Available!")
                                        .setMessage("New version available in google play store please update this version")
                                        .setCancelable(false)
                                        .setPositiveButton(
                                            com.shopnolive.shopnolive.R.string.update
                                        ) { _, _ ->
                                            startActivity(
                                                Intent(
                                                    Intent.ACTION_VIEW,
                                                    Uri.parse("market://details?id=${this@SplashActivity.packageName}")
                                                )
                                            )
                                            finish()
                                        }
                                        .create().show()
                                } else {
                                    if (preferenceUtils.isUpdateSkipped()) {
                                        if (playId.isNullOrEmpty()) {
                                            goToHomeActivity()
                                        } else {
                                            checkLiveStatus()
                                        }
                                    } else {
                                        MaterialAlertDialogBuilder(this@SplashActivity)
                                            .setTitle("New Update Available!")
                                            .setMessage("New version available in google play store please update this version")
                                            .setCancelable(false)
                                            .setNegativeButton(
                                                com.shopnolive.shopnolive.R.string.not_now
                                            ) { _, _ ->
                                                preferenceUtils.setUpdateSkipped(
                                                    true
                                                )
                                                if (playId.isNullOrEmpty()) {
                                                    goToHomeActivity()
                                                } else {
                                                    checkLiveStatus()
                                                }
                                            }
                                            .setPositiveButton(
                                                com.shopnolive.shopnolive.R.string.update
                                            ) { _, _ ->
                                                startActivity(
                                                    Intent(
                                                        Intent.ACTION_VIEW,
                                                        Uri.parse("market://details?id=${this@SplashActivity.packageName}")
                                                    )
                                                )
                                                finish()
                                            }
                                            .create().show()
                                    }
                                }
                            } else {
                                if (playId.isNullOrEmpty()) {
                                    goToHomeActivity()
                                } else {
                                    checkLiveStatus()
                                }
                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        toast(error.message)
                        MaterialAlertDialogBuilder(this@SplashActivity)
                            .setTitle("Connection Problem")
                            .setMessage(error.message)
                            .setCancelable(false)
                            .setPositiveButton(
                                com.shopnolive.shopnolive.R.string.okay
                            ) { _, _ -> this@SplashActivity.onBackPressed() }
                            .create().show()
                    }

                })

            } else {
                startActivity(Intent(applicationContext, OTPPhoneNumberActivity::class.java))
                finish()
            }
        } else {
            MaterialAlertDialogBuilder(this)
                .setTitle("No Internet Connection!")
                .setMessage("Please turn on your wifi or mobile data and try again")
                .setCancelable(false)
                .setPositiveButton(
                    com.shopnolive.shopnolive.R.string.okay
                ) { _, _ -> super.onBackPressed() }
                .create().show()
        }
    }

    private fun checkLiveStatus() {
        userReference.orderByChild("id").equalTo(playId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        checkUserStatus()
                    } else {
                        toast("This live ended")
                        goToHomeActivity()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast(error.message)
                    goToHomeActivity()
                }

            })
    }

    private fun checkUserStatus() {
        callApi(getRestApis().myProfile, onApiSuccess = {
            if (it.isSuccess) {
                userInfo = it.profileData
                userProfileFollow = it.followers

                if (userInfo.getImage().isNullOrEmpty()) {
                    userInfo.setImage("")
                }

                //toast(userInfo.name)
                myName = userInfo.name
                myImage = userInfo.image
                if (userInfo.status != "active") {
                    val intent = Intent(this@SplashActivity, BannedActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    this@SplashActivity.finish()
                } else {
                    gotoLiveActivity()
                }
            } else {
                toast(it.message)
            }
        }, onApiError = {
            toast(it)
        }, onNetworkError = {
            toast("No Internet Connection")
        })
    }

    private fun gotoLiveActivity() {
        val intent = Intent(this, PlayBroadcastActivity::class.java)
        intent.putExtra("position", 0)
        intent.putExtra("id", playId)
        intent.putExtra("waiting", "other")
        intent.putExtra("name", playId)
        userPosition = 0
        selectedUserId = playId
        startActivity(intent)
        finish()
    }
}