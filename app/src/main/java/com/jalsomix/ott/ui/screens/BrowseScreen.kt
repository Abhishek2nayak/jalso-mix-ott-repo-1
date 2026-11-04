package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.ContentType
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.data.forLangs
import com.jalsomix.ott.ui.IosSearchBar
import com.jalsomix.ott.ui.Avatar
import com.jalsomix.ott.ui.IosSegmented
import com.jalsomix.ott.ui.LargeTitle
import com.jalsomix.ott.ui.PosterCard
import com.jalsomix.ott.ui.langLabel
import com.jalsomix.ott.ui.theme.JM

@Composable
fun BrowseScreen(
    type: ContentType,
    state: AppState,
    gridState: LazyGridState = rememberLazyGridState(),
    onOpen: (String) -> Unit,
    onSearch: (Boolean) -> Unit,
    onProfile: () -> Unit
) {
    val all = remember(type, state.contentLangs) {
        SampleData.byType(type).forLangs(state.contentLangs)
    }
    val langs = remember(all) { all.map { it.language }.distinct() }
    var selected by rememberSaveable(type) { mutableStateOf("") }
    val shown = if (selected.isEmpty()) all else all.filter { it.language == selected }

    val heading = when (type) {
        ContentType.SHORT_DRAMA -> R.string.nav_short
        ContentType.WEB_SERIES -> R.string.nav_series
        ContentType.MOVIE -> R.string.nav_movies
    }
    val sub = when (type) {
        ContentType.SHORT_DRAMA -> R.string.browse_short_sub
        ContentType.WEB_SERIES -> R.string.browse_series_sub
        ContentType.MOVIE -> R.string.browse_movies_sub
    }
    val columns = if (type == ContentType.SHORT_DRAMA) 2 else 3
    val aspect = if (type == ContentType.SHORT_DRAMA) 0.66f else 0.68f

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        state = gridState,
        modifier = Modifier
            .fillMaxSize()
            .background(JM.Bg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 116.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item(span = { GridItemSpan(maxLineSpan) }, key = "title") {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    LargeTitle(stringResource(heading), stringResource(sub), Modifier.weight(1f))
                    Avatar(state.session, 38.dp, Modifier.clickable { onProfile() })
                }
                Spacer(Modifier.height(14.dp))
                IosSearchBar(onClick = { onSearch(false) }, onMic = { onSearch(true) })
            }
        }
        item(span = { GridItemSpan(maxLineSpan) }, key = "chips") {
            val options = listOf(stringResource(R.string.chip_all)) + langs.map { langLabel(it) }
            val sel = if (selected.isEmpty()) 0 else langs.indexOf(selected) + 1
            if (langs.size > 1) {
                IosSegmented(options, sel, { i -> selected = if (i == 0) "" else langs[i - 1] })
            }
        }
        items(shown, key = { it.id }) { t ->
            PosterCard(t, onClick = { onOpen(t.id) }, aspect = aspect)
        }
    }
}
