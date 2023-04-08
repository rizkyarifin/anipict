package id.rizky.anipict.data.network.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import id.rizky.anipict.data.db.FavouritePhotoDao
import id.rizky.anipict.data.network.PhotoService
import id.rizky.anipict.data.model.Photo
import retrofit2.HttpException
import java.io.IOException

private const val STARTING_PAGE_INDEX = 1

class PhotoPagingDataSource(
    private val service: PhotoService,
    private val favouritePhotoDao: FavouritePhotoDao,
    private val query: String
) : PagingSource<Int, Photo>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> {
        val page = params.key ?: STARTING_PAGE_INDEX
        return try {
            val photos = service.getPhotos(query, page, params.loadSize).photos

            photos.map {
                it.liked = favouritePhotoDao.isFavouritePhotoExist(it.id)
            }
            LoadResult.Page(
                data = photos,
                prevKey = if (page == STARTING_PAGE_INDEX) null else page - 1,
                nextKey = if (photos.isEmpty()) null else page + 1,
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? {
        return state.anchorPosition
    }
}