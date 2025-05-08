package com.zebralinkos.bridge

import com.facebook.react.bridge.*
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.annotations.ReactModule

import com.zebralinkos.NativeZebraLinkOsPrinterManagerSpec
import com.zebralinkos.lib.*
import com.zebralinkos.bridge.util.printerManagerPrint
import com.zebralinkos.bridge.util.toPrinterManagerJobsDto
import com.zebralinkos.bridge.util.toPrinterManagerAddressesDto
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*

@ReactModule(name = ZebraLinkOsPrinterManagerModule.NAME)
class ZebraLinkOsPrinterManagerModule(reactContext: ReactApplicationContext) :
  NativeZebraLinkOsPrinterManagerSpec(reactContext), CoroutineScope by CoroutineScope(Dispatchers.Default) {

  override fun getName(): String {
    return NAME
  }

  override fun print(map: ReadableMap, promise: Promise) {
    launch {
      try {
        var result = PrinterManager.print(
          this@ZebraLinkOsPrinterManagerModule.reactApplicationContext,
          if (map.hasKey("jobs")) map.getMap("jobs")?.toPrinterManagerJobsDto() ?: mapOf() else mapOf(),
          if (map.hasKey("defaultAddresses")) map.getArray("defaultAddresses")?.toPrinterManagerAddressesDto() ?: listOf() else listOf(),
        )
        withContext(Dispatchers.Main) {
          promise.resolve(printerManagerPrint(result))
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          promise.reject("PrintManagerError", e.message, e)
        }
      }
    }
  }

  companion object {
    const val NAME = "ZebraLinkOsPrinterManager"
  }
}
