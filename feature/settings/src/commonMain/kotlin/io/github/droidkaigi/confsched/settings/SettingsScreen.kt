package io.github.droidkaigi.confsched.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import conference_app_2024.feature.settings.generated.resources.settings_title
import io.github.droidkaigi.confsched.compose.rememberEventEmitter
import io.github.droidkaigi.confsched.designsystem.theme.KaigiTheme
import io.github.droidkaigi.confsched.model.FontFamily
import io.github.droidkaigi.confsched.settings.section.accessibility
import io.github.droidkaigi.confsched.settings.section.lookAndFeel
import io.github.droidkaigi.confsched.ui.SnackbarMessageEffect
import io.github.droidkaigi.confsched.ui.UserMessageStateHolder
import io.github.droidkaigi.confsched.ui.UserMessageStateHolderImpl
import io.github.droidkaigi.confsched.ui.component.AnimatedLargeTopAppBar
import io.github.droidkaigi.confsched.ui.handleOnClickIfNotNavigating
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

const val settingsScreenRoute = "settings"

fun NavGraphBuilder.settingsScreens(
    onNavigationIconClick: () -> Unit,
) {
    composable(settingsScreenRoute) {
        val lifecycleOwner = LocalLifecycleOwner.current

        SettingsScreen(
            onNavigationIconClick = {
                handleOnClickIfNotNavigating(
                    lifecycleOwner,
                    onNavigationIconClick,
                )
            },
        )
    }
}

data class SettingsUiState(
    val useFontFamily: FontFamily?,
    val enableAnimation: Boolean,
    val enableFallbackMode: Boolean,
    val userMessageStateHolder: UserMessageStateHolder,
)

@Composable
fun SettingsScreen(
    onNavigationIconClick: () -> Unit,
    modifier: Modifier = Modifier,
    isTopAppBarHidden: Boolean = false,
) {
    val eventEmitter = rememberEventEmitter<SettingsScreenEvent>()
    val uiState = settingsScreenPresenter(events = eventEmitter)

    val snackbarHostState = remember { SnackbarHostState() }

    SnackbarMessageEffect(
        snackbarHostState = snackbarHostState,
        userMessageStateHolder = uiState.userMessageStateHolder,
    )
    SettingsScreen(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBackClick = onNavigationIconClick,
        onSelectUseFontFamily = { eventEmitter.tryEmit(SettingsScreenEvent.SelectUseFontFamily(it)) },
        onSelectEnableAnimation = { eventEmitter.tryEmit(SettingsScreenEvent.SelectEnableAnimation(it)) },
        onSelectFallbackMode = { eventEmitter.tryEmit(SettingsScreenEvent.SelectEnableFallbackMode(it)) },
        modifier = modifier,
        isTopAppBarHidden = isTopAppBarHidden,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    uiState: SettingsUiState,
    snackbarHostState: SnackbarHostState,
    onBackClick: () -> Unit,
    onSelectUseFontFamily: (FontFamily) -> Unit,
    onSelectEnableAnimation: (Boolean) -> Unit,
    onSelectFallbackMode: (Boolean) -> Unit,
    isTopAppBarHidden: Boolean,
    modifier: Modifier = Modifier,
) {
    val scrollBehavior =
        if (!isTopAppBarHidden) {
            TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
        } else {
            null
        }
    val lazyListState = rememberLazyListState()

    Scaffold(
        modifier = modifier,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            if (!isTopAppBarHidden) {
                AnimatedLargeTopAppBar(
                    title = stringResource(SettingsRes.string.settings_title),
                    onBackClick = onBackClick,
                    scrollBehavior = scrollBehavior,
                    navIconContentDescription = "Back",
                )
            }
        },
    ) { padding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .let {
                    if (scrollBehavior != null) {
                        it.nestedScroll(scrollBehavior.nestedScrollConnection)
                    } else {
                        it
                    }
                },
            contentPadding = padding,
            state = lazyListState,
        ) {
            accessibility(
                uiState = uiState,
                onSelectUseFontFamily = onSelectUseFontFamily,
            )
            lookAndFeel(
                uiState = uiState,
                onSelectEnableAnimation = onSelectEnableAnimation,
                onSelectFallbackMode = onSelectFallbackMode,
            )
        }
    }
}

@Composable
@Preview
fun SettingsScreenPreview() {
    KaigiTheme {
        Surface {
            SettingsScreen(
                uiState = SettingsUiState(
                    useFontFamily = FontFamily.DotGothic16Regular,
                    enableAnimation = true,
                    enableFallbackMode = false,
                    userMessageStateHolder = UserMessageStateHolderImpl(),
                ),
                snackbarHostState = SnackbarHostState(),
                onBackClick = {},
                onSelectUseFontFamily = {},
                onSelectEnableAnimation = {},
                onSelectFallbackMode = {},
                isTopAppBarHidden = false,
            )
        }
    }
}
