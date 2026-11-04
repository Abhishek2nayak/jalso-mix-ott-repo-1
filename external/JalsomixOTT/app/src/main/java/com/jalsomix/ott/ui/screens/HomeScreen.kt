package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.ContentType
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.Title
import com.jalsomix.ott.data.forLangs
import com.jalsomix.ott.ui.Avatar
import com.jalsomix.ott.ui.GlassButton
import com.jalsomix.ott.ui.GoldButton
import com.jalsomix.ott.ui.IosSearchBar
import com.jalsomix.ott.ui.JmChip
import com.jalsomix.ott.ui.LogoMarkNew
import com.jalsomix.ott.ui.PosterArt
import com.jalsomix.ott.ui.PosterCard
import com.jalsomix.ott.ui.SectionHeader
import com.jalsomix.ott.ui.Wordmark
import com.jalsomix.ott.ui.theme.JM
import kotlinx.coroutines.delay

@Composable
fun typeLabel(type: ContentType): String = stringResource(
    when (type) {
        ContentType.SHORT_DRAMA -> R.string.type_short
        ContentType.WEB_SERIES -> R.string.type_series
        ContentType.MOVIE -> R.string.type_movie
    }
)

@Composable
fun HomeScreen(
    state: AppState,
    onOpen: (String) -> Unit,
    onSearch: (Boolean) -> Unit,
    onSub: () -> Unit,
    onSwitchTab: (Int) -> Unit,
    onToggleList: (String) -> Unit,
    onProfile: () -> Unit,
    listState: LazyListState = rememberLazyListState()
) {
    val langs = state.contentLangs
    val all = remember(langs) { SampleData.titles.forLangs(langs) }
    val hero = remember(all) {
        all.filter { it.trending }.take(6).ifEmpty { all.sortedByDescending { it.rating }.take(3) }
    }
    val top10 = remember(all) { all.sortedByDescending { it.rating }.take(10) }
    val trending = remember(all) { all.filter { it.trending } }
    val dramas = remember(all) { all.filter { it.type == ContentType.SHORT_DRAMA } }
    val series = remember(all) { all.filter { it.type == ContentType.WEB_SERIES } }
    val movies = remember(all) { all.filter { it.type == ContentType.MOVIE } }
    val premium = remember(all) { all.filter { it.premium } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(JM.Bg),
        state = listState,
        contentPadding = PaddingValues(bottom = 116.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp)
    ) {
        item(key = "top", contentType = "top") {
            Column(Modifier.padding(horizontal = 16.dp)) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LogoMarkNew(40.dp)
                    Spacer(Modifier.width(6.dp))
                    Wordmark(19)
                    Spacer(Modifier.weight(1f))
                    if (!state.subscription.active) {
                        Row(
                            Modifier
                                .clip(RoundedCornerShape(50))
                                .background(JM.GoldGradient)
                                .clickable { onSub() }
                                .padding(horizontal = 12.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Rounded.WorkspacePremium, null, tint = JM.OnGold, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(5.dp))
                            Text(stringResource(R.string.tag_premium), style = MaterialTheme.typography.labelSmall, color = JM.OnGold)
                        }
                        Spacer(Modifier.width(12.dp))
                    }
                    Avatar(state.session, 36.dp, Modifier.clickable { onProfile() })
                }
                IosSearchBar(onClick = { onSearch(false) }, onMic = { onSearch(true) })
            }
        }
        item(key = "hero", contentType = "hero") {
            HeroPager(hero, state, onOpen, onToggleList)
        }
        if (!state.subscription.active) {
            item(key = "banner", contentType = "banner") { PremiumBanner(onSub) }
        }
        item(key = "trending", contentType = "row") {
            ContentRow(stringResource(R.string.sec_trending), trending, onOpen)
        }
        item(key = "top10", contentType = "top10") { Top10Row(top10, onOpen) }
        item(key = "dramas", contentType = "row") {
            ContentRow(stringResource(R.string.sec_short), dramas, onOpen, aspect = 0.62f, cardWidth = 150)
        }
        item(key = "series", contentType = "row") { ContentRow(stringResource(R.string.sec_series), series, onOpen) }
        item(key = "premium", contentType = "row") { ContentRow(stringResource(R.string.sec_premium), premium, onOpen) }
        item(key = "movies", contentType = "row") { ContentRow(stringResource(R.string.sec_movies), movies, onOpen) }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HeroPager(
    items: List<Title>,
    state: AppState,
    onOpen: (String) -> Unit,
    onToggleList: (String) -> Unit
) {
    if (items.isEmpty()) return
    val pager = rememberPagerState(pageCount = { items.size })

    LaunchedEffect(items.size) {
        while (items.size > 1) {
            delay(5000)
            pager.animateScrollToPage((pager.currentPage + 1) % items.size)
        }
    }

    Box(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(440.dp)
            .clip(RoundedCornerShape(24.dp))
    ) {
        HorizontalPager(state = pager, modifier = Modifier.fillMaxSize(), beyondBoundsPageCount = 1) { page ->
            val t = items[page]
            Box(
                Modifier
                    .fillMaxSize()
                    .clickable { onOpen(t.id) }
            ) {
                PosterArt(t, t.heroUrl, Modifier.fillMaxSize())
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                0.35f to Color.Transparent,
                                0.75f to Color.Black.copy(alpha = 0.65f),
                                1f to Color.Black.copy(alpha = 0.92f)
                            )
                        )
                )
                Column(
                    Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 18.dp, end = 18.dp, bottom = 40.dp)
                ) {
                    Text(
                        typeLabel(t.type).uppercase() + "  •  " + t.genres.joinToString(" / "),
                        style = MaterialTheme.typography.labelSmall,
                        color = JM.Gold
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        t.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "★ ${t.rating}   •   ${t.year}   •   ${t.ageRating}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                    Spacer(Modifier.height(14.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        GoldButton(
                            stringResource(R.string.btn_play),
                            onClick = { onOpen(t.id) },
                            icon = Icons.Rounded.PlayArrow,
                            modifier = Modifier.width(130.dp)
                        )
                        val inList = t.id in state.myList
                        GlassButton(
                            stringResource(if (inList) R.string.btn_in_list else R.string.btn_my_list),
                            onClick = { onToggleList(t.id) },
                            icon = if (inList) Icons.Rounded.Check else Icons.Rounded.Add
                        )
                    }
                }
            }
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            repeat(items.size) { i ->
                val on = i == pager.currentPage
                Box(
                    Modifier
                        .height(6.dp)
                        .width(if (on) 20.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (on) JM.Gold else Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}

@Composable
private fun PremiumBanner(onSub: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(shape)
            .background(JM.FireGradient)
            .clickable { onSub() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(Color.Black.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Rounded.WorkspacePremium, null, tint = Color.White, modifier = Modifier.size(26.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.profile_go_premium), style = MaterialTheme.typography.titleMedium, color = Color.White)
            Text(
                stringResource(R.string.profile_go_premium_sub),
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        Icon(Icons.Rounded.ChevronRight, null, tint = Color.White)
    }
}

@Composable
private fun ContentRow(
    title: String,
    items: List<Title>,
    onOpen: (String) -> Unit,
    aspect: Float = 0.68f,
    cardWidth: Int = 124
) {
    if (items.isEmpty()) return
    Column {
        SectionHeader(title)
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            itemsIndexed(items, key = { _, t -> t.id }) { _, t ->
                PosterCard(t, onClick = { onOpen(t.id) }, width = cardWidth.dp, aspect = aspect)
            }
        }
    }
}

@Composable
private fun Top10Row(items: List<Title>, onOpen: (String) -> Unit) {
    if (items.isEmpty()) return
    Column {
        SectionHeader(stringResource(R.string.sec_top10))
        Spacer(Modifier.height(12.dp))
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            itemsIndexed(items, key = { _, t -> t.id }) { index, t ->
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        "${index + 1}",
                        style = MaterialTheme.typography.displaySmall.copy(
                            fontSize = 84.sp,
                            fontWeight = FontWeight.Black,
                            brush = JM.EmberGradient
                        ),
                        modifier = Modifier
                            .offset(x = 10.dp, y = 14.dp)
                            .padding(bottom = 18.dp)
                    )
                    PosterCard(t, onClick = { onOpen(t.id) }, width = 112.dp, showTitle = false)
                    Spacer(Modifier.width(10.dp))
                }
            }
        }
    }
}
