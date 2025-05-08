package com.zebralinkos.lib.connectivity

import DiscoveryType
import android.content.Context
import android.util.Log
import com.zebra.sdk.btleComm.BluetoothLeConnection
import com.zebra.sdk.comm.BluetoothConnection
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.comm.TcpConnection
import com.zebra.sdk.printer.ZebraPrinter
import com.zebra.sdk.printer.ZebraPrinterFactory
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException
import com.zebralinkos.lib.discoverer.util.AddressDetail
import com.zebralinkos.lib.extensions.closeSafely

object ZebraPrinterFactory {
    fun newPrinter(address: AddressDetail, context: Context?): ZebraPrinter? {
        val connection: Connection
        when (address.type) {
            DiscoveryType.BLUETOOTH_LE -> {
                connection = BluetoothLeConnection(address.address, context)
            }

            DiscoveryType.BLUETOOTH -> {
                connection = BluetoothConnection(address.address)
            }

            else -> {
                val port = address.port
                val ip = address.address
                connection = TcpConnection(ip, port ?: 0)
            }
        }

        try {
            connection.open()
        } catch (e: ConnectionException) {
            Log.e("ZebraPrinterFactory", "Connection opening error", e)
            connection.closeSafely()
            throw e
        }

        var printer: ZebraPrinter?

        if (connection.isConnected) {
            try {
                printer = ZebraPrinterFactory.getInstance(connection)
            } catch (e: ConnectionException) {
                Log.e("ZebraPrinterFactory", "Printer instantiation error", e)
                printer = null
                connection.closeSafely()
                throw e
            } catch (e: ZebraPrinterLanguageUnknownException) {
                Log.e("ZebraPrinterFactory", "Printer unknown language error", e)
                printer = null
                connection.closeSafely()
                throw e
            }
        } else {
            printer = null
        }

        return printer
    }
}