package id.rizky.anipict.data.model


data class Photo(
    val alt: String,
    val avg_color: String,
    val height: Int,
    val id: Int,
    var liked: Boolean,
    val src: Src,
    val url: String,
    val width: Int,
    val animal: String
)