package com.zebralinkos.lib.extensions

import android.util.Log
import com.zebra.sdk.comm.Connection
import com.zebra.sdk.comm.ConnectionException
import com.zebra.sdk.util.internal.Sleeper

fun Connection.closeSafely() {
    try {
        Sleeper.sleep(1000)
        this.close()
        Sleeper.sleep(500)
    } catch (e: ConnectionException) {
        Log.e("Connection", "Error closing connection", e)
        throw e
    }
}