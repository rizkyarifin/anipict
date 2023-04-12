package id.rizky.anipict.di

import android.app.Application
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import id.rizky.anipict.data.db.AniPictDB
import id.rizky.anipict.data.db.FavouritePhotoDao
import id.rizky.anipict.data.db.TypeResponseConverters
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(
        application: Application,
        typeConverter: TypeResponseConverters
    ): AniPictDB {
        return Room
            .databaseBuilder(application, AniPictDB::class.java, "anipict.db")
            .fallbackToDestructiveMigration()
            .addTypeConverter(typeConverter)
            .build()
    }

    @Provides
    @Singleton
    fun provideFavouritePhotoDao(appDatabase: AniPictDB): FavouritePhotoDao {
        return appDatabase.favouritePhotos()
    }
}