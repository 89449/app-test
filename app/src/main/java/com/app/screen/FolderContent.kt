package com.app.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.app.data.Image
import com.app.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FolderContent(
    folderId: Long,
    folderName: String,
    onImageClick: (Long) -> Unit
) {
    val context = LocalContext.current
	var images by remember { mutableStateOf<List<Image>>(emptyList()) }
	
	LaunchedEffect(folderId) {
		images = MediaLoader(context).getImageForFolder(folderId)
	}
	
    Column {
        TopAppBar(
        title = { Text(folderName) }
        )
        LazyVerticalGrid(GridCells.Adaptive(120.dp)) {
            items(images) {
                AsyncImage(
                    model = it.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .clickable {
                            onImageClick(it.id)
                        },
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
    
}