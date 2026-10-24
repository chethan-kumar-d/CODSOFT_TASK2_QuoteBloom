package com.example

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.Quote
import com.example.data.ResilientQuoteStorage
import com.example.ui.QuoteBloomTab
import com.example.ui.QuoteBloomViewModel
import com.example.ui.components.bevelSurface3D
import com.example.ui.screens.AppearanceScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.SavedScreen
import com.example.ui.screens.TodayScreen
import com.example.ui.theme.QuoteBloomTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val storage = ResilientQuoteStorage(applicationContext)
        setContent {
            val viewModel: QuoteBloomViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    @Suppress("UNCHECKED_CAST")
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        return QuoteBloomViewModel(storage) as T
                    }
                }
            )
            QuoteBloomApp(viewModel = viewModel)
        }
    }
}

private data class NavTabInfo(
    val tab: QuoteBloomTab,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun QuoteBloomApp(
    viewModel: QuoteBloomViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Automatically check and refresh daily quote when the app returns to the foreground (ON_RESUME)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.checkAndRefreshDailyQuote()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Show transient snackbar feedback for copy/favourite actions
    LaunchedEffect(uiState.feedbackMessage) {
        val msg = uiState.feedbackMessage
        if (!msg.isNullOrBlank()) {
            snackbarHostState.showSnackbar(msg)
            viewModel.clearFeedbackMessage()
        }
    }

    // Back button returns to Today tab when on secondary screens
    BackHandler(enabled = uiState.activeTab != QuoteBloomTab.TODAY) {
        viewModel.selectTab(QuoteBloomTab.TODAY)
    }

    val navItems = remember {
        listOf(
            NavTabInfo(
                tab = QuoteBloomTab.TODAY,
                labelRes = R.string.nav_today,
                selectedIcon = Icons.Filled.WbSunny,
                unselectedIcon = Icons.Outlined.WbSunny,
                testTag = "nav_tab_today"
            ),
            NavTabInfo(
                tab = QuoteBloomTab.EXPLORE,
                labelRes = R.string.nav_explore,
                selectedIcon = Icons.Filled.Explore,
                unselectedIcon = Icons.Outlined.Explore,
                testTag = "nav_tab_explore"
            ),
            NavTabInfo(
                tab = QuoteBloomTab.SAVED,
                labelRes = R.string.nav_saved,
                selectedIcon = Icons.Filled.Bookmark,
                unselectedIcon = Icons.Outlined.BookmarkBorder,
                testTag = "nav_tab_saved"
            ),
            NavTabInfo(
                tab = QuoteBloomTab.APPEARANCE,
                labelRes = R.string.nav_appearance,
                selectedIcon = Icons.Filled.Palette,
                unselectedIcon = Icons.Outlined.Palette,
                testTag = "nav_tab_appearance"
            )
        )
    }

    QuoteBloomTheme(themePreference = uiState.themePreference) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val isExpandedScreen = maxWidth >= 700.dp

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                contentWindowInsets = WindowInsets.safeDrawing,
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                bottomBar = {
                    if (!isExpandedScreen) {
                        Surface(
                            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                            shadowElevation = 12.dp,
                            tonalElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface,
                            modifier = Modifier
                                .shadow(
                                    elevation = 10.dp,
                                    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
                                )
                                .bevelSurface3D(
                                    cornerRadius = 24.dp,
                                    topHighlight = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                                    bottomLip = MaterialTheme.colorScheme.outline,
                                    strokeWidth = 1.4.dp
                                )
                        ) {
                            NavigationBar(
                                containerColor = MaterialTheme.colorScheme.surface,
                                tonalElevation = 0.dp,
                                modifier = Modifier.testTag("bottom_navigation_bar")
                            ) {
                                navItems.forEach { item ->
                                    val selected = uiState.activeTab == item.tab
                                    val label = stringResource(item.labelRes)
                                    NavigationBarItem(
                                        selected = selected,
                                        onClick = { viewModel.selectTab(item.tab) },
                                        icon = {
                                            Icon(
                                                imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                                contentDescription = label
                                            )
                                        },
                                        label = {
                                            Text(
                                                text = label,
                                                maxLines = 1,
                                                softWrap = false,
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    fontSize = 11.sp,
                                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                                                )
                                            )
                                        },
                                        alwaysShowLabel = true,
                                        colors = NavigationBarItemDefaults.colors(
                                            selectedIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                                            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                                        ),
                                        modifier = Modifier.testTag(item.testTag)
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (isExpandedScreen) {
                        NavigationRail(
                            containerColor = MaterialTheme.colorScheme.surface,
                            modifier = Modifier.testTag("side_navigation_rail")
                        ) {
                            navItems.forEach { item ->
                                val selected = uiState.activeTab == item.tab
                                val label = stringResource(item.labelRes)
                                NavigationRailItem(
                                    selected = selected,
                                    onClick = { viewModel.selectTab(item.tab) },
                                    icon = {
                                        Icon(
                                            imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = label
                                        )
                                    },
                                    label = {
                                        Text(
                                            text = label,
                                            maxLines = 1,
                                            softWrap = false,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    },
                                    modifier = Modifier.testTag(item.testTag)
                                )
                            }
                        }
                    }

                    val shareHandler: (Quote) -> Unit = { quote ->
                        shareQuoteWithFallback(
                            context = context,
                            quote = quote,
                            onFallbackCopied = {
                                viewModel.showFeedbackMessage(context.getString(R.string.copied_to_clipboard))
                            }
                        )
                    }

                    val copyHandler: (Quote) -> Unit = { quote ->
                        copyQuoteToClipboard(context, quote)
                        viewModel.showFeedbackMessage(context.getString(R.string.copied_to_clipboard))
                    }

                    when (uiState.activeTab) {
                        QuoteBloomTab.TODAY -> TodayScreen(
                            uiState = uiState,
                            isExpandedScreen = isExpandedScreen,
                            onDiscoverAnother = viewModel::discoverAnotherQuote,
                            onReturnToDaily = viewModel::returnToDailyQuote,
                            onToggleFavourite = viewModel::toggleFavourite,
                            onShareQuote = shareHandler,
                            onCopyQuote = copyHandler,
                            onSelectCardBackground = viewModel::setCardBackground,
                            modifier = Modifier.weight(1f)
                        )

                        QuoteBloomTab.EXPLORE -> ExploreScreen(
                            uiState = uiState,
                            isExpandedScreen = isExpandedScreen,
                            onSearchQueryChange = viewModel::updateExploreSearchQuery,
                            onSelectCategory = viewModel::selectExploreCategory,
                            onToggleFavourite = viewModel::toggleFavourite,
                            onShareQuote = shareHandler,
                            onCopyQuote = copyHandler,
                            modifier = Modifier.weight(1f)
                        )

                        QuoteBloomTab.SAVED -> SavedScreen(
                            uiState = uiState,
                            isExpandedScreen = isExpandedScreen,
                            onSearchQueryChange = viewModel::updateSavedSearchQuery,
                            onToggleFavourite = viewModel::toggleFavourite,
                            onShareQuote = shareHandler,
                            onCopyQuote = copyHandler,
                            onNavigateToExplore = { viewModel.selectTab(QuoteBloomTab.EXPLORE) },
                            modifier = Modifier.weight(1f)
                        )

                        QuoteBloomTab.APPEARANCE -> AppearanceScreen(
                            uiState = uiState,
                            isExpandedScreen = isExpandedScreen,
                            onSelectThemePreference = viewModel::setThemePreference,
                            onSelectCardBackground = viewModel::setCardBackground,
                            onToggleReducedMotion = viewModel::setReducedMotion,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

fun formatSharePayload(quote: Quote): String {
    return "“${quote.text}”\n— ${quote.author}\n\nShared via QuoteBloom"
}

fun copyQuoteToClipboard(context: Context, quote: Quote) {
    try {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("QuoteBloom Quote", formatSharePayload(quote))
        clipboard?.setPrimaryClip(clip)
    } catch (_: Throwable) {
        // Ignore if clipboard service is unavailable
    }
}

fun shareQuoteWithFallback(
    context: Context,
    quote: Quote,
    onFallbackCopied: () -> Unit
) {
    val payload = formatSharePayload(quote)
    try {
        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Quote from ${quote.author}")
            putExtra(Intent.EXTRA_TEXT, payload)
        }
        val chooserIntent = Intent.createChooser(sendIntent, "Share quote").apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooserIntent)
    } catch (_: Throwable) {
        copyQuoteToClipboard(context, quote)
        onFallbackCopied()
    }
}
