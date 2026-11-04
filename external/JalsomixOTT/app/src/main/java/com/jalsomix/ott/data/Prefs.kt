package com.jalsomix.ott.data

import android.content.Context
import android.os.Build
import android.provider.Settings
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.jalsomix.ott.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "jalsomix_prefs")

/**
 * Local persistence: session, language, subscription, devices, downloads and My List.
 *
 * NOTE: mobile-number login is stored on-device (demo) so the app works instantly.
 * To go live, verify the OTP with Firebase Phone Auth / MSG91 / your API and keep the
 * device list on your server — the UI only depends on [AuthResult].
 */
class Prefs(private val context: Context) {

    private companion object {
        const val SEP = "\u001F"
        val K_LANG_CHOSEN = booleanPreferencesKey("lang_chosen")
        val K_UI_LANG = stringPreferencesKey("ui_lang")
        val K_CONTENT_LANGS = stringSetPreferencesKey("content_langs")
        val K_LOGGED = booleanPreferencesKey("logged_in")
        val K_NAME = stringPreferencesKey("user_name")
        val K_PHONE = stringPreferencesKey("user_phone")
        val K_USERS = stringSetPreferencesKey("users")
        val K_PLAN = stringPreferencesKey("plan_id")
        val K_EXPIRY = longPreferencesKey("plan_expiry")
        val K_LIST = stringSetPreferencesKey("my_list")
        val K_EMAIL = stringPreferencesKey("user_email")
        val K_AVATAR_COLOR = intPreferencesKey("avatar_color")
        val K_AVATAR_PHOTO = stringPreferencesKey("avatar_photo")
        val K_DOB = stringPreferencesKey("user_dob")
        val K_GENDER = stringPreferencesKey("user_gender")
        val K_CITY = stringPreferencesKey("user_city")
        val K_DEVICES = stringSetPreferencesKey("devices")
        val K_DOWNLOADS = stringSetPreferencesKey("downloads")
    }

    val deviceId: String by lazy {
        Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID) ?: "unknown"
    }
    val deviceModel: String = "${Build.MANUFACTURER.replaceFirstChar { it.uppercase() }} ${Build.MODEL}"

    val state: Flow<AppState> = context.dataStore.data.map { p: Preferences ->
        AppState(
            loaded = true,
            languageChosen = p[K_LANG_CHOSEN] ?: false,
            uiLang = (p[K_UI_LANG] ?: "en").let { if (it == "hi") "hi" else "en" },
            contentLangs = (p[K_CONTENT_LANGS] ?: SampleData.contentLanguages.toSet())
                .intersect(SampleData.contentLanguages.toSet())
                .ifEmpty { SampleData.contentLanguages.toSet() },
            session = UserSession(
                loggedIn = p[K_LOGGED] ?: false,
                name = p[K_NAME] ?: "",
                phone = p[K_PHONE] ?: "",
                email = p[K_EMAIL] ?: "",
                avatarColor = p[K_AVATAR_COLOR] ?: 0,
                avatarPhoto = p[K_AVATAR_PHOTO] ?: "",
                dob = p[K_DOB] ?: "",
                gender = p[K_GENDER] ?: "",
                city = p[K_CITY] ?: ""
            ),
            subscription = Subscription(p[K_PLAN] ?: "", p[K_EXPIRY] ?: 0L),
            myList = p[K_LIST] ?: emptySet(),
            devices = (p[K_DEVICES] ?: emptySet()).mapNotNull {
                val x = it.split(SEP)
                if (x.size == 3) DeviceInfo(x[0], x[1], x[2].toLongOrNull() ?: 0L) else null
            }.sortedBy { it.addedAt },
            downloads = (p[K_DOWNLOADS] ?: emptySet()).mapNotNull {
                val x = it.split(SEP)
                if (x.size == 3) DownloadRecord(x[0], x[1].toIntOrNull() ?: 0, x[2].toLongOrNull() ?: -1L) else null
            }
        )
    }

    suspend fun saveUiLanguage(uiLang: String) {
        context.dataStore.edit {
            it[K_LANG_CHOSEN] = true
            it[K_UI_LANG] = uiLang
        }
    }

    suspend fun saveContentLanguages(langs: Set<String>) {
        context.dataStore.edit { it[K_CONTENT_LANGS] = langs }
    }

    /**
     * Called after the OTP was verified. Logs the user in, or – for a new number –
     * returns [AuthResult.NeedName] until a [name] is supplied.
     */
    suspend fun completePhoneAuth(phone: String, name: String, email: String): AuthResult {
        var result: AuthResult = AuthResult.Success
        val now = System.currentTimeMillis()
        context.dataStore.edit { p ->
            val users = p[K_USERS] ?: emptySet()
            val existing = users.map { it.split(SEP) }.firstOrNull { it.size >= 2 && it[0] == phone }
            if (existing == null && (name.isBlank() || email.isBlank())) {
                result = AuthResult.NeedName
            } else {
                val devices = p[K_DEVICES] ?: emptySet()
                val planId = p[K_PLAN] ?: ""
                val active = planId.isNotEmpty() && (p[K_EXPIRY] ?: 0L) > now
                val limit = if (active) SampleData.planById(planId)?.screens ?: 1 else 1
                val already = devices.any { it.startsWith(deviceId + SEP) }
                if (!already && devices.size >= limit) {
                    result = AuthResult.Error(R.string.err_device_limit)
                } else {
                    val finalName = existing?.get(1) ?: name.trim()
                    val finalEmail = existing?.getOrNull(2) ?: email.trim().lowercase()
                    if (existing == null) p[K_USERS] = users + listOf(phone, finalName, finalEmail).joinToString(SEP)
                    p[K_EMAIL] = finalEmail
                    p[K_LOGGED] = true
                    p[K_NAME] = finalName
                    p[K_PHONE] = phone
                    if (!already) {
                        p[K_DEVICES] = devices + listOf(deviceId, deviceModel, now.toString()).joinToString(SEP)
                    }
                }
            }
        }
        return result
    }

    suspend fun logout() {
        context.dataStore.edit {
            it[K_LOGGED] = false
            it[K_NAME] = ""
            it[K_PHONE] = ""
            it[K_EMAIL] = ""
            it[K_DOB] = ""
            it[K_GENDER] = ""
            it[K_CITY] = ""
            it[K_DEVICES] = (it[K_DEVICES] ?: emptySet()).filterNot { d -> d.startsWith(deviceId + SEP) }.toSet()
        }
    }

    suspend fun updateProfile(
        name: String, email: String, avatarColor: Int, avatarPhoto: String,
        dob: String, gender: String, city: String
    ) {
        context.dataStore.edit { p ->
            val phone = p[K_PHONE] ?: ""
            p[K_NAME] = name
            p[K_EMAIL] = email
            p[K_AVATAR_COLOR] = avatarColor
            p[K_AVATAR_PHOTO] = avatarPhoto
            p[K_DOB] = dob
            p[K_GENDER] = gender
            p[K_CITY] = city
            p[K_USERS] = (p[K_USERS] ?: emptySet()).map { u ->
                if (u.startsWith(phone + SEP)) listOf(phone, name, email).joinToString(SEP) else u
            }.toSet()
        }
    }

    suspend fun removeDevice(id: String) {
        context.dataStore.edit {
            it[K_DEVICES] = (it[K_DEVICES] ?: emptySet()).filterNot { d -> d.startsWith(id + SEP) }.toSet()
        }
    }

    suspend fun subscribe(plan: Plan) {
        context.dataStore.edit {
            it[K_PLAN] = plan.id
            it[K_EXPIRY] = System.currentTimeMillis() + plan.days * 24L * 60 * 60 * 1000
        }
    }

    suspend fun toggleList(titleId: String) {
        context.dataStore.edit {
            val cur = it[K_LIST] ?: emptySet()
            it[K_LIST] = if (titleId in cur) cur - titleId else cur + titleId
        }
    }

    suspend fun addDownload(rec: DownloadRecord) {
        context.dataStore.edit {
            val cur = (it[K_DOWNLOADS] ?: emptySet()).filterNot { d -> d.startsWith(rec.titleId + SEP + rec.episode + SEP) }
            it[K_DOWNLOADS] = (cur + listOf(rec.titleId, rec.episode.toString(), rec.downloadId.toString()).joinToString(SEP)).toSet()
        }
    }

    suspend fun removeDownload(titleId: String, episode: Int) {
        context.dataStore.edit {
            it[K_DOWNLOADS] = (it[K_DOWNLOADS] ?: emptySet())
                .filterNot { d -> d.startsWith(titleId + SEP + episode + SEP) }.toSet()
        }
    }
}
