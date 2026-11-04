package com.jalsomix.ott.data

import androidx.annotation.StringRes

enum class ContentType { SHORT_DRAMA, WEB_SERIES, MOVIE }

data class Episode(
    val index: Int,
    val title: String,
    val durationMin: Int,
    val videoUrl: String
)

data class Title(
    val id: String,
    val name: String,
    val type: ContentType,
    val language: String,
    val genres: List<String>,
    val year: Int,
    val rating: Double,
    val ageRating: String,
    val synopsis: String,
    val premium: Boolean,
    /** For premium titles: how many first episodes are free to watch. */
    val freeEpisodes: Int,
    val episodes: List<Episode>,
    val hue: Int,
    val trending: Boolean = false
) {
    val posterUrl get() = "https://picsum.photos/seed/jm$id/400/600"
    val heroUrl get() = "https://picsum.photos/seed/jmh$id/800/1100"
    val bannerUrl get() = "https://picsum.photos/seed/jmb$id/900/520"
}

data class Plan(
    val id: String,
    val name: String,
    val priceRs: Int,
    val days: Int,
    val yearly: Boolean,
    val quality: String,
    /** Number of devices that can be logged in at the same time. */
    val screens: Int,
    val popular: Boolean = false,
    val bestValue: Boolean = false
)

data class UserSession(
    val loggedIn: Boolean = false,
    val name: String = "",
    /** 10-digit Indian mobile number (without +91). */
    val phone: String = "",
    val email: String = "",
    val avatarColor: Int = 0,
    /** Absolute path of the profile photo in app storage ("" = none). */
    val avatarPhoto: String = "",
    val dob: String = "",
    val gender: String = "",
    val city: String = ""
)

data class DeviceInfo(val id: String, val model: String, val addedAt: Long)

data class DownloadRecord(val titleId: String, val episode: Int, val downloadId: Long)

data class Subscription(
    val planId: String = "",
    val expiryMillis: Long = 0L
) {
    val active: Boolean get() = planId.isNotEmpty() && expiryMillis > System.currentTimeMillis()
}

data class AppState(
    val loaded: Boolean = false,
    val languageChosen: Boolean = false,
    val uiLang: String = "en",
    val contentLangs: Set<String> = SampleData.contentLanguages.toSet(),
    val session: UserSession = UserSession(),
    val subscription: Subscription = Subscription(),
    val myList: Set<String> = emptySet(),
    val devices: List<DeviceInfo> = emptyList(),
    val downloads: List<DownloadRecord> = emptyList()
)

sealed class AuthResult {
    data object Success : AuthResult()
    /** Mobile number is new – ask for the user's name to finish sign up. */
    data object NeedName : AuthResult()
    data class Error(@StringRes val message: Int) : AuthResult()
}

/** Edit these with your real support details. */
object Support {
    const val EMAIL = "jalsomixott@gmail.com"
    const val PHONE = "+910000000000"
    const val WHATSAPP = "910000000000"
}

/** Whether the user may watch episode [epIndex] of title [t]. */
fun canWatch(t: Title, epIndex: Int, sub: Subscription): Boolean =
    !t.premium || epIndex < t.freeEpisodes || sub.active

/** How many devices may be logged in at once. */
fun deviceLimit(sub: Subscription): Int =
    if (sub.active) SampleData.planById(sub.planId)?.screens ?: 1 else 1
