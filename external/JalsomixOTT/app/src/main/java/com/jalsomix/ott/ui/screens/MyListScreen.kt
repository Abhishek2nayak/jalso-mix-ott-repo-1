package com.jalsomix.ott.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.jalsomix.ott.R
import com.jalsomix.ott.data.AppState
import com.jalsomix.ott.data.SampleData
import com.jalsomix.ott.ui.IosNavBar
import com.jalsomix.ott.ui.PosterCard
import com.jalsomix.ott.ui.theme.JM

@Composable
fun MyListScreen(state: AppState, onBack: () -> Unit, onOpen: (String) -> Unit) {
    val items = remember(state.myList) { SampleData.titles.filter { it.id in state.myList } }

    Column(
        Modifier
            .fillMaxSize()
            .background(JM.Bg)
    ) {
        IosNavBar(stringResource(R.string.mylist_title), onBack)
        if (items.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    stringResource(R.string.mylist_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = JM.Muted,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items, key = { it.id }) { t ->
                    PosterCard(t, onClick = { onOpen(t.id) })
                }
            }
        }
    }
}
