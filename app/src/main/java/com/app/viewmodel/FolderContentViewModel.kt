package com.app.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.Image
import com.app.data.MediaLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderContentViewModel : ViewModel() {

    var images = mutableStateOf<List<Image>>(emptyList())
        private set

    var selectedItemIds = mutableStateOf<Set<Long>>(emptySet())
        private set

    var isSelectionMode = mutableStateOf(false)
        private set

    fun loadImages(context: Context, folderId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            images.value = MediaLoader(context).getImageForFolder(folderId)
        }
    }

    fun toggleSelectionMode() {
        isSelectionMode.value = !isSelectionMode.value
        if (!isSelectionMode.value) {
            selectedItemIds.value = emptySet()
        }
    }
    
    fun toggleSelection(imageId: Long) {
        selectedItemIds.value = if (selectedItemIds.value.contains(imageId)) {
            selectedItemIds.value - imageId
        } else {
            selectedItemIds.value + imageId
        }
        if (selectedItemIds.value.isEmpty()) {
            isSelectionMode.value = false
        }
    }
}