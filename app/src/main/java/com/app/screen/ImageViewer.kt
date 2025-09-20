package com.app.screen

import android.net.Uri
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Edit
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import android.app.Activity
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope
import android.content.Intent
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

import com.app.data.Image
import com.app.data.MediaLoader
import com.app.viewmodel.FolderContentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewer(
    imageId: Long,
    folderId: Long,
    viewModel: FolderContentViewModel = viewModel()
) {
    val context = LocalContext.current
	var isToolbarVisible by remember { mutableStateOf(true) }
	
	val deleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
            viewModel.loadImages(context, folderId)
        } else {
            Toast.makeText(context, "Deletion failed", Toast.LENGTH_SHORT).show()
        }
    }
    
    fun getEditIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_EDIT).apply {
            setDataAndType(uri, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
    fun createShareIntent(uri: Uri): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
	
	val images = viewModel.images.value
	
	LaunchedEffect(folderId) {
		viewModel.loadImages(context, folderId)
	}
	
	LaunchedEffect(isToolbarVisible) {
        val window = (context as? android.app.Activity)?.window ?: return@LaunchedEffect
        val insetsController = WindowCompat.getInsetsController(window, window.decorView)
        if (isToolbarVisible) {
            insetsController.show(WindowInsetsCompat.Type.systemBars())
        } else {
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
        }
    }
	
	if(images.isNotEmpty()) {
	    val startingIndex = images.indexOfFirst { it.id == imageId }
        val pagerState = rememberPagerState( initialPage = if (startingIndex == -1) 0 else startingIndex ) {
            images.size
        }
        val currentItem = images[pagerState.currentPage]
        Box(
            modifier = Modifier
                .background(Color.Black)
        ) {
            HorizontalPager(state = pagerState) { page ->
                val item = images[page]
                val zoomState = rememberZoomState()
                AsyncImage(
                    model = item.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState = zoomState, onTap = { isToolbarVisible = !isToolbarVisible} ),
                    contentScale = ContentScale.Fit
                )
            }
            if(isToolbarVisible) {
                TopAppBar(
                    title = { 
                        Text(
                            text = currentItem.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    actions = {
                        IconButton(
                            onClick = {
                                val currentImageUri = images[pagerState.currentPage].uri
                                val editIntent = getEditIntent(currentImageUri)
                                context.startActivity(Intent.createChooser(editIntent, null))
                            }
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                val currentImageUri = images[pagerState.currentPage].uri
                                val shareIntent = createShareIntent(currentImageUri)
                                context.startActivity(Intent.createChooser(shareIntent, null))
                            }
                        ) {
                            Icon(Icons.Filled.Share, contentDescription = null)
                        }
                        IconButton(
                            onClick = {
                                currentItem?.let { item ->
                                    try {
                                        val intentSender = MediaLoader(context).deleteMediaItems(listOf(item.uri))
                                        val request = IntentSenderRequest.Builder(intentSender).build()
                                        deleteLauncher.launch(request)
                                    } catch (e: Exception) {
                                    
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Filled.Delete, contentDescription = null)
                        }
                    },
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
	} 
}