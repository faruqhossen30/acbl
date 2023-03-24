package com.shopnolive.shopnolive.ui.main

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationBarView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.api.client.ApiClient
import com.shopnolive.shopnolive.databinding.ActivityHomeBinding
import com.shopnolive.shopnolive.model.notification.CloudToken
import com.shopnolive.shopnolive.model.token.AgoraTokenModel
import com.shopnolive.shopnolive.ppal.util.SaveFCM
import com.shopnolive.shopnolive.ui.live.LiveBroadcastActivity
import com.shopnolive.shopnolive.ui.main.fragments.ChatFragment
import com.shopnolive.shopnolive.ui.main.fragments.HomeFragment
import com.shopnolive.shopnolive.ui.main.fragments.NewsFeedFragment
import com.shopnolive.shopnolive.ui.main.fragments.ProfileFragment
import com.shopnolive.shopnolive.utils.*
import com.shopnolive.shopnolive.utils.Constants.ALL_PERMISSIONS
import com.shopnolive.shopnolive.utils.Constants.REQUEST_CODE_ALL_PERMISSION
import com.shopnolive.shopnolive.utils.Variable.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.Response
import retrofit2.http.GET


class HomeActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding

    private var selectedFragment: Fragment? = null
    private val mHomeFragment = HomeFragment()
    private val mProfileFragment = ProfileFragment()
    private val mChatFragment = ChatFragment()
    private val mNewsFeedFragment = NewsFeedFragment()
    private lateinit var progressDialog: CustomProgressDialog
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (supportFragmentManager.findFragmentById(binding.fragmentPlaceholder.id) != null) {
            supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentById(binding.fragmentPlaceholder.id)!!)
                .commit()
        }

        progressDialog = CustomProgressDialog(this)
        progressDialog.showLoadingBar("Please wait")
        sharedPreferences = getSharedPreferences("login", MODE_PRIVATE)
        editor = sharedPreferences.edit()


        loadFragment(mHomeFragment)

        binding.bottomNavigationView.setOnItemSelectedListener(this)
        binding.bottomNavigationView.setOnItemReselectedListener {}

//        binding.fabLive.setOnClickListener {
//            if (!Tools.hasPermissions(this, ALL_PERMISSIONS)) {
//                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
//                        requestPermissions(ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSION)
//                    }
//                }
//            } else {
//                val intent = Intent(this, LiveBroadcastActivity::class.java)
//                if (!userInfo.getId().isNullOrEmpty()) {
//                    intent.putExtra("id", userInfo.getId())
//                } else {
//                    intent.putExtra("id", "random")
//                }
//                startActivity(intent)
//            }
//        }

        binding.ivSearch.setOnClickListener {
            toast("Under development")
        }

        if (!Tools.hasPermissions(this, ALL_PERMISSIONS)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                requestPermissions(ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSION)
            }
        }

        getAntMediaUrl()

        //save token
        //saveToken()
    }

    private fun saveToken() {
        myId = sharedPreferences.getString("myId", "")
        //save token
        SaveFCM.getInstance().storeFcmToken(this, myId)
        //Toast.makeText(applicationContext, ""+ myId, Toast.LENGTH_SHORT).show()
    }

    private fun loadFragment(aFragment: Fragment) {
        if (selectedFragment != null) {
            if (selectedFragment == aFragment) return
            hideFragment(selectedFragment!!)
        }
        if (aFragment.isAdded) {
            showFragment(aFragment)
        } else {
            addFragment(aFragment, binding.fragmentPlaceholder.id)
        }
        selectedFragment = aFragment
    }

    private fun loadMyProfile() {

        callApi(getRestApis().myProfile, onApiSuccess = {
            progressDialog.dismiss()
            if (it.isSuccess) {
                userInfo = it.profileData
                userProfileFollow = it.followers

                if (userInfo.getImage().isNullOrEmpty()) {
                    userInfo.setImage("")
                }

                //toast(userInfo.name)
                myName = userInfo.name
                myImage = userInfo.image
                editor.putString("myId", userInfo.id).commit()
                editor.putString("myName", userInfo.name).commit()
                editor.putString("myImage", userInfo.getImage()).commit()

                if (userInfo.status != "active") {
                    MaterialAlertDialogBuilder(this)
                        .setTitle("You are blocked!")
                        .setMessage("Your account is temporary blocked. Please try again later or contact with admin")
                        .setCancelable(false)
                        .setPositiveButton(
                            R.string.okay
                        ) { _, _ -> super.onBackPressed() }
                        .create().show()
                }
            } else {
                toast(it.message)
            }
        }, onApiError = {
            progressDialog.dismiss()
            toast(it)
        }, onNetworkError = {
            progressDialog.dismiss()
            toast("No Internet Connection")
        })
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> {
                binding.ivSearch.visibility = View.GONE
                loadFragment(mHomeFragment)
                return true
            }
            R.id.nav_search -> {
                binding.ivSearch.visibility = View.GONE
                loadFragment(mNewsFeedFragment)
                return true
            }

            R.id.nav_live -> {
                if (!Tools.hasPermissions(this, ALL_PERMISSIONS)) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                            requestPermissions(ALL_PERMISSIONS, REQUEST_CODE_ALL_PERMISSION)
                        }
                    }
                } else {
                    val intent = Intent(this, LiveBroadcastActivity::class.java)
                    if (!userInfo.getId().isNullOrEmpty()) {
                        intent.putExtra("id", userInfo.getId())
                    } else {
                        intent.putExtra("id", "random")
                    }
                    startActivity(intent)
                }
                return true
            }

            R.id.nav_chat -> {
                //binding.ivSearch.visibility = View.VISIBLE
                loadFragment(mChatFragment)
                //Toast.makeText(applicationContext,"Under Development",Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.nav_profile -> {
                binding.ivSearch.visibility = View.GONE
                loadFragment(mProfileFragment)
                return true
            }
            else -> {
                binding.ivSearch.visibility = View.GONE
                return false
            }
        }
        return false
    }

    override fun onStart() {
        super.onStart()
        userLoginToken = sharedPreferences.getString("phone", "")
        accessToken = sharedPreferences.getString("token", "")
        myId = sharedPreferences.getString("myId", "")
        loadMyProfile()
        updateFirebaseCloudToken()

        getAgoraToken();
    }

    private fun updateFirebaseCloudToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            if (!myId.isNullOrEmpty()) {
                val cloudToken = CloudToken(token = token)
                FirebaseDatabase.getInstance().getReference("Tokens")
                    .child(myId)
                    .setValue(cloudToken)
            }
        })
    }

    override fun onBackPressed() {
        if (selectedFragment != mHomeFragment) {
            loadFragment(mHomeFragment)
            binding.bottomNavigationView.selectedItemId = R.id.nav_home
        } else {
            super.onBackPressed()
        }
    }

    private fun getAntMediaUrl() {
        FirebaseDatabase.getInstance().getReference("AntMediaUrl")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        RTMP_BASE_URL = snapshot.getValue(String::class.java)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    toast(error.message)
                }

            })
    }

    companion object {
        @JvmStatic
        fun setBroadCasterInfo(
            userId: Int,
            tvName: TextView,
            tvDiamond: TextView,
            ivProfile: CircleImageView
        ) {
            callApi(getRestApis().userPersonalData(userId.toString()),
                onApiSuccess = {
                    val user = it.data
                    tvName.text = user.getName()
                    tvDiamond.text = user.getPresentCoinBalance()
                    if (!user.getImage().isNullOrEmpty())
                        ivProfile.loadImageFromUrl(ApiClient.BASE_URL + user.getImage())
                })
        }
    }


    private fun getAgoraToken() {

//        callApi(getRestApis().agora_token, onApiSuccess = {
//            progressDialog.dismiss()
//           Log.d("AGORA_TOKEN","success : "+it.success+it.getMessage()+","+it.data.toString());
//        }, onApiError = {
//            progressDialog.dismiss()
//            Log.d("AGORA_TOKEN","ERROR : "+it);
//
//            toast(it)
//        }, onNetworkError = {
//            progressDialog.dismiss()
//            toast("No Internet Connection")
//        })



        val agoraModelData = RetrofitHelper.getInstance().create(AgoraTokenApi::class.java)
        // launching a new coroutine
        GlobalScope.launch {
            val result = agoraModelData.getQuotes()
            if (result != null)
            // Checking the results
                Log.d("ayush: ", result.body().toString())

            val body = result.body()
            if (body != null) {
                Gson().toJson(body)
                Log.d("ayush: ",Gson().toJson(body)
                )

                var model=AgoraTokenModel()
                model=body


                val agoratoken=model.data.agoraToken

                editor.putString("agoratoken", agoratoken).commit()

                Log.d("agoratoken: ",sharedPreferences.getString("agoratoken","").toString())



            } else {



            }
        }






    }




    interface AgoraTokenApi {
        @GET("settings.php?settings=agora_token")
        suspend fun getQuotes() : Response<AgoraTokenModel>
    }

}