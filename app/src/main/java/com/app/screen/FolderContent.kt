package com.app.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.scale
import coil.compose.AsyncImage

import com.app.data.Image
import com.app.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class,ExperimentalFoundationApi::class)
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
	
	var isSelectionMode by remember { mutableStateOf(false) }
	var selectedItemIds by remember { mutableStateOf<Set<Long>>(emptySet()) }
	
	BackHandler(enabled = isSelectionMode) {
        isSelectionMode = false
        selectedItemIds = emptySet()
    }
	
	val coroutineScope = rememberCoroutineScope()
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            isSelectionMode = false
            selectedItemIds = emptySet()
            coroutineScope.launch {
                images = MediaLoader(context).getImageForFolder(folderId)
            }
        }
    }
	
    Column {
        TopAppBar(
            title = {
                Text(
                    text = if(isSelectionMode) selectedItemIds.size.toString() else folderName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                ) 
            },
            actions = {
                if(isSelectionMode) {
                    IconButton(
                        onClick = {
                            val selectedUris = images.filter { it.id in selectedItemIds }.map { it.uri }
                            if (selectedUris.isNotEmpty()) {
                                val intentSender = MediaLoader(context).deleteMediaItems(selectedUris)
                                deleteLauncher.launch(IntentSenderRequest.Builder(intentSender).build())
                            }
                        }
                    ) {
                        Icon(Icons.Filled.Delete, contentDescription = null)
                    }
                }
            }
        )
        LazyVerticalGrid(GridCells.Adaptive(120.dp)) {
            items(images) {
                val isSelected = selectedItemIds.contains(it.id)
                
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 0.8f else 1f,
                    label = "scale"
                )
                val cornerRadius by animateDpAsState(
                    targetValue = if (isSelected) 16.dp else 0.dp,
                    label = "cornerRadius"
                )

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(2.dp)
                        .combinedClickable(
                            onLongClick = {
                                isSelectionMode = true
                                selectedItemIds += it.id
                            },
                            onClick = {
                                if(isSelectionMode) {
                                    if(isSelected) {
                                        selectedItemIds -= it.id
                                    } else {
                                        selectedItemIds += it.id
                                    }
                                    if(selectedItemIds.isEmpty()) {
                                        isSelectionMode = false
                                    }
                                } else {
                                    onImageClick(it.id)
                                }
                            }
                        ),
                        contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = it.uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale)
                            .clip(RoundedCornerShape(cornerRadius)),
                        contentScale = ContentScale.Crop
                    )
                    if(isSelected) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.TopStart),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}