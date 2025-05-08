package com.zebralinkos.lib.printer.util

data class PrintJob(
    val id: String,
    val address: String? = null,
    val content: String,
    val count: Int = 1,
    var printLanguage: String?
)
