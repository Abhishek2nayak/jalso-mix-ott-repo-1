package com.jalsomix.ott.data

/**
 * Demo catalogue. Replace with your real backend (REST / Firebase / Supabase) later —
 * every screen only depends on [Title] and [Episode].
 * Videos are Google's public sample streams so playback works out of the box.
 */
object SampleData {

    private val videos = listOf(
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerFun.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerJoyrides.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerMeltdowns.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/Sintel.mp4",
        "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/TearsOfSteel.mp4"
    )

    /** Content languages offered inside the app. */
    val contentLanguages = listOf("Hindi", "Gujarati")

    val plans = listOf(
        Plan("basic", "Basic", 49, 30, yearly = false, quality = "480p", screens = 1),
        Plan("premium", "Premium", 149, 30, yearly = false, quality = "1080p HD", screens = 2, popular = true),
        Plan("vip", "VIP Annual", 999, 365, yearly = true, quality = "4K Ultra HD", screens = 4, bestValue = true)
    )

    private fun eps(count: Int, minutes: Int, seed: Int, label: String = ""): List<Episode> =
        (0 until count).map { i ->
            Episode(
                index = i,
                title = if (label.isEmpty()) "Episode ${i + 1}" else label,
                durationMin = minutes,
                videoUrl = videos[(seed + i) % videos.size]
            )
        }

    private fun drama(
        id: String, name: String, lang: String, genres: List<String>, year: Int, rating: Double,
        synopsis: String, hue: Int, episodes: Int, free: Int, premium: Boolean = true,
        trending: Boolean = false
    ) = Title(
        id, name, ContentType.SHORT_DRAMA, lang, genres, year, rating, "16+", synopsis,
        premium, free, eps(episodes, 2, id.hashCode().and(0xff)), hue, trending
    )

    private fun series(
        id: String, name: String, lang: String, genres: List<String>, year: Int, rating: Double,
        synopsis: String, hue: Int, episodes: Int, free: Int, age: String = "18+",
        premium: Boolean = true, trending: Boolean = false
    ) = Title(
        id, name, ContentType.WEB_SERIES, lang, genres, year, rating, age, synopsis,
        premium, free, eps(episodes, 38, id.hashCode().and(0xff)), hue, trending
    )

    private fun movie(
        id: String, name: String, lang: String, genres: List<String>, year: Int, rating: Double,
        synopsis: String, hue: Int, minutes: Int, age: String = "U/A 13+",
        premium: Boolean = false, trending: Boolean = false
    ) = Title(
        id, name, ContentType.MOVIE, lang, genres, year, rating, age, synopsis,
        premium, 0, eps(1, minutes, id.hashCode().and(0xff), label = name), hue, trending
    )

    val titles: List<Title> = listOf(
        // ---------- Short dramas ----------
        drama("d1", "Mohabbat Ka Contract", "Hindi", listOf("Romance", "Drama"), 2026, 4.7,
            "Ek bossy CEO aur ek simple si ladki ke beech hui ek ajeeb si contract marriage — kya ye sauda pyaar mein badlega?",
            340, 24, 3, trending = true),
        drama("d2", "CEO Ki Dulhan", "Hindi", listOf("Romance", "Family"), 2026, 4.5,
            "Ek galat-fehmi ki wajah se ek garib ladki ban jaati hai shehar ke sabse ameer parivaar ki bahu.",
            12, 30, 4, trending = true),
        drama("d3", "Badla: The Revenge Bride", "Hindi", listOf("Revenge", "Thriller"), 2026, 4.6,
            "Dulhan ke libaas mein chhupa ek purana zakhm. Ek shaadi, ek saazish aur ek badla.",
            0, 20, 3),
        drama("d4", "Chhoti Si Zindagi", "Hindi", listOf("Family", "Emotional"), 2025, 4.4,
            "Chhote shehar ki ek maa apne sapno aur parivaar ke beech apna raasta dhoondti hai.",
            45, 18, 5, premium = false),
        drama("d5", "Kismat Connection", "Hindi", listOf("Romance", "Comedy"), 2026, 4.3,
            "Ek wrong-number call aur do ajnabiyon ki ek pyaari si kahani.",
            300, 22, 3, trending = true),
        drama("d6", "Shadi Ka Twist", "Hindi", listOf("Comedy", "Family"), 2025, 4.2,
            "Mandap tak pahunchne se pehle hi shadi mein aa gaye saat bade twist!",
            28, 16, 4, premium = false),
        drama("d7", "Amdavad Ni Prem", "Gujarati", listOf("Romance", "Drama"), 2026, 4.5,
            "Purano Amdavad ni poles ma janmi ek navi prem kahani.",
            200, 26, 3),
        drama("d8", "Maharani Returns", "Gujarati", listOf("Action", "Drama"), 2026, 4.6,
            "Ek aurat jo sab kuch kho kar wapas aati hai apna samrajya wapas lene.",
            355, 28, 3, trending = true),
        drama("d9", "Gharwali Baharwali", "Hindi", listOf("Comedy", "Family"), 2025, 4.0,
            "Do aurten, ek ghar aur ek pati — masti, jhagde aur dher saara pyaar.",
            60, 20, 5, premium = false),

        // ---------- Web series ----------
        series("s1", "Raat Ke Raaz", "Hindi", listOf("Thriller", "Mystery"), 2026, 4.8,
            "Ek hi raat mein shehar ke paanch log gayab. Ek inspector, ek raaz aur kayi jhoot.",
            270, 8, 1, trending = true),
        series("s2", "Kaali Ghati", "Hindi", listOf("Horror", "Mystery"), 2026, 4.4,
            "Pahadon mein basi ek purani haveli, jahan se koi laut kar nahi aaya.",
            0, 6, 1),
        series("s3", "Sultan of Sector 9", "Hindi", listOf("Crime", "Drama"), 2025, 4.7,
            "Ek chhote gunde ke ek bade don banne ki kahani — sector 9 ka asli sultan kaun?",
            25, 10, 2, trending = true),
        series("s4", "Aakhri Broadcast", "Hindi", listOf("Sci-Fi", "Thriller"), 2026, 4.6,
            "Ek radio host ko future se ek signal milta hai — aur uska sirf ek paigaam hai.",
            215, 8, 2),
        series("s5", "Mumbai Midnight", "Hindi", listOf("Crime", "Thriller"), 2025, 4.5,
            "Jab shehar sota hai, tab ek cop aur ek chor ka khel shuru hota hai.",
            230, 9, 2, age = "18+"),
        series("s6", "Surat Nights", "Gujarati", listOf("Mystery", "Crime"), 2026, 4.5,
            "Ek purani photo, ek khoon aur Surat shehar ke chhupe hue raaz.",
            190, 7, 1),
        series("s7", "Kathiyawad Mafia", "Gujarati", listOf("Action", "Crime"), 2026, 4.6,
            "Kathiyawad ki galiyon mein ek gang ka raaj — aur ek naya chehra jo sab badalne aaya hai.",
            10, 10, 2, trending = true),
        series("s8", "Jaipur Heist", "Hindi", listOf("Action", "Comedy"), 2025, 4.3,
            "Paanch dost, ek bank aur ek plan jo kabhi plan ke mutabik nahi chalta.",
            50, 8, 3, age = "16+", premium = false),

        // ---------- Movies ----------
        movie("m1", "Jalsa Junction", "Hindi", listOf("Comedy", "Family"), 2026, 4.4,
            "Ek railway station, ek barat aur ek din jisme sab kuch galat ho sakta tha — ho gaya!",
            38, 132, trending = true),
        movie("m2", "Aakhri Baazi", "Hindi", listOf("Action", "Thriller"), 2026, 4.7,
            "Ek purana jasoos, ek aakhri mission aur ek aisi baazi jisme jeet ki koi guarantee nahi.",
            5, 148, age = "U/A 16+", premium = true, trending = true),
        movie("m3", "Silent Monsoon", "Hindi", listOf("Drama", "Romance"), 2025, 4.6,
            "Baarish ki ek raat do ajnabi, ek sheher aur ek adhoori si mohabbat.",
            205, 124, premium = true),
        movie("m4", "Neon Yodha", "Hindi", listOf("Action", "Sci-Fi"), 2026, 4.5,
            "Neon roshni mein nahaye mahanagar mein aakhri yodha ek aakhri contract leta hai.",
            280, 118, age = "U/A 16+", premium = true),
        movie("m5", "Pyaar Ki Raah", "Hindi", listOf("Romance", "Musical"), 2025, 4.3,
            "Ek road trip, gaane aur do dil jo manzil se pehle hi kho gaye.",
            330, 128),
        movie("m6", "Veer Vanraj", "Gujarati", listOf("Action", "Drama"), 2026, 4.6,
            "Ek seedha-saada gaon ka ladka jab hathiyar uthata hai, to poora shehar hil jaata hai.",
            15, 142, age = "U/A 16+", premium = true),
        movie("m7", "Gujarat Express", "Gujarati", listOf("Comedy", "Drama"), 2025, 4.2,
            "Ek lambi train yatra, ek anokhi sawari aur ek mazedaar safar.",
            90, 112),
        movie("m8", "Desi Puttar", "Hindi", listOf("Comedy", "Family"), 2026, 4.4,
            "Canada se aaya ek NRI aur uska desi parivaar — masti ka pura tadka.",
            48, 120, trending = true),
        movie("m9", "Gujju Bhai Ki Shaadi", "Gujarati", listOf("Comedy", "Family"), 2025, 4.1,
            "Ek vyapari ki beti ki shaadi aur ek hazaar chhote-bade drame.",
            35, 116)
    )

    fun byId(id: String): Title? = titles.firstOrNull { it.id == id }

    fun planById(id: String): Plan? = plans.firstOrNull { it.id == id }

    fun byType(type: ContentType) = titles.filter { it.type == type }
}

/** Puts titles in the user's chosen content languages first (keeps relative order otherwise). */
fun List<Title>.preferLangs(langs: Set<String>): List<Title> =
    if (langs.isEmpty()) this else sortedByDescending { it.language in langs }

/** Keeps only titles in the user's chosen content languages (empty selection = everything). */
fun List<Title>.forLangs(langs: Set<String>): List<Title> =
    if (langs.isEmpty()) this else filter { it.language in langs }
