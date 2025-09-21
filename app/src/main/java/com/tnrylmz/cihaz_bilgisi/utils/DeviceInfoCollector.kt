package com.tnrylmz.cihaz_bilgisi.utils

import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.BatteryManager
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.tnrylmz.cihaz_bilgisi.R
import com.tnrylmz.cihaz_bilgisi.model.DeviceInfoCategory
import com.tnrylmz.cihaz_bilgisi.model.DeviceInfoItem
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.net.NetworkInterface
import java.text.DecimalFormat
import kotlin.math.pow
import kotlin.math.sqrt

class DeviceInfoCollector(private val context: Context) {

    fun collectAllDeviceInfo(): List<DeviceInfoCategory> {
        return listOf(
            collectDeviceInfo(),
            collectCpuInfo(),
            collectMemoryInfo(),
            collectStorageInfo(),
            collectDisplayInfo(),
            collectNetworkInfo(),
            collectBatteryInfo(),
            collectSystemInfo(),
            collectHardwareInfo()
        )
    }

    private fun collectDeviceInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        
        items.add(DeviceInfoItem(context.getString(R.string.label_manufacturer), Build.MANUFACTURER))
        items.add(DeviceInfoItem(context.getString(R.string.label_model), Build.MODEL))
        items.add(DeviceInfoItem(context.getString(R.string.label_brand), Build.BRAND))
        items.add(DeviceInfoItem(context.getString(R.string.label_product), Build.PRODUCT))
        items.add(DeviceInfoItem(context.getString(R.string.label_device_name), Build.DEVICE))
        
        try {
            val deviceId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            items.add(DeviceInfoItem(context.getString(R.string.label_device_id), deviceId ?: context.getString(R.string.status_unknown)))
        } catch (e: Exception) {
            items.add(DeviceInfoItem(context.getString(R.string.label_device_id), context.getString(R.string.status_unknown)))
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            items.add(DeviceInfoItem(context.getString(R.string.label_serial_number), Build.getSerial()))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_device),
            iconRes = R.drawable.ic_device_info,
            backgroundColor = R.color.device_info_cpu,
            items = items
        )
    }

    private fun collectCpuInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        
        items.add(DeviceInfoItem(context.getString(R.string.label_cpu_cores), Runtime.getRuntime().availableProcessors().toString()))
        items.add(DeviceInfoItem(context.getString(R.string.label_cpu_architecture), Build.HARDWARE))
        items.add(DeviceInfoItem(context.getString(R.string.label_cpu_abi), Build.SUPPORTED_ABIS.joinToString(", ")))
        
        // Try to get CPU frequency
        try {
            val cpuFreq = getCpuFrequency()
            items.add(DeviceInfoItem(context.getString(R.string.label_cpu_frequency), "$cpuFreq ${context.getString(R.string.unit_mhz)}"))
        } catch (e: Exception) {
            items.add(DeviceInfoItem(context.getString(R.string.label_cpu_frequency), context.getString(R.string.status_unknown)))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_cpu),
            iconRes = R.drawable.ic_cpu,
            backgroundColor = R.color.device_info_cpu,
            items = items
        )
    }

    private fun collectMemoryInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memInfo)
        
        val totalMem = memInfo.totalMem / (1024 * 1024 * 1024.0)
        val availableMem = memInfo.availMem / (1024 * 1024 * 1024.0)
        val usedMem = totalMem - availableMem
        
        val df = DecimalFormat("#.##")
        
        items.add(DeviceInfoItem(context.getString(R.string.label_total_memory), "${df.format(totalMem)} ${context.getString(R.string.unit_gb)}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_available_memory), "${df.format(availableMem)} ${context.getString(R.string.unit_gb)}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_used_memory), "${df.format(usedMem)} ${context.getString(R.string.unit_gb)}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_memory_class), "${activityManager.memoryClass} ${context.getString(R.string.unit_mb)}"))

        return DeviceInfoCategory(
            title = context.getString(R.string.category_memory),
            iconRes = R.drawable.ic_memory,
            backgroundColor = R.color.device_info_memory,
            items = items
        )
    }

    private fun collectStorageInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        val df = DecimalFormat("#.##")
        
        // Internal storage
        val internalPath = Environment.getDataDirectory()
        val internalStat = StatFs(internalPath.path)
        val internalTotal = (internalStat.totalBytes / (1024.0 * 1024.0 * 1024.0))
        val internalAvailable = (internalStat.availableBytes / (1024.0 * 1024.0 * 1024.0))
        val internalUsed = internalTotal - internalAvailable
        
        items.add(DeviceInfoItem(context.getString(R.string.label_internal_storage), "${df.format(internalTotal)} ${context.getString(R.string.unit_gb)}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_available_storage), "${df.format(internalAvailable)} ${context.getString(R.string.unit_gb)}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_used_storage), "${df.format(internalUsed)} ${context.getString(R.string.unit_gb)}"))
        
        // External storage (if available)
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            val externalPath = Environment.getExternalStorageDirectory()
            val externalStat = StatFs(externalPath.path)
            val externalTotal = (externalStat.totalBytes / (1024.0 * 1024.0 * 1024.0))
            items.add(DeviceInfoItem(context.getString(R.string.label_external_storage), "${df.format(externalTotal)} ${context.getString(R.string.unit_gb)}"))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_storage),
            iconRes = R.drawable.ic_storage,
            backgroundColor = R.color.device_info_storage,
            items = items
        )
    }

    private fun collectDisplayInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val metrics = DisplayMetrics()
        display.getMetrics(metrics)
        
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        val density = metrics.density
        val densityDpi = metrics.densityDpi
        
        items.add(DeviceInfoItem(context.getString(R.string.label_screen_resolution), "${width}x${height}"))
        items.add(DeviceInfoItem(context.getString(R.string.label_screen_density), "$densityDpi ${context.getString(R.string.unit_dpi)}"))
        
        // Calculate screen size in inches
        val xdpi = metrics.xdpi
        val ydpi = metrics.ydpi
        val widthInches = width / xdpi
        val heightInches = height / ydpi
        val diagonalInches = sqrt(widthInches.pow(2) + heightInches.pow(2))
        
        items.add(DeviceInfoItem(context.getString(R.string.label_screen_size), "${DecimalFormat("#.#").format(diagonalInches)}\""))
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val refreshRate = display.refreshRate
            items.add(DeviceInfoItem(context.getString(R.string.label_refresh_rate), "${refreshRate.toInt()} ${context.getString(R.string.unit_hz)}"))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_display),
            iconRes = R.drawable.ic_display,
            backgroundColor = R.color.device_info_display,
            items = items
        )
    }

    private fun collectNetworkInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        
        // Network type
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
            val networkType = when {
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true -> "WiFi"
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) == true -> "Mobile"
                networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) == true -> "Ethernet"
                else -> context.getString(R.string.status_unknown)
            }
            items.add(DeviceInfoItem(context.getString(R.string.label_network_type), networkType))
        }
        
        // WiFi status
        val wifiEnabled = wifiManager.isWifiEnabled
        items.add(DeviceInfoItem(context.getString(R.string.label_wifi_status), 
            if (wifiEnabled) context.getString(R.string.status_enabled) else context.getString(R.string.status_disabled)))
        
        // Network operator
        try {
            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val networkOperator = telephonyManager.networkOperatorName
            if (networkOperator.isNotEmpty()) {
                items.add(DeviceInfoItem(context.getString(R.string.label_network_operator), networkOperator))
            }
        } catch (e: Exception) {
            // Permission might not be granted
        }
        
        // IP Address
        try {
            val ipAddress = getIPAddress()
            items.add(DeviceInfoItem(context.getString(R.string.label_ip_address), ipAddress ?: context.getString(R.string.status_unknown)))
        } catch (e: Exception) {
            items.add(DeviceInfoItem(context.getString(R.string.label_ip_address), context.getString(R.string.status_unknown)))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_network),
            iconRes = R.drawable.ic_network,
            backgroundColor = R.color.device_info_network,
            items = items
        )
    }

    private fun collectBatteryInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        items.add(DeviceInfoItem(context.getString(R.string.label_battery_level), "$batteryLevel${context.getString(R.string.unit_percent)}"))
        
        val batteryStatus = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS)
        val statusText = when (batteryStatus) {
            BatteryManager.BATTERY_STATUS_CHARGING -> context.getString(R.string.status_charging)
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> context.getString(R.string.status_not_charging)
            else -> context.getString(R.string.status_unknown)
        }
        items.add(DeviceInfoItem(context.getString(R.string.label_battery_status), statusText))
        
        val batteryHealth = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_ENERGY_COUNTER)
        items.add(DeviceInfoItem(context.getString(R.string.label_battery_health), context.getString(R.string.status_good)))
        
        items.add(DeviceInfoItem(context.getString(R.string.label_battery_technology), "Li-ion"))

        return DeviceInfoCategory(
            title = context.getString(R.string.category_battery),
            iconRes = R.drawable.ic_battery,
            backgroundColor = R.color.device_info_battery,
            items = items
        )
    }

    private fun collectSystemInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        
        items.add(DeviceInfoItem(context.getString(R.string.label_android_version), Build.VERSION.RELEASE))
        items.add(DeviceInfoItem(context.getString(R.string.label_api_level), Build.VERSION.SDK_INT.toString()))
        items.add(DeviceInfoItem(context.getString(R.string.label_kernel_version), System.getProperty("os.version") ?: context.getString(R.string.status_unknown)))
        items.add(DeviceInfoItem(context.getString(R.string.label_build_number), Build.DISPLAY))
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            items.add(DeviceInfoItem(context.getString(R.string.label_security_patch), Build.VERSION.SECURITY_PATCH))
        }

        return DeviceInfoCategory(
            title = context.getString(R.string.category_system),
            iconRes = R.drawable.ic_system,
            backgroundColor = R.color.device_info_system,
            items = items
        )
    }

    private fun collectHardwareInfo(): DeviceInfoCategory {
        val items = mutableListOf<DeviceInfoItem>()
        
        // Sensors
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL)
        items.add(DeviceInfoItem(context.getString(R.string.label_sensors), sensorList.size.toString()))
        
        // Cameras
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as android.hardware.camera2.CameraManager
                val cameraIds = cameraManager.cameraIdList
                items.add(DeviceInfoItem(context.getString(R.string.label_cameras), cameraIds.size.toString()))
            } catch (e: Exception) {
                items.add(DeviceInfoItem(context.getString(R.string.label_cameras), context.getString(R.string.status_unknown)))
            }
        }
        
        // Bluetooth
        val hasBluetoothFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
        items.add(DeviceInfoItem(context.getString(R.string.label_bluetooth), 
            if (hasBluetoothFeature) context.getString(R.string.status_enabled) else context.getString(R.string.status_disabled)))
        
        // GPS
        val hasGPSFeature = context.packageManager.hasSystemFeature(PackageManager.FEATURE_LOCATION_GPS)
        items.add(DeviceInfoItem(context.getString(R.string.label_gps), 
            if (hasGPSFeature) context.getString(R.string.status_enabled) else context.getString(R.string.status_disabled)))

        return DeviceInfoCategory(
            title = context.getString(R.string.category_hardware),
            iconRes = R.drawable.ic_hardware,
            backgroundColor = R.color.device_info_hardware,
            items = items
        )
    }

    private fun getCpuFrequency(): String {
        return try {
            val br = BufferedReader(FileReader("/proc/cpuinfo"))
            var line: String?
            var cpuFreq = "Unknown"
            while (br.readLine().also { line = it } != null) {
                if (line!!.contains("cpu MHz") || line!!.contains("BogoMIPS")) {
                    cpuFreq = line!!.split(":")[1].trim()
                    break
                }
            }
            br.close()
            cpuFreq
        } catch (e: IOException) {
            "Unknown"
        }
    }

    private fun getIPAddress(): String? {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val networkInterface = interfaces.nextElement()
                val addresses = networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val address = addresses.nextElement()
                    if (!address.isLoopbackAddress && address is java.net.Inet4Address) {
                        return address.hostAddress
                    }
                }
            }
        } catch (e: Exception) {
            // Handle exception
        }
        return null
    }
}