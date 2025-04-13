package com.example.blesample

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.*
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Context.BLUETOOTH_SERVICE
import android.util.Log
import androidx.annotation.RequiresPermission
import java.util.UUID

@SuppressLint("StaticFieldLeak")
object BluetoothLeService {

    var instance: BluetoothLeService? = null

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var gatt: BluetoothGatt? = null
    private lateinit var gattCallback: BluetoothGattCallbackHandler
    var onDataReceived: ((String) -> Unit)? = null

    lateinit var context: Context
    lateinit var serviceUUID: UUID
    lateinit var charUUID: UUID

    operator fun invoke(
        ctx: Context,
        serviceUUID: UUID,
        charUUID: UUID
    ): BluetoothLeService {
        instance = this
        this.context = ctx
        this.serviceUUID = serviceUUID
        this.charUUID = charUUID
        bluetoothAdapter = (ctx.getSystemService(BLUETOOTH_SERVICE) as BluetoothManager).adapter
        return this
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    fun startScan(targetName: String) {
        bluetoothAdapter?.bluetoothLeScanner?.startScan(object : ScanCallback() {
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            override fun onScanResult(type: Int, result: ScanResult) {
                Log.d("SCAN", "onScanResult: ${result.device.name}")
                if (result.device.name == targetName) {
                    bluetoothAdapter?.bluetoothLeScanner?.stopScan(this)
                    connect(result.device)
                }
            }
        })
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private fun connect(device: BluetoothDevice) {
        gattCallback = BluetoothGattCallbackHandler(serviceUUID, charUUID) { value ->
            onDataReceived?.invoke(value)
        }
        gatt = device.connectGatt(context, false, gattCallback)
    }

    fun write(data: String) {
        gattCallback.write(data)
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun disconnect() {
        gatt?.disconnect()
        gatt = null
    }
}
