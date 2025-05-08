package com.zebralinkos.lib.discoverer

import android.os.Looper
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryException
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import com.zebra.sdk.printer.discovery.NetworkDiscoverer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class DirectedBroadcast {
    suspend fun discover(ip: String): List<DiscoveredPrinter> {
        return suspendCancellableCoroutine { continuation ->
            val discoveredPrinters = mutableListOf<DiscoveredPrinter>()

            val handler = object : DiscoveryHandler {
                override fun foundPrinter(printer: DiscoveredPrinter?) {
                    println("CurrentLog : DirectedBroadcast, foundPrinter")
                    printer?.let { discoveredPrinters.add(it) }
                }

                override fun discoveryFinished() {
                    println("CurrentLog : DirectedBroadcast, discoveryFinished")
                    continuation.resume(discoveredPrinters)
                }

                override fun discoveryError(error: String?) {
                    println("CurrentLog : DirectedBroadcast, discoveryError")
                    continuation.resumeWithException(
                        Exception(error ?: "Unknown discovery error")
                    )
                }
            }

            Thread {
                Looper.prepare()
                try {
                    NetworkDiscoverer.directedBroadcast(handler, ip);
                } catch (e: DiscoveryException) {
                    continuation.resumeWithException(e)
                } finally {
                    Looper.myLooper()?.quit()
                }
            }.start()
            println("CurrentLog : DirectedBroadcast, startDiscover")

            continuation.invokeOnCancellation {}
        }
    }
}