package com.jalsomix.ott.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.DownloadRecord
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.PosterArt
import com.jalsomix.ott.ui.rememberDownloadStatus
import com.jalsomix.ott.ui.theme.JM

@Composable
fun DownloadsScreen(
    state: AppState,
    onBack: () -> Unit,
    onPlay: (String, Int) -> Unit,
    onDelete: (DownloadRecord) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.dl_title), onBack)
        if (state.downloads.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(36.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.dl_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.downloads, key = { "${it.titleId}_${it.episode}" }) { rec ->
                    DownloadRow(rec, onPlay, onDelete)
                }
            }
        }
    }
}

@Composable
private fun DownloadRow(rec: DownloadRecord, onPlay: (String, Int) -> Unit, onDelete: (DownloadRecord) -> Unit) {
    val title = remember(rec.titleId) { SampleData.byId(rec.titleId) } ?: return
    val status = rememberDownloadStatus(rec)
    val done = status?.done == true

    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(JM.Surface)
            .clickable(enabled = done) { onPlay(rec.titleId, rec.episode) }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(112.dp)
                .height(64.dp)
                .clip(RoundedCornerShape(10.dp))
        ) {
            PosterArt(title, "https://picsum.photos/seed/jme${title.id}${rec.episode}/320/180", Modifier.fillMaxSize())
            if (done) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.PlayArrow, null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                title.name,
                style = MaterialTheme.typography.titleMedium,
                color = JM.Text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            val sub = if (title.type == com.jalsomix.ott.data.ContentType.MOVIE) ""
            else stringResource(R.string.episode_n, rec.episode + 1) + " • "
            val statusText = when {
                status == null -> ""
                status.done -> stringResource(R.string.dl_done)
                status.failed -> stringResource(R.string.dl_failed)
                else -> stringResource(R.string.dl_progress, (status.progress * 100).toInt())
            }
            Text(
                sub + statusText,
                style = MaterialTheme.typography.bodySmall,
                color = if (status?.failed == true) JM.Danger else JM.Muted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Box(
            Modifier
                .size(40.dp)
                .clickable { onDelete(rec) },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Delete, stringResource(R.string.dl_delete), tint = JM.Danger, modifier = Modifier.size(22.dp))
        }
    }
}
