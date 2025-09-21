package com.app.screen

import androidx.compose.runtime.Composable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextOverflow
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.IntentSenderRequest
import androidx.activity.compose.BackHandler
import android.app.Activity
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.scale
import coil3.compose.AsyncImage
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.collectAsState
import com.app.data.MediaLoader
import com.app.viewmodel.FolderContentViewModel
import android.widget.Toast

import android.util.Log

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun FolderContent(
    folderId: Long,
    folderName: String,
    onImageClick: (Long) -> Unit,
    viewModel: FolderContentViewModel = viewModel()
) {
    val context = LocalContext.current

    val images = viewModel.images.value
    val isSelectionMode = viewModel.isSelectionMode.value
    val selectedItemIds = viewModel.selectedItemIds.value
    
    LaunchedEffect(folderId) {
        viewModel.loadImages(context, folderId)
    }
    
    if(images.isEmpty()) {
        Log.d("IS EMPTY","?EMPTY")
    }

    BackHandler(enabled = isSelectionMode) {
        viewModel.toggleSelectionMode()
    }
    
    val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        when(result.resultCode) {
            Activity.RESULT_OK -> {
                val deletedCount = selectedItemIds.size
                Toast.makeText(context, if (deletedCount == 1) "Image deleted" else "$deletedCount images deleted", Toast.LENGTH_SHORT).show()
                viewModel.loadImages(context, folderId)
                viewModel.toggleSelectionMode()
            }
            Activity.RESULT_CANCELED -> {
                
            }
            else -> {
                Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column {
        TopAppBar(
            title = {
                Text(
                    text = if(isSelectionMode) "" else folderName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                
            },
            actions = {
                if (isSelectionMode) {
                    FilledTonalButton(
                        onClick = {
                            if (selectedItemIds.size == images.size) {
                                viewModel.clearSelection()
                            } else {
                                val allImageIds = images.map { it.id }.toSet()
                                viewModel.setSelection(allImageIds)
                            }
                        }
                    ) {
                        Text(if(selectedItemIds.size == images.size) "Deselect All" else "Select All")
                    }
                    TextButton(
                        onClick = {
                            val selectedUris = images
                                .filter { it.id in selectedItemIds }
                                .map { it.uri }
                            if (selectedUris.isNotEmpty()) {
                                val intentSender = MediaLoader(context).deleteMediaItems(selectedUris)
                                val request = IntentSenderRequest.Builder(intentSender).build()
                                deleteLauncher.launch(request)
                            }
                        },
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Delete (${selectedItemIds.size})")
                    }
                }
            }
        )
        LazyVerticalGrid(
            columns = GridCells.Adaptive(120.dp)
        ) {
            items(images) { image ->
                val isSelected = selectedItemIds.contains(image.id)
                
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
                                viewModel.toggleSelection(image.id)
                                viewModel.toggleSelectionMode() 
                            },
                            onClick = {
                                if (isSelectionMode) {
                                    viewModel.toggleSelection(image.id)
                                } else {
                                    onImageClick(image.id)
                                }
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = image.uri,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(scale)
                            .clip(RoundedCornerShape(cornerRadius)),
                        contentScale = ContentScale.Crop
                    )
                    if (isSelected) {
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