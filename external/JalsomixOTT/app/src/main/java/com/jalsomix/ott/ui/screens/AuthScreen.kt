package com.jalsomix.ott.ui.screens

import android.util.Patterns
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AuthResult
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.Wordmark
import com.jalsomix.ott.ui.theme.JM
import kotlinx.coroutines.delay

private enum class Step { PHONE, OTP, NAME }

/** Login / sign up with a mobile number + OTP (one flow for both). */
@Composable
fun AuthScreen(vm: AppViewModel, onSuccess: () -> Unit) {
    var step by rememberSaveable { mutableStateOf(Step.PHONE) }
    var phone by rememberSaveable { mutableStateOf("") }
    var otp by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf<Int?>(null) }
    var loading by remember { mutableStateOf(false) }
    var seconds by remember { mutableIntStateOf(0) }
    var resendKey by remember { mutableIntStateOf(0) }

    val phoneValid = phone.length == 10 && phone[0] in '6'..'9'

    LaunchedEffect(step, resendKey) {
        if (step == Step.OTP) {
            seconds = 30
            while (seconds > 0) {
                delay(1000)
                seconds--
            }
        }
    }

    fun finish(nm: String, em: String) {
        loading = true
        vm.completeAuth(phone, nm, em) { result ->
            loading = false
            if (result is AuthResult.Success) onSuccess()
            else if (result is AuthResult.NeedName) {
                error = null
                step = Step.NAME
            } else if (result is AuthResult.Error) error = result.message
        }
    }

    fun verify(code: String) {
        if (vm.verifyOtp(code)) {
            error = null
            finish("", "")
        } else {
            error = R.string.err_otp
            otp = ""
        }
    }

    BackHandler(enabled = step != Step.PHONE) {
        error = null
        otp = ""
        step = Step.PHONE
    }

    Box(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        Box(
            Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-150).dp)
                .size(460.dp)
                .background(
                    Brush.radialGradient(
                        listOf(JM.GoldDeep.copy(alpha = 0.26f), JM.Ember.copy(alpha = 0.07f), Color.Transparent)
                    ),
                    CircleShape
                )
        )

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 22.dp, vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Wordmark(24)
            Spacer(Modifier.height(30.dp))

            when (step) {
                Step.PHONE -> {
                    Heading(R.string.auth_head, stringResource(R.string.auth_sub))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(JM.Surface)
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🇮🇳  +91", style = MaterialTheme.typography.titleMedium, color = JM.Text)
                        Box(
                            Modifier
                                .padding(horizontal = 14.dp)
                                .width(0.5.dp)
                                .height(26.dp)
                                .background(JM.Outline)
                        )
                        BasicTextField(
                            value = phone,
                            onValueChange = { v ->
                                phone = v.filter { it.isDigit() }.take(10)
                                error = null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            textStyle = TextStyle(color = JM.Text, fontSize = 20.sp, letterSpacing = 1.sp),
                            cursorBrush = SolidColor(JM.Gold),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 17.dp),
                            decorationBox = { inner ->
                                if (phone.isEmpty()) {
                                    Text(
                                        stringResource(R.string.field_mobile),
                                        style = TextStyle(fontSize = 18.sp),
                                        color = JM.Muted
                                    )
                                }
                                inner()
                            }
                        )
                    }
                    ErrorLine(error)
                    Spacer(Modifier.height(20.dp))
                    LoadingButton(
                        stringResource(R.string.btn_get_otp), phoneValid && !loading, loading
                    ) {
                        if (!phoneValid) {
                            error = R.string.err_mobile
                        } else {
                            error = null
                            otp = ""
                            resendKey++
                            step = Step.OTP
                        }
                    }
                    if (phone.isNotEmpty() && !phoneValid && phone.length == 10) ErrorLine(R.string.err_mobile)
                }

                Step.OTP -> {
                    val fr = remember { FocusRequester() }
                    LaunchedEffect(Unit) { runCatching { fr.requestFocus() } }
                    Heading(R.string.otp_title, stringResource(R.string.otp_sub, phone))
                    BasicTextField(
                        value = otp,
                        onValueChange = { v ->
                            val d = v.filter { it.isDigit() }.take(6)
                            otp = d
                            error = null
                            if (d.length == 6 && !loading) verify(d)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(color = Color.Transparent),
                        cursorBrush = SolidColor(Color.Transparent),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(fr),
                        decorationBox = {
                            Row(
                                Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                repeat(6) { i ->
                                    val active = i == otp.length
                                    Box(
                                        Modifier
                                            .weight(1f)
                                            .aspectRatio(0.82f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(JM.Surface)
                                            .border(
                                                if (active) 1.5.dp else 0.5.dp,
                                                if (active) JM.Gold else JM.Outline,
                                                RoundedCornerShape(12.dp)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            otp.getOrNull(i)?.toString() ?: "",
                                            style = MaterialTheme.typography.headlineSmall,
                                            color = JM.Text
                                        )
                                    }
                                }
                            }
                        }
                    )
                    ErrorLine(error)
                    Spacer(Modifier.height(10.dp))
                    // Demo hint – remove once real SMS OTP is connected.
                    Text(
                        stringResource(R.string.otp_demo),
                        style = MaterialTheme.typography.bodySmall,
                        color = JM.Gold.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(16.dp))
                    LoadingButton(stringResource(R.string.btn_verify), otp.length == 6 && !loading, loading) {
                        verify(otp)
                    }
                    Spacer(Modifier.height(18.dp))
                    if (seconds > 0) {
                        Text(
                            stringResource(R.string.otp_resend_in, seconds),
                            style = MaterialTheme.typography.bodyMedium,
                            color = JM.Muted
                        )
                    } else {
                        Text(
                            stringResource(R.string.otp_resend),
                            style = MaterialTheme.typography.labelLarge,
                            color = JM.Gold,
                            modifier = Modifier
                                .clickable {
                                    otp = ""
                                    error = null
                                    resendKey++
                                }
                                .padding(8.dp)
                        )
                    }
                    Text(
                        stringResource(R.string.otp_change),
                        style = MaterialTheme.typography.labelLarge,
                        color = JM.Gold,
                        modifier = Modifier
                            .clickable {
                                otp = ""
                                error = null
                                step = Step.PHONE
                            }
                            .padding(8.dp)
                    )
                }

                Step.NAME -> {
                    Heading(R.string.name_title, stringResource(R.string.name_sub))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(JM.Surface)
                            .padding(horizontal = 16.dp, vertical = 17.dp)
                    ) {
                        BasicTextField(
                            value = name,
                            onValueChange = {
                                name = it.take(40)
                                error = null
                            },
                            singleLine = true,
                            textStyle = TextStyle(color = JM.Text, fontSize = 18.sp),
                            cursorBrush = SolidColor(JM.Gold),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (name.isEmpty()) {
                                    Text(stringResource(R.string.field_name), style = TextStyle(fontSize = 18.sp), color = JM.Muted)
                                }
                                inner()
                            }
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(JM.Surface)
                            .padding(horizontal = 16.dp, vertical = 17.dp)
                    ) {
                        BasicTextField(
                            value = email,
                            onValueChange = {
                                email = it.trim().take(60)
                                error = null
                            },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            textStyle = TextStyle(color = JM.Text, fontSize = 18.sp),
                            cursorBrush = SolidColor(JM.Gold),
                            modifier = Modifier.fillMaxWidth(),
                            decorationBox = { inner ->
                                if (email.isEmpty()) {
                                    Text(stringResource(R.string.field_email), style = TextStyle(fontSize = 18.sp), color = JM.Muted)
                                }
                                inner()
                            }
                        )
                    }
                    ErrorLine(error)
                    Spacer(Modifier.height(20.dp))
                    LoadingButton(stringResource(R.string.btn_finish), !loading, loading) {
                        if (name.isBlank()) error = R.string.err_name
                        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) error = R.string.err_email
                        else finish(name, email)
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            Text(
                stringResource(R.string.terms_note),
                style = MaterialTheme.typography.bodySmall,
                color = JM.Muted,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )
        }
    }
}

@Composable
private fun Heading(titleRes: Int, sub: String) {
    Column(Modifier.fillMaxWidth().padding(bottom = 20.dp)) {
        Text(stringResource(titleRes), style = MaterialTheme.typography.headlineMedium, color = JM.Text)
        Text(
            sub,
            style = MaterialTheme.typography.bodyMedium,
            color = JM.Muted,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ErrorLine(res: Int?) {
    if (res != null) {
        Text(
            stringResource(res),
            color = JM.Danger,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 4.dp)
        )
    }
}

@Composable
private fun LoadingButton(text: String, enabled: Boolean, loading: Boolean, onClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        GoldButton(text, onClick = onClick, modifier = Modifier.fillMaxWidth(), enabled = enabled)
        if (loading) {
            CircularProgressIndicator(color = JM.Gold, strokeWidth = 2.5.dp, modifier = Modifier.size(24.dp))
        }
    }
}
