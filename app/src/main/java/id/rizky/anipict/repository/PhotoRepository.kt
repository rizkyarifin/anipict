package id.rizky.anipict.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import id.rizky.anipict.data.db.FavouritePhotoDao
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.data.network.PhotoService
import id.rizky.anipict.data.network.datasource.PhotoPagingDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PhotoRepository @Inject constructor(
    private val photoService: PhotoService,
    private val favouritePhotoDao: FavouritePhotoDao
) {

    fun getPhotos(query: String): Flow<PagingData<Photo>> =
        Pager(
            config = PagingConfig(
                initialLoadSize = 15,
                pageSize = 15,
                maxSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { PhotoPagingDataSource(photoService, favouritePhotoDao, query) }
        ).flow
}