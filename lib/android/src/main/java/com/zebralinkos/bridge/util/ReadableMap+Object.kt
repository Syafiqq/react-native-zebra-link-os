package com.zebralinkos.bridge.util

import com.facebook.react.bridge.*
import com.zebralinkos.lib.discoverer.util.PrinterDiscovererDto

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
