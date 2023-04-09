package id.rizky.anipict.repository

import id.rizky.anipict.data.db.FavouritePhotoDao
import id.rizky.anipict.data.db.entity.mapper.mapToFavoritePhotoEntity
import id.rizky.anipict.data.db.entity.mapper.mapToPhotoDomain
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.utils.AniBrowseDispatchers
import id.rizky.anipict.utils.Dispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritePhotoRepository @Inject constructor(
    private val favouritePhotoDao: FavouritePhotoDao,
    @Dispatcher(AniBrowseDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {
    fun getAllFavouritePhotos(animal: String): Flow<List<Photo>> =
        favouritePhotoDao.getAllFavouritePhotosByAnimal(animal).map { it.mapToPhotoDomain() }
            .flowOn(ioDispatcher)

    suspend fun addPhotoToFavorite(
        photo: Photo, animal: String
    ) =
        favouritePhotoDao.insertFavouritePhoto(photo.mapToFavoritePhotoEntity(animal))

    suspend fun removePhotoFromFavorite(
        id: Int
    ) = favouritePhotoDao.deleteFavouritePhotoById(id)
}