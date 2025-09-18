package com.app.screen

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable

import com.app.data.Image
import com.app.data.MediaLoader

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageViewer(
    imageId: Long,
    folderId: Long
) {
    val context = LocalContext.current
	var images by remember { mutableStateOf<List<Image>>(emptyList()) }
	var isToolbarVisible by remember { mutableStateOf(true) }
	
	LaunchedEffect(folderId) {
		images = MediaLoader(context).getImageForFolder(folderId)
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
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            isToolbarVisible = !isToolbarVisible
                        }
                    )
                }
        ) {
            HorizontalPager(state = pagerState) { page ->
                val item = images[page]
                val zoomState = rememberZoomState()
                AsyncImage(
                    model = item.uri,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(zoomState = zoomState),
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
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
	} 
}