package com.jalsomix.ott.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.net.Uri
import android.graphics.Color as AndroidColor
import android.view.ViewGroup
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.jalsomix.ott.R
import com.jalsomix.ott.data.DownloadRecord
import com.jalsomix.ott.data.Downloads
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.Subscription
import com.jalsomix.ott.data.canWatch
import com.jalsomix.ott.ui.RoundIconButton

private tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

/**
 * Full-screen landscape player. Plays the unlocked episodes of a title as a playlist
 * starting from [startEpisode], so the next episode auto-plays.
 */
@Composable
fun PlayerScreen(
    titleId: String,
    startEpisode: Int,
    subscription: Subscription,
    downloads: List<DownloadRecord>,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val title = remember(titleId) { SampleData.byId(titleId) }
    if (title == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }
    val playable = remember(titleId, subscription) {
        title.episodes.filter { canWatch(title, it.index, subscription) }
    }
    val startPos = playable.indexOfFirst { it.index == startEpisode }.coerceAtLeast(0)

    val exo = remember(titleId) {
        ExoPlayer.Builder(context).build().apply {
            // Use the offline copy when the episode has been downloaded.
            setMediaItems(playable.map { ep ->
                val rec = downloads.firstOrNull { it.titleId == titleId && it.episode == ep.index }
                val file = Downloads.fileFor(context, titleId, ep.index)
                if (rec != null && file.exists() && Downloads.status(context, rec.downloadId).done) {
                    MediaItem.fromUri(Uri.fromFile(file))
                } else {
                    MediaItem.fromUri(ep.videoUrl)
                }
            })
            seekTo(startPos, 0L)
            repeatMode = Player.REPEAT_MODE_OFF
            playWhenReady = true
            prepare()
        }
    }

    val activity = context.findActivity()

    DisposableEffect(exo) {
        val window = activity?.window
        val controller = window?.let { WindowCompat.getInsetsController(it, it.decorView) }
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        controller?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        controller?.hide(WindowInsetsCompat.Type.systemBars())

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_PAUSE) exo.pause()
        }
        val lifecycle = (activity as? LifecycleOwner)?.lifecycle
        lifecycle?.addObserver(observer)

        onDispose {
            lifecycle?.removeObserver(observer)
            exo.release()
            controller?.show(WindowInsetsCompat.Type.systemBars())
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    BackHandler(onBack = onBack)

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exo
                    useController = true
                    keepScreenOn = true
                    setBackgroundColor(AndroidColor.BLACK)
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
            }
        )

        Row(
            Modifier
                .align(Alignment.TopStart)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RoundIconButton(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.cd_back), onBack)
            Spacer(Modifier.width(12.dp))
            Text(
                title.name,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
