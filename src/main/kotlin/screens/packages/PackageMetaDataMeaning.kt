package screens.packages

fun getApkSigningVersionMeaning(version: String): String {
    if(version.isBlank()) return version
    return when (version) {
        "1" -> "v1 (JAR signing)"
        "2" -> "v2 (Full APK signing)"
        "3" -> "v3 (Android 9+)"
        "4" -> "v4 (Enhanced with new features and security)"
        else -> "V${version} Unknown"
    }
}


fun getSdkVersionMeaning(version: String): String {
    if(version.isBlank()) return ""
    return "$version"+" ("+when (version) {
        "1" -> "Android 1.0 (No official code name)"
        "2" -> "Android 1.1 (No official code name)"
        "3" -> "Android 1.5 Cupcake"
        "4" -> "Android 1.6 Donut"
        "5" -> "Android 2.0 Eclair"
        "6" -> "Android 2.0.1 Eclair"
        "7" -> "Android 2.1 Eclair"
        "8" -> "Android 2.2 FroYo"
        "9" -> "Android 2.3 Gingerbread"
        "10" -> "Android 2.3.3 Gingerbread"
        "11" -> "Android 3.0 Honeycomb"
        "12" -> "Android 3.1 Honeycomb"
        "13" -> "Android 3.2 Honeycomb"
        "14" -> "Android 4.0 Ice Cream Sandwich"
        "15" -> "Android 4.0.3 Ice Cream Sandwich"
        "16" -> "Android 4.1 Jelly Bean"
        "17" -> "Android 4.2 Jelly Bean"
        "18" -> "Android 4.3 Jelly Bean"
        "19" -> "Android 4.4 KitKat"
        "20" -> "Android 4.4W KitKat (Wear)"
        "21" -> "Android 5.0 Lollipop"
        "22" -> "Android 5.1 Lollipop"
        "23" -> "Android 6.0 Marshmallow"
        "24" -> "Android 7.0 Nougat"
        "25" -> "Android 7.1 Nougat"
        "26" -> "Android 8.0 Oreo"
        "27" -> "Android 8.1 Oreo"
        "28" -> "Android 9.0 Pie"
        "29" -> "Android 10 Q"
        "30" -> "Android 11 R"
        "31" -> "Android 12 S"
        "32" -> "Android 12L S"
        "33" -> "Android 13 Tiramisu"
        "34" -> "Android 14 UpsideDownCake"
        "35" -> "Android 15 VanillaIceCream"
        "36" -> "Android 16"
        else -> "Unknown SDK Version"
    }+")"
}