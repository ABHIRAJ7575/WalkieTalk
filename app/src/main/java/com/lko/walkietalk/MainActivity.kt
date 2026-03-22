package com.lko.walkietalk

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lko.walkietalk.ui.JoinRoomScreen
import com.lko.walkietalk.ui.WalkieTalkieScreen
import com.lko.walkietalk.ui.components.PermissionDialog
import com.lko.walkietalk.ui.theme.WalkieTalkTheme
import com.lko.walkietalk.viewmodel.WalkieTalkieViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WalkieTalkTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WalkieTalkApp(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun WalkieTalkApp(modifier: Modifier = Modifier) {
    val viewModel: WalkieTalkieViewModel = viewModel()
    val context = LocalContext.current
    
    var currentScreen by remember { mutableStateOf("join") }
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }
    var showPermissionDialog by remember { mutableStateOf(!hasMicPermission) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        showPermissionDialog = !isGranted
    }

    // Checking permission effect
    LaunchedEffect(Unit) {
        if (!hasMicPermission) {
            showPermissionDialog = true
        }
    }

    if (showPermissionDialog) {
        PermissionDialog(
            onGrantClicked = {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            },
            onDismissClicked = {
                showPermissionDialog = false
            }
        )
    }

    val roomId by viewModel.currentRoomId.collectAsState()
    val roomState by viewModel.roomState.collectAsState()
    val isTransmitting by viewModel.isTransmitting.collectAsState()

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            (fadeIn(animationSpec = tween(400)) + slideInHorizontally(
                animationSpec = tween(400),
                initialOffsetX = { fullWidth -> fullWidth }
            ) + scaleIn(initialScale = 0.95f, animationSpec = tween(400))).togetherWith(
                fadeOut(animationSpec = tween(300)) + slideOutHorizontally(
                    animationSpec = tween(300),
                    targetOffsetX = { fullWidth -> -fullWidth }
                ) + scaleOut(targetScale = 0.95f, animationSpec = tween(300))
            )
        },
        label = "screenTransition",
        modifier = modifier.fillMaxSize()
    ) { targetScreen ->
        if (targetScreen == "join") {
            JoinRoomScreen(
                onJoinClicked = { code ->
                    if (hasMicPermission) {
                        viewModel.joinRoom(code)
                        currentScreen = "walkie_talkie"
                    } else {
                        showPermissionDialog = true
                    }
                }
            )
        } else {
            WalkieTalkieScreen(
                roomId = roomId,
                roomState = roomState,
                isTransmitting = isTransmitting,
                onPttPressed = { viewModel.setPttActive(true) },
                onPttReleased = { viewModel.setPttActive(false) }
            )
        }
    }
}