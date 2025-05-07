package com.zebralinkos.lib.discoverer

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Looper
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveryException
import com.zebra.sdk.printer.discovery.DiscoveryHandler
import com.zebra.sdk.printer.discovery.NetworkDiscoverer
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class Multicast {
    suspend fun discover(context: Context, hops: Int): List<DiscoveredPrinter> {
        return suspendCancellableCoroutine { continuation ->
            val discoveredPrinters = mutableListOf<DiscoveredPrinter>()

            val handler = object : DiscoveryHandler {
                override fun foundPrinter(printer: DiscoveredPrinter?) {
                    printer?.let { discoveredPrinters.add(it) }
                }

                override fun discoveryFinished() {
                    continuation.resume(discoveredPrinters)
                }

                override fun discoveryError(error: String?) {
                    continuation.resumeWithException(
                        Exception(error ?: "Unknown discovery error")
                    )
                }
            }

            val wifi = context.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifi == null) {
                continuation.resume(listOf())
                continuation.invokeOnCancellation {}
                return@suspendCancellableCoroutine
            }

            Thread {
                Looper.prepare()
                try {
                    val lock = wifi.createMulticastLock("wifi_multicast_lock")
                    lock.setReferenceCounted(true)
                    lock.acquire()
                    NetworkDiscoverer.multicast(handler, hops)
                    lock.release()
                } catch (e: DiscoveryException) {
                    continuation.resumeWithException(e)
                } finally {
                    Looper.myLooper()?.quit()
                }
            }.start()

            continuation.invokeOnCancellation {}
        }
    }
}