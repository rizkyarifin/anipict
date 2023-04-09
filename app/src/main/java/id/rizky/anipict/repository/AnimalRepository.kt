package id.rizky.anipict.repository

import id.rizky.anipict.data.network.AnimalService
import id.rizky.anipict.data.network.mapper.mapToFilterModel
import id.rizky.anipict.utils.AniPictDispatchers
import id.rizky.anipict.utils.Dispatcher
import id.rizky.anipict.utils.FilterAnimalAdapter
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
        animal: String,
        onStart: () -> Unit,
        onComplete: () -> Unit,
        onError: (String?) -> Unit
    ): Flow<List<FilterAnimalAdapter.Filter>> = flow {
        try {
            val response = animalService.getAnimals(animal)
            emit(response.mapToFilterModel())
        } catch (e: Exception) {
            onError(e.message)
        }
    }.onStart { onStart() }.onCompletion { onComplete() }.flowOn(ioDispatcher)

}