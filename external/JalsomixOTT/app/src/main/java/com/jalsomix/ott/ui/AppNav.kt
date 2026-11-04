package com.jalsomix.ott.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jalsomix.ott.AppViewModel
import com.jalsomix.ott.R
import com.jalsomix.ott.data.canWatch
import com.jalsomix.ott.ui.screens.AuthScreen
import com.jalsomix.ott.ui.screens.ContentLanguageScreen
import com.jalsomix.ott.ui.screens.DetailScreen
import com.jalsomix.ott.ui.screens.EditProfileScreen
import com.jalsomix.ott.ui.screens.ProfileScreen
import com.jalsomix.ott.ui.screens.DevicesScreen
import com.jalsomix.ott.ui.screens.DownloadsScreen
import com.jalsomix.ott.ui.screens.HelpScreen
import com.jalsomix.ott.ui.screens.LanguageScreen
import com.jalsomix.ott.ui.screens.MainScreen
import com.jalsomix.ott.ui.screens.MyListScreen
import com.jalsomix.ott.ui.screens.PlayerScreen
import com.jalsomix.ott.ui.screens.SearchScreen
import com.jalsomix.ott.ui.screens.SplashScreen
import com.jalsomix.ott.ui.screens.SubscriptionScreen
import com.jalsomix.ott.ui.theme.JM

object Routes {
    const val SPLASH = "splash"
    const val LANG_ONBOARD = "lang_onboard"
    const val LANG_SETTINGS = "lang_settings"
    const val CONTENT_LANG = "content_lang"
    const val AUTH = "auth"
    const val MAIN = "main"
    const val DETAIL = "detail/{id}"
    const val PLAYER = "player/{id}/{ep}"
    const val SUB = "subscription"
    const val SEARCH = "search/{voice}"
    const val MYLIST = "mylist"
    const val DOWNLOADS = "downloads"
    const val DEVICES = "devices"
    const val HELP = "help"
    const val PROFILE = "profile"
    const val EDIT_PROFILE = "edit_profile"

    fun detail(id: String) = "detail/$id"
    fun player(id: String, ep: Int) = "player/$id/$ep"
    fun search(voice: Boolean) = "search/$voice"
}

private const val NAV_MS = 360
private val navEase = FastOutSlowInEasing

@Composable
fun JalsomixRoot() {
    val vm: AppViewModel = viewModel()
    val nav = rememberNavController()
    val state by vm.state.collectAsStateWithLifecycle()

    // iOS-style push transition: new screen slides in from the right, old one parallaxes left.
    NavHost(
        navController = nav,
        startDestination = Routes.SPLASH,
        modifier = Modifier
            .fillMaxSize()
            .background(JM.Bg),
        enterTransition = { slideInHorizontally(tween(NAV_MS, easing = navEase)) { it } + fadeIn(tween(NAV_MS)) },
        exitTransition = { slideOutHorizontally(tween(NAV_MS, easing = navEase)) { -it / 4 } + fadeOut(tween(NAV_MS)) },
        popEnterTransition = { slideInHorizontally(tween(NAV_MS, easing = navEase)) { -it / 4 } + fadeIn(tween(NAV_MS)) },
        popExitTransition = { slideOutHorizontally(tween(NAV_MS, easing = navEase)) { it } + fadeOut(tween(NAV_MS)) }
    ) {
        composable(Routes.SPLASH, exitTransition = { fadeOut(tween(300)) }) {
            SplashScreen(state) { dest ->
                nav.navigate(dest) { popUpTo(Routes.SPLASH) { inclusive = true } }
            }
        }

        composable(Routes.LANG_ONBOARD) {
            LanguageScreen(state, vm, onboarding = true, onBack = {}) {
                nav.navigate(Routes.AUTH) { popUpTo(Routes.LANG_ONBOARD) { inclusive = true } }
            }
        }

        composable(Routes.LANG_SETTINGS) {
            LanguageScreen(state, vm, onboarding = false, onBack = { nav.popBackStack() }) { nav.popBackStack() }
        }

        composable(Routes.CONTENT_LANG) {
            ContentLanguageScreen(state, vm) { nav.popBackStack() }
        }

        composable(Routes.AUTH) {
            AuthScreen(vm) {
                nav.navigate(Routes.MAIN) { popUpTo(Routes.AUTH) { inclusive = true } }
            }
        }

        composable(Routes.MAIN) {
            MainScreen(
                state = state,
                onOpen = { nav.navigate(Routes.detail(it)) },
                onSearch = { voice -> nav.navigate(Routes.search(voice)) },
                onSub = { nav.navigate(Routes.SUB) },
                onProfile = { nav.navigate(Routes.PROFILE) },
                onToggleList = vm::toggleList
            )
        }

        composable(Routes.PROFILE) {
            Column(
                Modifier
                    .fillMaxSize()
                    .background(JM.Bg)
            ) {
                IosNavBar(stringResource(R.string.nav_profile), onBack = { nav.popBackStack() })
                ProfileScreen(
                    state = state,
                    onSub = { nav.navigate(Routes.SUB) },
                    onAppLanguage = { nav.navigate(Routes.LANG_SETTINGS) },
                    onContentLanguage = { nav.navigate(Routes.CONTENT_LANG) },
                    onMyList = { nav.navigate(Routes.MYLIST) },
                    onDownloads = { nav.navigate(Routes.DOWNLOADS) },
                    onDevices = { nav.navigate(Routes.DEVICES) },
                    onHelp = { nav.navigate(Routes.HELP) },
                    onEditProfile = { nav.navigate(Routes.EDIT_PROFILE) },
                    onLogout = {
                        vm.logout {
                            nav.navigate(Routes.AUTH) { popUpTo(nav.graph.id) { inclusive = true } }
                        }
                    }
                )
            }
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(state, vm, onBack = { nav.popBackStack() })
        }

        composable(
            Routes.DETAIL,
            arguments = listOf(navArgument("id") { type = NavType.StringType })
        ) { entry ->
            val id = entry.arguments?.getString("id").orEmpty()
            DetailScreen(
                titleId = id,
                state = state,
                onBack = { nav.popBackStack() },
                onPlay = { t, ep ->
                    if (canWatch(t, ep, state.subscription)) nav.navigate(Routes.player(t.id, ep))
                    else nav.navigate(Routes.SUB)
                },
                onToggleList = vm::toggleList,
                onOpen = { nav.navigate(Routes.detail(it)) },
                onDownload = { t, ep -> vm.startDownload(t, ep) },
                onDeleteDownload = { vm.deleteDownload(it) },
                onSub = { nav.navigate(Routes.SUB) }
            )
        }

        composable(
            Routes.PLAYER,
            arguments = listOf(
                navArgument("id") { type = NavType.StringType },
                navArgument("ep") { type = NavType.IntType }
            )
        ) { entry ->
            PlayerScreen(
                titleId = entry.arguments?.getString("id").orEmpty(),
                startEpisode = entry.arguments?.getInt("ep") ?: 0,
                subscription = state.subscription,
                downloads = state.downloads,
                onBack = { nav.popBackStack() }
            )
        }

        composable(Routes.SUB) {
            SubscriptionScreen(state, vm, onBack = { nav.popBackStack() })
        }

        composable(
            Routes.SEARCH,
            arguments = listOf(navArgument("voice") { type = NavType.BoolType })
        ) { entry ->
            SearchScreen(
                state = state,
                autoVoice = entry.arguments?.getBoolean("voice") ?: false,
                onBack = { nav.popBackStack() },
                onOpen = { nav.navigate(Routes.detail(it)) }
            )
        }

        composable(Routes.MYLIST) {
            MyListScreen(state, onBack = { nav.popBackStack() }, onOpen = { nav.navigate(Routes.detail(it)) })
        }

        composable(Routes.DOWNLOADS) {
            DownloadsScreen(
                state = state,
                onBack = { nav.popBackStack() },
                onPlay = { id, ep -> nav.navigate(Routes.player(id, ep)) },
                onDelete = { vm.deleteDownload(it) }
            )
        }

        composable(Routes.DEVICES) {
            DevicesScreen(
                state = state,
                currentDeviceId = vm.deviceId,
                onBack = { nav.popBackStack() },
                onRemove = { vm.removeDevice(it) },
                onUpgrade = { nav.navigate(Routes.SUB) }
            )
        }

        composable(Routes.HELP) {
            HelpScreen(onBack = { nav.popBackStack() })
        }
    }
}
