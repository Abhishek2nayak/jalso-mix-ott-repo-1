package com.jalsomix.ott.ui.screens

import android.app.DatePickerDialog
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.ui.Avatar
import com.jalsomix.ott.ui.IosGroup
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.IosRow
import com.jalsomix.ott.ui.IosSegmented
import com.jalsomix.ott.ui.avatarBrush
import com.jalsomix.ott.ui.avatarColorCount
import com.jalsomix.ott.ui.theme.JM
import java.util.Calendar

/** Edit name, profile photo and avatar colour. The mobile number is read-only. */
@Composable
fun EditProfileScreen(state: AppState, vm: AppViewModel, onBack: () -> Unit) {
    var name by rememberSaveable { mutableStateOf(state.session.name) }
    var email by rememberSaveable { mutableStateOf(state.session.email) }
    var color by rememberSaveable { mutableIntStateOf(state.session.avatarColor) }
    var photo by rememberSaveable { mutableStateOf(state.session.avatarPhoto) }
    var dob by rememberSaveable { mutableStateOf(state.session.dob) }
    var gender by rememberSaveable { mutableStateOf(state.session.gender) }
    var city by rememberSaveable { mutableStateOf(state.session.city) }
    val ctx = LocalContext.current
    val genderCodes = listOf("M", "F", "O")
    val genderLabels = listOf(
        stringResource(R.string.gender_male),
        stringResource(R.string.gender_female),
        stringResource(R.string.gender_other)
    )
    val pickDob = {
        val c = Calendar.getInstance()
        val parts = dob.split("/")
        if (parts.size == 3) {
            c.set(parts[2].toIntOrNull() ?: c.get(Calendar.YEAR), (parts[1].toIntOrNull() ?: 1) - 1, parts[0].toIntOrNull() ?: 1)
        } else {
            c.add(Calendar.YEAR, -20)
        }
        val dlg = DatePickerDialog(
            ctx,
            android.R.style.Theme_DeviceDefault_Dialog_Alert,
            { _, y, m, d -> dob = "%02d/%02d/%04d".format(d, m + 1, y) },
            c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        )
        dlg.datePicker.maxDate = System.currentTimeMillis()
        dlg.show()
    }

    val picker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) vm.savePhoto(uri) { photo = it }
    }
    val preview = state.session.copy(name = name.ifBlank { "J" }, avatarColor = color, avatarPhoto = photo)
    val valid = name.isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()
    val phone = state.session.phone.let { if (it.length == 10) "+91 ${it.take(5)} ${it.drop(5)}" else it }

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.profile_edit), onBack) {
            Text(
                stringResource(R.string.btn_save),
                style = MaterialTheme.typography.titleMedium,
                color = if (valid) JM.Gold else JM.Muted,
                modifier = Modifier.clickable(enabled = valid) {
                    vm.updateProfile(name.trim(), email.trim(), color, photo, dob, gender, city.trim())
                    onBack()
                }
            )
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(bottom = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            Avatar(preview, 104.dp)
            Spacer(Modifier.height(12.dp))
            Text(
                stringResource(R.string.edit_photo),
                style = MaterialTheme.typography.titleMedium,
                color = JM.Gold,
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable {
                        picker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )
            if (photo.isNotEmpty()) {
                Text(
                    stringResource(R.string.edit_photo_remove),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Danger,
                    modifier = Modifier
                        .clip(CircleShape)
                        .clickable { photo = "" }
                        .padding(horizontal = 14.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(22.dp))
            IosGroup(footer = stringResource(R.string.edit_phone_note)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it.take(40) },
                        singleLine = true,
                        textStyle = TextStyle(color = JM.Text, fontSize = 17.sp),
                        cursorBrush = SolidColor(JM.Gold),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (name.isEmpty()) {
                                Text(stringResource(R.string.field_name), style = TextStyle(fontSize = 17.sp), color = JM.Muted)
                            }
                            inner()
                        }
                    )
                }
                Box(
                    Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(JM.Outline)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    BasicTextField(
                        value = email,
                        onValueChange = { email = it.trim().take(60) },
                        singleLine = true,
                        textStyle = TextStyle(color = JM.Text, fontSize = 17.sp),
                        cursorBrush = SolidColor(JM.Gold),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (email.isEmpty()) {
                                Text(stringResource(R.string.field_email), style = TextStyle(fontSize = 17.sp), color = JM.Muted)
                            }
                            inner()
                        }
                    )
                }
                Box(
                    Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(JM.Outline)
                )
                IosRow(stringResource(R.string.field_mobile), value = phone, chevron = false, divider = false)
            }

            Spacer(Modifier.height(24.dp))
            IosGroup(header = stringResource(R.string.edit_about)) {
                IosRow(
                    stringResource(R.string.field_dob),
                    value = dob.ifEmpty { stringResource(R.string.dob_select) },
                    onClick = { pickDob() }
                )
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(stringResource(R.string.field_gender), style = MaterialTheme.typography.bodySmall, color = JM.Muted)
                    Spacer(Modifier.height(6.dp))
                    IosSegmented(
                        genderLabels,
                        genderCodes.indexOf(gender),
                        { i -> gender = genderCodes[i] }
                    )
                }
                Box(
                    Modifier
                        .padding(start = 16.dp)
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(JM.Outline)
                )
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    BasicTextField(
                        value = city,
                        onValueChange = { city = it.take(40) },
                        singleLine = true,
                        textStyle = TextStyle(color = JM.Text, fontSize = 17.sp),
                        cursorBrush = SolidColor(JM.Gold),
                        modifier = Modifier.fillMaxWidth(),
                        decorationBox = { inner ->
                            if (city.isEmpty()) {
                                Text(stringResource(R.string.field_city), style = TextStyle(fontSize = 17.sp), color = JM.Muted)
                            }
                            inner()
                        }
                    )
                }
            }

            if (photo.isEmpty()) {
                Spacer(Modifier.height(24.dp))
                IosGroup(header = stringResource(R.string.edit_color)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        repeat(avatarColorCount) { i ->
                            Box(
                                Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(avatarBrush(i))
                                    .then(if (i == color) Modifier.border(2.dp, Color.White, CircleShape) else Modifier)
                                    .clickable { color = i },
                                contentAlignment = Alignment.Center
                            ) {
                                if (i == color) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
