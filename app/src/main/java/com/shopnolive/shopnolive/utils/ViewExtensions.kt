package com.shopnolive.shopnolive.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.*
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.shopnolive.shopnolive.FamousLiveApp.Companion.getAppInstance
import com.shopnolive.shopnolive.R
import kotlin.math.roundToInt


fun View.hideSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}


fun View.showSoftKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun View.changeBackgroundColor(@ColorInt newColor: Int, duration: Int = 300) {
    val oldBackground = background
    val color = ColorDrawable(newColor)
    val ld = LayerDrawable(arrayOf<Drawable>(color))
    if (oldBackground == null) background = ld
    else {
        val td = TransitionDrawable(arrayOf(oldBackground, ld))
        background = td
        td.startTransition(duration)
    }
}

/**
 * Create a Screnshot of the view and returns it as a Bitmap
 */
fun View.screenshot(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}

fun View.fadeIn(duration: Int = 400) {
    clearAnimation()
    val alphaAnimation = AlphaAnimation(this.alpha, 1.0f)
    alphaAnimation.duration = duration.toLong()
    startAnimation(alphaAnimation)
}

fun View.fadeOut(duration: Int = 400) {
    clearAnimation()
    val alphaAnimation = AlphaAnimation(this.alpha, 0.0f)
    alphaAnimation.duration = duration.toLong()
    startAnimation(alphaAnimation)
}

val View.res: Resources get() = resources
val View.ctx: Context get() = context

fun View.showIf(condition: Boolean) = if (condition) show() else hide()

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun View.isVisible() = visibility == View.VISIBLE

fun View.isGone() = visibility == View.GONE

fun View.isInvisible() = visibility == View.INVISIBLE

fun View.changeBackgroundTint(color: Int) {
    (background as GradientDrawable).setColor(color)
    (background as GradientDrawable).setStroke(0, 0)

    background.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY)
}

fun View.setStrokedBackground(
    backgroundColor: Int,
    strokeColor: Int = 0,
    alpha: Float = 1.0f,
    strokeWidth: Int = 3
) {
    val drawable = background as GradientDrawable
    drawable.setStroke(strokeWidth, strokeColor)
    drawable.setColor(adjustAlpha(backgroundColor, alpha))
}

fun adjustAlpha(color: Int, factor: Float): Int {
    val alpha = (Color.alpha(color) * factor).roundToInt()
    val red = Color.red(color)
    val green = Color.green(color)
    val blue = Color.blue(color)
    return Color.argb(alpha, red, green, blue)
}

fun Context.getDisplayWidth(): Int = resources.displayMetrics.widthPixels

fun Context.getDisplayHeight(): Int = resources.displayMetrics.heightPixels

fun Context.convertDpToPx(dp: Int): Int = (dp * resources.displayMetrics.density).toInt()

fun ImageView.loadImageFromUrl(
    aImageUrl: String,
    aPlaceHolderImage: Int = R.drawable.user1,
    aErrorImage: Int = R.drawable.user1
) {
    try {
        if (aImageUrl.isNotEmpty()) {
            Glide.with(getAppInstance()).load(aImageUrl).placeholder(aPlaceHolderImage)
                .diskCacheStrategy(
                    DiskCacheStrategy.AUTOMATIC
                ).error(aErrorImage).into(this)
        } else {
            loadImageFromDrawable(aPlaceHolderImage)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun ImageView.loadImageFromDrawable(@DrawableRes aPlaceHolderImage: Int) {
    Glide.with(getAppInstance()).load(aPlaceHolderImage).diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}