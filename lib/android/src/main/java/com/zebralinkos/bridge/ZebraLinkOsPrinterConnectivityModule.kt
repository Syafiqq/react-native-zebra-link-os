package com.zebralinkos.bridge

import com.facebook.react.bridge.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.zebralinkos.NativeZebraLinkOsPrinterConnectivitySpec
import com.zebralinkos.lib.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*

@ReactModule(name = ZebraLinkOsPrinterConnectivityModule.NAME)
class ZebraLinkOsPrinterConnectivityModule(reactContext: ReactApplicationContext) :
  NativeZebraLinkOsPrinterConnectivitySpec(reactContext), CoroutineScope by CoroutineScope(Dispatchers.Default) {

  override fun getName(): String {
    return NAME
  }

  override fun connect(map: ReadableMap, promise: Promise) {
    launch {
      try {
        PrinterConnectivity.connect(
          if (map.hasKey("urn")) map.getString("urn") ?: "" else "",
          if (map.hasKey("printTest")) map.getBoolean("printTest") else false,
          this@ZebraLinkOsPrinterConnectivityModule.reactApplicationContext,
        )
        withContext(Dispatchers.Main) {
          promise.resolve(null)
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          promise.reject("PrinterConnectivityError", e.message, e)
        }
      }
    }
  }

  companion object {
    const val NAME = "ZebraLinkOsPrinterConnectivity"
  }
}
