/*
 * GitMessengerBot © 2021 지성빈 & 구환. all rights reserved.
 * GitMessengerBot license is under the GPL-3.0.
 *
 * [GitRepo.kt] created by Ji Sungbin on 21. 7. 13. 오전 1:52.
 *
 * Please see: https://github.com/GitMessengerBot/GitMessengerBot-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.activity.main.editor.git.repo

import kotlinx.coroutines.flow.Flow
import me.sungbin.gitmessengerbot.activity.main.editor.git.model.GitFile
import me.sungbin.gitmessengerbot.activity.main.editor.git.model.Repo
import me.sungbin.gitmessengerbot.util.config.StringConfig

interface GitRepo {
    fun getFileContent(
        repoName: String,
        path: String,
        branch: String = StringConfig.GitDefaultBranch
    ): Flow<GitResult>

    fun createRepo(repo: Repo): Flow<GitResult>
    fun updateFile(repoName: String, path: String, gitFile: GitFile): Flow<GitResult>
}
