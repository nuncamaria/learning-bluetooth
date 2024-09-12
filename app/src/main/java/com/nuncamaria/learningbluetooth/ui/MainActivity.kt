package com.nuncamaria.learningbluetooth.ui

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import com.nuncamaria.learningbluetooth.ui.theme.LearningBluetoothTheme

class MainActivity : ComponentActivity() {

    // This has to be in both, BluetoothServerImpl and here in the Activity
    private val bluetoothManager by lazy { applicationContext.getSystemService(BluetoothManager::class.java) }
    private val bluetoothAdapter by lazy { bluetoothManager?.adapter }
    private val isBluetoothEnabled: Boolean
        get() = bluetoothAdapter?.isEnabled == true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val enableBluetoothLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {}

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permission ->
            val canEnableBluetooth = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permission[android.Manifest.permission.BLUETOOTH_CONNECT] == true
            } else {
                true
            }

            if (canEnableBluetooth && !isBluetoothEnabled) {
                enableBluetoothLauncher.launch(
                    Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                )
            }
        }

        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.BLUETOOTH_SCAN,
                android.Manifest.permission.BLUETOOTH_CONNECT
            )
        )

        setContent {
            LearningBluetoothTheme { MainView() }
        }
    }
}