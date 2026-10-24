package com.example.data

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.random.Random

object DailyQuoteSelector {

    /**
     * Produces a canonical local calendar date key in `YYYY-MM-DD` format using the device's local timezone.
     */
    fun getLocalDateKey(calendar: Calendar = Calendar.getInstance()): String {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    /**
     * Formats a local calendar date into an editorial readable string, e.g., "Saturday, October 24, 2026".
     */
    fun formatReadableDate(date: Date = Date(), locale: Locale = Locale.getDefault()): String {
        val formatter = SimpleDateFormat("EEEE, MMMM d, yyyy", locale)
        return formatter.format(date)
    }

    /**
     * Deterministically selects the Quote of the Day for a given `YYYY-MM-DD` local date key.
     * Advances through the entire collection with zero repeats until all `catalog.size` quotes
     * have been used (bijection over `0 until catalog.size` via a coprime stride).
     * Calling this multiple times on the same calendar day always returns the exact same quote.
     */
    fun selectDailyQuote(
        localDateKey: String,
        catalog: List<Quote> = QuoteCatalog.quotes
    ): Quote {
        require(catalog.isNotEmpty()) { "Quote catalog must not be empty" }
        val size = catalog.size
        if (size == 1) return catalog.first()

        val daysNumber = parseDaysOrdinal(localDateKey)
        val stride = findCoprimeStride(size)
        val offset = 17
        val index = ((daysNumber.toLong() * stride.toLong()) + offset.toLong()).mod(size.toLong()).toInt()
        return catalog[index]
    }

    private fun findCoprimeStride(size: Int): Int {
        // Prefer 77 for a 380-quote catalog (5 * 76) so consecutive days step across both categories and items
        val preferredCandidates = listOf(77, 83, 89, 97, 29, 31, 1)
        for (candidate in preferredCandidates) {
            if (candidate < size && gcd(candidate, size) == 1) {
                return candidate
            }
        }
        for (candidate in 2 until size) {
            if (gcd(candidate, size) == 1) return candidate
        }
        return 1
    }

    private tailrec fun gcd(a: Int, b: Int): Int = if (b == 0) kotlin.math.abs(a) else gcd(b, a % b)

    private fun parseDaysOrdinal(localDateKey: String): Int {
        val parts = localDateKey.trim().split("-")
        if (parts.size == 3) {
            val year = parts[0].toIntOrNull()
            val month = parts[1].toIntOrNull()
            val day = parts[2].toIntOrNull()
            if (year != null && month != null && day != null) {
                // Continuous day count calculation (Rata Die approximation) so consecutive days differ by 1
                val a = (14 - month) / 12
                val y = year + 4800 - a
                val m = month + 12 * a - 3
                return day + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045
            }
        }
        // Fallback deterministic hash if non-standard key is passed
        var hash = 2166136261L
        for (ch in localDateKey) {
            hash = (hash xor ch.code.toLong()) * 16777619L
        }
        return (hash and 0x7FFFFFFFL).toInt()
    }

    /**
     * Selects a discovered quote that avoids immediate repeats and avoids repeating the current quote
     * or recent discovery history until the pool is exhausted.
     */
    fun selectNextDiscoveryQuote(
        currentDisplayedQuoteId: String,
        dailyQuoteId: String,
        recentDiscoveryIds: List<String>,
        catalog: List<Quote> = QuoteCatalog.quotes,
        random: Random = Random.Default
    ): Quote {
        require(catalog.isNotEmpty()) { "Quote catalog must not be empty" }
        if (catalog.size == 1) return catalog.first()

        // Never repeat the immediately visible quote
        val nonImmediatePool = catalog.filter { it.id != currentDisplayedQuoteId }
        if (nonImmediatePool.size == 1) return nonImmediatePool.first()

        // Prefer quotes that aren't in the recent discovery window and aren't the daily quote
        val recentSet = recentDiscoveryIds.toSet()
        val freshPool = nonImmediatePool.filter { it.id != dailyQuoteId && it.id !in recentSet }

        if (freshPool.isNotEmpty()) {
            return freshPool[random.nextInt(freshPool.size)]
        }

        // If the user has cycled through almost all quotes, avoid at least the last 3 seen & current
        val lastFew = recentDiscoveryIds.takeLast(3).toSet()
        val fallbackPool = nonImmediatePool.filter { it.id !in lastFew }
        return if (fallbackPool.isNotEmpty()) {
            fallbackPool[random.nextInt(fallbackPool.size)]
        } else {
            nonImmediatePool[random.nextInt(nonImmediatePool.size)]
        }
    }

    /**
     * Filters quotes safely by search query (matching quote text or author) and optional category.
     */
    fun filterQuotes(
        quotes: List<Quote>,
        searchQuery: String,
        selectedCategory: QuoteCategory? = null
    ): List<Quote> {
        val normalizedQuery = searchQuery.trim()
        return quotes.filter { quote ->
            val matchesCategory = selectedCategory == null || quote.category == selectedCategory
            val matchesQuery = if (normalizedQuery.isEmpty()) {
                true
            } else {
                quote.text.contains(normalizedQuery, ignoreCase = true) ||
                    quote.author.contains(normalizedQuery, ignoreCase = true) ||
                    quote.category.displayName.contains(normalizedQuery, ignoreCase = true)
            }
            matchesCategory && matchesQuery
        }
    }
}
