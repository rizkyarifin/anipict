package id.rizky.anipict.repository

import id.rizky.anipict.data.network.AnimalService
import id.rizky.anipict.data.network.mapper.mapToFilterModel
import id.rizky.anipict.ui.photos.PhotosViewModel
import id.rizky.anipict.utils.AniPictDispatchers
import id.rizky.anipict.utils.Dispatcher
import id.rizky.anipict.utils.FilterAnimalAdapter
import id.rizky.anipict.utils.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnimalRepository @Inject constructor(
    private val animalService: AnimalService,
    @Dispatcher(AniPictDispatchers.IO) private val ioDispatcher: CoroutineDispatcher
) {

    fun getAnimals(
        animal: String
    ): Flow<Resource<List<FilterAnimalAdapter.Filter>>> = flow {
        try {
            val response = animalService.getAnimals(animal)
            emit(Resource.Success(response.mapToFilterModel()))
        } catch (e: Exception) {
            emit(Resource.Error(e.message.orEmpty()))
        }
    }.flowOn(ioDispatcher)

}