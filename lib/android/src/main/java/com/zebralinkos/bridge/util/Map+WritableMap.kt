package com.zebralinkos.bridge.util


import com.facebook.react.bridge.*

fun printerDiscoveriesList(printers: List<Map<String, String?>>): WritableArray {
    val array: WritableArray = Arguments.createArray()
    printers.forEach { printer ->
        array.pushMap(printerDiscoveriesMap(printer))
    }
    return array
}

fun printerDiscoveriesMap(printer: Map<String, String?>): WritableMap {
    val map: WritableMap = Arguments.createMap()
    map.putString("name", printer["name"])
    map.putString("urn", printer["urn"])
    map.putString("address", printer["address"])
    map.putString("type", printer["type"])
    return map
}

fun printerManagerPrint(result: Map<String, Boolean>): WritableMap {
    val map: WritableMap = Arguments.createMap()
    result.forEach { (key, value) ->
        map.putBoolean(key, value)
    }
    return map
}
