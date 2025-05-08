package com.zebralinkos.lib.discoverer

import android.os.Looper
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryException
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import com.zebra.sdk.printer.discovery.NetworkDiscoverer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class SubnetRange {
    suspend fun discover(range: String): List<DiscoveredPrinter> {
        return suspendCancellableCoroutine { continuation ->
            val discoveredPrinters = mutableListOf<DiscoveredPrinter>()

            val handler = object : DiscoveryHandler {
                override fun foundPrinter(printer: DiscoveredPrinter?) {
                    println("CurrentLog : SubnetRange, foundPrinter")
                    printer?.let { discoveredPrinters.add(it) }
                }

                override fun discoveryFinished() {
                    println("CurrentLog : SubnetRange, discoveryFinished")
                    continuation.resume(discoveredPrinters)
                }

                override fun discoveryError(error: String?) {
                    println("CurrentLog : SubnetRange, discoveryError")
                    continuation.resumeWithException(
                        Exception(error ?: "Unknown discovery error")
                    )
                }
            }

            Thread {
                Looper.prepare()
                try {
                    NetworkDiscoverer.subnetSearch(handler, range)
                } catch (e: DiscoveryException) {
                    continuation.resumeWithException(e)
                } finally {
                    Looper.myLooper()?.quit()
                }
            }.start()
            println("CurrentLog : SubnetRange, startDiscover")

            continuation.invokeOnCancellation {}
        }
    }
}