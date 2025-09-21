package com.tnrylmz.cihaz_bilgisi.model

data class DeviceInfoItem(
    val label: String,
    val value: String
)

data class DeviceInfoCategory(
    val title: String,
    val iconRes: Int,
    val backgroundColor: Int,
    val items: List<DeviceInfoItem>,
    var isExpanded: Boolean = true
)

enum class DeviceInfoType {
    DEVICE,
    CPU,
    MEMORY,
    STORAGE,
    DISPLAY,
    NETWORK,
    BATTERY,
    SYSTEM,
    HARDWARE
}