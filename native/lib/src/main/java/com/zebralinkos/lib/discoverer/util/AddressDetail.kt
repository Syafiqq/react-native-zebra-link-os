package com.zebralinkos.lib.discoverer.util

import DiscoveryType

data class AddressDetail(
    val address: String,
    val port: Int?,
    val type: DiscoveryType
) {
    override fun toString(): String {
        return "${type.type}:$address${port?.let { ":$it" } ?: ""}"
    }

    companion object {
        fun fromString(address: String): AddressDetail {
            val parts = address.split(":")
            val type = DiscoveryType.entries.firstOrNull { it.type == parts[0] }
                ?: throw IllegalArgumentException("Invalid discovery type: ${parts[0]}")
            val portPart = parts.lastOrNull()?.toIntOrNull()
            val addressPart = parts.drop(1).dropLast(1).joinToString(":")
            return AddressDetail(addressPart, portPart, type)
        }
    }
}
