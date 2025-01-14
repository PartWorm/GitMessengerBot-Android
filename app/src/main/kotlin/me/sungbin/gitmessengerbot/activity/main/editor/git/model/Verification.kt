/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * [Verification.kt] created by Ji Sungbin on 21. 7. 13. 오전 1:47.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.activity.main.editor.git.model

import com.google.gson.annotations.SerializedName

data class Verification(
    @SerializedName("reason")
    val reason: String,

    @SerializedName("signature")
    val signature: Any,

    @SerializedName("payload")
    val payload: Any,

    @SerializedName("verified")
    val verified: Boolean
)
