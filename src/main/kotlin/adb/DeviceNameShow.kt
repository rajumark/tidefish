package adb
import java.io.BufferedReader
import java.io.InputStreamReader
object DeviceNameShow {
    private val cache = mutableMapOf<String, String>()

    fun getDeviceName(deviceId: String, prop: String): String? {
        val key = "$deviceId-$prop"
        return cache[key] ?: run {
            try {
                val command = "${ADBConst.path} -s $deviceId shell getprop $prop"
                val process = Runtime.getRuntime().exec(command)
                val reader = BufferedReader(InputStreamReader(process.inputStream))
                val output = reader.readLine().orEmpty()
                process.waitFor()
                if(output.isValidName()){
                    cache[key] = output
                    output
                }else{
                    null
                }
            } catch (e: Exception) {
                return null
            }
        }
    }

    fun getHumanNameByID(did: String): String {
        val name= if (did.contains("emulator")) {
            "${getDeviceName(did, "ro.kernel.qemu.avd_name")} ${getDeviceName(did, "ro.build.version.release")}"
        } else {
            "${getDeviceName(did, "ro.product.brand")} ${getDeviceName(did, "ro.product.model")} ${getDeviceName(did, "ro.build.version.release")}"
        }
        if (name.contains("null")){
            return did
        }else{
            return name
        }
    }

}

fun String?.isValidName(): Boolean {
    return this.isNullOrBlank().not()
}