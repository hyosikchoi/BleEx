package com.example.blesample

import android.Manifest
import android.bluetooth.*
import androidx.annotation.RequiresPermission
import java.util.UUID

class BluetoothGattCallbackHandler(
    private val serviceUUID: UUID,
    private val charUUID: UUID,
    private val onDataReceived: (String) -> Unit
) : BluetoothGattCallback() {

    private var characteristic: BluetoothGattCharacteristic? = null
    private var gatt: BluetoothGatt? = null

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            this.gatt = gatt
            gatt.discoverServices()
        }
    }

    override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
        val service = gatt.getService(serviceUUID)
        characteristic = service?.getCharacteristic(charUUID)

        characteristic?.let {
            gatt.setCharacteristicNotification(it, true)
            val descriptor = it.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"))
            descriptor?.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
            gatt.writeDescriptor(descriptor)
        }
    }

    override fun onCharacteristicChanged(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
        val value = characteristic.value.toString(Charsets.UTF_8)
        onDataReceived(value)
    }

    fun write(data: String) {
        characteristic?.let {
            it.value = data.toByteArray(Charsets.UTF_8)
            gatt?.writeCharacteristic(it)
        }
    }
}
