package screens.packages

data class PackageModel(val packageName:String,val isCurrentApp:Boolean,val packageMetadata:PackageMetadata?=null){
    override fun toString(): String {
        return packageName
    }
}
