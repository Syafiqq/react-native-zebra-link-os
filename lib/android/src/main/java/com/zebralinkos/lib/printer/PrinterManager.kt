package com.zebralinkos.lib.printer

import android.content.Context
import android.os.Looper
import android.util.Log
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.util.internal.Sleeper
import com.zebralinkos.lib.connectivity.ZebraPrinterFactory
import com.zebralinkos.lib.discoverer.util.AddressDetail
import com.zebralinkos.lib.extensions.closeSafely
import com.zebralinkos.lib.printer.util.PrintJob
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PrinterManager() {
    private var printer = WeakReference<ZebraPrinter>(null)

    suspend fun print(
        context: Context,
        jobs: Map<String, PrintJob>,
        defaultAddresses: List<String>,
    ): Map<String, Boolean> {
        return suspendCancellableCoroutine { continuation ->
            Thread {
                try {
                    Looper.prepare()
                    val currentAddress = getCurrentAddress(defaultAddresses)
                    val defaultAddress = currentAddress ?: defaultAddresses.firstOrNull()

                    val mappedJob = getMappedJob(currentAddress, jobs, defaultAddress)
                    val result = doPrint(mappedJob, context)
                    continuation.resume(result)
                    Looper.loop()
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                } finally {
                    printer.get()?.closeSafely()
                    printer.clear()
                    Looper.myLooper()?.quit()
                }
            }.start()

            continuation.invokeOnCancellation {
                printer.get()?.closeSafely()
                printer.clear()
            }
        }
    }

    private fun doPrint(
        mappedJob: MutableMap<String, List<PrintJob>>,
        context: Context,
    ): Map<String, Boolean> {
        val result = mutableMapOf<String, Boolean>()
        for ((address, jobList) in mappedJob) {
            val addressDetail = AddressDetail.fromString(address)
            if (printer.get()?.connection?.simpleConnectionName?.contains(addressDetail.address) != true) {
                printer.get()?.closeSafely()
                printer = WeakReference(
                    ZebraPrinterFactory.newPrinter(addressDetail, context)
                )
            }
            val connection = printer.get()?.connection
            for (job in jobList) {
                try {
                    for (i in 0 until job.count) {
                        if (printer.get()?.printerControlLanguage?.name != job.printLanguage) {
                            throw IllegalArgumentException("The job language is not supported")
                        }
                        if (connection?.isConnected == true) {
                            connection.write(job.content.toByteArray())
                            Sleeper.sleep(100)
                        } else {
                            connection?.open()
                            Sleeper.sleep(1000)
                            connection?.write(job.content.toByteArray())
                            Sleeper.sleep(100)
                        }
                    }
                    Sleeper.sleep(1000)
                    result[job.id] = true
                } catch (e: Exception) {
                    Log.e("PrinterManager", "Error printing job ${job.id}", e)
                    result[job.id] = false
                }
            }
            Sleeper.sleep(1000)
        }
        return result;
    }

    private fun getMappedJob(
        currentAddress: String?, jobs: Map<String, PrintJob>, defaultAddress: String?
    ): MutableMap<String, List<PrintJob>> {
        val result = mutableMapOf<String, List<PrintJob>>()
        currentAddress?.let { result[it] = mutableListOf() }

        for ((_, job) in jobs) {
            val address = job.address ?: defaultAddress
            if (address == null) {
                continue
            }

            (result.computeIfAbsent(address) { mutableListOf() } as? MutableList)?.add(job)
        }
        return result
    }

    private fun getCurrentAddress(defaultAddresses: List<String>): String? {
        val defaultAddressesDetail =
            defaultAddresses.associate { it to AddressDetail.fromString(it).address }
        val currentPrinterIdentifier = printer.get()?.connection?.simpleConnectionName
        val defaultAddress =
            defaultAddressesDetail.entries.firstOrNull { currentPrinterIdentifier?.contains(it.value) == true }?.key
        return defaultAddress
    }
}