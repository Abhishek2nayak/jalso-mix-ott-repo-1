package com.jalsomix.ott.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.Wordmark
import com.jalsomix.ott.ui.theme.JM
import java.util.Locale

private data class UiLang(val tag: String, val native: String, val english: String)

private val uiLanguages = listOf(
    UiLang("en", "English", "English"),
    UiLang("hi", "हिन्दी", "Hindi")
)

/** App (UI) language picker – shown first on a fresh install and again from Profile. */
@Composable
fun LanguageScreen(
    state: AppState,
    vm: AppViewModel,
    onboarding: Boolean,
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    var selected by rememberSaveable { mutableStateOf(state.uiLang) }

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        if (!onboarding) IosNavBar(stringResource(R.string.profile_app_language), onBack)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = if (onboarding) 40.dp else 8.dp)
        ) {
            if (onboarding) {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Wordmark(22)
                }
                Spacer(Modifier.height(28.dp))
            }
            Text(
                stringResource(R.string.lang_title),
                style = MaterialTheme.typography.headlineMedium,
                color = JM.Text,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Text(
                stringResource(R.string.lang_sub),
                style = MaterialTheme.typography.bodyMedium,
                color = JM.Muted,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 4.dp, bottom = 22.dp)
            )

            IosGroup {
                uiLanguages.forEachIndexed { i, l ->
                    Column {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable { selected = l.tag }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(l.native, style = MaterialTheme.typography.titleMedium, color = JM.Text)
                                Text(l.english, style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                            }
                            if (selected == l.tag) {
                                Icon(Icons.Rounded.Check, null, tint = JM.Gold, modifier = Modifier.size(24.dp))
                            }
                        }
                        if (i < uiLanguages.lastIndex) {
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

        GoldButton(
            text = stringResource(R.string.btn_continue),
            onClick = {
                val chosen = selected
                vm.saveUiLanguage(chosen) {
                    onDone()
                    applyAppLocale(chosen)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        )
    }
}

/** Applies the app UI language (values / values-hi / values-gu) – the activity recreates itself if it changed. */
fun applyAppLocale(tag: String) {
    val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        .ifEmpty { Locale.getDefault().language }
    if (!current.startsWith(tag)) {
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag))
    }
}
