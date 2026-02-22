package adb

object ADBConst {

   // const val path="src/main/resources/platform-tools/adb"
    var _path=""
    val path:String
       get() {
           if (_path==""){
               _path=(ZipHelper.getAdbPathZipForEXe())
           }
           return _path
       }
    const val feedback_formlink=""

}