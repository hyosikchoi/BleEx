package com.example.blesample

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.content.ContextCompat
import com.example.blesample.extension.requestPermissionList
import com.example.blesample.ui.screen.BleScreen
import com.example.blesample.ui.theme.BleSampleTheme
import java.util.UUID

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val serviceUUID = UUID.fromString("0000180A-0000-1000-8000-00805f9b34fb")
        val charUUID = UUID.fromString("00002A57-0000-1000-8000-00805f9b34fb")
        val bleService = BluetoothLeService(this, serviceUUID, charUUID)

        val permissionList = arrayOf<String>(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.READ_MEDIA_AUDIO
        )
        // 1. 필요한 권한 리스트가 전부 granted 인지 확인
        val allPermissionsGranted = permissionList.all { permission ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        // 2. 하나라도 denied 가 있으면 권한 요청 시작
        if (allPermissionsGranted.not()) {
            requestPermissionList(
                requestPermissions = permissionList,
                onGranted = {
                    Toast.makeText(this, "전부 허용 완료", Toast.LENGTH_SHORT).show()
                },
                onDenied = { deniedList ->
                    deniedList.forEach { permissionString ->
                        if (shouldShowRequestPermissionRationale(permissionString)) {
                            Toast.makeText(this, "처음 거절 : $permissionString", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "또 거절 : $permissionString", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }

        enableEdgeToEdge()
        setContent {
            BleSampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    var receivedText by remember { mutableStateOf("None") }

                    BleScreen(
                        modifier = Modifier.padding(innerPadding),
                        onScanClick = { bleService.startScan("BLE_SIMULATOR") },
                        onSendClick = { bleService.write("Hello from Compose!") },
                        receivedText = receivedText
                    )

                    // BLE 알림 수신 시 UI 업데이트
                    bleService.onDataReceived = {
                        receivedText = it
                    }
                }
            }
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDestroy() {
        super.onDestroy()
        BluetoothLeService.instance?.disconnect()
    }

}

