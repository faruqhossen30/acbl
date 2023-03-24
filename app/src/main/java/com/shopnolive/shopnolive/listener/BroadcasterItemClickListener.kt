package com.shopnolive.shopnolive.listener

interface BroadcasterItemClickListener {
    fun onClick(userId: Int, name: String)
    fun onInfoClicked(userId: Int, name: String)
}