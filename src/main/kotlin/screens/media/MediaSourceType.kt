package screens.media

enum class MediaSourceType(val title: String, val value: String) {
    External("External", "external"),
    Internal("Internal", "internal");

    override fun toString(): String {
        return title
    }
}


enum class MediaContentType(val title: String, val value: String) {
    Images("Images", "images"),
    Audio("Audio", "audio"),
    Video("Video", "video");

    override fun toString(): String {
        return title
    }
}