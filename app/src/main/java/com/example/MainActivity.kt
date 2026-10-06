package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.DirectoryScreen
import com.example.ui.screens.OtpLoginScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.DirectoryViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        enable120HzHighRefreshRate()

        setContent {
            MyApplicationTheme {
                val viewModel: DirectoryViewModel = viewModel()
                val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
                val otpMobileInput by viewModel.otpMobileInput.collectAsStateWithLifecycle()
                val isOtpSent by viewModel.isOtpSent.collectAsStateWithLifecycle()
                val generatedOtp by viewModel.generatedOtp.collectAsStateWithLifecycle()
                val pendingPersonnel by viewModel.pendingPersonnel.collectAsStateWithLifecycle()
                val loginError by viewModel.loginError.collectAsStateWithLifecycle()

                // Request phone call permission gracefully
                val callPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { /* Handled gracefully by CallHelper fallback to dialer */ }

                LaunchedEffect(Unit) {
                    if (ContextCompat.checkSelfPermission(
                            this@MainActivity,
                            Manifest.permission.CALL_PHONE
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        callPermissionLauncher.launch(Manifest.permission.CALL_PHONE)
                    }
                }

                Surface(modifier = Modifier.fillMaxSize()) {
                    if (!isLoggedIn) {
                        OtpLoginScreen(
                            mobileInput = otpMobileInput,
                            onMobileChange = { viewModel.onOtpMobileChange(it) },
                            isOtpSent = isOtpSent,
                            generatedOtp = generatedOtp,
                            pendingPersonnel = pendingPersonnel,
                            loginError = loginError,
                            onSendOtp = { viewModel.sendOtp() },
                            onVerifyOtp = { code -> viewModel.verifyOtp(code) },
                            onResendOtp = { viewModel.resendOtp() },
                            onBackToMobile = { viewModel.onOtpMobileChange("") },
                            onQuickSelectMobile = { phone ->
                                viewModel.onOtpMobileChange(phone)
                                viewModel.sendOtp()
                            }
                        )
                    } else {
                        DirectoryScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }

    /**
     * Enables 120Hz display refresh rate mode where supported by device hardware and display.
     */
    private fun enable120HzHighRefreshRate() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val displayObj = display
                val supportedModes = displayObj?.supportedModes ?: emptyArray()
                val maxRefreshMode = supportedModes.maxByOrNull { it.refreshRate }
                if (maxRefreshMode != null && maxRefreshMode.refreshRate >= 90f) {
                    val layoutParams = window.attributes
                    layoutParams.preferredDisplayModeId = maxRefreshMode.modeId
                    window.attributes = layoutParams
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                @Suppress("DEPRECATION")
                val modes = windowManager.defaultDisplay.supportedModes
                val maxRefreshMode = modes.maxByOrNull { it.refreshRate }
                if (maxRefreshMode != null && maxRefreshMode.refreshRate >= 90f) {
                    val layoutParams = window.attributes
                    layoutParams.preferredDisplayModeId = maxRefreshMode.modeId
                    window.attributes = layoutParams
                }
            }
        } catch (_: Exception) {
            // Silently handled on emulators or fixed-frequency displays
        }
    }
}
