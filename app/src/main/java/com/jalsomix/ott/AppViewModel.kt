package com.jalsomix.ott

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.AuthResult
import com.jalsomix.ott.data.DownloadRecord
import com.jalsomix.ott.data.Downloads
import com.jalsomix.ott.data.Episode
import com.jalsomix.ott.data.Plan
import com.jalsomix.ott.data.Prefs
import com.jalsomix.ott.data.Title
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(private val app: Application) : AndroidViewModel(app) {

    private val prefs = Prefs(app)

    val deviceId: String get() = prefs.deviceId

    val state: StateFlow<AppState> =
        prefs.state.stateIn(viewModelScope, SharingStarted.Eagerly, AppState())

    fun saveUiLanguage(uiLang: String, done: () -> Unit) {
        viewModelScope.launch {
            prefs.saveUiLanguage(uiLang)
            done()
        }
    }

    fun saveContentLanguages(langs: Set<String>) {
        viewModelScope.launch { prefs.saveContentLanguages(langs) }
    }

    /**
     * Demo OTP check. Replace with Firebase Phone Auth / MSG91 / your backend:
     * send the OTP to [phone] and verify it on the server.
     */
    fun verifyOtp(otp: String): Boolean = otp == DEMO_OTP

    fun completeAuth(phone: String, name: String, email: String, onResult: (AuthResult) -> Unit) {
        viewModelScope.launch { onResult(prefs.completePhoneAuth(phone, name, email)) }
    }

    fun logout(done: () -> Unit) {
        viewModelScope.launch {
            prefs.logout()
            done()
        }
    }

    fun updateProfile(
        name: String, email: String, avatarColor: Int, avatarPhoto: String,
        dob: String, gender: String, city: String
    ) {
        viewModelScope.launch { prefs.updateProfile(name, email, avatarColor, avatarPhoto, dob, gender, city) }
    }

    /** Copies a picked image into app storage and returns its path through [done]. */
    fun savePhoto(uri: Uri, done: (String) -> Unit) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                runCatching {
                    val out = File(app.filesDir, "avatar_${System.currentTimeMillis()}.jpg")
                    app.contentResolver.openInputStream(uri)?.use { input ->
                        out.outputStream().use { input.copyTo(it) }
                    }
                    out.absolutePath
                }.getOrNull()
            }
            if (path != null) done(path)
        }
    }

    fun removeDevice(id: String) {
        viewModelScope.launch { prefs.removeDevice(id) }
    }

    /**
     * Demo checkout. Replace the delay with Google Play Billing / Razorpay / Cashfree,
     * and only call [Prefs.subscribe] after the payment is verified on your server.
     */
    fun purchase(plan: Plan, done: () -> Unit) {
        viewModelScope.launch {
            delay(1600)
            prefs.subscribe(plan)
            done()
        }
    }

    fun toggleList(titleId: String) {
        viewModelScope.launch { prefs.toggleList(titleId) }
    }

    fun startDownload(t: Title, ep: Episode) {
        viewModelScope.launch {
            val id = Downloads.enqueue(app, t, ep)
            prefs.addDownload(DownloadRecord(t.id, ep.index, id))
        }
    }

    fun deleteDownload(rec: DownloadRecord) {
        viewModelScope.launch {
            Downloads.remove(app, rec)
            prefs.removeDownload(rec.titleId, rec.episode)
        }
    }

    companion object {
        const val DEMO_OTP = "123456"
    }
}
