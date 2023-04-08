package id.rizky.anipict.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.rizky.anipict.data.model.Src

@Entity(tableName = "favourite_photos")
data class FavouritePhotoEntity(
    val alt: String,
    val avg_color: String,
    val height: Int,
    @PrimaryKey val id: Int,
    val liked: Boolean,
    val src: Src,
    val url: String,
    val width: Int,
    val animal: String,
    val createdAt: Long
)