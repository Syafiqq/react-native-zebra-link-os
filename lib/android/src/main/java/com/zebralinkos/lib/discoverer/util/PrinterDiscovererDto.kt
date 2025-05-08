package com.zebralinkos.lib.discoverer.util

data class PrinterDiscovererDto(
    val bluetooth: Boolean = false,
    val bluetoothLE: Boolean = false,
    val localBroadcast: Boolean = false,
    val directIpAddress: String? = null,
    val multicastHops: Int? = null,
    val subnetRange: String? = null,
    val nearby: Boolean = false,
)
