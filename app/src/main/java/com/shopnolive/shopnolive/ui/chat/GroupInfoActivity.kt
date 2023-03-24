package com.shopnolive.shopnolive.ui.chat

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.shopnolive.shopnolive.adapter.GroupMemberAdapter
import com.shopnolive.shopnolive.databinding.ActivityGroupInfoBinding
import com.shopnolive.shopnolive.model.chat.GroupConversation
import com.shopnolive.shopnolive.model.chat.GroupMember
import com.shopnolive.shopnolive.ui.main.HomeActivity
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.getNormalDate
import com.shopnolive.shopnolive.utils.toast

class GroupInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupInfoBinding

    private lateinit var groupReference: DatabaseReference
    private lateinit var groupMemberList: ArrayList<GroupMember>
    private lateinit var memberAdapter: GroupMemberAdapter

    private var groupId: String = ""
    private var groupInfo = GroupConversation()
    private var currentMember = GroupMember()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //setup toolbar
        setSupportActionBar(binding.toolbar.toolbar)
        supportActionBar!!.title = "Group Info"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //init
        groupId = intent.getStringExtra("groupId").toString()
        groupReference =
            FirebaseDatabase.getInstance().getReference("GroupConversations").child(groupId)
        groupMemberList = ArrayList()

        binding.recyclerViewGroupMembers.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@GroupInfoActivity)
        }

        getGroupInfo()
        getGroupMemberList()

        binding.btnAddMember.setOnClickListener {
            startActivity(
                Intent(this, AddMemberActivity::class.java)
                    .putExtra("groupId", groupId)
            )
        }

        binding.btnLeaveGroup.setOnClickListener {
            groupReference.child("Members").child(myId)
                .removeValue().addOnSuccessListener {
                    toast("You left from this group")
                    val intent = Intent(this@GroupInfoActivity, HomeActivity::class.java)
                        .putExtra("groupId", groupId)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    this@GroupInfoActivity.finish()
                }
        }

    }

    private fun getGroupInfo() {
        groupReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    groupInfo = dataSnapshot.getValue(GroupConversation::class.java)!!
                    updateGroupInfoUI()
                } else {
                    toast("No Group info.")
                }
            }

            override fun onCancelled(p0: DatabaseError) {
                toast(p0.message)
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateGroupInfoUI() {
        binding.tvGroupName.text = groupInfo.groupName
        binding.tvGroupCreatedAt.text = "Since ${groupInfo.createdTime.getNormalDate()}"
    }

    private fun getGroupMemberList() {
        groupReference.child("Members")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    groupMemberList.clear()
                    dataSnapshot.children.forEach {
                        val member = it.getValue(GroupMember::class.java)!!
                        groupMemberList.add(member)
                        if (member.memberId == myId) {
                            currentMember = member
                            binding.btnLeaveGroup.isVisible = currentMember.role != "admin"
                            binding.btnAddMember.isVisible = currentMember.role == "admin"
                        }
                    }

                    //groupMemberList.reverse()

                    memberAdapter =
                        GroupMemberAdapter(
                            this@GroupInfoActivity,
                            currentMember.role,
                            groupMemberList,
                            groupId
                        )
                    binding.recyclerViewGroupMembers.adapter = memberAdapter
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}