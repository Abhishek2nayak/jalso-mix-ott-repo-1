package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.DeviceInfo
import com.jalsomix.ott.data.deviceLimit
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.IosAlert
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.IosRow
import com.jalsomix.ott.ui.theme.JM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Device limit: Free/Basic 1, Premium 2, VIP 4 (see SampleData.plans).
 * The list is stored on this phone; for real cross-device enforcement keep it on your server.
 */
@Composable
fun DevicesScreen(
    state: AppState,
    currentDeviceId: String,
    onBack: () -> Unit,
    onRemove: (String) -> Unit,
    onUpgrade: () -> Unit
) {
    val limit = deviceLimit(state.subscription)
    val used = state.devices.size.coerceAtLeast(1)
    var pending by remember { mutableStateOf<DeviceInfo?>(null) }
    val fmt = remember { SimpleDateFormat("dd MMM yyyy", Locale.getDefault()) }
    val current = state.devices.firstOrNull { it.id == currentDeviceId }
    val others = state.devices.filter { it.id != currentDeviceId }

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.dev_title), onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            // Usage meter
            Column(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(JM.Surface)
                    .padding(16.dp)
            ) {
                Text(
                    stringResource(R.string.dev_usage, used.coerceAtMost(limit), limit),
                    style = MaterialTheme.typography.titleMedium,
                    color = JM.Text
                )
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(JM.SurfaceHigh)
                ) {
                    Box(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth((used.toFloat() / limit).coerceIn(0.05f, 1f))
                            .clip(RoundedCornerShape(50))
                            .background(JM.GoldGradient)
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.dev_this)) {
                IosRow(
                    title = current?.model ?: android.os.Build.MODEL,
                    subtitle = current?.let { stringResource(R.string.dev_added, fmt.format(Date(it.addedAt))) },
                    icon = Icons.Rounded.Smartphone, tile = JM.Blue, divider = false
                )
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.dev_other), footer = stringResource(R.string.dev_footer, limit)) {
                if (others.isEmpty()) {
                    IosRow(stringResource(R.string.dev_none), chevron = false, divider = false)
                } else {
                    others.forEachIndexed { i, d ->
                        IosRow(
                            title = d.model,
                            subtitle = stringResource(R.string.dev_added, fmt.format(Date(d.addedAt))),
                            icon = Icons.Rounded.Smartphone, tile = JM.Gray,
                            value = stringResource(R.string.dev_remove), chevron = false,
                            divider = i < others.lastIndex,
                            onClick = { pending = d }
                        )
                    }
                }
            }
        }

        if (!state.subscription.active || limit < 4) {
            GoldButton(
                stringResource(R.string.dev_upgrade),
                onClick = onUpgrade,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 14.dp)
            )
        }
    }

    pending?.let { d ->
        IosAlert(
            title = stringResource(R.string.dev_remove) + " " + d.model + "?",
            message = null,
            confirmText = stringResource(R.string.dev_remove),
            dismissText = stringResource(R.string.btn_cancel),
            destructive = true,
            onConfirm = {
                onRemove(d.id)
                pending = null
            },
            onDismiss = { pending = null }
        )
    }
}
