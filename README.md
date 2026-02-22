# 🐠 Tidefish

<div align="center">

![Tidefish Logo](desktop-icons/icon-linux.png)

**A powerful desktop application for Android device management and debugging**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-blue.svg)](https://kotlinlang.org/)
[![Platform](https://img.shields.io/badge/platform-Windows%20%7C%20macOS%20%7C%20Linux-lightgrey.svg)]()

</div>

## 📋 Quick Overview

Tidefish is a comprehensive Android Debug Bridge (ADB) GUI client built with Kotlin. It provides developers and power users with an intuitive interface to manage, debug, and interact with Android devices through a desktop application.

### 🎯 Key Benefits
- **Device Management**: Connect and manage multiple Android devices simultaneously
- **App Management**: View, install, uninstall, and manage applications  
- **Communication Analysis**: Access and analyze SMS/MMS messages and call logs
- **File Management**: Browse and manage device files through an intuitive interface
- **ADB Terminal**: Execute ADB commands directly from the GUI
- **Cross-Platform**: Works on Windows, macOS, and Linux

---

## 📑 Navigation

<details>
<summary>📱 <strong>Features</strong></summary>

### Device Management
- **Device Discovery**: Automatically detect connected Android devices
- **Device Information**: View detailed device specifications and status
- **Multi-Device Support**: Manage multiple devices simultaneously
- **Real-time Status**: Monitor device connection status in real-time

### Application Management
- **App Listing**: View all installed applications with detailed information
- **App Details**: Package name, version, permissions, and storage usage
- **App Actions**: Uninstall, clear data, force stop applications
- **Search & Filter**: Quickly find specific applications

### Communication Analysis
- **Message Management**: View and analyze SMS/MMS messages
- **Conversation Threads**: Group messages by conversation
- **Call Logs**: Access and analyze call history
- **Search Functionality**: Search messages and calls by content or date

### File Operations
- **File Browser**: Navigate device file system
- **File Transfer**: Upload and download files between device and computer
- **Directory Operations**: Create, rename, and delete directories

### Advanced Tools
- **ADB Terminal**: Execute ADB commands with syntax highlighting
- **Screen Capture**: Take screenshots of connected devices
- **Logcat Viewer**: Monitor system and application logs
- **Service Management**: View and control system services

</details>

<details>
<summary>🚀 <strong>Installation</strong></summary>

### Download Releases

Releases will be available soon. Currently, you can build from source using the instructions in the Development section.

### System Requirements

- **Java**: JDK 17 or higher
- **ADB**: Android Debug Bridge (included with auto-update)
- **Operating System**: Windows 10+, macOS 10.15+, or Ubuntu 18.04+

### Quick Start

1. Build Tidefish from source using the instructions in the Development section
2. Launch the application
3. Connect your Android device via USB with USB debugging enabled
4. Tidefish will automatically detect and display your device

</details>

<details>
<summary>📖 <strong>Usage Guide</strong></summary>

### Basic Device Connection

1. Enable USB Debugging on your Android device:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times to enable Developer Options
   - Go to Developer Options and enable "USB Debugging"

2. Connect your device via USB cable

3. Tidefish will automatically detect and display connected devices

### Main Interface

The application is organized into several main screens:

- **Apps Screen**: Manage installed applications
- **Messages Screen**: View SMS/MMS messages
- **Call Logs Screen**: Access call history
- **Media Screen**: Browse media files
- **Services Screen**: Monitor system services
- **Settings Screen**: Configure application preferences

### Keyboard Shortcuts

| Shortcut | Action |
|----------|--------|
| `Ctrl+R` | Refresh device list |
| `Ctrl+T` | Open ADB terminal |
| `Ctrl+S` | Take screenshot |
| `Ctrl+F` | Search current view |
| `F5` | Refresh current screen |

</details>

<details>
<summary>🏗️ <strong>Architecture</strong></summary>

### Technology Stack

- **Framework**: Kotlin
- **Architecture**: MVC pattern with separate controllers, models, and views
- **ADB Integration**: Direct ADB command execution with result parsing
- **Data Processing**: Kotlinx Coroutines for asynchronous operations

### Key Components

- **ADB Helper**: Core ADB command execution and device communication
- **Screen Controllers**: Manage individual screen logic and data
- **UI Components**: Reusable UI components
- **Data Models**: Structured data representation for device information

</details>

<details>
<summary>🔧 <strong>Development</strong></summary>

### Prerequisites

- JDK 17 or higher
- IntelliJ IDEA or Android Studio
- Git

### Building from Source

```bash
# Clone the repository
git clone https://github.com/your-repo/tidefish.git
cd tidefish

# Build the application
./gradlew build

# Run the application
./gradlew run
```

### Creating Distribution Packages

```bash
# Create all distribution packages
./gradlew createDistributables

# Create specific platform packages
./gradlew createDistributableForCurrentOS
```

</details>

---

## License

This project is licensed under the MIT License – see the LICENSE file for details.

---

<div align="center">

**Made with by the Tidefish Team**

</div>