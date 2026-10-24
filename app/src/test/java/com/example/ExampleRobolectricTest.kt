package com.example

import android.content.Context
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.example.data.DailyQuoteSelector
import com.example.data.QuoteCardBackground
import com.example.data.QuoteCatalog
import com.example.data.QuoteCategory
import com.example.data.ResilientQuoteStorage
import com.example.data.ThemePreference
import com.example.ui.QuoteBloomViewModel
import com.example.ui.theme.toCanvasPalette
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Calendar
import java.util.Locale
import kotlin.math.pow

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.getSharedPreferences(ResilientQuoteStorage.PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
    }

    @Test
    fun catalog_hasAtLeast365UniqueQuotes_preservesOriginalIds_andHasNoDuplicates() {
        val quotes = QuoteCatalog.quotes
        assertTrue(
            "Catalog must contain at least 365 quotes, found ${quotes.size}",
            quotes.size >= 365
        )

        // Verify all IDs are unique
        val uniqueIds = quotes.map { it.id }.toSet()
        assertEquals("Every quote must have a unique ID", quotes.size, uniqueIds.size)

        // Verify all normalized quote texts are unique (no duplicates or near-duplicates)
        val normalizedTexts = mutableSetOf<String>()
        for (q in quotes) {
            val norm = q.text.lowercase(Locale.US).replace(Regex("[^a-z0-9]"), "")
            assertTrue("Duplicate or near-duplicate quote text found: ${q.id} -> ${q.text}", normalizedTexts.add(norm))
        }

        // Verify category distribution across all 5 categories
        QuoteCategory.entries.forEach { category ->
            val count = quotes.count { it.category == category }
            assertTrue("Category $category must have at least 70 quotes, found $count", count >= 70)
        }

        // Verify all 65 original quote IDs are preserved
        val prefixes = listOf("mot", "lrn", "cnf", "knd", "lif")
        for (prefix in prefixes) {
            for (idx in 1..13) {
                val expectedId = String.format(Locale.US, "%s_%02d", prefix, idx)
                assertTrue("Original quote ID $expectedId must be preserved", expectedId in uniqueIds)
            }
        }
    }

    @Test
    fun dailyQuoteSelection_advancesThroughEntireCollectionWithNoRepeatsUntilFullCollectionUsed() {
        val catalog = QuoteCatalog.quotes
        val totalSize = catalog.size
        val seenIds = mutableSetOf<String>()

        val cal = Calendar.getInstance(Locale.US).apply {
            set(2026, Calendar.JANUARY, 1, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Advance day by day through the entire collection (380 consecutive calendar days, which is >= 365 days)
        for (dayOffset in 0 until totalSize) {
            val dateKey = DailyQuoteSelector.getLocalDateKey(cal)
            val selected = DailyQuoteSelector.selectDailyQuote(dateKey, catalog)
            // Calling again on the same dateKey must return the exact same quote
            val selectedAgain = DailyQuoteSelector.selectDailyQuote(dateKey, catalog)
            assertEquals("Daily quote must remain unchanged within the same day ($dateKey)", selected.id, selectedAgain.id)

            assertTrue(
                "Daily quote repeated prematurely on day offset $dayOffset ($dateKey): ${selected.id}",
                seenIds.add(selected.id)
            )
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }

        assertEquals(
            "All $totalSize quotes must be visited before any repeat occurs",
            totalSize,
            seenIds.size
        )
    }

    @Test
    fun dailyQuoteSelection_isConsistentWithinSameDay_andRollsOverOnNewDay() {
        var currentDay = "2026-10-24"
        val storage = ResilientQuoteStorage(context)
        val viewModel = QuoteBloomViewModel(storage = storage, dateKeyProvider = { currentDay })

        val initialDaily = viewModel.uiState.value.dailyQuote
        // Calling checkAndRefreshDailyQuote on the same day keeps the exact same daily quote
        viewModel.checkAndRefreshDailyQuote()
        assertEquals(initialDaily.id, viewModel.uiState.value.dailyQuote.id)

        // Simulate foreground return on the next calendar day
        currentDay = "2026-10-25"
        viewModel.checkAndRefreshDailyQuote()
        val nextDayDaily = viewModel.uiState.value.dailyQuote
        assertNotEquals("Daily quote should roll over when local calendar date changes", initialDaily.id, nextDayDaily.id)
        assertEquals("2026-10-25", viewModel.uiState.value.localDateKey)
    }

    @Test
    fun discoverAnotherQuote_avoidsImmediateRepeats_andPreservesDailyQuote() {
        val storage = ResilientQuoteStorage(context)
        val viewModel = QuoteBloomViewModel(storage = storage, dateKeyProvider = { "2026-10-24" })
        val originalDaily = viewModel.uiState.value.dailyQuote

        var previousShownId = viewModel.uiState.value.activeHomeQuote.id
        repeat(25) {
            viewModel.discoverAnotherQuote()
            val currentShownId = viewModel.uiState.value.activeHomeQuote.id
            assertNotEquals("Discover must never immediately repeat the previous quote", previousShownId, currentShownId)
            assertEquals("Discover must not overwrite the stored dailyQuote", originalDaily.id, viewModel.uiState.value.dailyQuote.id)
            assertTrue(viewModel.uiState.value.isShowingDiscoveredQuote)
            previousShownId = currentShownId
        }

        // Returning to daily quote restores the original daily quote
        viewModel.returnToDailyQuote()
        assertFalse(viewModel.uiState.value.isShowingDiscoveredQuote)
        assertEquals(originalDaily.id, viewModel.uiState.value.activeHomeQuote.id)
    }

    @Test
    fun storageResilience_preservesExistingFavouritesAndNewFavouritesAcrossRestarts() {
        val storage = ResilientQuoteStorage(context)
        // Simulate pre-existing saved favourites from original IDs + new expanded IDs
        storage.writeRawForTesting(
            ResilientQuoteStorage.KEY_FAVOURITES_JSON,
            "[\"mot_01\",\"lrn_05\",\"cnf_03\",\"knd_01\",\"lif_76\"]"
        )

        val vmLoaded = QuoteBloomViewModel(storage = storage, dateKeyProvider = { "2026-10-24" })
        assertEquals(5, vmLoaded.uiState.value.savedQuotes.size)
        assertTrue(vmLoaded.uiState.value.isFavourite("mot_01"))
        assertTrue(vmLoaded.uiState.value.isFavourite("lif_76"))

        // Inject corrupted JSON strings to verify graceful fallback
        storage.writeRawForTesting(ResilientQuoteStorage.KEY_FAVOURITES_JSON, "{bad_json:[123")
        storage.writeRawForTesting(ResilientQuoteStorage.KEY_APPEARANCE_JSON, "not-a-json-object")
        storage.writeRawForTesting(ResilientQuoteStorage.KEY_DAILY_RECORD_JSON, "{{corrupted")

        val recovered = storage.loadState()
        assertTrue(recovered.favouriteIds.isEmpty())
        assertEquals(ThemePreference.LIGHT, recovered.themePreference)
        assertEquals(QuoteCardBackground.WARM_IVORY, recovered.cardBackground)

        // Save valid favourites and appearance preferences and verify persistence
        val vm1 = QuoteBloomViewModel(storage = storage, dateKeyProvider = { "2026-10-24" })
        val sampleId = QuoteCatalog.quotes.first().id
        vm1.toggleFavourite(sampleId)
        vm1.setThemePreference(ThemePreference.DARK)
        vm1.setCardBackground(QuoteCardBackground.ROYAL_VIOLET)
        vm1.setReducedMotion(true)

        val vm2 = QuoteBloomViewModel(storage = storage, dateKeyProvider = { "2026-10-24" })
        assertTrue(vm2.uiState.value.isFavourite(sampleId))
        assertEquals(ThemePreference.DARK, vm2.uiState.value.themePreference)
        assertEquals(QuoteCardBackground.ROYAL_VIOLET, vm2.uiState.value.cardBackground)
        assertTrue(vm2.uiState.value.reducedMotion)
    }

    @Test
    fun backgroundContrast_allSixCuratedBackgroundsMeetReadableContrastRatio() {
        QuoteCardBackground.entries.forEach { bg ->
            val palette = bg.toCanvasPalette()
            val ratio = calculateContrastRatio(
                palette.primaryText.red, palette.primaryText.green, palette.primaryText.blue,
                palette.gradientStart.red, palette.gradientStart.green, palette.gradientStart.blue
            )
            assertTrue(
                "Background ${bg.id} must have WCAG AA contrast >= 4.5, was $ratio",
                ratio >= 4.5
            )
        }
    }

    @Test
    fun uiJourney_todayExploreSavedAndAppearanceWorkEndToEnd() {
        composeTestRule.waitForIdle()

        // Verify Home screen renders single-line "QuoteBloom" header, quote card, and discover button
        composeTestRule.onNodeWithTag("home_header_title").assertExists()
        assertEquals("Theme", context.getString(R.string.nav_appearance))
        composeTestRule.onNodeWithTag("home_quote_card").assertExists()
        composeTestRule.onNodeWithTag("discover_another_button").assertExists()

        // Tap favourite on Home quote (scroll into view first on compact Robolectric viewport)
        composeTestRule.onNodeWithTag("home_favourite_button").performScrollTo().performClick()
        composeTestRule.waitForIdle()

        // Navigate to Saved tab and verify the quote is listed (not empty state)
        composeTestRule.onNodeWithTag("nav_tab_saved").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("saved_quotes_list").assertExists()

        // Navigate to Explore tab and search
        composeTestRule.onNodeWithTag("nav_tab_explore").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("explore_search_input").performClick()
        composeTestRule.onNodeWithTag("explore_search_input").performTextInput("Aesop")
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("quote_item_card_knd_01").assertExists()

        // Navigate to Theme (Appearance) tab and select Deep Plum theme & Deep Plum card background
        composeTestRule.onNodeWithTag("nav_tab_appearance").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("theme_option_dark").performScrollTo().performClick()
        composeTestRule.onNodeWithTag("card_bg_option_deep_plum").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun calculateContrastRatio(
        r1: Float, g1: Float, b1: Float,
        r2: Float, g2: Float, b2: Float
    ): Double {
        fun lum(c: Float): Double {
            val v = c.toDouble()
            return if (v <= 0.03928) v / 12.92 else ((v + 0.055) / 1.055).pow(2.4)
        }
        val l1 = 0.2126 * lum(r1) + 0.7152 * lum(g1) + 0.0722 * lum(b1)
        val l2 = 0.2126 * lum(r2) + 0.7152 * lum(g2) + 0.0722 * lum(b2)
        val brightest = maxOf(l1, l2)
        val darkest = minOf(l1, l2)
        return (brightest + 0.05) / (darkest + 0.05)
    }
}
