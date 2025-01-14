/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * [Util.kt] created by Ji Sungbin on 21. 7. 10. 오후 12:31.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.util

import android.content.Context
import android.widget.Toast
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import me.sungbin.gitmessengerbot.util.extension.toast

object Util {
    fun error(context: Context, message: String) {
        toast(context, message, Toast.LENGTH_LONG)
        println("Error: $message")
    }

    fun glideRequestOption() = RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .format(DecodeFormat.PREFER_ARGB_8888)
}
