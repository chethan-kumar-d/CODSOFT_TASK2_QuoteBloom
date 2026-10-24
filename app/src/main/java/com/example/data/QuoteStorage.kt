package com.example.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

data class StoredPreferencesState(
    val favouriteIds: Set<String> = emptySet(),
    val themePreference: ThemePreference = ThemePreference.LIGHT,
    val cardBackground: QuoteCardBackground = QuoteCardBackground.WARM_IVORY,
    val reducedMotion: Boolean = false,
    val cachedDailyDateKey: String? = null,
    val cachedDailyQuoteId: String? = null
)

interface QuoteStorage {
    fun loadState(): StoredPreferencesState
    fun saveFavourites(favouriteIds: Set<String>)
    fun saveThemePreference(themePreference: ThemePreference)
    fun saveCardBackground(cardBackground: QuoteCardBackground)
    fun saveReducedMotion(reducedMotion: Boolean)
    fun saveDailyQuoteRecord(dateKey: String, quoteId: String)
}

/**
 * Local storage implementation that gracefully handles malformed JSON/corrupted preferences
 * and falls back to an in-memory map if SharedPreferences is unavailable or throws an exception.
 */
class ResilientQuoteStorage(
    private val sharedPreferencesProvider: () -> SharedPreferences?
) : QuoteStorage {

    constructor(context: Context) : this({
        try {
            context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        } catch (_: Throwable) {
            null
        }
    })

    private val memoryFallback = mutableMapOf<String, String>()
    private val validQuoteIds: Set<String> = QuoteCatalog.quotes.map { it.id }.toSet()

    override fun loadState(): StoredPreferencesState {
        val rawFavourites = readString(KEY_FAVOURITES_JSON)
        val parsedFavourites = parseFavouritesSafely(rawFavourites)

        val rawPrefs = readString(KEY_APPEARANCE_JSON)
        val (theme, background, reducedMotion) = parseAppearanceSafely(rawPrefs)

        val rawDaily = readString(KEY_DAILY_RECORD_JSON)
        val (dailyDate, dailyQuoteId) = parseDailyRecordSafely(rawDaily)

        return StoredPreferencesState(
            favouriteIds = parsedFavourites,
            themePreference = theme,
            cardBackground = background,
            reducedMotion = reducedMotion,
            cachedDailyDateKey = dailyDate,
            cachedDailyQuoteId = dailyQuoteId
        )
    }

    override fun saveFavourites(favouriteIds: Set<String>) {
        val sanitized = favouriteIds.filter { it in validQuoteIds }
        val jsonArray = JSONArray()
        sanitized.forEach { jsonArray.put(it) }
        writeString(KEY_FAVOURITES_JSON, jsonArray.toString())
    }

    override fun saveThemePreference(themePreference: ThemePreference) {
        val current = loadState()
        writeAppearanceJson(themePreference, current.cardBackground, current.reducedMotion)
    }

    override fun saveCardBackground(cardBackground: QuoteCardBackground) {
        val current = loadState()
        writeAppearanceJson(current.themePreference, cardBackground, current.reducedMotion)
    }

    override fun saveReducedMotion(reducedMotion: Boolean) {
        val current = loadState()
        writeAppearanceJson(current.themePreference, current.cardBackground, reducedMotion)
    }

    override fun saveDailyQuoteRecord(dateKey: String, quoteId: String) {
        try {
            val obj = JSONObject().apply {
                put("dateKey", dateKey)
                put("quoteId", quoteId)
            }
            writeString(KEY_DAILY_RECORD_JSON, obj.toString())
        } catch (_: Throwable) {
            // Ignore serialization issues
        }
    }

    private fun writeAppearanceJson(
        theme: ThemePreference,
        background: QuoteCardBackground,
        reducedMotion: Boolean
    ) {
        try {
            val obj = JSONObject().apply {
                put("theme", theme.name)
                put("cardBackground", background.id)
                put("reducedMotion", reducedMotion)
            }
            writeString(KEY_APPEARANCE_JSON, obj.toString())
        } catch (_: Throwable) {
            // Ignore serialization issues
        }
    }

    /**
     * Exposed internal helper for testing resilience against corrupted or malformed data strings.
     */
    fun writeRawForTesting(key: String, rawValue: String?) {
        writeString(key, rawValue)
    }

    private fun parseFavouritesSafely(raw: String?): Set<String> {
        if (raw.isNullOrBlank()) return emptySet()
        return try {
            val trimmed = raw.trim()
            if (trimmed.startsWith("[")) {
                val arr = JSONArray(trimmed)
                val result = linkedSetOf<String>()
                for (i in 0 until arr.length()) {
                    val item = arr.optString(i, "").trim()
                    if (item.isNotEmpty() && item in validQuoteIds) {
                        result.add(item)
                    }
                }
                result
            } else {
                // Fallback if stored as comma-separated string
                trimmed.split(",")
                    .map { it.trim() }
                    .filter { it in validQuoteIds }
                    .toSet()
            }
        } catch (_: Throwable) {
            emptySet()
        }
    }

    private fun parseAppearanceSafely(raw: String?): Triple<ThemePreference, QuoteCardBackground, Boolean> {
        if (raw.isNullOrBlank()) {
            return Triple(ThemePreference.LIGHT, QuoteCardBackground.WARM_IVORY, false)
        }
        return try {
            val obj = JSONObject(raw)
            val theme = ThemePreference.fromString(obj.optString("theme", ThemePreference.LIGHT.name))
            val bg = QuoteCardBackground.fromId(obj.optString("cardBackground", QuoteCardBackground.WARM_IVORY.id))
            val reducedMotion = obj.optBoolean("reducedMotion", false)
            Triple(theme, bg, reducedMotion)
        } catch (_: Throwable) {
            Triple(ThemePreference.LIGHT, QuoteCardBackground.WARM_IVORY, false)
        }
    }

    private fun parseDailyRecordSafely(raw: String?): Pair<String?, String?> {
        if (raw.isNullOrBlank()) return null to null
        return try {
            val obj = JSONObject(raw)
            val dateKey = obj.optString("dateKey", "").trim().takeIf { it.matches(Regex("\\d{4}-\\d{2}-\\d{2}")) }
            val quoteId = obj.optString("quoteId", "").trim().takeIf { it in validQuoteIds }
            if (dateKey != null && quoteId != null) {
                dateKey to quoteId
            } else {
                null to null
            }
        } catch (_: Throwable) {
            null to null
        }
    }

    private fun readString(key: String): String? {
        return try {
            val prefs = sharedPreferencesProvider()
            if (prefs != null) {
                prefs.getString(key, memoryFallback[key])
            } else {
                memoryFallback[key]
            }
        } catch (_: Throwable) {
            memoryFallback[key]
        }
    }

    private fun writeString(key: String, value: String?) {
        if (value == null) {
            memoryFallback.remove(key)
        } else {
            memoryFallback[key] = value
        }
        try {
            val prefs = sharedPreferencesProvider()
            if (prefs != null) {
                val editor = prefs.edit()
                if (value == null) {
                    editor.remove(key)
                } else {
                    editor.putString(key, value)
                }
                editor.apply()
            }
        } catch (_: Throwable) {
            // Gracefully rely on memoryFallback if disk write fails
        }
    }

    companion object {
        const val PREFS_NAME = "quotebloom_local_prefs"
        const val KEY_FAVOURITES_JSON = "quotebloom_favourites_v1"
        const val KEY_APPEARANCE_JSON = "quotebloom_appearance_v1"
        const val KEY_DAILY_RECORD_JSON = "quotebloom_daily_record_v1"
    }
}
