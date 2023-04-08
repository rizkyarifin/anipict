package id.rizky.anipict.data.db.entity.mapper

import id.rizky.anipict.data.db.entity.FavouritePhotoEntity
import id.rizky.anipict.data.model.Photo
import java.util.Date

fun Photo.mapToFavoritePhotoEntity(animal: String): FavouritePhotoEntity {
    return FavouritePhotoEntity(
        alt = this.alt,
        avg_color = this.avg_color,
        height = this.height,
        id = this.id,
        liked = this.liked,
        src = this.src,
        url = this.url,
        width = this.width,
        animal = animal,
        createdAt = Date().time
    )
}

fun List<FavouritePhotoEntity>.mapToPhotoDomain(): List<Photo> {
    return this.map {
        Photo(
            alt = it.alt,
            avg_color = it.avg_color,
            height = it.height,
            id = it.id,
            liked = it.liked,
            src = it.src,
            url = it.url,
            width = it.width,
            animal = it.animal
        )
    }
}