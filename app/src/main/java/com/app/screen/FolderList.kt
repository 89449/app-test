package com.app.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.app.data.Folder
import com.app.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderList(
    onFolderClick: (Long, String) -> Unit
) {
	val context = LocalContext.current
	var folders by remember { mutableStateOf<List<Folder>>(emptyList()) }
	
	LaunchedEffect(Unit) {
		folders = MediaLoader(context).getImageFolder()
	}
	Column {
		TopAppBar(
			title = { Text("Folders") }
		)
		LazyVerticalGrid(GridCells.Adaptive(180.dp)) {
			items(folders) {
				Column(
					modifier = Modifier
						.padding(2.dp)
						.clickable { onFolderClick(it.id, it.name)}
				) {
					AsyncImage(
						model = it.thumbnailUri,
						contentDescription = null,
						modifier = Modifier
							.aspectRatio(1f),
						contentScale = ContentScale.Crop
					)
					Text(
						text = it.name,
						style = MaterialTheme.typography.titleMedium
					)
					Text(
						text = it.count.toString(),
						style = MaterialTheme.typography.bodySmall
					)
				}
			}
		}
	}
}