/*
 * DevEventAndroid © 2021 용감한 친구들. all rights reserved.
 * DevEventAndroid license is under the MIT.
 *
 * [Licenser.kt] created by Ji Sungbin on 21. 6. 27. 오전 3:12.
 *
 * Please see: https://github.com/brave-people/Dev-Event-Android/blob/master/LICENSE.
 */

package me.sungbin.gitmessengerbot.ui.licenser

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.sp
import me.sungbin.gitmessengerbot.R
import me.sungbin.gitmessengerbot.util.Web

data class Project(val name: String, val link: String, val license: License)

sealed class License(val name: String) {
    object MIT : License("MIT")
    object BSD : License("BSD")
    object Apache2 : License("Apache2")
    object GPL3 : License("GPL3")
    object LGPL3 : License("LGPL3")
    object AGPL3 : License("AGPL3")
    class Custom(name: String) : License(name)
}

@Composable
private fun LicenserHeader(license: License) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = license.name,
            color = Color.Black,
            fontSize = 15.sp,
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Color(0xFFEEEEEE))
        )
    }
}

@Composable
private fun LicenserItem(project: Project) {
    val context = LocalContext.current

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = project.name, color = Color.Black, fontSize = 20.sp)
        Icon(
            painter = painterResource(R.drawable.ic_round_insert_link_24),
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.clickable { Web.open(context, Web.Link.Custom(project.link)) }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.Licenser(projects: List<Project>) {
    val projectGroup = projects.groupBy { it.license }
    projectGroup.forEach { (license, projects) ->
        stickyHeader {
            LicenserHeader(license)
        }

        items(projects.sortedByDescending { it.name }.asReversed()) { project ->
            LicenserItem(project)
        }
    }
}
