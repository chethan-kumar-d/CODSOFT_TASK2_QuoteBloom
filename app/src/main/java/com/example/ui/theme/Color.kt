package com.example.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.data.QuoteCardBackground

// Core Editorial & 3D Sculpted Brand Palette
val WarmIvory = Color(0xFFFAF7F2)
val WarmIvorySurface = Color(0xFFF3EDE2)
val WarmIvoryElevated = Color(0xFFFFFFFF)
val DeepPlum = Color(0xFF292039)
val DeepPlumSurface = Color(0xFF362A4B)
val DeepPlumDarkBg = Color(0xFF1A1425)
val EditorialViolet = Color(0xFF7455D9)
val EditorialVioletLight = Color(0xFFEBE5FC)
val SoftVioletAccent = Color(0xFFB7A2F6)
val SoftLavender = Color(0xFFD9CEFA)
val RestrainedCoral = Color(0xFFF07868)
val RestrainedCoralSoft = Color(0xFFFCE7E4)
val MutedSage = Color(0xFF6B8A7A)
val MutedSageSurface = Color(0xFFE7EFEA)
val InkMutedLight = Color(0xFF5D536B)
val InkMutedDark = Color(0xFFCFC5E0)
val BorderLight = Color(0xFFE1D8CA)
val BorderDark = Color(0xFF4C3E66)

// 3D Lighting & Bevel Tokens
val HighlightRimLight = Color(0xE6FFFFFF)
val HighlightRimDark = Color(0x47FAF7F2)
val ShadowLipLight = Color(0xFFD5CABB)
val ShadowLipDark = Color(0xFF110C1A)

data class CardCanvasPalette(
    val gradientStart: Color,
    val gradientMid: Color,
    val gradientEnd: Color,
    val primaryText: Color,
    val secondaryText: Color,
    val accentColor: Color,
    val badgeBackground: Color,
    val badgeText: Color,
    val decorativeOrbPrimary: Color,
    val decorativeOrbSecondary: Color,
    val borderColor: Color,
    val topBevelHighlight: Color,
    val bottomBevelShadow: Color,
    val stackLayerBack: Color,
    val stackLayerMid: Color
)

fun QuoteCardBackground.toCanvasPalette(): CardCanvasPalette {
    return when (this) {
        QuoteCardBackground.WARM_IVORY -> CardCanvasPalette(
            gradientStart = Color(0xFFFFFDF9),
            gradientMid = Color(0xFFFAF7F2),
            gradientEnd = Color(0xFFEDE4D5),
            primaryText = Color(0xFF292039),
            secondaryText = Color(0xFF594D6B),
            accentColor = Color(0xFF7455D9),
            badgeBackground = Color(0xFFEBE4F8),
            badgeText = Color(0xFF422A8A),
            decorativeOrbPrimary = Color(0x247455D9),
            decorativeOrbSecondary = Color(0x28F07868),
            borderColor = Color(0xFFDFD5C3),
            topBevelHighlight = Color(0xF2FFFFFF),
            bottomBevelShadow = Color(0xFFD2C5B0),
            stackLayerBack = Color(0xFFE3D8C6),
            stackLayerMid = Color(0xFFEEE6D8)
        )
        QuoteCardBackground.DEEP_PLUM -> CardCanvasPalette(
            gradientStart = Color(0xFF382C4E),
            gradientMid = Color(0xFF292039),
            gradientEnd = Color(0xFF1B1427),
            primaryText = Color(0xFFFAF7F2),
            secondaryText = Color(0xFFD8CFE8),
            accentColor = Color(0xFFF07868),
            badgeBackground = Color(0xFF43345D),
            badgeText = Color(0xFFFAF7F2),
            decorativeOrbPrimary = Color(0x387455D9),
            decorativeOrbSecondary = Color(0x2CF07868),
            borderColor = Color(0xFF544373),
            topBevelHighlight = Color(0x59B7A2F6),
            bottomBevelShadow = Color(0xFF110B1A),
            stackLayerBack = Color(0xFF1E162B),
            stackLayerMid = Color(0xFF261D36)
        )
        QuoteCardBackground.BOTANICAL_SAGE -> CardCanvasPalette(
            gradientStart = Color(0xFFF4F8F5),
            gradientMid = Color(0xFFEBF2ED),
            gradientEnd = Color(0xFFD6E4DA),
            primaryText = Color(0xFF1C2D24),
            secondaryText = Color(0xFF3F574A),
            accentColor = Color(0xFF446E58),
            badgeBackground = Color(0xFFCCE0D2),
            badgeText = Color(0xFF193024),
            decorativeOrbPrimary = Color(0x2E6B8A7A),
            decorativeOrbSecondary = Color(0x227455D9),
            borderColor = Color(0xFFBDD1C4),
            topBevelHighlight = Color(0xEBFFFFFF),
            bottomBevelShadow = Color(0xFFB4C9BC),
            stackLayerBack = Color(0xFFC5D8CC),
            stackLayerMid = Color(0xFFD5E4DA)
        )
        QuoteCardBackground.ROYAL_VIOLET -> CardCanvasPalette(
            gradientStart = Color(0xFF5940A8),
            gradientMid = Color(0xFF46318A),
            gradientEnd = Color(0xFF2B1D59),
            primaryText = Color(0xFFFAF7F2),
            secondaryText = Color(0xFFE6E0FA),
            accentColor = Color(0xFFF59B8E),
            badgeBackground = Color(0xFF654BB8),
            badgeText = Color(0xFFFAF7F2),
            decorativeOrbPrimary = Color(0x38F07868),
            decorativeOrbSecondary = Color(0x30FAF7F2),
            borderColor = Color(0xFF7B61D1),
            topBevelHighlight = Color(0x66D9CEFA),
            bottomBevelShadow = Color(0xFF1B113B),
            stackLayerBack = Color(0xFF281A52),
            stackLayerMid = Color(0xFF36246E)
        )
        QuoteCardBackground.SUNLIT_CORAL -> CardCanvasPalette(
            gradientStart = Color(0xFFFFF6F4),
            gradientMid = Color(0xFFFCEEEB),
            gradientEnd = Color(0xFFF5DDD6),
            primaryText = Color(0xFF331F24),
            secondaryText = Color(0xFF634148),
            accentColor = Color(0xFFD15847),
            badgeBackground = Color(0xFFF3CFC8),
            badgeText = Color(0xFF571F18),
            decorativeOrbPrimary = Color(0x2EF07868),
            decorativeOrbSecondary = Color(0x227455D9),
            borderColor = Color(0xFFE8C2B9),
            topBevelHighlight = Color(0xF2FFFFFF),
            bottomBevelShadow = Color(0xFFDDB2A8),
            stackLayerBack = Color(0xFFE8C6BE),
            stackLayerMid = Color(0xFFF2D6CF)
        )
        QuoteCardBackground.MIDNIGHT_BOTANICA -> CardCanvasPalette(
            gradientStart = Color(0xFF22302C),
            gradientMid = Color(0xFF192421),
            gradientEnd = Color(0xFF101816),
            primaryText = Color(0xFFFAF7F2),
            secondaryText = Color(0xFFCCE0D8),
            accentColor = Color(0xFF8AB89F),
            badgeBackground = Color(0xFF293D37),
            badgeText = Color(0xFFEBF5F0),
            decorativeOrbPrimary = Color(0x366B8A7A),
            decorativeOrbSecondary = Color(0x29F07868),
            borderColor = Color(0xFF38524B),
            topBevelHighlight = Color(0x4D8AB89F),
            bottomBevelShadow = Color(0xFF0A0F0E),
            stackLayerBack = Color(0xFF111917),
            stackLayerMid = Color(0xFF17211E)
        )
    }
}
