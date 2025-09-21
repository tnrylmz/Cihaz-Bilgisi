# Cihaz Bilgisi / Device Info

A modern Android application that displays comprehensive device information using Material Design 3.

## Features

### Device Information Categories
- **📱 Device Info**: Manufacturer, model, brand, product details
- **🔧 CPU Info**: Processor cores, architecture, frequency
- **💾 Memory Info**: Total, available, and used memory
- **💿 Storage Info**: Internal and external storage details
- **📺 Display Info**: Resolution, density, screen size, refresh rate
- **🌐 Network Info**: Connection type, WiFi status, IP address
- **🔋 Battery Info**: Level, status, health, technology
- **⚙️ System Info**: Android version, API level, build details
- **🛠️ Hardware Info**: Sensors, cameras, Bluetooth, GPS

### Design Features
- **Material Design 3** with custom color scheme
- **Colorful category icons** for easy identification
- **Expandable/collapsible** sections with smooth animations
- **Card-based UI** for clean presentation
- **Turkish language support** (Türkçe dil desteği)
- **Permission handling** for sensitive information

## Screenshots

The app features:
- Modern Material Design 3 interface
- Color-coded categories with custom icons
- Smooth expand/collapse animations
- Comprehensive device information display

## Technical Details

### Architecture
- **Language**: Kotlin
- **UI Framework**: Android Views with Material Design Components
- **Architecture**: MVVM pattern with data collectors
- **Minimum SDK**: API 21 (Android 5.0)
- **Target SDK**: API 34 (Android 14)

### Key Components
- `DeviceInfoCollector`: Collects device information from various Android APIs
- `DeviceInfoCategoryAdapter`: RecyclerView adapter for category display
- `DeviceInfoDetailAdapter`: Nested adapter for detail items
- Material Design 3 theming with custom colors

### Permissions Required
- `READ_PHONE_STATE`: For device and network information
- `ACCESS_NETWORK_STATE`: For network status details

## Installation

1. Clone this repository
2. Open in Android Studio
3. Build and run on an Android device or emulator

```bash
git clone https://github.com/tnrylmz/Cihaz-Bilgisi.git
cd Cihaz-Bilgisi
./gradlew assembleDebug
```

## Usage

Simply launch the app to view comprehensive device information organized in expandable categories. Tap on any category to expand or collapse its details.

## Language Support

The app supports both English and Turkish languages:
- **English**: Default language with comprehensive labels
- **Türkçe**: Full Turkish translation for all UI elements

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is open source. Feel free to use and modify as needed.

---

**Developer**: tnrylmz  
**Version**: 1.0  
**Last Updated**: 2024