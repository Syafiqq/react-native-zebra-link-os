package com.zebralinkos.lib

import DiscoveryType
import android.content.Context
import android.util.Log
import com.zebra.sdk.btleComm.DiscoveredPrinterBluetoothLe
import com.zebra.sdk.printer.discovery.DiscoveredPrinter
import com.zebra.sdk.printer.discovery.DiscoveredPrinterBluetooth
import com.zebra.sdk.printer.discovery.DiscoveredPrinterNetwork
import com.zebralinkos.lib.discoverer.Bluetooth
import com.zebralinkos.lib.discoverer.BluetoothLE
import com.zebralinkos.lib.discoverer.DirectedBroadcast
import com.zebralinkos.lib.discoverer.LocalBroadcast
import com.zebralinkos.lib.discoverer.Multicast
import com.zebralinkos.lib.discoverer.Nearby
import com.zebralinkos.lib.discoverer.SubnetRange
import com.zebralinkos.lib.discoverer.util.PrinterDiscovererDto

object PrinterDiscoverer {
    suspend fun discover(
        context: Context,
        parameter: PrinterDiscovererDto? = null,
    ): List<Map<String, String?>> {
        val printers = mutableListOf<Map<String, String?>>()
        if (parameter?.bluetooth == true) {
            try {
                Bluetooth().discover(context).map {
                    it.discoveryDataMapCustom(DiscoveryType.BLUETOOTH)
                }.let(printers::addAll)
            } catch (e: Exception) {
                Log.e("PrinterDiscoverer", "Bluetooth discovery error", e)
            }
        }

        if (parameter?.bluetoothLE == true) {
            try {
                BluetoothLE().discover(context).map {
                    it.discoveryDataMapCustom(DiscoveryType.BLUETOOTH_LE)
                }.let(printers::addAll)
            } catch (e: Exception) {
                Log.e("PrinterDiscoverer", "BluetoothLE discovery error", e)
            }
        }

        if (parameter?.localBroadcast == true) {
            try {
                LocalBroadcast().discover().map {
                    it.discoveryDataMapCustom(DiscoveryType.LOCAL_BROADCAST)
                }.let(printers::addAll)
            } catch (e: Exception) {
                Log.e("PrinterDiscoverer", "Local Broadcast discovery error", e)
            }
        }

        try {
            parameter?.directIpAddress?.let {
                DirectedBroadcast().discover(it).map {
                    it.discoveryDataMapCustom(DiscoveryType.DIRECT_BROADCAST)
                }
            }?.let(printers::addAll)
        } catch (e: Exception) {
            Log.e("PrinterDiscoverer", "Directed Broadcast discovery error", e)
        }

        try {
            parameter?.multicastHops?.let {
                Multicast().discover(context, it).map {
                    it.discoveryDataMapCustom(DiscoveryType.MULTICAST)
                }
            }?.let(printers::addAll)
        } catch (e: Exception) {
            Log.e("PrinterDiscoverer", "Multicast discovery error", e)
        }

        try {
            parameter?.subnetRange?.let {
                SubnetRange().discover(it).map {
                    it.discoveryDataMapCustom(DiscoveryType.SUBNET)
                }
            }?.let(printers::addAll)
        } catch (e: Exception) {
            Log.e("PrinterDiscoverer", "Subnet discovery error", e)
        }

        if (parameter?.nearby == true) {
            try {
                Nearby().discover(context).map {
                    it.discoveryDataMapCustom(DiscoveryType.NEARBY)
                }.let(printers::addAll)
            } catch (e: Exception) {
                Log.e("PrinterDiscoverer", "Nearby discovery error", e)
            }
        }

        return printers
    }
}

private fun DiscoveredPrinter.discoveryDataMapCustom(
    type: DiscoveryType,
): Map<String, String?> {
    val result = mutableMapOf<String, String?>()
    result["type"] = type.type
    val name = this.discoveryDataMap["FRIENDLY_NAME"]
        ?: this.discoveryDataMap["DNS_NAME"]
        ?: this.discoveryDataMap["SERIAL_NUMBER"]
        ?: this.discoveryDataMap["ADDRESS"]

    val btAddress = this.discoveryDataMap["MAC_ADDRESS"]
    val networkAddress = this.discoveryDataMap["ADDRESS"]
    val port = this.discoveryDataMap["PORT_NUMBER"]
    when (this) {
        is DiscoveredPrinterBluetooth -> {
            result["name"] = name
            result["urn"] = "${type.type}:${btAddress ?: ""}:"
            result["address"] = btAddress ?: ""
        }

        is DiscoveredPrinterBluetoothLe -> {
            result["name"] = name
            result["urn"] = "${type.type}:${btAddress ?: ""}:"
            result["address"] = btAddress ?: ""
        }

        is DiscoveredPrinterNetwork -> {
            result["name"] = name
            result["urn"] = "${type.type}:${networkAddress ?: ""}:${port ?: ""}"
            result["address"] = networkAddress ?: ""
        }

        else -> {
            result["name"] = name
            result["urn"] = "${type.type}:${btAddress ?: networkAddress ?: ""}:${port ?: ""}"
            result["address"] = btAddress ?: networkAddress ?: ""
        }
    }
    return result
}
