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
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.al.qrzen.permissionhandler.CameraPermissionHandler
import com.al.qrzen.permissionhandler.PermissionDeniedMessage
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
        enableEdgeToEdge()
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
    var showSuccessDialog by remember { mutableStateOf(false) }
    var isScanningEnabled by remember { mutableStateOf(true) }
    var isProcessing by remember { mutableStateOf(false) }
    var lastScannedResult by remember { mutableStateOf<String?>(null) } // Track last scanned QR

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
            showSuccessDialog = true
            isScanningEnabled = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (hasCameraPermission) {
            true -> {
                if (isScanningEnabled) {
                    ZenScannerScreen(
                        modifier = Modifier.fillMaxSize(),
                        isScanningEnabled = isScanningEnabled,
                        isFlashEnabled = true,
                        isZoomEnabled = true,
                        isTapToFocusEnabled = true,
                        onQrCodeScanned = { result ->
                            if (isProcessing || result == lastScannedResult) {
                                return@ZenScannerScreen // Ignore if already processing or same QR
                            }

                            isProcessing = true
                            lastScannedResult = result // Store the last scanned result
                            Log.d("QR Code", "Scanned result: $result")

                            val lat = location?.latitude
                            val lon = location?.longitude

                            if (lat != null && lon != null) {
                                cameraViewModel.addSecurityRecord(result, lat, lon)
                            }

                            isProcessing = false
                        }
                    )
                }
            }
            false -> PermissionDeniedMessage()
            null -> Text("Requesting permission...")
        }

        if (showSuccessDialog) {
            AlertDialog(
                onDismissRequest = {
                    showSuccessDialog = false
                    (context as? Activity)?.finish()
                },
                title = { Text("Success") },
                text = { Text("Security record added successfully!") },
                confirmButton = {
                    Button(onClick = {
                        showSuccessDialog = false
                        (context as? Activity)?.finish()
                    }) {
                        Text("OK")
                    }
                }
            )
        }
    }
}

@SuppressLint("MissingPermission")
fun getLastKnownLocation(context: Context): Location? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
}
