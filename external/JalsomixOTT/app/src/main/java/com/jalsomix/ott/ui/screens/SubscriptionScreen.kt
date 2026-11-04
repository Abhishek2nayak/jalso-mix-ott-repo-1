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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.QrCode2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.Plan
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.IosAlert
import com.jalsomix.ott.ui.LogoMark
import com.jalsomix.ott.ui.RoundIconButton
import com.jalsomix.ott.ui.theme.JM
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(state: AppState, vm: AppViewModel, onBack: () -> Unit) {
    var selectedId by rememberSaveable { mutableStateOf("premium") }
    var showPay by rememberSaveable { mutableStateOf(false) }
    var method by rememberSaveable { mutableStateOf("upi") }
    var processing by remember { mutableStateOf(false) }
    var success by remember { mutableStateOf(false) }

    val plan = SampleData.planById(selectedId) ?: SampleData.plans[1]
    val sub = state.subscription

    Box(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-160).dp)
                .size(480.dp)
                .background(
                    Brush.radialGradient(
                        listOf(JM.GoldDeep.copy(alpha = 0.32f), JM.Ember.copy(alpha = 0.08f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth()) {
                    RoundIconButton(Icons.AutoMirrored.Rounded.ArrowBack, stringResource(R.string.cd_back), onBack)
                }
                LogoMark(96.dp)
                Spacer(Modifier.height(6.dp))
                Text(
                    stringResource(R.string.sub_title),
                    style = MaterialTheme.typography.headlineMedium.copy(brush = JM.GoldGradient),
                    textAlign = TextAlign.Center
                )
                Text(
                    stringResource(R.string.sub_sub),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                )

                if (sub.active) {
                    val planName = SampleData.planById(sub.planId)?.name.orEmpty()
                    val until = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(sub.expiryMillis))
                    Text(
                        "$planName • " + stringResource(R.string.sub_active_until, until),
                        style = MaterialTheme.typography.labelLarge,
                        color = JM.OnGold,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(JM.GoldGradient)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                }

                Spacer(Modifier.height(6.dp))
                SampleData.plans.forEach { p ->
                    PlanCard(p, selected = p.id == selectedId) { selectedId = p.id }
                    Spacer(Modifier.height(12.dp))
                }

                Spacer(Modifier.height(4.dp))
                Benefit(stringResource(R.string.feat_all))
                Benefit(stringResource(R.string.feat_adfree))
                Benefit(stringResource(R.string.feat_download))
                Benefit(stringResource(R.string.feat_early))
                Spacer(Modifier.height(12.dp))
                Text(
                    stringResource(R.string.sub_terms),
                    style = MaterialTheme.typography.bodySmall,
                    color = JM.Muted,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                Modifier
                    .background(JM.Surface)
                    .padding(horizontal = 18.dp, vertical = 14.dp)
                    .navigationBarsPadding()
            ) {
                GoldButton(
                    stringResource(R.string.pay_btn, plan.priceRs),
                    onClick = { showPay = true },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

    if (showPay) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { if (!processing) showPay = false },
            sheetState = sheetState,
            containerColor = JM.Surface
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp)
                    .padding(bottom = 24.dp)
            ) {
                Text(
                    stringResource(R.string.pay_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = JM.Text
                )
                Text(
                    "${plan.name} • ₹${plan.priceRs}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
                )
                PayMethod(Icons.Rounded.QrCode2, stringResource(R.string.pay_upi), method == "upi") { method = "upi" }
                PayMethod(Icons.Rounded.CreditCard, stringResource(R.string.pay_card), method == "card") { method = "card" }
                PayMethod(Icons.Rounded.AccountBalance, stringResource(R.string.pay_net), method == "net") { method = "net" }
                PayMethod(Icons.Rounded.AccountBalanceWallet, stringResource(R.string.pay_wallet), method == "wallet") { method = "wallet" }
                Spacer(Modifier.height(18.dp))
                if (processing) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = JM.Gold, strokeWidth = 2.5.dp, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(12.dp))
                        Text(stringResource(R.string.processing), color = JM.Text)
                    }
                } else {
                    GoldButton(
                        stringResource(R.string.pay_btn, plan.priceRs),
                        onClick = {
                            processing = true
                            vm.purchase(plan) {
                                processing = false
                                showPay = false
                                success = true
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    if (success) {
        IosAlert(
            title = stringResource(R.string.sub_success),
            message = stringResource(R.string.sub_success_sub),
            confirmText = stringResource(R.string.btn_done),
            onConfirm = {
                success = false
                onBack()
            },
            onDismiss = {}
        )
    }
}

@Composable
private fun PlanCard(plan: Plan, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(if (selected) JM.SurfaceHigh else JM.Surface)
            .border(if (selected) 1.8.dp else 1.dp, if (selected) JM.Gold else JM.Outline, shape)
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (selected) JM.Gold else JM.Muted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(JM.GoldGradient)
                    )
                }
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                if (plan.popular || plan.bestValue) {
                    Text(
                        stringResource(if (plan.popular) R.string.most_popular else R.string.best_value),
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                        color = Color.White,
                        modifier = Modifier
                            .padding(bottom = 6.dp)
                            .clip(RoundedCornerShape(50))
                            .background(JM.FireGradient)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
                Text(plan.name, style = MaterialTheme.typography.titleLarge, color = JM.Text)
                Text(
                    stringResource(R.string.feat_quality, plan.quality) + " • " +
                        stringResource(R.string.feat_screens, plan.screens),
                    style = MaterialTheme.typography.bodySmall,
                    color = JM.Muted
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "₹${plan.priceRs}",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold),
                    color = JM.Gold
                )
                Text(
                    stringResource(if (plan.yearly) R.string.per_year else R.string.per_month),
                    style = MaterialTheme.typography.bodySmall,
                    color = JM.Muted
                )
            }
        }
    }
}

@Composable
private fun Benefit(text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Rounded.Check, null, tint = JM.Gold, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium, color = JM.Text)
    }
}

@Composable
private fun PayMethod(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .clip(shape)
            .background(if (selected) JM.SurfaceHigh else Color.Transparent)
            .border(1.dp, if (selected) JM.Gold else JM.Outline, shape)
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = if (selected) JM.Gold else JM.Muted)
        Spacer(Modifier.width(14.dp))
        Text(label, style = MaterialTheme.typography.titleMedium, color = JM.Text, modifier = Modifier.weight(1f))
        if (selected) Icon(Icons.Rounded.Check, null, tint = JM.Gold, modifier = Modifier.size(20.dp))
    }
}
