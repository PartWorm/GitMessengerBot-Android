/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * [MessageService.kt] created by Ji Sungbin on 21. 7. 10. 오전 11:05.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import me.sungbin.gitmessengerbot.R
import me.sungbin.gitmessengerbot.bot.Bot
import me.sungbin.gitmessengerbot.bot.StackManager.sessions
import me.sungbin.gitmessengerbot.util.Util
import me.sungbin.gitmessengerbot.util.extension.toast

class MessageService : NotificationListenerService() {

    override fun onCreate() {
        super.onCreate()
        toast(applicationContext, getString(R.string.service_message_bot_start))
    }

    override fun onDestroy() {
        // toast(applicationContext, getString(R.string.service_message_bot_stop)) [TODO] Fix memory leak
        super.onDestroy()
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        if (sbn.packageName != "com.kakao.talk") return
        // todo: 커스텀 패키지, power 추가
        val wExt = Notification.WearableExtender(sbn.notification)
        for (action in wExt.actions) {
            if (action.remoteInputs.isNotEmpty()) {
                if (action.title.toString().lowercase().contains("reply") ||
                    action.title.toString().contains("답장")
                ) {
                    val extras = sbn.notification.extras
                    var isGroupChat: Boolean

                    var room = extras.getString("android.summaryText")
                    val sender = extras.get("android.title").toString()
                    val message = extras.get("android.text").toString()

                    if (room == null) {
                        room = sender
                        isGroupChat = false
                    } else isGroupChat = true

                    if (!sessions.containsKey(room)) sessions[room] = action
                    /* if (!PictureManager.profileImage.containsKey(room)) PictureManager.profileImage[sender] =
                         sbn.notification.getLargeIcon().toBitmap(context)*/ // todo

                    println(listOf(room, message, sender, isGroupChat))
                    chatHook(room, message, sender, isGroupChat)
                }
            }
        }
    }

    private fun chatHook(
        room: String,
        message: String,
        sender: String,
        isGroupChat: Boolean
    ) {
        try {
            for (script in Bot.getPowerOnScripts()) {
                Bot.callJsResponder(
                    context = applicationContext,
                    script = script,
                    room = room,
                    message = message,
                    sender = sender,
                    isGroupChat = isGroupChat,
                    isDebugMode = false
                )
            }
        } catch (exception: Exception) {
            Util.error(applicationContext, "chatHook 에러\n\n$exception")
        }
    }
}
