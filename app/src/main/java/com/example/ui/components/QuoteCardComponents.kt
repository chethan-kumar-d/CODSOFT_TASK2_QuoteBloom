package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Quote
import com.example.data.QuoteCardBackground
import com.example.ui.theme.CleanSansFontFamily
import com.example.ui.theme.EditorialSerifFontFamily
import com.example.ui.theme.toCanvasPalette

/**
 * Reusable modifier that adds a subtle 3D bevel rim (top specular highlight + bottom sculpted lip)
 * to cards, buttons, and raised surfaces.
 */
fun Modifier.bevelSurface3D(
    cornerRadius: Dp = 22.dp,
    topHighlight: Color,
    bottomLip: Color,
    strokeWidth: Dp = 1.5.dp
): Modifier = this.drawWithContent {
    drawContent()
    val strokePx = strokeWidth.toPx()
    val radiusPx = cornerRadius.toPx()
    // Top-left specular highlight rim
    drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(topHighlight, Color.Transparent, bottomLip)
        ),
        topLeft = Offset(strokePx / 2f, strokePx / 2f),
        size = Size(size.width - strokePx, size.height - strokePx),
        cornerRadius = CornerRadius(radiusPx, radiusPx),
        style = Stroke(width = strokePx)
    )
}

/**
 * Softly bevelled 3D icon button with realistic press depth animation that respects reducedMotion.
 */
@Composable
fun Sculpted3DIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String,
    containerColor: Color,
    contentColor: Color,
    topHighlight: Color,
    bottomShadowLip: Color,
    reducedMotion: Boolean = false,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.93f else 1f,
        animationSpec = spring(dampingRatio = 0.65f, stiffness = 500f),
        label = "icon_btn_3d_scale"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 3f else 0f,
        animationSpec = tween(90),
        label = "icon_btn_3d_offset"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 6.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = bottomShadowLip,
                spotColor = bottomShadowLip
            )
            .clip(RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        containerColor,
                        containerColor.copy(alpha = 0.90f)
                    )
                )
            )
            .bevelSurface3D(
                cornerRadius = 14.dp,
                topHighlight = topHighlight,
                bottomLip = bottomShadowLip,
                strokeWidth = 1.4.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = contentColor,
            modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorialHeroQuoteCard(
    quote: Quote,
    cardBackground: QuoteCardBackground,
    isFavourite: Boolean,
    isDiscoveredMode: Boolean,
    reducedMotion: Boolean,
    onToggleFavourite: () -> Unit,
    onShareQuote: () -> Unit,
    onCopyQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = cardBackground.toCanvasPalette()

    // Layered 3D Deck: Back sculptural layer + Middle sculptural layer + Foreground Hero Card
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
    ) {
        // 3rd (Deepest) stacked card layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 22.dp)
                .offset(y = 10.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = palette.bottomBevelShadow,
                    spotColor = palette.bottomBevelShadow
                )
                .clip(RoundedCornerShape(28.dp))
                .background(palette.stackLayerBack)
                .bevelSurface3D(
                    cornerRadius = 28.dp,
                    topHighlight = palette.topBevelHighlight.copy(alpha = 0.35f),
                    bottomLip = palette.bottomBevelShadow
                )
        )

        // 2nd (Middle) stacked card layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(horizontal = 11.dp)
                .offset(y = 5.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = palette.bottomBevelShadow,
                    spotColor = palette.bottomBevelShadow
                )
                .clip(RoundedCornerShape(28.dp))
                .background(palette.stackLayerMid)
                .bevelSurface3D(
                    cornerRadius = 28.dp,
                    topHighlight = palette.topBevelHighlight.copy(alpha = 0.5f),
                    bottomLip = palette.bottomBevelShadow
                )
        )

        // Foreground Sculpted 3D Hero Quote Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 14.dp,
                    shape = RoundedCornerShape(28.dp),
                    ambientColor = palette.bottomBevelShadow,
                    spotColor = palette.bottomBevelShadow
                )
                .bevelSurface3D(
                    cornerRadius = 28.dp,
                    topHighlight = palette.topBevelHighlight,
                    bottomLip = palette.bottomBevelShadow,
                    strokeWidth = 1.8.dp
                )
                .testTag("home_quote_card"),
            shape = RoundedCornerShape(28.dp),
            border = BorderStroke(1.dp, palette.borderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
            colors = CardDefaults.cardColors(containerColor = palette.gradientStart)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                palette.gradientStart,
                                palette.gradientMid,
                                palette.gradientEnd
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(900f, 1300f)
                        )
                    )
                    .drawBehind {
                        // Sculpted 3D sphere orb top-right with radial highlight
                        val orb1Center = Offset(size.width * 0.88f, size.height * 0.14f)
                        val orb1Radius = size.minDimension * 0.38f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    palette.topBevelHighlight.copy(alpha = 0.28f),
                                    palette.decorativeOrbPrimary,
                                    Color.Transparent
                                ),
                                center = Offset(orb1Center.x - orb1Radius * 0.25f, orb1Center.y - orb1Radius * 0.25f),
                                radius = orb1Radius
                            ),
                            radius = orb1Radius,
                            center = orb1Center
                        )

                        // Sculpted 3D sphere orb bottom-left
                        val orb2Center = Offset(size.width * 0.10f, size.height * 0.86f)
                        val orb2Radius = size.minDimension * 0.30f
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    palette.decorativeOrbSecondary,
                                    Color.Transparent
                                ),
                                center = orb2Center,
                                radius = orb2Radius
                            ),
                            radius = orb2Radius,
                            center = orb2Center
                        )

                        // Sculpted 3D botanical petal relief bottom-right
                        val leafPath = Path().apply {
                            moveTo(size.width * 0.80f, size.height * 0.84f)
                            cubicTo(
                                size.width * 0.70f, size.height * 0.68f,
                                size.width * 0.86f, size.height * 0.54f,
                                size.width * 0.95f, size.height * 0.67f
                            )
                            cubicTo(
                                size.width * 0.93f, size.height * 0.79f,
                                size.width * 0.85f, size.height * 0.85f,
                                size.width * 0.80f, size.height * 0.84f
                            )
                            close()
                        }
                        drawPath(path = leafPath, color = palette.decorativeOrbPrimary)
                    }
                    .padding(horizontal = 24.dp, vertical = 26.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Top Row: Mode Label + Raised 3D Category Pill Badge
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isDiscoveredMode) {
                                stringResource(R.string.discovered_quote_label)
                            } else {
                                stringResource(R.string.daily_reflection_label)
                            },
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = palette.secondaryText,
                                fontWeight = FontWeight.SemiBold,
                                letterSpacing = 1.3.sp
                            ),
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .testTag("quote_mode_badge")
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = palette.badgeBackground,
                            shadowElevation = 4.dp,
                            border = BorderStroke(1.dp, palette.topBevelHighlight.copy(alpha = 0.6f)),
                            modifier = Modifier
                                .bevelSurface3D(
                                    cornerRadius = 50.dp,
                                    topHighlight = palette.topBevelHighlight,
                                    bottomLip = palette.bottomBevelShadow,
                                    strokeWidth = 1.dp
                                )
                                .testTag("home_quote_category_badge")
                        ) {
                            Text(
                                text = quote.category.displayName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = palette.badgeText,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sculpted 3D Quotation-Flower Medallion Emblem inside the hero card
                    Box(
                        modifier = Modifier
                            .semantics { contentDescription = "Opening quotation mark" }
                    ) {
                        // Subtle 3D drop shadow copy of quotation mark
                        Text(
                            text = "“",
                            fontFamily = EditorialSerifFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 68.sp,
                            lineHeight = 50.sp,
                            color = palette.bottomBevelShadow.copy(alpha = 0.45f),
                            modifier = Modifier.offset(x = 2.dp, y = 3.dp)
                        )
                        Text(
                            text = "“",
                            fontFamily = EditorialSerifFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 68.sp,
                            lineHeight = 50.sp,
                            color = palette.accentColor
                        )
                    }

                    // Animated or immediate quote body depending on reducedMotion
                    val quoteBodyContent: @Composable (Quote) -> Unit = { targetQuote ->
                        Column {
                            Text(
                                text = targetQuote.text,
                                style = MaterialTheme.typography.displayMedium.copy(
                                    color = palette.primaryText,
                                    fontFamily = EditorialSerifFontFamily,
                                    fontSize = 26.sp,
                                    lineHeight = 36.sp
                                ),
                                modifier = Modifier.testTag("home_quote_text")
                            )

                            Spacer(modifier = Modifier.height(22.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(palette.accentColor, palette.secondaryText.copy(alpha = 0.4f))
                                            )
                                        )
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = targetQuote.author,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            color = palette.primaryText,
                                            fontFamily = CleanSansFontFamily,
                                            fontWeight = FontWeight.SemiBold
                                        ),
                                        modifier = Modifier.testTag("home_quote_author")
                                    )
                                    if (targetQuote.note.isNotBlank()) {
                                        Text(
                                            text = targetQuote.note,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                color = palette.secondaryText,
                                                fontSize = 12.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }

                    if (reducedMotion) {
                        quoteBodyContent(quote)
                    } else {
                        AnimatedContent(
                            targetState = quote,
                            transitionSpec = {
                                (fadeIn(animationSpec = tween(240)) + scaleIn(initialScale = 0.97f, animationSpec = tween(240)))
                                    .togetherWith(fadeOut(animationSpec = tween(160)))
                            },
                            label = "quote_3d_transition"
                        ) { targetQuote ->
                            quoteBodyContent(targetQuote)
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    HorizontalDivider(
                        color = palette.borderColor,
                        thickness = 1.dp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Raised 3D Action Toolbar inside Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = cardBackground.title,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = palette.secondaryText,
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.weight(1f, fill = false)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val favDesc = if (isFavourite) {
                                stringResource(R.string.action_remove_favourite)
                            } else {
                                stringResource(R.string.action_save_favourite)
                            }

                            Sculpted3DIconButton(
                                onClick = onToggleFavourite,
                                icon = if (isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = favDesc,
                                containerColor = palette.badgeBackground,
                                contentColor = if (isFavourite) palette.accentColor else palette.primaryText,
                                topHighlight = palette.topBevelHighlight,
                                bottomShadowLip = palette.bottomBevelShadow,
                                reducedMotion = reducedMotion,
                                modifier = Modifier.testTag("home_favourite_button")
                            )

                            Sculpted3DIconButton(
                                onClick = onCopyQuote,
                                icon = Icons.Filled.ContentCopy,
                                contentDescription = stringResource(R.string.action_copy),
                                containerColor = palette.badgeBackground,
                                contentColor = palette.primaryText,
                                topHighlight = palette.topBevelHighlight,
                                bottomShadowLip = palette.bottomBevelShadow,
                                reducedMotion = reducedMotion,
                                modifier = Modifier.testTag("home_copy_button")
                            )

                            Sculpted3DIconButton(
                                onClick = onShareQuote,
                                icon = Icons.Filled.Share,
                                contentDescription = stringResource(R.string.action_share),
                                containerColor = palette.badgeBackground,
                                contentColor = palette.primaryText,
                                topHighlight = palette.topBevelHighlight,
                                bottomShadowLip = palette.bottomBevelShadow,
                                reducedMotion = reducedMotion,
                                modifier = Modifier.testTag("home_share_button")
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuoteCollectionItemCard(
    quote: Quote,
    isFavourite: Boolean,
    reducedMotion: Boolean = false,
    onToggleFavourite: () -> Unit,
    onShareQuote: () -> Unit,
    onCopyQuote: () -> Unit,
    modifier: Modifier = Modifier
) {
    val topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val bottomLip = MaterialTheme.colorScheme.outline

    Card(
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(22.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.22f)
            )
            .bevelSurface3D(
                cornerRadius = 22.dp,
                topHighlight = topHighlight,
                bottomLip = bottomLip,
                strokeWidth = 1.4.dp
            )
            .testTag("quote_item_card_${quote.id}"),
        shape = RoundedCornerShape(22.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                        )
                    )
                )
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shadowElevation = 2.dp,
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.25f))
                ) {
                    Text(
                        text = quote.category.displayName,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Sculpted3DIconButton(
                        onClick = onCopyQuote,
                        icon = Icons.Filled.ContentCopy,
                        contentDescription = stringResource(R.string.action_copy),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        topHighlight = topHighlight,
                        bottomShadowLip = bottomLip,
                        reducedMotion = reducedMotion,
                        modifier = Modifier.testTag("copy_button_${quote.id}")
                    )

                    Sculpted3DIconButton(
                        onClick = onShareQuote,
                        icon = Icons.Filled.Share,
                        contentDescription = stringResource(R.string.action_share),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        topHighlight = topHighlight,
                        bottomShadowLip = bottomLip,
                        reducedMotion = reducedMotion,
                        modifier = Modifier.testTag("share_button_${quote.id}")
                    )

                    Sculpted3DIconButton(
                        onClick = onToggleFavourite,
                        icon = if (isFavourite) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                        contentDescription = if (isFavourite) {
                            stringResource(R.string.action_remove_favourite)
                        } else {
                            stringResource(R.string.action_save_favourite)
                        },
                        containerColor = if (isFavourite) {
                            MaterialTheme.colorScheme.tertiaryContainer
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant
                        },
                        contentColor = if (isFavourite) {
                            MaterialTheme.colorScheme.tertiary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        topHighlight = topHighlight,
                        bottomShadowLip = bottomLip,
                        reducedMotion = reducedMotion,
                        modifier = Modifier.testTag("fav_button_${quote.id}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "“${quote.text}”",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = EditorialSerifFontFamily,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 20.sp,
                    lineHeight = 28.sp
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = quote.author,
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
                if (quote.note.isNotBlank()) {
                    Text(
                        text = "• ${quote.note}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }
    }
}
