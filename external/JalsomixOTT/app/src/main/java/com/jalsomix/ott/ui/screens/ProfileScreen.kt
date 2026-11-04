package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bookmarks
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Smartphone
import androidx.compose.material.icons.rounded.SupportAgent
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.deviceLimit
import com.jalsomix.ott.ui.Avatar
import com.jalsomix.ott.ui.IosAlert
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosRow
import com.jalsomix.ott.ui.langLabel
import com.jalsomix.ott.ui.theme.JM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private enum class ProfileDialog { ABOUT, LOGOUT }

@Composable
fun ProfileScreen(
    state: AppState,
    onSub: () -> Unit,
    onAppLanguage: () -> Unit,
    onContentLanguage: () -> Unit,
    onMyList: () -> Unit,
    onDownloads: () -> Unit,
    onDevices: () -> Unit,
    onHelp: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    var dialog by remember { mutableStateOf<ProfileDialog?>(null) }
    val sub = state.subscription
    val name = state.session.name.ifBlank { "Jalsomix" }
    val phone = state.session.phone.let { if (it.length == 10) "+91 ${it.take(5)} ${it.drop(5)}" else it }
    val contentNames = state.contentLangs.sorted().map { langLabel(it) }.joinToString(", ")
    val appLangName = when (state.uiLang) {
        "hi" -> "हिन्दी"
        else -> "English"
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        // brand glow behind the header
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-120).dp)
                .size(420.dp)
                .background(
                    Brush.radialGradient(
                        listOf(JM.GoldDeep.copy(alpha = 0.22f), JM.Ember.copy(alpha = 0.06f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 8.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar with gold ring
            Box(
                Modifier
                    .size(104.dp)
                    .border(2.dp, JM.GoldGradient, CircleShape)
                    .padding(5.dp),
                contentAlignment = Alignment.Center
            ) { Avatar(state.session, 92.dp) }

            Spacer(Modifier.height(12.dp))
            Text(name, style = MaterialTheme.typography.headlineSmall, color = JM.Text)
            Text(phone, style = MaterialTheme.typography.bodyMedium, color = JM.Muted)
            if (state.session.email.isNotBlank()) {
                Text(state.session.email, style = MaterialTheme.typography.bodySmall, color = JM.Muted)
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(if (sub.active) R.string.member_premium else R.string.member_free),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (sub.active) JM.OnGold else JM.Text,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(if (sub.active) JM.GoldGradient else Brush.linearGradient(listOf(JM.SurfaceHigh, JM.SurfaceHigh)))
                        .padding(horizontal = 14.dp, vertical = 7.dp)
                )
                Text(
                    stringResource(R.string.profile_edit),
                    style = MaterialTheme.typography.labelLarge,
                    color = JM.Gold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, JM.Gold.copy(alpha = 0.5f), RoundedCornerShape(50))
                        .clickable { onEditProfile() }
                        .padding(horizontal = 16.dp, vertical = 7.dp)
                )
            }

            // quick stats
            Spacer(Modifier.height(22.dp))
            Row(
                Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatCard("${state.myList.size}", stringResource(R.string.stat_list), Modifier.weight(1f), onMyList)
                StatCard("${state.downloads.size}", stringResource(R.string.stat_downloads), Modifier.weight(1f), onDownloads)
                StatCard("${state.devices.size}/${deviceLimit(sub)}", stringResource(R.string.stat_devices), Modifier.weight(1f), onDevices)
            }

            Spacer(Modifier.height(18.dp))
            SubscriptionCard(state, onSub)

            Spacer(Modifier.height(26.dp))
            IosGroup(header = stringResource(R.string.sec_account)) {
                IosRow(stringResource(R.string.profile_my_list), icon = Icons.Rounded.Bookmarks, onClick = onMyList)
                IosRow(
                    stringResource(R.string.profile_downloads), icon = Icons.Rounded.Download,
                    onClick = onDownloads
                )
                IosRow(
                    stringResource(R.string.profile_devices), icon = Icons.Rounded.Smartphone,
                    onClick = onDevices
                )
                IosRow(
                    stringResource(R.string.profile_manage_sub), icon = Icons.Rounded.WorkspacePremium,
                    divider = false, onClick = onSub
                )
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.sec_preferences)) {
                IosRow(
                    stringResource(R.string.profile_app_language), icon = Icons.Rounded.Translate,
                    value = appLangName, onClick = onAppLanguage
                )
                IosRow(
                    stringResource(R.string.profile_content_language), icon = Icons.Rounded.Language,
                    value = contentNames, divider = false, onClick = onContentLanguage
                )
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.sec_support)) {
                IosRow(stringResource(R.string.profile_help), icon = Icons.Rounded.SupportAgent, onClick = onHelp)
                IosRow(
                    stringResource(R.string.profile_about), icon = Icons.Rounded.Info,
                    divider = false, onClick = { dialog = ProfileDialog.ABOUT }
                )
            }

            Spacer(Modifier.height(24.dp))
            IosGroup {
                IosRow(
                    stringResource(R.string.profile_logout), destructive = true, chevron = false, divider = false,
                    onClick = { dialog = ProfileDialog.LOGOUT }
                )
            }
        }
    }

    when (dialog) {
        ProfileDialog.ABOUT -> IosAlert(
            title = stringResource(R.string.about_title),
            message = stringResource(R.string.about_msg),
            confirmText = stringResource(R.string.btn_ok),
            onConfirm = { dialog = null },
            onDismiss = { dialog = null }
        )
        ProfileDialog.LOGOUT -> IosAlert(
            title = stringResource(R.string.logout_title),
            message = stringResource(R.string.logout_msg),
            confirmText = stringResource(R.string.profile_logout),
            dismissText = stringResource(R.string.btn_cancel),
            destructive = true,
            onConfirm = {
                dialog = null
                onLogout()
            },
            onDismiss = { dialog = null }
        )
        null -> Unit
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .clip(shape)
            .background(Brush.verticalGradient(listOf(Color(0xFF1D1C21), Color(0xFF141417))))
            .border(0.6.dp, JM.Gold.copy(alpha = 0.14f), shape)
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            value,
            style = MaterialTheme.typography.titleLarge.copy(brush = JM.GoldGradient, fontWeight = FontWeight.Bold)
        )
        Text(label, style = MaterialTheme.typography.bodySmall, color = JM.Muted)
    }
}

@Composable
private fun SubscriptionCard(state: AppState, onSub: () -> Unit) {
    val sub = state.subscription
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (sub.active) JM.GoldGradient else JM.FireGradient)
            .clickable { onSub() }
            .padding(18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val fg = if (sub.active) JM.OnGold else Color.White
        Icon(Icons.Rounded.WorkspacePremium, null, tint = fg, modifier = Modifier.size(34.dp))
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            if (sub.active) {
                val planName = SampleData.planById(sub.planId)?.name.orEmpty()
                val until = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(sub.expiryMillis))
                Text(planName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = fg)
                Text(stringResource(R.string.sub_active_until, until), style = MaterialTheme.typography.bodySmall, color = fg)
            } else {
                Text(stringResource(R.string.profile_go_premium), style = MaterialTheme.typography.titleMedium, color = fg)
                Text(stringResource(R.string.profile_go_premium_sub), style = MaterialTheme.typography.bodySmall, color = fg.copy(alpha = 0.9f))
            }
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = fg)
    }
}
