package com.zebralinkos.lib.connectivity

import android.content.Context
import android.os.Looper
import android.util.Log
import com.zebra.sdk.btleComm.BluetoothLeConnection
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.PrinterLanguage
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.util.internal.Sleeper
import com.zebralinkos.lib.discoverer.util.AddressDetail
import com.zebralinkos.lib.extensions.closeSafely
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ConnectivitySimpleTest(
    address: String,
    private val printTestLabel: Boolean,
    context: Context,
) {
    private val addressDetail = AddressDetail.fromString(address)
    private val contextRef = WeakReference(context)
    private var context: Context? = contextRef.get()

    private var printer: ZebraPrinter? = null
    private var isProcessing = false

    fun start() {
        Thread {
            try {
                Looper.prepare()
                doConnectionTest()
                Looper.loop()
            } catch (e: Exception) {
                cleanup()
                throw e
            } finally {
                Looper.myLooper()?.quit()
            }
        }.start()
    }

    suspend fun startSync() {
        return suspendCancellableCoroutine { continuation ->
            Thread {
                try {
                    Looper.prepare()
                    doConnectionTest()
                    continuation.resume(Unit)
                    Looper.loop()
                } catch (e: Exception) {
                    cleanup()
                    continuation.resumeWithException(e)
                } finally {
                    Looper.myLooper()?.quit()
                }
            }.start()

            continuation.invokeOnCancellation {
                cleanup()
            }
        }
    }

    fun stop() {
        cleanup()
    }

    fun cleanup() {
        disconnect()
    }

    fun disconnect() {
        try {
            printer?.closeSafely()
            printer = null
        } catch (e: ConnectionException) {
            Log.e("PrinterConnectivity", "Unable to close connection", e)
            throw e
        } finally {
            isProcessing = false
        }
    }

    private fun doConnectionTest() {
        isProcessing = true
        val printer = ZebraPrinterFactory.newPrinter(
            addressDetail,
            context,
        )
        this.printer = printer
        if (printer != null && printTestLabel) {
            testPrint()
        }
        disconnect()
    }

    private fun testPrint() {
        try {
            val configLabel = getTestLabel()
            if (configLabel != null) {
                printer?.connection?.write(configLabel)
                Sleeper.sleep(1500)
                if (printer?.connection is BluetoothConnection || printer?.connection is BluetoothLeConnection) {
                    Sleeper.sleep(500)
                }
            }
        } catch (e: ConnectionException) {
            Log.e("PrinterConnectivity", "Print test error", e)
            throw e
        }
    }

    private fun getTestLabel(): ByteArray? {
        val printerLanguage = printer?.printerControlLanguage

        var configLabel: ByteArray? = null
        if (printerLanguage == PrinterLanguage.ZPL) {
            val label = """
                ^XA
                ^FO10,10
                ^A0N,20,20
                ^FDType: ${addressDetail.type}^FS
                ^FO10,30
                ^A0N,20,20
                ^FDAddress: ${addressDetail.address}^FS
                ^XZ
                """.trimIndent().plus("\n").replace("\n", "\r\n")

            configLabel = label.toByteArray()
        } else if ((printerLanguage == PrinterLanguage.CPCL) || (printerLanguage == PrinterLanguage.LINE_PRINT)) {
            val label = """
                ! 0 200 200 350 1
                TEXT 0 2 50 30 ----------------
                TEXT 0 2 50 100 Name: ${addressDetail.type}
                TEXT 0 2 50 170 Address: ${addressDetail.address}
                TEXT 0 2 50 240 ----------------
                FORM
                PRINT
                """.trimIndent().plus("\n").replace("\n", "\r\n")

            configLabel = label.toByteArray()
        }
        return configLabel
    }
}