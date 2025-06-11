package com.doni.simling.views.activities

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.al.qrzen.scanner.BorderScanner
import com.al.qrzen.scanner.CameraPermissionHandler
import com.al.qrzen.scanner.PermissionDeniedMessage
import com.al.qrzen.scanner.ZenScannerScreen

class CameraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }
}


@Composable
fun MainScreen() {
    var hasCameraPermission by remember { mutableStateOf<Boolean?>(null) }

    CameraPermissionHandler { granted ->
        hasCameraPermission = granted
    }

    when (hasCameraPermission) {
        true -> {
            ZenScannerScreen(
                modifier = Modifier.fillMaxSize(),
                isScanningEnabled = true,
                isFlashEnabled = true,
                onQrCodeScanned = { result ->
                    Log.d("QR Code", "Scanned result: $result")
                }
            )
        }
        false -> PermissionDeniedMessage()
        null -> Text("Requesting permission...")
    }
}