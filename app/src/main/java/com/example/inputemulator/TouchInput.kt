package com.example.inputemulator

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class TouchInput {
    enum class ErrorCode {
        PERMISSION_DENIED,
    }

    interface Listener {
        fun onError(code: ErrorCode, message: String) {}
        fun onSuccess() {}
        fun onConnect() {}
    }

    private var _listener: Listener = object : Listener {}
    private var _hidDevice: BluetoothHidDevice? = null
    private var _hostDevice: BluetoothDevice? = null

    //@formatter:off
    private val _hidReportDesp: ByteArray = byteArrayOf(
        0x05.toByte(), 0x01.toByte(),       // Usage Page (Generic Desktop)
        0x09.toByte(), 0x02.toByte(),       // Usage (Mouse)
        0xA1.toByte(), 0x01.toByte(),       // Collection (Application)
        0x09.toByte(), 0x01.toByte(),       //   Usage (Pointer)
        0xA1.toByte(), 0x00.toByte(),       //   Collection (Physical)
        0x05.toByte(), 0x09.toByte(),       //     Usage Page (Buttons)
        0x19.toByte(), 0x01.toByte(),       //     Usage Minimum (01)
        0x29.toByte(), 0x03.toByte(),       //     Usage Maximum (03)
        0x15.toByte(), 0x00.toByte(),       //     Logical Minimum (0)
        0x25.toByte(), 0x01.toByte(),       //     Logical Maximum (1)
        0x95.toByte(), 0x03.toByte(),       //     Report Count (3)
        0x75.toByte(), 0x01.toByte(),       //     Report Size (1)
        0x81.toByte(), 0x02.toByte(),       //     Input (Data, Variable, Absolute) [3 button bits]
        0x95.toByte(), 0x01.toByte(),       //     Report Count (1)
        0x75.toByte(), 0x05.toByte(),       //     Report Size (5)
        0x81.toByte(), 0x03.toByte(),       //     Input (Constant, Variable, Absolute) [5 bit padding]
        0x05.toByte(), 0x01.toByte(),       //     Usage Page (Generic Desktop)
        0x09.toByte(), 0x30.toByte(),       //     Usage (X)
        0x09.toByte(), 0x31.toByte(),       //     Usage (Y)
        0x15.toByte(), 0x81.toByte(),       //     Logical Minimum (-127)
        0x25.toByte(), 0x7F.toByte(),       //     Logical Maximum (127)
        0x75.toByte(), 0x08.toByte(),       //     Report Size (8)
        0x95.toByte(), 0x02.toByte(),       //     Report Count (2)
        0x81.toByte(), 0x06.toByte(),       //     Input (Data, Variable, Relative) [X & Y movement]
        0xC0.toByte(),                   //   End Collection
        0xC0.toByte()                    // End Collection
    )
    //@formatter:on
    private val _sdkSetting: BluetoothHidDeviceAppSdpSettings = BluetoothHidDeviceAppSdpSettings(
        "Touch", "Touch", "Phone", BluetoothHidDevice.SUBCLASS1_MOUSE, _hidReportDesp
    )


    @RequiresPermission(allOf = [Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_ADVERTISE])
    fun start(context: Context, listener: Listener = object : Listener {}) {
        _listener = listener
        if (!checkPermission(context)) {
            onError(ErrorCode.PERMISSION_DENIED);
            return
        }
        val intent = Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE)
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300)
        context.startActivity(intent)
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter
        bluetoothAdapter.getProfileProxy(
            context,
            object : BluetoothProfile.ServiceListener {
                override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        _hidDevice = proxy as BluetoothHidDevice
                        registerApp(context)
                    }
                }

                @SuppressLint("MissingPermission")
                override fun onServiceDisconnected(profile: Int) {
                    if (profile == BluetoothProfile.HID_DEVICE) {
                        unregisterApp()
                        _hidDevice = null
                    }
                }
            },
            BluetoothProfile.HID_DEVICE
        )

    }

    private fun checkPermission(context: Context): Boolean {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_ADMIN
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.BLUETOOTH_ADVERTISE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        return true
    }

    @SuppressLint("MissingPermission")
    private fun registerApp(context: Context) {
        _hidDevice?.registerApp(
            _sdkSetting,
            null,
            null,
            context.mainExecutor,
            object : BluetoothHidDevice.Callback() {
                override fun onAppStatusChanged(
                    pluggedDevice: BluetoothDevice?,
                    registered: Boolean
                ) {
                }

                override fun onConnectionStateChanged(
                    device: BluetoothDevice?,
                    state: Int
                ) {
                    if (state == BluetoothProfile.STATE_CONNECTED) {
                        if (device != null) {
                            _hostDevice = device
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("MissingPermission")
    private fun unregisterApp() {
        _hidDevice?.unregisterApp()
    }

    private fun onError(code: ErrorCode, message: String = "") {
        _listener.onError(code, message)
    }
}
