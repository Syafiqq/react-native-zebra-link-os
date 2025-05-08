package com.zebralinkos.lib.discoverer

import android.content.Context
import android.os.Looper
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Bluetooth {
    suspend fun discover(context: Context): List<DiscoveredPrinter> {
        return suspendCancellableCoroutine { continuation ->
            val discoveredPrinters = mutableListOf<DiscoveredPrinter>()

            val handler = object : DiscoveryHandler {
                override fun foundPrinter(printer: DiscoveredPrinter?) {
                    println("CurrentLog : Bluetooth, foundPrinter")
                    printer?.let { discoveredPrinters.add(it) }
                }

                override fun discoveryFinished() {
                    println("CurrentLog : Bluetooth, discoveryFinished")
                    continuation.resume(discoveredPrinters)
                }

                override fun discoveryError(error: String?) {
                    println("CurrentLog : Bluetooth, discoveryError")
                    continuation.resumeWithException(
                        Exception(error ?: "Unknown discovery error")
                    )
                }
            }

            Thread {
                Looper.prepare()
                try {
                    BluetoothDiscoverer.findPrinters(context, handler)
                } catch (e: ConnectionException) {
                    continuation.resumeWithException(e)
                } finally {
                    Looper.myLooper()?.quit()
                }
            }.start()
            println("CurrentLog : Bluetooth, startDiscover")

            continuation.invokeOnCancellation {
            }
        }
    }
}