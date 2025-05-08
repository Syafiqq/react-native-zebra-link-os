package com.zebralinkos.lib.extensions

import com.zebra.sdk.printer.ZebraPrinter

fun ZebraPrinter.closeSafely() {
    this.connection?.closeSafely()
}