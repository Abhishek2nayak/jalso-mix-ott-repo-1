package com.jalsomix.ott.ui.screens

import androidx.compose.animation.core.Spring
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Tv
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.SmartDisplay
import androidx.compose.material.icons.rounded.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.ContentType
import com.jalsomix.ott.ui.theme.JM

private data class TabItem(val labelRes: Int, val off: ImageVector, val on: ImageVector)

private val tabs = listOf(
    TabItem(R.string.nav_home, Icons.Outlined.Home, Icons.Rounded.Home),
    TabItem(R.string.nav_short, Icons.Outlined.SmartDisplay, Icons.Rounded.SmartDisplay),
    TabItem(R.string.nav_series, Icons.Outlined.Tv, Icons.Rounded.Tv),
    TabItem(R.string.nav_movies, Icons.Outlined.Movie, Icons.Rounded.Movie)
)

@Composable
fun MainScreen(
    state: AppState,
    onOpen: (String) -> Unit,
    onSearch: (Boolean) -> Unit,
    onSub: () -> Unit,
    onProfile: () -> Unit,
    onToggleList: (String) -> Unit
) {
    var tab by rememberSaveable { mutableIntStateOf(0) }
    // Hoisted scroll states keep every tab's scroll position while switching (feels native & smooth).
    val homeState = rememberLazyListState()
    val dramaState = rememberLazyGridState()
    val seriesState = rememberLazyGridState()
    val movieState = rememberLazyGridState()

    Box(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        when (tab) {
            0 -> HomeScreen(
                state = state,
                listState = homeState,
                onOpen = onOpen,
                onSearch = onSearch,
                onSub = onSub,
                onSwitchTab = { tab = it },
                onToggleList = onToggleList,
                onProfile = onProfile
            )
            1 -> BrowseScreen(ContentType.SHORT_DRAMA, state, dramaState, onOpen, onSearch, onProfile)
            2 -> BrowseScreen(ContentType.WEB_SERIES, state, seriesState, onOpen, onSearch, onProfile)
            else -> BrowseScreen(ContentType.MOVIE, state, movieState, onOpen, onSearch, onProfile)
        }

        FloatingTabBar(
            selected = tab,
            onSelect = { tab = it },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        )
    }
}

/**
 * iOS-26 style floating glass tab bar: a rounded capsule hovering above the content,
 * with a highlighted pill behind the selected tab and outline ▸ filled icons.
 */
@Composable
private fun FloatingTabBar(selected: Int, onSelect: (Int) -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        modifier
            .fillMaxWidth()
            .shadow(24.dp, shape, ambientColor = Color.Black, spotColor = Color.Black)
            .clip(shape)
            .background(Color(0xFF1C1C1E).copy(alpha = 0.94f))
            .border(0.8.dp, Brush.verticalGradient(listOf(Color.White.copy(alpha = 0.22f), Color.White.copy(alpha = 0.05f))), shape)
            .padding(6.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { i, item ->
            val on = i == selected
            val bg by animateColorAsState(
                if (on) JM.Gold.copy(alpha = 0.20f) else Color.Transparent,
                tween(220), label = "tabbg"
            )
            val scale by animateFloatAsState(
                if (on) 1.06f else 1f,
                spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessMedium),
                label = "tab"
            )
            Column(
                Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(bg)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(i) }
                    .padding(vertical = 7.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    if (on) item.on else item.off,
                    contentDescription = null,
                    tint = if (on) JM.Gold else JM.Text.copy(alpha = 0.62f),
                    modifier = Modifier
                        .size(25.dp)
                        .scale(scale)
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    stringResource(item.labelRes),
                    style = MaterialTheme.typography.labelSmall.copy(letterSpacing = 0.sp, fontSize = 10.sp),
                    color = if (on) JM.Gold else JM.Text.copy(alpha = 0.62f),
                    maxLines = 1
                )
            }
        }
    }
}
