/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * [Bot.kt] created by Ji Sungbin on 21. 7. 10. 오전 11:20.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.bot

import android.app.Notification
import android.app.RemoteInput
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import me.sungbin.gitmessengerbot.App
import me.sungbin.gitmessengerbot.activity.main.script.ScriptItem
import me.sungbin.gitmessengerbot.activity.main.script.toScriptDefaultSource
import me.sungbin.gitmessengerbot.util.Json
import me.sungbin.gitmessengerbot.util.Storage
import me.sungbin.gitmessengerbot.util.Util
import me.sungbin.gitmessengerbot.util.config.StringConfig

@Suppress("ObjectPropertyName")
object Bot {
    private val _app = mutableStateOf(App())
    private val _scripts = SnapshotStateList<ScriptItem>()
    private val _scriptPowers: HashMap<Int, MutableState<Boolean>> = hashMapOf()
    private val _compileStates: HashMap<Int, MutableState<Boolean>> = hashMapOf()

    val scripts
        get() = _scripts
            .sortedByDescending { it.name }
            .sortedByDescending { it.lang }
            .asReversed()
    val app: State<App> get() = _app

    init {
        _scripts.addAll(getList())
        Storage.read(StringConfig.AppData, null)?.let { appDataJson ->
            _app.value = Json.toModel(appDataJson, App::class)
        }
    }

    fun getPowerOnScripts() = scripts.filter { it.power }

    /**
     * 앱 정보 json 파일 저장
     */
    fun save(app: App) {
        _app.value = app
        Storage.write(StringConfig.AppData, Json.toString(app))
    }

    /**
     * 스크립트 추가 및 json 정보 파일 저장
     */
    fun save(script: ScriptItem) {
        _scripts.removeIf { it.id == script.id }
        _scripts.add(script)
        Storage.write(StringConfig.ScriptData(script.name, script.lang), Json.toString(script))
    }

    /**
     * 스크립트 소스코드 저장
     */
    fun save(script: ScriptItem, code: String) {
        Storage.write(
            StringConfig.Script(script.name, script.lang),
            code
        )
    }

    fun addScript(script: ScriptItem) {
        _scripts.add(script)
        Storage.write(
            StringConfig.Script(script.name, script.lang),
            script.lang.toScriptDefaultSource()
        )
        Storage.write(StringConfig.ScriptData(script.name, script.lang), Json.toString(script))
    }

    fun removeScript(script: ScriptItem) {
        _scripts.remove(script)
        Storage.remove(StringConfig.Script(script.name, script.lang))
    }

    private fun getList(): List<ScriptItem> {
        val scripts = mutableListOf<ScriptItem>()
        repeat(4) { lang ->
            Storage.fileList(StringConfig.ScriptPath(lang)).filter { it.path.endsWith(".json") }
                .forEach { scriptDataFile ->
                    scripts.add(
                        Json.toModel(
                            Storage.read(scriptDataFile.path, null)!!,
                            ScriptItem::class
                        )
                    )
                }
        }
        return scripts
    }

    fun getScriptById(id: Int) = scripts.first { it.id == id }

    fun getCode(script: ScriptItem) =
        Storage.read(StringConfig.Script(script.name, script.lang), "")!!

    fun replyToSession(context: Context, session: Notification.Action, message: String) {
        try {
            val sendIntent = Intent()
            val msg = Bundle()
            for (inputable in session.remoteInputs) msg.putCharSequence(
                inputable.resultKey,
                message
            )
            RemoteInput.addResultsToIntent(session.remoteInputs, sendIntent, msg)
            session.actionIntent.send(context, 0, sendIntent)
        } catch (exception: Exception) {
            Util.error(context, "메시지 답장 실패\n\n$exception")
        }
    }

    fun callJsResponder(
        context: Context,
        script: ScriptItem,
        room: String,
        message: String,
        sender: String,
        isGroupChat: Boolean,
        isDebugMode: Boolean
    ) {
        try {
            if (!isDebugMode) {
                val v8 = StackManager.v8[script.id] ?: run {
                    println("${script.name} - v8 instance is null")
                    return
                }
                v8.locker.acquire()
                val arguments = listOf(room, message, sender, isGroupChat, "null") // todo
                v8.executeJSFunction("onMessage", *arguments.toTypedArray())
                v8.locker.release()
                println("${script.name}: 실행됨")
            } else {
                // todo: 디버그 만들기
            }
        } catch (exception: Exception) {
            Util.error(context, "js response 호출 실패\n\n$exception")
        }
    }
}
