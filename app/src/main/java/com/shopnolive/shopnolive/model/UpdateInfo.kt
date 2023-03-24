package com.shopnolive.shopnolive.model

data class UpdateInfo(
    val forceUpdate: Boolean = true,
    val versionCode: String = "",
    val updating: Boolean = false
)