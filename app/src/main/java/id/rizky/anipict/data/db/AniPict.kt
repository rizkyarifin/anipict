package id.rizky.anipict.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import id.rizky.anipict.data.db.entity.FavouritePhotoEntity

@Database(
    entities = [FavouritePhotoEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(value = [TypeResponseConverters::class])
abstract class AniPict : RoomDatabase(){
    abstract fun favouritePhotos() : FavouritePhotoDao
}