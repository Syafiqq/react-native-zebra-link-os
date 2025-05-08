package com.zebralinkos.lib

import android.content.Context
import com.zebralinkos.lib.connectivity.ConnectivitySimpleTest

object PrinterConnectivity {
    suspend fun connect(
        address: String,
        printTestLabel: Boolean,
        context: Context,
    ) {
        return ConnectivitySimpleTest(
            address,
            printTestLabel,
            context
        ).startSync()
    }
}