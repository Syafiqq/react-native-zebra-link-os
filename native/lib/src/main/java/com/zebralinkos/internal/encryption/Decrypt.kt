package com.zebralinkos.internal.encryption

object Decrypt {
    fun decrypt(input: String, keys: List<Char>): String {
        return input.removeSuffix("==")
            .lowercase()
            .chunked(2).map { byteHex ->
                var value = byteHex.toInt(16)
                for (key in keys.reversed()) {
                    value = value xor key.code
                }
                value.toChar()
            }.joinToString("")
    }
}