package com.jalsomix.ott.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.DownloadDone
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.DownloadRecord
import com.jalsomix.ott.data.Downloads
import com.jalsomix.ott.data.Episode
import com.jalsomix.ott.data.Title
import com.jalsomix.ott.ui.theme.JM
import kotlinx.coroutines.delay

/** Live status of a download (polls the system DownloadManager until it finishes). */
@Composable
fun rememberDownloadStatus(rec: DownloadRecord?): Downloads.Status? {
    val ctx = LocalContext.current
    val s by produceState<Downloads.Status?>(null, rec?.downloadId) {
        val r = rec
        if (r == null) {
            value = null
        } else {
            while (true) {
                val st = Downloads.status(ctx, r.downloadId)
                value = st
                if (st.done || st.failed) break
                delay(700)
            }
        }
    }
    return s
}

/** iOS-style circular download control: download ▸ progress ring ▸ done tick. */
@Composable
fun DownloadButton(
    t: Title,
    ep: Episode,
    state: AppState,
    onStart: () -> Unit,
    onDelete: (DownloadRecord) -> Unit,
    onNeedSub: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rec = state.downloads.firstOrNull { it.titleId == t.id && it.episode == ep.index }
    val status = rememberDownloadStatus(rec)

    Box(
        modifier
            .size(40.dp)
            .clip(CircleShape)
            .clickable {
                when {
                    rec != null -> onDelete(rec)
                    !state.subscription.active -> onNeedSub()
                    else -> onStart()
                }
            },
        contentAlignment = Alignment.Center
    ) {
        when {
            rec == null -> Icon(
                Icons.Rounded.Download, stringResource(R.string.cd_download),
                tint = JM.Text, modifier = Modifier.size(24.dp)
            )
            status == null -> CircularProgressIndicator(
                color = JM.Gold, strokeWidth = 2.dp, modifier = Modifier.size(22.dp)
            )
            status.done -> Icon(
                Icons.Rounded.DownloadDone, stringResource(R.string.cd_downloaded),
                tint = JM.Green, modifier = Modifier.size(24.dp)
            )
            status.failed -> Icon(
                Icons.Rounded.Download, stringResource(R.string.dl_failed),
                tint = JM.Danger, modifier = Modifier.size(24.dp)
            )
            else -> CircularProgressIndicator(
                progress = status.progress.coerceAtLeast(0.04f),
                color = JM.Gold, trackColor = JM.Outline, strokeWidth = 2.5.dp,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}
