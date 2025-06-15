package com.doni.simling.views.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.activity.viewModels
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.al.qrzen.permissionhandler.CameraPermissionHandler
import com.al.qrzen.permissionhandler.PermissionDeniedMessage
import com.al.qrzen.scanner.BorderQRScanner
import com.al.qrzen.scanner.ZenScannerScreen
import com.doni.simling.helper.Resource
import com.doni.simling.viewmodels.CameraViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.doni.simling.helper.liveDataAsState


@AndroidEntryPoint
class CameraActivity : AppCompatActivity() {

    private val cameraViewModel: CameraViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(cameraViewModel)
        }
    }
}


@Composable
fun MainScreen(cameraViewModel: CameraViewModel) {
    var hasCameraPermission by remember { mutableStateOf<Boolean?>(null) }
    var hasLocationPermission by remember { mutableStateOf(false) }
    var location: Location? by remember { mutableStateOf(null) }
    var showDialog by remember { mutableStateOf(false) }
    var scanningEnabled by remember { mutableStateOf(true) }

    CameraPermissionHandler { granted ->
        hasCameraPermission = granted
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasLocationPermission = granted
    }

    LaunchedEffect(Unit) {
        locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    val context = LocalContext.current
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            location = getLastKnownLocation(context)
        }
    }

    val addSecurityRecordState = liveDataAsState(cameraViewModel.addSecurityRecord).value

    LaunchedEffect(addSecurityRecordState) {
        if (addSecurityRecordState is Resource.Success) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text("Success") },
            text = { Text("Security record added successfully!") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    (context as? Activity)?.finish()
                }) {
                    Text("OK")
                }
            }
        )
    }

    when (hasCameraPermission) {
        true -> {
            BorderQRScanner(
                modifier = Modifier.fillMaxSize(),
                isScanningEnabled = scanningEnabled,
                isFlashEnabled = true,
                isZoomEnabled = true,
                isTapToFocusEnabled = true,
                onQrCodeScanned = { result ->
                    val block = result
                    val lat = location?.latitude
                    val lon = location?.longitude
                    if (lat != null && lon != null) {
                        scanningEnabled = false // Stop scanning
                        cameraViewModel.addSecurityRecord(block, lat, lon)
                    }
                }
            )
        }
        false -> PermissionDeniedMessage()
        null -> Text("Requesting permission...")
    }
}


@SuppressLint("MissingPermission")
fun getLastKnownLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
}