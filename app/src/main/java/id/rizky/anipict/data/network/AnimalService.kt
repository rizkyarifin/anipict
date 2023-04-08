package id.rizky.anipict.data.network

import id.rizky.anipict.BuildConfig
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface AnimalService {

    @Headers("X-Api-Key: $API_KEY")
    @GET("animals")
    suspend fun getAnimals(
        @Query("name") name: String,
    ): AnimalResponse

    companion object {
        const val API_KEY = BuildConfig.ANIMAL_API_KEY
    }
}