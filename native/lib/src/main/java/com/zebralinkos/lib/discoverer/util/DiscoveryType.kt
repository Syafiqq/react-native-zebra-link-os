enum class DiscoveryType(val type: String) {
    BLUETOOTH("dt_bluetooth"),
    BLUETOOTH_LE("dt_bluetooth_le"),
    LOCAL_BROADCAST("dt_local_broadcast"),
    DIRECT_BROADCAST("dt_direct_broadcast"),
    MULTICAST("dt_multicast"),
    SUBNET("dt_subnet"),
    NEARBY("dt_nearby"),
}