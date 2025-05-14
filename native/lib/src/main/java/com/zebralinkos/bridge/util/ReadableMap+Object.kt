package com.zebralinkos.bridge.util

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.zebralinkos.lib.discoverer.util.PrinterDiscovererDto
import com.zebralinkos.lib.printer.util.PrintJob

fun ReadableMap.toPrinterDiscovererDto(): PrinterDiscovererDto {
    return PrinterDiscovererDto(
        bluetooth = if (hasKey("bluetooth")) getBoolean("bluetooth") else false,
        bluetoothLE = if (hasKey("bluetoothLE")) getBoolean("bluetoothLE") else false,
        localBroadcast = if (hasKey("localBroadcast")) getBoolean("localBroadcast") else false,
        directIpAddress = if (hasKey("directIpAddress")) getString("directIpAddress") else null,
        multicastHops = if (hasKey("multicastHops")) getInt("multicastHops") else null,
        subnetRange = if (hasKey("subnetRange")) getString("subnetRange") else null,
        nearby = if (hasKey("nearby")) getBoolean("nearby") else false,
    )
}

fun ReadableMap.toPrinterManagerJobsDto(): Map<String, PrintJob> {
    val result = mutableMapOf<String, PrintJob>()
    val iterator = keySetIterator()
    while (iterator.hasNextKey()) {
        val key = iterator.nextKey()
        getMap(key)?.toPrintJob()?.let { result[key] = it }
    }
    return result
}

fun ReadableMap.toPrintJob(): PrintJob {
    return PrintJob(
        id = if (hasKey("id")) getString("id") ?: "" else "",
        address = if (hasKey("address")) getString("address") else null,
        content = if (hasKey("content")) getString("content") ?: "" else "",
        count = if (hasKey("count")) getInt("count") ?: 1 else 1,
        printLanguage = if (hasKey("printLanguage")) getString("printLanguage") ?: "ZPL" else "ZPL",
    )
}

fun ReadableArray.toPrinterManagerAddressesDto(): List<String> {
    val result = mutableListOf<String>()
    for (i in 0 until this.size()) {
        when (this.getType(i)) {
            ReadableType.String -> result.add(this.getString(i))
            else -> {
                // Handle other types if necessary
            }
        }
    }
    return result
}
