package com.example.blesample.extension

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

// 권한 요청
fun Context.requestPermissionList(
    requestPermissions: Array<String>,
    onGranted: () -> Unit,
    onDenied: (deniedList: List<String>) -> Unit
) {
    if (this is ComponentActivity) {
    val deniedList = mutableListOf<String>()
    var isAllGranted: Boolean = true
    val permissionLauncher = this.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            // permissions 맵은 권한 이름을 키로, Boolean 값을 결과로 가진다.
            permissions.entries.forEach{ permission ->
                if (permission.value.not()){
                    deniedList.add(permission.key)
                    isAllGranted = false
                }
            }

            if (isAllGranted) onGranted()
            else onDenied(deniedList)
        }
        permissionLauncher.launch(requestPermissions)
    }
}
