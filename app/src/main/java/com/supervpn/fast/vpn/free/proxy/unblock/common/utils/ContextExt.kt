package com.supervpn.fast.vpn.free.proxy.unblock.common.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.supervpn.fast.vpn.free.proxy.unblock.BuildConfig
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.BaseView
import com.supervpn.fast.vpn.free.proxy.unblock.common.base.SubActivity
import io.reactivex.CompletableTransformer
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.BufferedReader
import kotlin.math.roundToInt

fun Context?.startActivity(fragment: Class<*>, bundle: Bundle? = null, flags: Int? = null) {
    SubActivity.start(this, fragment, bundle, flags)
}

fun Context?.sp2Px(sp: Float): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp,
        this?.resources?.displayMetrics
    )
}

fun Context?.dp2Px(dp: Float): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp,
        this?.resources?.displayMetrics
    ).roundToInt()
}

fun Context?.rateApp() {
    val uri: Uri = Uri.parse("market://details?id=${BuildConfig.APPLICATION_ID}")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri)
    goToMarket.addFlags(
        Intent.FLAG_ACTIVITY_NO_HISTORY or
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK
    )
    try {
        this?.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        this?.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://play.google.com/store/apps/details?id=${BuildConfig.APPLICATION_ID}")
            )
        )
    }
}

fun Activity?.updateColorStatusBar(@ColorRes colorRes: Int, flagLight: Boolean = false) {
    this ?: return
    val color = ContextCompat.getColor(this, colorRes)
    this.window?.statusBarColor = color

    val view = this.window.decorView
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (flagLight) {
            view.systemUiVisibility =
                view.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        } else {
            view.systemUiVisibility =
                view.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
    }
}

fun Context?.showKeyboard(editText: EditText) {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT)
}

fun Activity?.hideSoftKeyboard() {
    val imm = this?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(this?.currentFocus?.windowToken, 0)
}

@SuppressLint("ClickableViewAccessibility")
fun Activity?.hideKeyboardWhenTouchOutside(view: View?, callback: (() -> Unit)? = null) {
    if (view !is EditText) {
        view?.setOnTouchListener { _, _ ->
            this?.hideSoftKeyboard()
            callback?.invoke()
            return@setOnTouchListener false
        }
    }

    if (view is ViewGroup) {
        for (i in 0 until view.childCount) {
            val innerView = view.getChildAt(i)
            hideKeyboardWhenTouchOutside(innerView)
        }
    }
}

fun Context?.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Context?.readAsset(fileName: String) = this
    ?.assets
    ?.open(fileName)
    ?.bufferedReader()
    ?.use(BufferedReader::readText)

fun <T> observableTransformer(view: BaseView? = null): ObservableTransformer<T, T> {
    return ObservableTransformer { observable ->
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showProgressDialog(true) }
            .doFinally { view?.showProgressDialog(false) }
    }
}

fun completableTransformer(view: BaseView? = null): CompletableTransformer {
    return CompletableTransformer { observable ->
        observable
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { view?.showProgressDialog(true) }
            .doFinally { view?.showProgressDialog(false) }
    }
}