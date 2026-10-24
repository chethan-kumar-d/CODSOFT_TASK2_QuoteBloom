package com.example.data

enum class QuoteCategory(val displayName: String) {
    MOTIVATION("Motivation"),
    LEARNING("Learning"),
    CONFIDENCE("Confidence"),
    KINDNESS("Kindness"),
    LIFE("Life");

    companion object {
        fun fromNameOrNull(value: String?): QuoteCategory? {
            if (value.isNullOrBlank()) return null
            return entries.firstOrNull { it.name.equals(value.trim(), ignoreCase = true) || it.displayName.equals(value.trim(), ignoreCase = true) }
        }
    }
}

data class Quote(
    val id: String,
    val text: String,
    val author: String,
    val category: QuoteCategory,
    val note: String = ""
)

enum class ThemePreference(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark");

    companion object {
        fun fromString(value: String?): ThemePreference {
            return entries.firstOrNull { it.name.equals(value?.trim(), ignoreCase = true) } ?: LIGHT
        }
    }
}

enum class QuoteCardBackground(
    val id: String,
    val title: String,
    val subtitle: String,
    val isDarkCanvas: Boolean
) {
    WARM_IVORY(
        id = "warm_ivory",
        title = "Warm Ivory",
        subtitle = "Classic editorial parchment with deep plum ink",
        isDarkCanvas = false
    ),
    DEEP_PLUM(
        id = "deep_plum",
        title = "Deep Plum",
        subtitle = "Nocturnal plum canvas with luminous ivory serif",
        isDarkCanvas = true
    ),
    BOTANICAL_SAGE(
        id = "botanical_sage",
        title = "Botanical Sage",
        subtitle = "Calm herbal sage mist with deep forest contrast",
        isDarkCanvas = false
    ),
    ROYAL_VIOLET(
        id = "royal_violet",
        title = "Royal Violet",
        subtitle = "Rich dusk violet with crisp cream typography",
        isDarkCanvas = true
    ),
    SUNLIT_CORAL(
        id = "sunlit_coral",
        title = "Terracotta Dawn",
        subtitle = "Warm blush terracotta paper with espresso ink",
        isDarkCanvas = false
    ),
    MIDNIGHT_BOTANICA(
        id = "midnight_botanica",
        title = "Midnight Botanica",
        subtitle = "Deep obsidian garden with warm gold & sage accents",
        isDarkCanvas = true
    );

    companion object {
        fun fromId(id: String?): QuoteCardBackground {
            if (id.isNullOrBlank()) return WARM_IVORY
            return entries.firstOrNull {
                it.id.equals(id.trim(), ignoreCase = true) || it.name.equals(id.trim(), ignoreCase = true)
            } ?: WARM_IVORY
        }
    }
}
