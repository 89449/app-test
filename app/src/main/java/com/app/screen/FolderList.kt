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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import com.app.R
import com.app.data.Folder
import com.app.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderList(
    onFolderClick: (Long, String) -> Unit,
    onSettingsClick: () -> Unit
) {
	val context = LocalContext.current
	var folders by remember { mutableStateOf<List<Folder>>(emptyList()) }
	
	LaunchedEffect(Unit) {
		folders = MediaLoader(context).getImageFolder()
	}
	Column {
		TopAppBar(
			title = { Text(stringResource(R.string.folders_title)) },
			actions = {
			    IconButton(onClick =  onSettingsClick ) {
			        Icon(Icons.Filled.Settings, contentDescription = null)
			    }
			}
		)
		LazyVerticalGrid(GridCells.Adaptive(180.dp)) {
			items(folders) {
				Column(
					modifier = Modifier
						.padding(8.dp)
						.clickable { onFolderClick(it.id, it.name)}
				) {
					AsyncImage(
						model = it.thumbnailUri,
						contentDescription = null,
						modifier = Modifier
							.aspectRatio(1f)
							.clip(RoundedCornerShape(16.dp)),
						contentScale = ContentScale.Crop
					)
					Column(modifier = Modifier.padding(4.dp)) {
					    Text(
    						text = it.name,
    						maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
    						style = MaterialTheme.typography.titleMedium
    					)
    					Text(
    						text = it.count.toString(),
    						maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
    						style = MaterialTheme.typography.bodyMedium
    					)
					}
				}
			}
		}
	}
}