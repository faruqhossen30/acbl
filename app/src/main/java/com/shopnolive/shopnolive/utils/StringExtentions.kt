package com.shopnolive.shopnolive.utils

import android.text.Html
import android.text.Spanned

fun String.checkIsEmpty(): Boolean = isNullOrEmpty() || "" == this || this.equals("null", ignoreCase = true)

fun String.getHtmlString(): Spanned = Html.fromHtml(this)