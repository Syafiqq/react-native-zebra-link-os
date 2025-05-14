package com.zebralinkos.internal.encryption

import kotlin.random.Random

object Encrypt {
    fun encrypt(input: String, keys: List<Char>): String {
        return input.map { char ->
            var value = char.code
            for (key in keys) {
                value = value xor key.code
            }
            val hexPair = "%02x".format(value)

            hexPair.map { c ->
                if (Random.nextBoolean()) c.uppercaseChar() else c.lowercaseChar()
            }
                .joinToString("")
        }
            .joinToString("")
            .plus("==")
    }
}