package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.langLabel
import com.jalsomix.ott.ui.theme.JM

/** Choose Hindi and / or Gujarati content. At least one stays selected. */
@Composable
fun ContentLanguageScreen(state: AppState, vm: AppViewModel, onBack: () -> Unit) {
    val langs = SampleData.contentLanguages
    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.content_title), onBack)
        Text(
            stringResource(R.string.content_sub),
            style = MaterialTheme.typography.bodyMedium,
            color = JM.Muted,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        IosGroup {
            langs.forEachIndexed { i, l ->
                val on = l in state.contentLangs
                Column {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clickable {
                                val next = if (on) state.contentLangs - l else state.contentLangs + l
                                if (next.isNotEmpty()) vm.saveContentLanguages(next)
                            }
                            .padding(horizontal = 16.dp, vertical = 15.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            langLabel(l),
                            style = MaterialTheme.typography.titleMedium,
                            color = JM.Text,
                            modifier = Modifier.weight(1f)
                        )
                        if (on) Icon(Icons.Rounded.Check, null, tint = JM.Gold, modifier = Modifier.size(24.dp))
                    }
                    if (i < langs.lastIndex) {
                        Box(
                            Modifier
                                .padding(start = 16.dp)
                                .fillMaxWidth()
                                .height(0.5.dp)
                                .background(JM.Outline)
                        )
                    }
                }
            }
        }
    }
}
