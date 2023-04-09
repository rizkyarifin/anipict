package id.rizky.anipict.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.rizky.anipict.BuildConfig
import id.rizky.anipict.data.network.AnimalService
import id.rizky.anipict.data.network.PhotoService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
internal object NetworkModule {

    private const val PEXEL_BASE_URL = "https://api.pexels.com/v1/"
    private const val NINJAS_BASE_URL = "https://api.api-ninjas.com/v1/"
    private const val CONNECT_TIMEOUT = 20L
    private const val READ_TIMEOUT = 60L
    private const val WRITE_TIMEOUT = 120L

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY
            else HttpLoggingInterceptor.Level.NONE
        }
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("Pexel")
    fun providePexelsRetrofit(okHttpClient: OkHttpClient): Retrofit = retrofitFactory(
        PEXEL_BASE_URL, okHttpClient
    )

    @Provides
    @Singleton
    @Named("Animal")
    fun provideNinjasRetrofit(okHttpClient: OkHttpClient): Retrofit = retrofitFactory(
        NINJAS_BASE_URL, okHttpClient
    )

    @Provides
    @Singleton
    fun providePhotoService(
        @Named("Pexel") retrofit: Retrofit
    ): PhotoService =
        retrofit.create(PhotoService::class.java)

    @Provides
    @Singleton
    fun provideAnimalService(
        @Named("Animal") retrofit: Retrofit
    ): AnimalService =
        retrofit.create(AnimalService::class.java)

    private fun retrofitFactory(baseUrl: String, okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

}