package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.Quote
import com.example.data.QuoteCardBackground
import com.example.data.QuoteCategory
import com.example.data.ThemePreference
import com.example.ui.QuoteBloomUiState
import com.example.ui.components.EditorialHeroQuoteCard
import com.example.ui.components.QuoteCollectionItemCard
import com.example.ui.components.bevelSurface3D
import com.example.ui.theme.EditorialSerifFontFamily
import com.example.ui.theme.toCanvasPalette

/**
 * Softly bevelled 3D primary/secondary action button with realistic press depth.
 */
@Composable
private fun Sculpted3DActionButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    isPrimary: Boolean,
    reducedMotion: Boolean,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 0.97f else 1f,
        animationSpec = spring(dampingRatio = 0.7f, stiffness = 450f),
        label = "btn_3d_scale"
    )
    val translationY by animateFloatAsState(
        targetValue = if (isPressed && !reducedMotion) 3f else 0f,
        animationSpec = tween(90),
        label = "btn_3d_offset"
    )

    val topColor = if (isPrimary) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surface
    }
    val bottomColor = if (isPrimary) {
        MaterialTheme.colorScheme.primary.copy(alpha = 0.85f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val contentColor = if (isPrimary) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurface
    }
    val highlightRim = if (isPrimary) {
        Color.White.copy(alpha = 0.45f)
    } else {
        Color.White.copy(alpha = 0.65f)
    }
    val shadowLip = if (isPrimary) {
        Color.Black.copy(alpha = 0.35f)
    } else {
        MaterialTheme.colorScheme.outline
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxWidth()
            .minimumInteractiveComponentSize()
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                this.translationY = translationY
            }
            .shadow(
                elevation = if (isPressed) 2.dp else 7.dp,
                shape = RoundedCornerShape(16.dp),
                ambientColor = shadowLip,
                spotColor = shadowLip
            )
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(topColor, bottomColor)
                )
            )
            .bevelSurface3D(
                cornerRadius = 16.dp,
                topHighlight = highlightRim,
                bottomLip = shadowLip,
                strokeWidth = 1.5.dp
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelLarge.copy(
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TodayScreen(
    uiState: QuoteBloomUiState,
    isExpandedScreen: Boolean,
    onDiscoverAnother: () -> Unit,
    onReturnToDaily: () -> Unit,
    onToggleFavourite: (String) -> Unit,
    onShareQuote: (Quote) -> Unit,
    onCopyQuote: (Quote) -> Unit,
    onSelectCardBackground: (QuoteCardBackground) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val activeQuote = uiState.activeHomeQuote

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = if (isExpandedScreen) 960.dp else 620.dp)
        ) {
            // Editorial Masthead with Date — Preserving exact structure and positions,
            // adjusting only QuoteBloom title font size so the full word "QuoteBloom" stays on one line.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.readableLocalDate.uppercase(),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.2.sp
                        ),
                        modifier = Modifier.testTag("home_local_date")
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.app_name),
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontFamily = EditorialSerifFontFamily,
                            fontSize = 27.sp,
                            lineHeight = 33.sp,
                            letterSpacing = (-0.4).sp,
                            color = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.testTag("home_header_title")
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = stringResource(R.string.tagline),
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isExpandedScreen) {
                // Thoughtfully arranged wider desktop/tablet layout
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1.35f)) {
                        EditorialHeroQuoteCard(
                            quote = activeQuote,
                            cardBackground = uiState.cardBackground,
                            isFavourite = uiState.isFavourite(activeQuote.id),
                            isDiscoveredMode = uiState.isShowingDiscoveredQuote,
                            reducedMotion = uiState.reducedMotion,
                            onToggleFavourite = { onToggleFavourite(activeQuote.id) },
                            onShareQuote = { onShareQuote(activeQuote) },
                            onCopyQuote = { onCopyQuote(activeQuote) }
                        )
                    }

                    Column(
                        modifier = Modifier.weight(0.85f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DiscoveryControlsCard(
                            isShowingDiscoveredQuote = uiState.isShowingDiscoveredQuote,
                            dailyQuote = uiState.dailyQuote,
                            reducedMotion = uiState.reducedMotion,
                            onDiscoverAnother = onDiscoverAnother,
                            onReturnToDaily = onReturnToDaily
                        )

                        QuickCanvasPickerCard(
                            selectedBackground = uiState.cardBackground,
                            onSelectBackground = onSelectCardBackground
                        )
                    }
                }
            } else {
                // Mobile handheld layout
                EditorialHeroQuoteCard(
                    quote = activeQuote,
                    cardBackground = uiState.cardBackground,
                    isFavourite = uiState.isFavourite(activeQuote.id),
                    isDiscoveredMode = uiState.isShowingDiscoveredQuote,
                    reducedMotion = uiState.reducedMotion,
                    onToggleFavourite = { onToggleFavourite(activeQuote.id) },
                    onShareQuote = { onShareQuote(activeQuote) },
                    onCopyQuote = { onCopyQuote(activeQuote) }
                )

                Spacer(modifier = Modifier.height(18.dp))

                DiscoveryControlsCard(
                    isShowingDiscoveredQuote = uiState.isShowingDiscoveredQuote,
                    dailyQuote = uiState.dailyQuote,
                    reducedMotion = uiState.reducedMotion,
                    onDiscoverAnother = onDiscoverAnother,
                    onReturnToDaily = onReturnToDaily
                )

                Spacer(modifier = Modifier.height(16.dp))

                QuickCanvasPickerCard(
                    selectedBackground = uiState.cardBackground,
                    onSelectBackground = onSelectCardBackground
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DiscoveryControlsCard(
    isShowingDiscoveredQuote: Boolean,
    dailyQuote: Quote,
    reducedMotion: Boolean,
    onDiscoverAnother: () -> Unit,
    onReturnToDaily: () -> Unit
) {
    val topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val bottomLip = MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 7.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.22f)
            )
            .bevelSurface3D(
                cornerRadius = 24.dp,
                topHighlight = topHighlight,
                bottomLip = bottomLip,
                strokeWidth = 1.5.dp
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = if (isShowingDiscoveredQuote) {
                    "Exploring beyond today's quote"
                } else {
                    "Want fresh inspiration right now?"
                },
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Text(
                text = if (isShowingDiscoveredQuote) {
                    "Today's Quote of the Day (${dailyQuote.author}) remains safely pinned for you."
                } else {
                    "Browse another curated reflection without replacing your daily quote."
                },
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Sculpted3DActionButton(
                text = stringResource(R.string.discover_another_button),
                icon = Icons.Filled.AutoAwesome,
                onClick = onDiscoverAnother,
                isPrimary = true,
                reducedMotion = reducedMotion,
                modifier = Modifier.testTag("discover_another_button")
            )

            if (isShowingDiscoveredQuote) {
                Sculpted3DActionButton(
                    text = stringResource(R.string.return_to_daily_button),
                    icon = Icons.AutoMirrored.Filled.Undo,
                    onClick = onReturnToDaily,
                    isPrimary = false,
                    reducedMotion = reducedMotion,
                    modifier = Modifier.testTag("return_to_daily_button")
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickCanvasPickerCard(
    selectedBackground: QuoteCardBackground,
    onSelectBackground: (QuoteCardBackground) -> Unit
) {
    val topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val bottomLip = MaterialTheme.colorScheme.outline

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp,
                shape = RoundedCornerShape(24.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.20f)
            )
            .bevelSurface3D(
                cornerRadius = 24.dp,
                topHighlight = topHighlight,
                bottomLip = bottomLip,
                strokeWidth = 1.4.dp
            ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.30f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Palette,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "3D Card Canvas Atmosphere",
                    style = MaterialTheme.typography.titleSmall.copy(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuoteCardBackground.entries.forEach { bg ->
                    val palette = bg.toCanvasPalette()
                    val isSelected = bg == selectedBackground
                    Surface(
                        shape = RoundedCornerShape(50),
                        shadowElevation = if (isSelected) 5.dp else 2.dp,
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f)
                        } else {
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                        },
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .clip(RoundedCornerShape(50))
                            .clickable(role = Role.Button) { onSelectBackground(bg) }
                            .testTag("quick_bg_chip_${bg.id}")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .shadow(2.dp, CircleShape)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(palette.gradientStart, palette.gradientEnd)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = bg.title,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExploreScreen(
    uiState: QuoteBloomUiState,
    isExpandedScreen: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (QuoteCategory?) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onShareQuote: (Quote) -> Unit,
    onCopyQuote: (Quote) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredQuotes = uiState.filteredExploreQuotes

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isExpandedScreen) 840.dp else 620.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.explore_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = EditorialSerifFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.explore_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3D Sculpted Search Bar
            Surface(
                shape = RoundedCornerShape(18.dp),
                shadowElevation = 5.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .bevelSurface3D(
                        cornerRadius = 18.dp,
                        topHighlight = MaterialTheme.colorScheme.surface,
                        bottomLip = MaterialTheme.colorScheme.outline
                    )
            ) {
                OutlinedTextField(
                    value = uiState.exploreSearchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_placeholder_explore),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = null
                        )
                    },
                    trailingIcon = {
                        if (uiState.exploreSearchQuery.isNotEmpty()) {
                            IconButton(
                                onClick = { onSearchQueryChange("") },
                                modifier = Modifier
                                    .minimumInteractiveComponentSize()
                                    .testTag("explore_clear_search_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Clear,
                                    contentDescription = stringResource(R.string.clear_search)
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("explore_search_input")
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Raised Category Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = uiState.exploreSelectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = {
                        Text(
                            text = stringResource(R.string.category_all),
                            maxLines = 1
                        )
                    },
                    elevation = FilterChipDefaults.filterChipElevation(elevation = 3.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .testTag("category_chip_all")
                )

                QuoteCategory.entries.forEach { category ->
                    val isSelected = uiState.exploreSelectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSelectCategory(if (isSelected) null else category)
                        },
                        label = {
                            Text(
                                text = category.displayName,
                                maxLines = 1
                            )
                        },
                        elevation = FilterChipDefaults.filterChipElevation(elevation = 3.dp),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier
                            .minimumInteractiveComponentSize()
                            .testTag("category_chip_${category.name.lowercase()}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${filteredQuotes.size} ${if (filteredQuotes.size == 1) "quote" else "quotes"} shown",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("explore_results_count")
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (filteredQuotes.isEmpty()) {
                EmptyStateCard(
                    title = stringResource(R.string.explore_no_matches_title),
                    body = stringResource(R.string.explore_no_matches_body),
                    actionLabel = stringResource(R.string.clear_search),
                    reducedMotion = uiState.reducedMotion,
                    onAction = {
                        onSearchQueryChange("")
                        onSelectCategory(null)
                    },
                    testTag = "explore_empty_state"
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("explore_quotes_list"),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(filteredQuotes, key = { it.id }) { quote ->
                        QuoteCollectionItemCard(
                            quote = quote,
                            isFavourite = uiState.isFavourite(quote.id),
                            reducedMotion = uiState.reducedMotion,
                            onToggleFavourite = { onToggleFavourite(quote.id) },
                            onShareQuote = { onShareQuote(quote) },
                            onCopyQuote = { onCopyQuote(quote) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SavedScreen(
    uiState: QuoteBloomUiState,
    isExpandedScreen: Boolean,
    onSearchQueryChange: (String) -> Unit,
    onToggleFavourite: (String) -> Unit,
    onShareQuote: (Quote) -> Unit,
    onCopyQuote: (Quote) -> Unit,
    onNavigateToExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allSaved = uiState.savedQuotes
    val filteredSaved = uiState.filteredSavedQuotes

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .widthIn(max = if (isExpandedScreen) 840.dp else 620.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.saved_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = EditorialSerifFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "${allSaved.size} saved in your local collection",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier.testTag("saved_total_count")
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (allSaved.isEmpty()) {
                EmptyStateCard(
                    title = stringResource(R.string.saved_empty_title),
                    body = stringResource(R.string.saved_empty_body),
                    actionLabel = stringResource(R.string.explore_cta_button),
                    reducedMotion = uiState.reducedMotion,
                    onAction = onNavigateToExplore,
                    testTag = "saved_empty_state"
                )
            } else {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    shadowElevation = 5.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .bevelSurface3D(
                            cornerRadius = 18.dp,
                            topHighlight = MaterialTheme.colorScheme.surface,
                            bottomLip = MaterialTheme.colorScheme.outline
                        )
                ) {
                    OutlinedTextField(
                        value = uiState.savedSearchQuery,
                        onValueChange = onSearchQueryChange,
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search_placeholder_saved),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = null
                            )
                        },
                        trailingIcon = {
                            if (uiState.savedSearchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { onSearchQueryChange("") },
                                    modifier = Modifier
                                        .minimumInteractiveComponentSize()
                                        .testTag("saved_clear_search_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Clear,
                                        contentDescription = stringResource(R.string.clear_search)
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(18.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saved_search_input")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (filteredSaved.isEmpty()) {
                    EmptyStateCard(
                        title = stringResource(R.string.saved_no_matches_title),
                        body = stringResource(R.string.saved_no_matches_body),
                        actionLabel = stringResource(R.string.clear_search),
                        reducedMotion = uiState.reducedMotion,
                        onAction = { onSearchQueryChange("") },
                        testTag = "saved_no_matches_state"
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("saved_quotes_list"),
                        contentPadding = PaddingValues(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(filteredSaved, key = { it.id }) { quote ->
                            QuoteCollectionItemCard(
                                quote = quote,
                                isFavourite = true,
                                reducedMotion = uiState.reducedMotion,
                                onToggleFavourite = { onToggleFavourite(quote.id) },
                                onShareQuote = { onShareQuote(quote) },
                                onCopyQuote = { onCopyQuote(quote) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyStateCard(
    title: String,
    body: String,
    actionLabel: String,
    reducedMotion: Boolean,
    onAction: () -> Unit,
    testTag: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(26.dp),
                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.16f),
                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.22f)
            )
            .bevelSurface3D(
                cornerRadius = 26.dp,
                topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                bottomLip = MaterialTheme.colorScheme.outline,
                strokeWidth = 1.5.dp
            )
            .testTag(testTag),
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surface,
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.40f)
                        )
                    )
                )
                .padding(horizontal = 26.dp, vertical = 34.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                shape = CircleShape,
                shadowElevation = 6.dp,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(1.5.dp, Color.White.copy(alpha = 0.5f)),
                modifier = Modifier.size(66.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Filled.BookmarkBorder,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = EditorialSerifFontFamily,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = body,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(22.dp))

            Sculpted3DActionButton(
                text = actionLabel,
                icon = Icons.Filled.Explore,
                onClick = onAction,
                isPrimary = true,
                reducedMotion = reducedMotion,
                modifier = Modifier.testTag("${testTag}_action_button")
            )
        }
    }
}

@Composable
fun AppearanceScreen(
    uiState: QuoteBloomUiState,
    isExpandedScreen: Boolean,
    onSelectThemePreference: (ThemePreference) -> Unit,
    onSelectCardBackground: (QuoteCardBackground) -> Unit,
    onToggleReducedMotion: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = if (isExpandedScreen) 840.dp else 620.dp)
        ) {
            Text(
                text = stringResource(R.string.appearance_title),
                style = MaterialTheme.typography.displaySmall.copy(
                    fontFamily = EditorialSerifFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = stringResource(R.string.appearance_subtitle),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3D Theme Mode Section
            Text(
                text = stringResource(R.string.theme_mode_section),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val options = listOf(
                    Triple(ThemePreference.LIGHT, stringResource(R.string.theme_light), Icons.Filled.LightMode),
                    Triple(ThemePreference.DARK, stringResource(R.string.theme_dark), Icons.Filled.DarkMode),
                    Triple(ThemePreference.SYSTEM, stringResource(R.string.theme_system), Icons.Filled.SettingsBrightness)
                )

                options.forEach { (pref, label, icon) ->
                    val selected = uiState.themePreference == pref
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .minimumInteractiveComponentSize()
                            .shadow(
                                elevation = if (selected) 8.dp else 3.dp,
                                shape = RoundedCornerShape(18.dp),
                                ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f),
                                spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.22f)
                            )
                            .bevelSurface3D(
                                cornerRadius = 18.dp,
                                topHighlight = Color.White.copy(alpha = if (selected) 0.5f else 0.3f),
                                bottomLip = MaterialTheme.colorScheme.outline
                            )
                            .clip(RoundedCornerShape(18.dp))
                            .clickable(role = Role.RadioButton) { onSelectThemePreference(pref) }
                            .testTag("theme_option_${pref.name.lowercase()}"),
                        shape = RoundedCornerShape(18.dp),
                        border = BorderStroke(
                            width = if (selected) 2.dp else 1.dp,
                            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 14.dp, horizontal = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                tint = if (selected) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = label,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = if (selected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    },
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                ),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Six Curated 3D Quote-Card Canvases
            Text(
                text = stringResource(R.string.card_background_section),
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                QuoteCardBackground.entries.forEach { bg ->
                    val palette = bg.toCanvasPalette()
                    val isSelected = uiState.cardBackground == bg

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = if (isSelected) 10.dp else 4.dp,
                                shape = RoundedCornerShape(22.dp),
                                ambientColor = palette.bottomBevelShadow,
                                spotColor = palette.bottomBevelShadow
                            )
                            .bevelSurface3D(
                                cornerRadius = 22.dp,
                                topHighlight = palette.topBevelHighlight,
                                bottomLip = palette.bottomBevelShadow,
                                strokeWidth = 1.6.dp
                            )
                            .clip(RoundedCornerShape(22.dp))
                            .clickable(role = Role.RadioButton) { onSelectCardBackground(bg) }
                            .testTag("card_bg_option_${bg.id}"),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else palette.borderColor
                        ),
                        colors = CardDefaults.cardColors(containerColor = palette.gradientStart)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(palette.gradientStart, palette.gradientMid, palette.gradientEnd)
                                    )
                                )
                                .padding(horizontal = 20.dp, vertical = 18.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = bg.title,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontFamily = EditorialSerifFontFamily,
                                            color = palette.primaryText,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(50),
                                        shadowElevation = 2.dp,
                                        color = palette.badgeBackground
                                    ) {
                                        Text(
                                            text = "Aa",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = palette.badgeText,
                                                fontWeight = FontWeight.Bold
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = bg.subtitle,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        color = palette.secondaryText
                                    )
                                )
                            }

                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = "Selected background",
                                    tint = palette.accentColor,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reduced Motion Accessibility Option
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.14f),
                        spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.18f)
                    )
                    .bevelSurface3D(
                        cornerRadius = 22.dp,
                        topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                        bottomLip = MaterialTheme.colorScheme.outline
                    ),
                shape = RoundedCornerShape(22.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.reduced_motion_title),
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = stringResource(R.string.reduced_motion_subtitle),
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Switch(
                        checked = uiState.reducedMotion,
                        onCheckedChange = onToggleReducedMotion,
                        modifier = Modifier.testTag("reduced_motion_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
