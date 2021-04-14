/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.util

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Created by Ji Sungbin on 2021/04/13.
 */

@ExperimentalCoroutinesApi
fun <T> Call<T>.asCallbackFlow() = callbackFlow<T> {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            if (response.isSuccessful) {
                response.body()?.let { offer(it) } ?: close(Exception("Body is empty."))
            } else {
                close(IOException("${response.code()}; ${response.errorBody()}"))
            }
        }

        override fun onFailure(call: Call<T>, throwable: Throwable) {
            close(throwable)
        }
    })

    awaitClose()
}