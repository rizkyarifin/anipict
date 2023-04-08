package id.rizky.anipict.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.rizky.anipict.data.db.entity.FavouritePhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritePhotoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouritePhoto(favouriteEntity: FavouritePhotoEntity)

    @Query("DELETE FROM favourite_photos WHERE id =:id")
    suspend fun deleteFavouritePhotoById(id: Int)

    @Query("SELECT * FROM favourite_photos ORDER BY createdAt DESC")
    fun getAllFavouritePhotos(): Flow<List<FavouritePhotoEntity>>

    @Query("SELECT * FROM favourite_photos WHERE animal LIKE '%' || :animal || '%' ORDER BY createdAt DESC")
    fun getAllFavouritePhotosByAnimal(animal: String): Flow<List<FavouritePhotoEntity>>

    @Query("SELECT EXISTS(SELECT * FROM favourite_photos WHERE id = :id)")
    suspend fun isFavouritePhotoExist(id: Int): Boolean
}