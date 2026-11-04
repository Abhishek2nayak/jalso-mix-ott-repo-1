package com.jalsomix.ott.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.layout.widthIn
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.jalsomix.ott.R
import com.jalsomix.ott.ui.theme.JM

/** iOS-style navigation bar: gold back chevron on the left, centred semibold title. */
@Composable
fun IosNavBar(title: String, onBack: () -> Unit, modifier: Modifier = Modifier, trailing: @Composable () -> Unit = {}) {
    Box(
        modifier
            .fillMaxWidth()
            .height(52.dp)
    ) {
        Row(
            Modifier
                .align(Alignment.CenterStart)
                .clip(RoundedCornerShape(10.dp))
                .clickable { onBack() }
                .padding(start = 6.dp, end = 12.dp, top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Rounded.ChevronLeft, stringResource(R.string.cd_back), tint = JM.Gold, modifier = Modifier.size(30.dp))
            Text(stringResource(R.string.cd_back), style = MaterialTheme.typography.bodyLarge, color = JM.Gold)
        }
        Text(
            title,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 96.dp),
            style = MaterialTheme.typography.titleMedium,
            color = JM.Text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Box(
            Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 12.dp)
        ) { trailing() }
    }
}

/** Large iOS title ("Home", "Movies"…). */
@Composable
fun LargeTitle(title: String, subtitle: String? = null, modifier: Modifier = Modifier) {
    Column(modifier.padding(horizontal = 20.dp)) {
        Text(title, style = MaterialTheme.typography.displaySmall, color = JM.Text)
        if (subtitle != null) {
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = JM.Muted)
        }
    }
}

/** iOS search pill with a magnifier and a microphone. Tapping it opens search; the mic starts voice search. */
@Composable
fun IosSearchBar(
    onClick: () -> Unit,
    onMic: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(RoundedCornerShape(13.dp))
            .background(JM.SurfaceHigh)
            .clickable { onClick() }
            .padding(start = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Search, stringResource(R.string.cd_search), tint = JM.Muted, modifier = Modifier.size(22.dp))
        Spacer(Modifier.width(8.dp))
        Text(
            stringResource(R.string.search_short) + " " + stringResource(R.string.search_hint),
            style = MaterialTheme.typography.bodyLarge,
            color = JM.Muted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Box(
            Modifier
                .size(44.dp)
                .clickable { onMic() },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.Mic, stringResource(R.string.cd_mic), tint = JM.Gold, modifier = Modifier.size(22.dp))
        }
    }
}

/** Inset grouped list container (like iOS Settings). */
@Composable
fun IosGroup(
    modifier: Modifier = Modifier,
    header: String? = null,
    footer: String? = null,
    content: @Composable () -> Unit
) {
    Column(modifier.padding(horizontal = 16.dp)) {
        if (header != null) {
            Text(
                header.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 1.2.sp, fontSize = 11.sp),
                color = JM.Gold,
                modifier = Modifier.padding(start = 6.dp, bottom = 8.dp)
            )
        }
        Column(
            Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.verticalGradient(listOf(Color(0xFF1D1C21), Color(0xFF141417))))
                .border(0.6.dp, JM.Gold.copy(alpha = 0.14f), RoundedCornerShape(18.dp))
        ) { content() }
        if (footer != null) {
            Text(
                footer,
                style = MaterialTheme.typography.bodySmall,
                color = JM.Muted,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp)
            )
        }
    }
}

/** A row in an [IosGroup]: coloured icon tile, title, optional value and chevron. */
@Composable
fun IosRow(
    title: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tile: Color = JM.Gold,
    subtitle: String? = null,
    value: String? = null,
    chevron: Boolean = true,
    destructive: Boolean = false,
    divider: Boolean = true,
    onClick: (() -> Unit)? = null
) {
    Column(
        modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(JM.Gold.copy(alpha = 0.26f), JM.Ember.copy(alpha = 0.14f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, tint = JM.GoldLight, modifier = Modifier.size(19.dp))
                }
                Spacer(Modifier.width(14.dp))
            }
            Column(Modifier.weight(1f, fill = true)) {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (destructive) JM.Danger else JM.Text,
                    textAlign = if (destructive && icon == null) TextAlign.Center else TextAlign.Start,
                    maxLines = 1,
                    softWrap = false,
                    overflow = TextOverflow.Ellipsis,
                    modifier = if (destructive && icon == null) Modifier.fillMaxWidth() else Modifier
                )
                if (subtitle != null) {
                    Text(subtitle, style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                }
            }
            if (value != null) {
                Spacer(Modifier.width(8.dp))
                Text(
                    value,
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.End,
                    modifier = Modifier.widthIn(max = 120.dp)
                )
                Spacer(Modifier.width(6.dp))
            }
            if (chevron && onClick != null) {
                Icon(Icons.Rounded.ChevronRight, null, tint = JM.Gray, modifier = Modifier.size(22.dp))
            }
        }
        if (divider) {
            Box(
                Modifier
                    .padding(start = if (icon != null) 64.dp else 16.dp)
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(JM.Outline)
            )
        }
    }
}

/** iOS-style alert: centred title/message, hairline-separated buttons. */
@Composable
fun IosAlert(
    title: String,
    message: String?,
    confirmText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    dismissText: String? = null,
    destructive: Boolean = false
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            Modifier
                .width(290.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(JM.SurfaceHigh),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(title, style = MaterialTheme.typography.titleMedium, color = JM.Text, textAlign = TextAlign.Center)
                if (message != null) {
                    Spacer(Modifier.height(6.dp))
                    Text(message, style = MaterialTheme.typography.bodySmall, color = JM.Text.copy(alpha = 0.8f), textAlign = TextAlign.Center)
                }
            }
            Box(Modifier.fillMaxWidth().height(0.5.dp).background(JM.Gray))
            if (dismissText != null) {
                Row(Modifier.fillMaxWidth().height(46.dp)) {
                    Box(
                        Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) { Text(dismissText, style = MaterialTheme.typography.bodyLarge, color = JM.Gold) }
                    Box(Modifier.width(0.5.dp).height(46.dp).background(JM.Gray))
                    Box(
                        Modifier
                            .weight(1f)
                            .height(46.dp)
                            .clickable { onConfirm() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            confirmText,
                            style = MaterialTheme.typography.titleMedium,
                            color = if (destructive) JM.Danger else JM.Gold
                        )
                    }
                }
            } else {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .clickable { onConfirm() },
                    contentAlignment = Alignment.Center
                ) { Text(confirmText, style = MaterialTheme.typography.titleMedium, color = JM.Gold) }
            }
        }
    }
}
