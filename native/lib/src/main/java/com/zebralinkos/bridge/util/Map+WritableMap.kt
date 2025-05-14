package com.zebralinkos.bridge.util


import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableArray
import com.facebook.react.bridge.WritableMap

fun printerDiscoveriesList(printers: List<Map<String, String?>>): WritableArray {
    val array: WritableArray = Arguments.createArray()
    printers.forEach { printer ->
        array.pushMap(printerDiscoveriesMap(printer))
    }
    return array
}

fun printerDiscoveriesMap(printer: Map<String, String?>): WritableMap {
    val map: WritableMap = Arguments.createMap()
    printer.forEach { (key, value) ->
        map.putString(key, value)
    }
    return map
}

fun printerManagerPrint(result: Map<String, Boolean>): WritableMap {
    val map: WritableMap = Arguments.createMap()
    result.forEach { (key, value) ->
        map.putBoolean(key, value)
    }
    return map
}
