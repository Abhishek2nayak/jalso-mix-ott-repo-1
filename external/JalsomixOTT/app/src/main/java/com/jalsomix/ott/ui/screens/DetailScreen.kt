package com.jalsomix.ott.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.ContentType
import com.jalsomix.ott.data.DownloadRecord
import com.jalsomix.ott.data.Episode
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.Title
import com.jalsomix.ott.data.canWatch
import com.jalsomix.ott.ui.DownloadButton
import com.jalsomix.ott.ui.GlassButton
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.PosterArt
import com.jalsomix.ott.ui.PosterCard
import com.jalsomix.ott.ui.PremiumBadge
import com.jalsomix.ott.ui.RoundIconButton
import com.jalsomix.ott.ui.SectionHeader
import com.jalsomix.ott.ui.theme.JM

@Composable
fun DetailScreen(
    titleId: String,
    state: AppState,
    onBack: () -> Unit,
    onPlay: (Title, Int) -> Unit,
    onToggleList: (String) -> Unit,
    onOpen: (String) -> Unit,
    onDownload: (Title, Episode) -> Unit,
    onDeleteDownload: (DownloadRecord) -> Unit,
    onSub: () -> Unit
) {
    val t = remember(titleId) { SampleData.byId(titleId) }
    if (t == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val context = LocalContext.current
    val sub = state.subscription
    val inList = t.id in state.myList
    val canPlayFirst = canWatch(t, 0, sub)
    val shareText = stringResource(R.string.share_text, t.name)
    val similar = remember(t.id) {
        SampleData.titles.filter { it.id != t.id && (it.type == t.type || it.genres.any { g -> g in t.genres }) }.take(10)
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .background(JM.Bg),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // ---- Banner ----
        item {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                PosterArt(t, t.bannerUrl, Modifier.fillMaxSize())
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Black.copy(alpha = 0.55f),
                                0.35f to Color.Transparent,
                                1f to JM.Bg
                            )
                        )
                )
                RoundIconButton(
                    Icons.AutoMirrored.Rounded.ArrowBack,
                    stringResource(R.string.cd_back),
                    onBack,
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                )
                RoundIconButton(
                    Icons.Rounded.Share,
                    stringResource(R.string.btn_share),
                    {
                        val send = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(send, null))
                    },
                    Modifier
                        .align(Alignment.TopEnd)
                        .padding(14.dp)
                )
                Box(
                    Modifier
                        .align(Alignment.Center)
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.45f))
                        .border(1.5.dp, JM.Gold, CircleShape)
                        .clickable { onPlay(t, 0) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (canPlayFirst) Icons.Rounded.PlayArrow else Icons.Rounded.Lock,
                        null,
                        tint = JM.Gold,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }

        // ---- Info ----
        item {
            Column(Modifier.padding(horizontal = 18.dp)) {
                if (t.premium) {
                    PremiumBadge()
                    Spacer(Modifier.height(8.dp))
                }
                Text(t.name, style = MaterialTheme.typography.headlineMedium, color = JM.Text)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("★ ${t.rating}", style = MaterialTheme.typography.labelLarge, color = JM.Gold)
                    Dot()
                    Text("${t.year}", style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                    Dot()
                    Text(
                        t.ageRating,
                        style = MaterialTheme.typography.labelSmall,
                        color = JM.Text,
                        modifier = Modifier
                            .border(1.dp, JM.Muted, RoundedCornerShape(4.dp))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    )
                    Dot()
                    Text(t.language, style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                    Dot()
                    Text(typeLabel(t.type), style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    t.genres.forEach { g ->
                        Text(
                            g,
                            style = MaterialTheme.typography.labelSmall,
                            color = JM.Gold,
                            modifier = Modifier
                                .clip(RoundedCornerShape(50))
                                .background(JM.Gold.copy(alpha = 0.12f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
                Spacer(Modifier.height(18.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    GoldButton(
                        stringResource(if (canPlayFirst) R.string.btn_play else R.string.btn_unlock),
                        onClick = { onPlay(t, 0) },
                        modifier = Modifier.weight(1f),
                        icon = if (canPlayFirst) Icons.Rounded.PlayArrow else Icons.Rounded.Lock
                    )
                    GlassButton(
                        stringResource(if (inList) R.string.btn_in_list else R.string.btn_my_list),
                        onClick = { onToggleList(t.id) },
                        icon = if (inList) Icons.Rounded.Check else Icons.Rounded.Add
                    )
                }
                if (t.type == ContentType.MOVIE) {
                    val movieEp = t.episodes.first()
                    Spacer(Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DownloadButton(
                            t, movieEp, state,
                            onStart = { onDownload(t, movieEp) },
                            onDelete = onDeleteDownload,
                            onNeedSub = onSub
                        )
                        Text(
                            stringResource(R.string.btn_download),
                            style = MaterialTheme.typography.bodyMedium,
                            color = JM.Text
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(t.synopsis, style = MaterialTheme.typography.bodyLarge, color = JM.Text.copy(alpha = 0.82f))
                Spacer(Modifier.height(24.dp))
            }
        }

        // ---- Episodes ----
        if (t.type != ContentType.MOVIE) {
            item {
                SectionHeader(
                    stringResource(R.string.episodes),
                    trailing = "${t.episodes.size}"
                )
                Spacer(Modifier.height(10.dp))
            }
            items(t.episodes, key = { it.index }) { ep ->
                val open = canWatch(t, ep.index, sub)
                Row(
                    Modifier
                        .fillMaxWidth()
                        .clickable { onPlay(t, ep.index) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier
                            .width(124.dp)
                            .height(72.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        PosterArt(t, "https://picsum.photos/seed/jme${t.id}${ep.index}/320/180", Modifier.fillMaxSize())
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                if (open) Icons.Rounded.PlayArrow else Icons.Rounded.Lock,
                                null,
                                tint = if (open) Color.White else JM.Gold,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            stringResource(R.string.episode_n, ep.index + 1),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = JM.Text,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            stringResource(R.string.min_fmt, ep.durationMin),
                            style = MaterialTheme.typography.bodySmall,
                            color = JM.Muted
                        )
                    }
                    if (!open) PremiumBadge()
                    Spacer(Modifier.width(6.dp))
                    DownloadButton(
                        t, ep, state,
                        onStart = { onDownload(t, ep) },
                        onDelete = onDeleteDownload,
                        onNeedSub = onSub
                    )
                }
            }
            item { Spacer(Modifier.height(20.dp)) }
        }

        // ---- More like this ----
        if (similar.isNotEmpty()) {
            item {
                SectionHeader(stringResource(R.string.more_like_this))
                Spacer(Modifier.height(12.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(similar, key = { it.id }) { s ->
                        PosterCard(s, onClick = { onOpen(s.id) }, width = 124.dp)
                    }
                }
            }
        }
    }
}

@Composable
private fun Dot() {
    Text("  •  ", style = MaterialTheme.typography.bodySmall, color = JM.Muted)
}
