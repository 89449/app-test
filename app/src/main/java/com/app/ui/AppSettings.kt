package com.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppSettings(
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Go back")
                    }
                }
            )
        }
    ) { innerPadding ->
        val context = LocalContext.current
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 8.dp)
                .fillMaxSize()
        ) {
            // Language setting
            ListItem(
                headlineContent = { Text("Language") },
                trailingContent = {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
                },
                modifier = Modifier.clickable { 
                    val intent = Intent(Settings.ACTION_APP_LOCALE_SETTINGS)
                    intent.data = Uri.fromParts("package", context.packageName, null)
                    context.startActivity(intent)
                }
            )
            
            // Show hidden folders setting
            ListItem(
                headlineContent = { Text("Show hidden folders") },
                trailingContent = {
                    var checked by remember { mutableStateOf(false) }
                    Switch(checked = checked, onCheckedChange = { checked = it })
                }
            )
            
            // Dark mode setting
            ListItem(
                headlineContent = { Text("Use device theme color") },
                trailingContent = {
                    var themeChecked by remember { mutableStateOf(false) }
                    Switch(checked = themeChecked, onCheckedChange = { themeChecked = it })
                }
            )
        }
    }
}