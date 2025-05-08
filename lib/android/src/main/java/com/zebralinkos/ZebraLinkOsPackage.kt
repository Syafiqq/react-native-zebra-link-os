package com.zebralinkos

import com.facebook.react.BaseReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.module.model.ReactModuleInfo
import com.facebook.react.module.model.ReactModuleInfoProvider
import java.util.HashMap
import com.zebralinkos.bridge.*

class ZebraLinkOsPackage : BaseReactPackage() {
  override fun getModule(name: String, reactContext: ReactApplicationContext): NativeModule? {
    return when (name) {
        ZebraLinkOsPrinterDiscovererModule.NAME -> ZebraLinkOsPrinterDiscovererModule(reactContext)
        else -> null
    }
  }

  override fun getReactModuleInfoProvider(): ReactModuleInfoProvider {
    return ReactModuleInfoProvider {
      val moduleInfos: MutableMap<String, ReactModuleInfo> = HashMap()
      moduleInfos[ZebraLinkOsPrinterDiscovererModule.NAME] = ReactModuleInfo(
        ZebraLinkOsPrinterDiscovererModule.NAME,
        ZebraLinkOsPrinterDiscovererModule.NAME,
        false,  // canOverrideExistingModule
        false,  // needsEagerInit
        false,  // isCxxModule
        true // isTurboModule
      )
      moduleInfos
    }
  }
}
