package com.jalsomix.ott.ui.screens

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cancel
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.forLangs
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.PosterCard
import com.jalsomix.ott.ui.theme.JM

@Composable
fun SearchScreen(
    state: AppState,
    autoVoice: Boolean,
    onBack: () -> Unit,
    onOpen: (String) -> Unit
) {
    val ctx = LocalContext.current
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    var query by rememberSaveable { mutableStateOf("") }
    val prompt = stringResource(R.string.voice_prompt)
    val unavailable = stringResource(R.string.voice_unavailable)
    val speechLang = when (state.uiLang) {
        "hi" -> "hi-IN"
        "gu" -> "gu-IN"
        else -> "en-IN"
    }

    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { r ->
        if (r.resultCode == Activity.RESULT_OK) {
            r.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()?.let { query = it }
        }
    }
    fun startVoice() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLang)
            putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        }
        try {
            voiceLauncher.launch(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(ctx, unavailable, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (autoVoice) startVoice() else runCatching { focus.requestFocus() }
    }

    val pool = remember(state.contentLangs) { SampleData.titles.forLangs(state.contentLangs) }
    val results = remember(query, pool) {
        val q = query.trim()
        if (q.isEmpty()) emptyList()
        else pool.filter { t ->
            t.name.contains(q, ignoreCase = true) ||
                t.language.contains(q, ignoreCase = true) ||
                t.genres.any { it.contains(q, ignoreCase = true) }
        }
    }
    val trending = remember(pool) { pool.sortedByDescending { it.rating }.take(6) }

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 4.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(JM.SurfaceHigh)
                    .padding(start = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Rounded.Search, null, tint = JM.Muted, modifier = Modifier.size(22.dp))
                Spacer(Modifier.width(8.dp))
                BasicTextField(
                    value = query,
                    onValueChange = { query = it },
                    singleLine = true,
                    textStyle = TextStyle(color = JM.Text, fontSize = 17.sp),
                    cursorBrush = SolidColor(JM.Gold),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focus),
                    decorationBox = { inner ->
                        if (query.isEmpty()) {
                            Text(stringResource(R.string.search_hint), style = TextStyle(fontSize = 17.sp), color = JM.Muted)
                        }
                        inner()
                    }
                )
                if (query.isNotEmpty()) {
                    Box(
                        Modifier
                            .size(40.dp)
                            .clickable { query = "" },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.Cancel, null, tint = JM.Muted, modifier = Modifier.size(20.dp))
                    }
                }
                Box(
                    Modifier
                        .size(44.dp)
                        .clickable { startVoice() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Mic, stringResource(R.string.cd_mic), tint = JM.Gold, modifier = Modifier.size(22.dp))
                }
            }
            Text(
                stringResource(R.string.btn_cancel),
                style = MaterialTheme.typography.bodyLarge,
                color = JM.Gold,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onBack() }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }

        when {
            query.isBlank() -> Column(Modifier.padding(top = 8.dp)) {
                IosGroup(header = stringResource(R.string.search_trending)) {
                    trending.forEachIndexed { i, t ->
                        Column(Modifier.clickable { query = t.name }) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 13.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Rounded.TrendingUp, null, tint = JM.Gold, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(14.dp))
                                Text(t.name, style = MaterialTheme.typography.bodyLarge, color = JM.Text)
                            }
                            if (i < trending.lastIndex) {
                                Box(
                                    Modifier
                                        .padding(start = 50.dp)
                                        .fillMaxWidth()
                                        .height(0.5.dp)
                                        .background(JM.Outline)
                                )
                            }
                        }
                    }
                }
            }
            results.isEmpty() -> Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    textAlign = TextAlign.Center
                )
            }
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(results, key = { it.id }) { t ->
                    PosterCard(t, onClick = { onOpen(t.id) })
                }
            }
        }
    }
}
