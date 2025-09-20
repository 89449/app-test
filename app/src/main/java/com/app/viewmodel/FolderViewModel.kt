package com.app.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.data.Folder
import com.app.data.MediaLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FolderViewModel : ViewModel() {

    var folders = mutableStateOf<List<Folder>>(emptyList())
        private set

    fun loadFolders(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val loadedFolders = MediaLoader(context).getImageFolder()
            folders.value = loadedFolders
        }
    }
}