package com.example.ui

import com.example.data.DailyQuoteSelector
import com.example.data.Quote
import com.example.data.QuoteCardBackground
import com.example.data.QuoteCatalog
import com.example.data.QuoteCategory
import com.example.data.QuoteStorage
import com.example.data.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Locale

enum class QuoteBloomTab(val route: String) {
    TODAY("today"),
    EXPLORE("explore"),
    SAVED("saved"),
    APPEARANCE("appearance")
}

data class QuoteBloomUiState(
    val activeTab: QuoteBloomTab = QuoteBloomTab.TODAY,
    val localDateKey: String = "",
    val readableLocalDate: String = "",
    val dailyQuote: Quote = QuoteCatalog.quotes.first(),
    val discoveredQuote: Quote? = null,
    val recentDiscoveryIds: List<String> = emptyList(),
    val favouriteIds: Set<String> = emptySet(),
    val exploreSearchQuery: String = "",
    val exploreSelectedCategory: QuoteCategory? = null,
    val savedSearchQuery: String = "",
    val themePreference: ThemePreference = ThemePreference.LIGHT,
    val cardBackground: QuoteCardBackground = QuoteCardBackground.WARM_IVORY,
    val reducedMotion: Boolean = false,
    val feedbackMessage: String? = null
) {
    val activeHomeQuote: Quote
        get() = discoveredQuote ?: dailyQuote

    val isShowingDiscoveredQuote: Boolean
        get() = discoveredQuote != null

    val filteredExploreQuotes: List<Quote>
        get() = DailyQuoteSelector.filterQuotes(
            quotes = QuoteCatalog.quotes,
            searchQuery = exploreSearchQuery,
            selectedCategory = exploreSelectedCategory
        )

    val savedQuotes: List<Quote>
        get() = QuoteCatalog.quotes.filter { it.id in favouriteIds }

    val filteredSavedQuotes: List<Quote>
        get() = DailyQuoteSelector.filterQuotes(
            quotes = savedQuotes,
            searchQuery = savedSearchQuery,
            selectedCategory = null
        )

    fun isFavourite(quoteId: String): Boolean = quoteId in favouriteIds
}

class QuoteBloomViewModel(
    private val storage: QuoteStorage,
    private val dateKeyProvider: () -> String = { DailyQuoteSelector.getLocalDateKey() }
) : androidx.lifecycle.ViewModel() {

    private val _uiState = MutableStateFlow( createInitialState() )
    val uiState: StateFlow<QuoteBloomUiState> = _uiState.asStateFlow()

    private fun createInitialState(): QuoteBloomUiState {
        val stored = storage.loadState()
        val todayKey = dateKeyProvider()
        val dailyQuote = resolveDailyQuoteForDate(todayKey, stored.cachedDailyDateKey, stored.cachedDailyQuoteId)
        storage.saveDailyQuoteRecord(todayKey, dailyQuote.id)

        return QuoteBloomUiState(
            activeTab = QuoteBloomTab.TODAY,
            localDateKey = todayKey,
            readableLocalDate = formatDateKeyForDisplay(todayKey),
            dailyQuote = dailyQuote,
            discoveredQuote = null,
            recentDiscoveryIds = emptyList(),
            favouriteIds = stored.favouriteIds,
            themePreference = stored.themePreference,
            cardBackground = stored.cardBackground,
            reducedMotion = stored.reducedMotion
        )
    }

    private fun resolveDailyQuoteForDate(
        todayKey: String,
        cachedDateKey: String?,
        cachedQuoteId: String?
    ): Quote {
        if (cachedDateKey == todayKey && !cachedQuoteId.isNullOrBlank()) {
            val match = QuoteCatalog.quotes.firstOrNull { it.id == cachedQuoteId }
            if (match != null) return match
        }
        return DailyQuoteSelector.selectDailyQuote(todayKey, QuoteCatalog.quotes)
    }

    /**
     * Checks whether the local calendar day has changed (e.g. when returning to the foreground or after midnight).
     * If the day changed, updates the daily quote and resets any temporary discovery view so the new daily quote shines.
     * If the day is the same, keeps the daily quote completely consistent.
     */
    fun checkAndRefreshDailyQuote(overrideDateKey: String? = null) {
        val currentKey = overrideDateKey ?: dateKeyProvider()
        val currentState = _uiState.value
        if (currentKey != currentState.localDateKey) {
            val nextDailyQuote = DailyQuoteSelector.selectDailyQuote(currentKey, QuoteCatalog.quotes)
            storage.saveDailyQuoteRecord(currentKey, nextDailyQuote.id)
            _uiState.update { state ->
                state.copy(
                    localDateKey = currentKey,
                    readableLocalDate = formatDateKeyForDisplay(currentKey),
                    dailyQuote = nextDailyQuote,
                    discoveredQuote = null,
                    recentDiscoveryIds = emptyList()
                )
            }
        }
    }

    fun selectTab(tab: QuoteBloomTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    /**
     * Selects a new discovered quote without replacing the stored Quote of the Day,
     * and guarantees no immediate repeat of the currently shown quote.
     */
    fun discoverAnotherQuote() {
        _uiState.update { state ->
            val currentShownId = state.activeHomeQuote.id
            val nextQuote = DailyQuoteSelector.selectNextDiscoveryQuote(
                currentDisplayedQuoteId = currentShownId,
                dailyQuoteId = state.dailyQuote.id,
                recentDiscoveryIds = state.recentDiscoveryIds,
                catalog = QuoteCatalog.quotes
            )
            val updatedHistory = (state.recentDiscoveryIds + nextQuote.id).takeLast(25)
            state.copy(
                discoveredQuote = nextQuote,
                recentDiscoveryIds = updatedHistory
            )
        }
    }

    /**
     * Returns from a discovered quote back to Today's consistent Quote of the Day.
     */
    fun returnToDailyQuote() {
        _uiState.update { it.copy(discoveredQuote = null) }
    }

    fun toggleFavourite(quoteId: String) {
        _uiState.update { state ->
            val updated = state.favouriteIds.toMutableSet()
            val added = if (quoteId in updated) {
                updated.remove(quoteId)
                false
            } else {
                updated.add(quoteId)
                true
            }
            storage.saveFavourites(updated)
            state.copy(
                favouriteIds = updated,
                feedbackMessage = if (added) "Saved to favourites" else "Removed from favourites"
            )
        }
    }

    fun updateExploreSearchQuery(query: String) {
        _uiState.update { it.copy(exploreSearchQuery = query) }
    }

    fun selectExploreCategory(category: QuoteCategory?) {
        _uiState.update { it.copy(exploreSelectedCategory = category) }
    }

    fun updateSavedSearchQuery(query: String) {
        _uiState.update { it.copy(savedSearchQuery = query) }
    }

    fun setThemePreference(preference: ThemePreference) {
        storage.saveThemePreference(preference)
        _uiState.update { it.copy(themePreference = preference) }
    }

    fun setCardBackground(background: QuoteCardBackground) {
        storage.saveCardBackground(background)
        _uiState.update { it.copy(cardBackground = background) }
    }

    fun setReducedMotion(enabled: Boolean) {
        storage.saveReducedMotion(enabled)
        _uiState.update { it.copy(reducedMotion = enabled) }
    }

    fun showFeedbackMessage(message: String) {
        _uiState.update { it.copy(feedbackMessage = message) }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun formatDateKeyForDisplay(dateKey: String): String {
        return try {
            val parser = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val parsed = parser.parse(dateKey)
            if (parsed != null) {
                DailyQuoteSelector.formatReadableDate(parsed)
            } else {
                dateKey
            }
        } catch (_: Throwable) {
            dateKey
        }
    }
}
