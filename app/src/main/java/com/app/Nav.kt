package com.app

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition

import com.app.screen.*

@Composable
fun Nav() {
	val navController = rememberNavController()
	NavHost(
		navController = navController,
		startDestination = "folder_list",
		enterTransition = { EnterTransition.None },
		exitTransition = { ExitTransition.None }
	) {
		composable("folder_list") {
			FolderList(
			    onFolderClick = { folderId, folderName ->
			        navController.navigate("folder_content/$folderId/$folderName")
			    }
			)
		}
		composable("folder_content/{folderId}/{folderName}") {
		    val folderId = it.arguments!!.getString("folderId")!!.toLong()
		    val folderName = it.arguments!!.getString("folderName")!!
		    FolderContent(
		        folderId = folderId,
		        folderName = folderName,
		        onImageClick = { imageId ->
		            navController.navigate("image_viewer/$imageId/$folderId")
		        }
		    )
		}
		composable("image_viewer/{imageId}/{folderId}") {
		    val imageId = it.arguments!!.getString("imageId")!!.toLong()
		    val folderId = it.arguments!!.getString("folderId")!!.toLong()
		    ImageViewer(
		        imageId = imageId,
		        folderId = folderId
		    )
		}
	}
}