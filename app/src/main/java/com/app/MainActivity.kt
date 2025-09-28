package com.app

import android.os.Bundle
import android.Manifest
import androidx.activity.enableEdgeToEdge
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier

import com.app.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		enableEdgeToEdge()
		super.onCreate(savedInstanceState)
		setContent {
			val isGranted: MutableState<Boolean> = remember { mutableStateOf(value = false) }
			val launcher = rememberLauncherForActivityResult(
		        contract = ActivityResultContracts.RequestPermission(),
		        onResult = { isGranted.value = it }
		    )
		
		    LaunchedEffect(Unit) {
		        launcher.launch(Manifest.permission.READ_MEDIA_IMAGES)
		    }
		    AppTheme {
		    	Surface(modifier = Modifier.fillMaxSize()) {
		    		if(isGranted.value) Nav()
		    	}
		    }
		}
    }
}