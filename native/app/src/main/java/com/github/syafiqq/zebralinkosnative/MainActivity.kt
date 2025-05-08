package com.github.syafiqq.zebralinkosnative

import DiscoveryType
import PrinterManager
import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import com.zebralinkos.lib.PrinterConnectivity
import com.zebralinkos.lib.printer.util.PrintJob
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            insets
        }
    }

    fun onRequestDiscoverTapped(view: View) {
        onRequestDiscover()
    }

    fun onRequestDiscover() {
        if (checkBluetoothPermission()) {
            if (enableBluetooth()) {
                startActivity(DiscoverPrinterActivity.newIntent(this@MainActivity))
            }
        }
    }

    fun onRequestTestTapped(view: View) {
        onRequestTest()
    }

    fun onRequestTest() {
        lifecycleScope.launch {
            Log.d("CurrentLog", "onRequestTest - start")
            PrinterConnectivity.connect(
                "${DiscoveryType.BLUETOOTH.type}:A4:DA:32:85:6E:99:",
                true,
                this@MainActivity
            )
            Log.d("CurrentLog", "onRequestTest - finish")
        }
    }

    fun onBulkPrintTapped(view: View) {
        onBulkPrint()
    }

    fun onBulkPrint() {
        lifecycleScope.launch {
            try {
                Log.d("CurrentLog", "onBulkPrint - start")
                val result = PrinterManager.print(
                    this@MainActivity,
                    mapOf(
                        "1" to PrintJob(
                            id = "1",
                            address = "${DiscoveryType.BLUETOOTH.type}:A4:DA:32:85:6E:99:",
                            content = """
                        ^XA
                        ^FO10,10
                        ^A0N,20,20
                        ^FDType: Testing^FS
                        ^XZ
                        """.trimIndent().plus("\n").replace("\n", "\r\n"),
                            printLanguage = "ZPL"
                        ),
                        "2" to PrintJob(
                            id = "2",
                            address = "${DiscoveryType.BLUETOOTH.type}:00:22:58:3C:D7:87:",
                            content = """
                        ! 0 200 200 350 1
                        TEXT 0 2 50 100 Testing
                        FORM
                        PRINT
                        """.trimIndent().plus("\n").replace("\n", "\r\n"),
                            printLanguage = "CPCL"
                        )
                    ),
                    listOf()
                )
                Log.d("CurrentLog", "onBulkPrint - finish - $result")
            } catch (e: Exception) {
                Log.e("CurrentLog", "onBulkPrint - error", e)
            }
        }
    }

    private fun checkBluetoothPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_DENIED || ContextCompat.checkSelfPermission(
                    this, Manifest.permission.BLUETOOTH_SCAN
                ) == PackageManager.PERMISSION_DENIED
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(
                        Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN
                    ), 3000
                )
            } else {
                if ((ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED)
                ) {
                    ActivityCompat.requestPermissions(
                        this, arrayOf<String>(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ), 4000
                    )
                } else {
                    return true
                }
            }
        } else {
            if ((ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) || (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED)
            ) {
                ActivityCompat.requestPermissions(
                    this, arrayOf<String>(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ), 4000
                )
            } else {
                return true
            }
        }
        return false
    }

    private fun enableBluetooth(): Boolean {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter != null) {
            if (!bluetoothAdapter.isEnabled) {
                Toast.makeText(this, "Please switch on the bluetooth", Toast.LENGTH_SHORT).show()
            } else {
                return true
            }
        } else {
            // Device does not support Bluetooth
        }
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRequestDiscover()
            } else {
                // Location permission denied, show a message or handle accordingly
                Toast.makeText(
                    this, "Location permission required for Bluetooth scanning", Toast.LENGTH_SHORT
                ).show()
            }
        } else if (requestCode == 4000) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRequestDiscover()
            } else {
                // Location permission denied, show a message or handle accordingly
                Toast.makeText(
                    this, "Location permission required for Bluetooth scanning", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}