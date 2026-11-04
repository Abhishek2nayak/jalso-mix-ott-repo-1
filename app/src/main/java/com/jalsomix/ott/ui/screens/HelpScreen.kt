package com.jalsomix.ott.ui.screens

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.animateContentSize
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Chat
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.material.icons.rounded.Mail
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.Support
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.IosRow
import com.jalsomix.ott.ui.theme.JM

private fun Context.safeStart(intent: Intent) {
    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(this, "No app found", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val ctx = LocalContext.current
    var report by rememberSaveable { mutableStateOf("") }
    val faqs = listOf(
        R.string.faq_q1 to R.string.faq_a1,
        R.string.faq_q2 to R.string.faq_a2,
        R.string.faq_q3 to R.string.faq_a3,
        R.string.faq_q4 to R.string.faq_a4,
        R.string.faq_q5 to R.string.faq_a5
    )

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.help_title), onBack)
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp)
        ) {
            Text(
                stringResource(R.string.help_headline),
                style = MaterialTheme.typography.headlineMedium,
                color = JM.Text,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 16.dp)
            )

            IosGroup(footer = stringResource(R.string.help_hours)) {
                IosRow(
                    stringResource(R.string.help_email), icon = Icons.Rounded.Mail, tile = JM.Blue,
                    subtitle = Support.EMAIL,
                    onClick = {
                        ctx.safeStart(
                            Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${Support.EMAIL}"))
                                .putExtra(Intent.EXTRA_SUBJECT, "JalsomixOTT support")
                        )
                    }
                )
                IosRow(
                    stringResource(R.string.help_whatsapp), icon = Icons.Rounded.Chat, tile = JM.Green,
                    onClick = {
                        ctx.safeStart(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${Support.WHATSAPP}")))
                    }
                )
                IosRow(
                    stringResource(R.string.help_call), icon = Icons.Rounded.Call, tile = JM.Orange,
                    divider = false,
                    onClick = { ctx.safeStart(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Support.PHONE}"))) }
                )
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.help_faq)) {
                faqs.forEachIndexed { i, (q, a) ->
                    FaqItem(stringResource(q), stringResource(a), divider = i < faqs.lastIndex)
                }
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.help_report)) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .padding(14.dp)
                ) {
                    BasicTextField(
                        value = report,
                        onValueChange = { report = it.take(500) },
                        textStyle = TextStyle(color = JM.Text, fontSize = 16.sp),
                        cursorBrush = SolidColor(JM.Gold),
                        modifier = Modifier.fillMaxSize(),
                        decorationBox = { inner ->
                            if (report.isEmpty()) {
                                Text(stringResource(R.string.help_report_hint), color = JM.Muted, style = TextStyle(fontSize = 16.sp))
                            }
                            inner()
                        }
                    )
                }
            }
            Spacer(Modifier.height(14.dp))
            GoldButton(
                stringResource(R.string.help_send),
                onClick = {
                    ctx.safeStart(
                        Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:${Support.EMAIL}"))
                            .putExtra(Intent.EXTRA_SUBJECT, "JalsomixOTT – problem report")
                            .putExtra(Intent.EXTRA_TEXT, report)
                    )
                },
                enabled = report.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun FaqItem(question: String, answer: String, divider: Boolean) {
    var open by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable { open = !open }
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                question,
                style = MaterialTheme.typography.bodyLarge,
                color = JM.Text,
                modifier = Modifier.weight(1f)
            )
            Icon(
                Icons.Rounded.ExpandMore, null, tint = JM.Muted,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(if (open) 180f else 0f)
            )
        }
        if (open) {
            Text(
                answer,
                style = MaterialTheme.typography.bodyMedium,
                color = JM.Muted,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 14.dp)
            )
        }
        if (divider) {
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
