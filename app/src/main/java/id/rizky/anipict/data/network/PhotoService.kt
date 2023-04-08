package id.rizky.anipict.data.network

import id.rizky.anipict.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


interface PhotoService {

    @Headers("Authorization: $ACCESS_KEY")
    @GET("search")
    suspend fun getPhotos(
        @Query("query") query: String,
        @Query("page") page: Int,
        @Query("per_page") perPage: Int,
    ): PhotosResponse

    companion object {
        const val ACCESS_KEY = BuildConfig.PEXEL_ACCESS_KEY
    }
}