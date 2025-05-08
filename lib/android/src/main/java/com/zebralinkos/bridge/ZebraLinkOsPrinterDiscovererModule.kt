package com.zebralinkos.bridge

import com.facebook.react.bridge.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.zebralinkos.NativeZebraLinkOsPrinterDiscovererSpec
import com.zebralinkos.lib.*
import com.zebralinkos.bridge.util.printerDiscoveriesList
import com.zebralinkos.bridge.util.toPrinterDiscovererDto
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*

@ReactModule(name = ZebraLinkOsPrinterDiscovererModule.NAME)
class ZebraLinkOsPrinterDiscovererModule(reactContext: ReactApplicationContext) :
  NativeZebraLinkOsPrinterDiscovererSpec(reactContext), CoroutineScope by CoroutineScope(Dispatchers.Default) {

  override fun getName(): String {
    return NAME
  }

  override fun discover(map: ReadableMap, promise: Promise) {
    launch {
      try {
        var printers = PrinterDiscoverer.discover(
          this@ZebraLinkOsPrinterDiscovererModule.reactApplicationContext,
          map.toPrinterDiscovererDto()
        )
        withContext(Dispatchers.Main) {
          promise.resolve(printerDiscoveriesList(printers))
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          promise.reject("PrintDiscovererError", e.message, e)
        }
      }
    }
  }

  companion object {
    const val NAME = "ZebraLinkOsPrinterDiscoverer"
  }
}
