package com.shopnolive.shopnolive.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.shopnolive.shopnolive.R
import com.shopnolive.shopnolive.databinding.LayoutGroupMemberItemBinding
import com.shopnolive.shopnolive.model.chat.GroupMember
import com.shopnolive.shopnolive.utils.Variable.myId
import com.shopnolive.shopnolive.utils.getNormalDate
import com.shopnolive.shopnolive.utils.loadImageFromUrl
import com.shopnolive.shopnolive.utils.toast

class GroupMemberAdapter(
    private val context: Context,
    private val currentUserRole: String,
    private val memberList: ArrayList<GroupMember>,
    groupId: String
) : RecyclerView.Adapter<GroupMemberAdapter.MemberViewHolder>() {
    private val groupReference = FirebaseDatabase.getInstance().getReference("GroupConversations").child(groupId)

    inner class MemberViewHolder(val binding: LayoutGroupMemberItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private fun removeMemberFromGroup(memberId: String) {
        groupReference.child("Members").child(memberId)
            .removeValue().addOnSuccessListener {
                context.toast("Member has been removed")
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        return MemberViewHolder(
            LayoutGroupMemberItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        with(holder.binding){
            with(memberList[position]){
                userFullName.text = name
                userProfilePic.loadImageFromUrl(profileImage)
                tvMemberSince.text = "Since ${joinAt.getNormalDate()} (${role})"
                holder.binding.optionMember.isVisible = currentUserRole == "admin" && memberId != myId
            }
        }

        holder.binding.optionMember.setOnClickListener {
            val popupMenu = PopupMenu(context, holder.binding.optionMember)
            popupMenu.inflate(R.menu.menu_group_member_option)

            popupMenu.setOnMenuItemClickListener { item: MenuItem ->

                when (item.itemId) {
                    R.id.menu_remove -> {
                        removeMemberFromGroup(memberList[position].memberId)
                    }
                }
                false
            }
            popupMenu.show()
        }
    }

    override fun getItemCount(): Int {
        return memberList.size
    }
}