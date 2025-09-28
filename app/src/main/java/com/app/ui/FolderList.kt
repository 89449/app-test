package com.app.ui

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
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.input.ImeAction // For ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.R
import com.app.data.Folder
import com.app.data.MediaLoader
import com.app.viewmodel.FolderViewModel

import android.util.Log

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderList(
    onFolderClick: (Long, String) -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: FolderViewModel = viewModel()
) {
	val context = LocalContext.current
	
	var active by remember { mutableStateOf(false) }
	var searchQuery by remember { mutableStateOf("") }
	
	LaunchedEffect(Unit) {
		viewModel.loadFolders(context)
	}
	
	val folders = viewModel.folders.value
	
	
	val filteredFolders = remember(folders, searchQuery) {
        if (searchQuery.isBlank()) {
            folders
        } else {
            folders.filter { 
                it.name.contains(searchQuery, ignoreCase = true) 
            }
        }
    }
	
	Scaffold(
	    topBar = {
	        DockedSearchBar(
	            query = searchQuery,
	            onQueryChange = { searchQuery = it },
	            onSearch = { active = false },
	            active = active,
                onActiveChange = { active = it }
	        )
	    }
	) {
	    LazyVerticalGrid(
    	    GridCells.Adaptive(180.dp),
    	    contentPadding = it
	    ) {
			items(filteredFolders) {
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