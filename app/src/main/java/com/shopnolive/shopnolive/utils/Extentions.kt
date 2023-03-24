package com.shopnolive.shopnolive.utils

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.provider.Settings
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.snackbar.Snackbar
import com.shopnolive.shopnolive.FamousLiveApp.Companion.getAppInstance
import com.shopnolive.shopnolive.R
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) =
    beginTransaction().func().commit()

inline fun <T : View> T.onClick(crossinline func: T.() -> Unit) = setOnClickListener { func() }

@RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
fun isNetworkAvailable(): Boolean {
    val info = getAppInstance().getConnectivityManager().activeNetworkInfo
    return info != null && info.isConnected
}

fun getStringToJSON(name: String, value: String): JSONObject {
    return JSONObject("{$name: $value}")
}

fun Activity.snackBar(msg: String, length: Int = Snackbar.LENGTH_SHORT) =
    Snackbar.make(findViewById(android.R.id.content), msg, length).setTextColor(Color.WHITE).show()

fun Fragment.snackBar(msg: String) = activity!!.snackBar(msg)

fun Activity.snackBarError(msg: String) {
    val snackBar = Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG)
    val sbView = snackBar.view
    sbView.setBackgroundColor(getAppInstance().resources.getColor(R.color.colorPrimary));snackBar.setTextColor(
        Color.WHITE
    );snackBar.show()
}

fun Snackbar.setTextColor(color: Int): Snackbar {
    val tv = view.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
    tv.setTextColor(color)
    return this
}

fun Activity.showPermissionAlert(view: View) {
    val snackBar = Snackbar.make(
        view,
        getString(R.string.error_permission_required),
        Snackbar.LENGTH_INDEFINITE
    )
    val sbView = snackBar.view
    snackBar.setAction("Enable") {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
        snackBar.dismiss()
    }
    sbView.setBackgroundColor(getAppInstance().resources.getColor(R.color.colorPrimary));snackBar.setTextColor(
        Color.WHITE
    );snackBar.show()
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int) =
    supportFragmentManager.inTransaction { replace(frameId, fragment) }

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int) =
    supportFragmentManager.inTransaction { add(frameId, fragment) }

fun AppCompatActivity.removeFragment(fragment: Fragment) =
    supportFragmentManager.inTransaction { remove(fragment) }

fun AppCompatActivity.showFragment(fragment: Fragment) = supportFragmentManager.inTransaction {
    setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
    show(fragment)
}

fun AppCompatActivity.hideFragment(fragment: Fragment) = supportFragmentManager.inTransaction {
    hide(fragment)
}

fun Long.getNormalDate(): String {
    val formatter = SimpleDateFormat("dd - MMMM - yyyy", Locale.getDefault())
    return formatter.format(this)
}

