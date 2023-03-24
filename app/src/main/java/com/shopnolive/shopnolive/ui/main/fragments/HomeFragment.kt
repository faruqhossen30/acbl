package com.shopnolive.shopnolive.ui.main.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.UserStoryAdapter
import com.shopnolive.shopnolive.databinding.FragmentHomeBinding
import com.shopnolive.shopnolive.model.BlockItem
import com.shopnolive.shopnolive.model.profile.ProfileData
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.callApi
import com.shopnolive.shopnolive.utils.getRestApis
import com.shopnolive.shopnolive.utils.isNetworkAvailable
import com.shopnolive.shopnolive.utils.toast


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var mReference: DatabaseReference
    private lateinit var blockUserRef: DatabaseReference
    private lateinit var blockConversationRef: DatabaseReference

    private lateinit var usersList: ArrayList<ProfileData>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val numberOfColumn: Int =
            if (this.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) 2 else 3

        myId = pref.getString("myId", "")
        gridLayoutManager = GridLayoutManager(requireContext(), numberOfColumn)
        mReference = FirebaseDatabase.getInstance().getReference("LiveUsers")
        blockUserRef = FirebaseDatabase.getInstance().getReference("BlockedUsers").child(myId)
        blockConversationRef = FirebaseDatabase.getInstance().getReference("ConversationBlocks").child(myId)
        usersList = ArrayList()

        binding.recyclerVewStreamers.layoutManager = gridLayoutManager

        loadApis()

        binding.swipeToRefresh.setOnRefreshListener {
            binding.swipeToRefresh.isEnabled = false
            //loadApis()
        }

        binding.swipeToRefresh.viewTreeObserver.addOnScrollChangedListener {
            binding.swipeToRefresh.isEnabled = binding.recyclerVewStreamers.scrollY == 0
        }

    }

    private fun loadApis() {
        if (isNetworkAvailable()) {
            //loadLiveList()
            //loadNewLiveList()
            //loadBlockUserList()
        } else {
            requireContext().toast("No Internet connection")
        }
    }

    private fun loadLiveList() {
        callApi(getRestApis().streamingList, onApiSuccess = {

            //hide progress
            binding.swipeToRefresh.isRefreshing = false

            if (it.success) {
                allUserInfo.clear()
                allUserInfo = it.data

                val userStoryAdapter = UserStoryAdapter(requireContext(), it.data)

                binding.recyclerVewStreamers.apply {
                    layoutManager = gridLayoutManager
                    adapter = userStoryAdapter
                }

                binding.shimmerLayoutHome.visibility = View.GONE
            } else {
                //data failed to load
                binding.shimmerLayoutHome.visibility = View.GONE
                requireContext().toast(it.message)
            }
        }, onApiError = {
            //hide progress
            binding.swipeToRefresh.isRefreshing = false
            binding.shimmerLayoutHome.visibility = View.GONE

            requireContext().toast(it)
        }, onNetworkError = {
            //hide progress
            binding.swipeToRefresh.isRefreshing = false
            binding.shimmerLayoutHome.visibility = View.GONE

            requireContext().toast("No Internet Connection")
        })
    }

    private val liveUserEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            binding.shimmerLayoutHome.visibility = View.GONE
            usersList.clear()
            allUserInfo.clear()
            if (snapshot.exists()) {
                for (data in snapshot.children) {
                    val profileData = data.getValue(ProfileData::class.java)
                    if (blockedUserIdList.contains(profileData!!.id)) {
                        val position = blockedUserIdList.indexOf(profileData.id)
                        val blockItem = blockedUserList[position]
                        if (blockItem.blockDuration < System.currentTimeMillis()) {
                            usersList.add(profileData)
                        }
                    } else {
                        usersList.add(profileData)
                    }
                }

                allUserInfo = usersList.sortedBy { it.presentCoinBalance }.reversed()
                allUserInfo = usersList

                binding.emptyLayout.root.visibility = View.GONE

                val userStoryAdapter = UserStoryAdapter(requireContext(), allUserInfo)

                binding.recyclerVewStreamers.adapter = userStoryAdapter

            } else {
                val userStoryAdapter = UserStoryAdapter(requireContext(), usersList)

                binding.recyclerVewStreamers.apply {
                    layoutManager = gridLayoutManager
                    adapter = userStoryAdapter
                }

                binding.emptyLayout.root.visibility = View.VISIBLE
                binding.shimmerLayoutHome.visibility = View.GONE
            }
        }

        override fun onCancelled(error: DatabaseError) {
            requireContext().toast(error.message)
        }

    }

    private val blockUserEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            blockedUserList.clear()
            blockedUserIdList.clear()
            for (data in snapshot.children) {
                val blockItem = data.getValue(BlockItem::class.java)
                blockedUserList.add(blockItem)
                blockedUserIdList.add(blockItem!!.userId)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            requireContext().toast(error.message)
        }
    }

    private val conversationBlockEvent = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            conversationBlockedList.clear()
            conversationBlockedIdList.clear()
            for (data in snapshot.children) {
                val blockItem = data.getValue(BlockItem::class.java)
                conversationBlockedList.add(blockItem)
                conversationBlockedIdList.add(blockItem!!.userId)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            requireContext().toast(error.message)
        }
    }

    /*override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        loadApis()
    }*/

    override fun onResume() {
        super.onResume()
        blockConversationRef.addValueEventListener(conversationBlockEvent)
        blockUserRef.addValueEventListener(blockUserEventListener)
        mReference.addValueEventListener(liveUserEventListener)
    }

    override fun onDestroy() {
        super.onDestroy()
        blockConversationRef.removeEventListener(conversationBlockEvent)
        blockUserRef.removeEventListener(blockUserEventListener)
        mReference.removeEventListener(liveUserEventListener)
    }
}