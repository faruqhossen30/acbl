package com.shopnolive.shopnolive.ui.chat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.databinding.ActivityCreateChatGroupBinding
import com.shopnolive.shopnolive.model.chat.AddMemberActivity
import com.shopnolive.shopnolive.model.chat.GroupConversation
import com.shopnolive.shopnolive.model.chat.GroupMember
import com.shopnolive.shopnolive.utils.CustomProgressDialog
import com.shopnolive.shopnolive.utils.Variable.*
import com.shopnolive.shopnolive.utils.toast

class CreateChatGroupActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateChatGroupBinding
    private lateinit var progressDialog: CustomProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateChatGroupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //init
        progressDialog = CustomProgressDialog(this)

        binding.btnCreateGroupNextPage.setOnClickListener {
            val groupName = binding.etGroupName.text.toString().trim()
            if (TextUtils.isEmpty(groupName)) {
                binding.etGroupName.error = "Enter your name"
                binding.etGroupName.requestFocus()
            } else {
                progressDialog.showLoadingBar("Please wait")
                val groupId = System.currentTimeMillis().toString()
                val member =
                    GroupMember(
                        myId,
                        myName,
                        myImage,
                        System.currentTimeMillis(),
                        "admin",
                        System.currentTimeMillis(),
                        false
                    )
                val groupConversation = GroupConversation(
                    groupId,
                    groupName,
                    System.currentTimeMillis(),
                    "",
                    "private",
                    myId,
                    System.currentTimeMillis(),
                    true,
                    "No messages yet"
                )

                val groupRef = FirebaseDatabase.getInstance().getReference("GroupConversations").child(groupId)


                if (myId != null) {
                    groupRef.setValue(groupConversation).addOnSuccessListener {

                        groupRef.child("Members").child(myId).setValue(member)
                            .addOnSuccessListener {
                                progressDialog.dismiss()
                                toast("Group created successfully")
                                // Navigate...
                                Intent(this, AddMemberActivity::class.java).apply {
                                    this.putExtra("groupId", groupId)
                                    this.putExtra("adminId", myId)
                                    finish()
                                    startActivity(this)
                                }
                            }
                    }.addOnFailureListener {
                        progressDialog.dismiss()
                        toast("Something went wrong")
                    }
                } else {
                    progressDialog.dismiss()
                    toast("Authentication Error!")
                }
            }
        }
    }
}