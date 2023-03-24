package com.shopnolive.shopnolive.utils

import android.content.Context
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.core.content.ContextCompat

fun Context.getConnectivityManager() =
    getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

fun Context.color(color: Int): Int = ContextCompat.getColor(this, color)

fun Context.toast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()