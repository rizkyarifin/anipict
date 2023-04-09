package id.rizky.anipict.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.repository.AnimalRepository
import id.rizky.anipict.repository.FavoritePhotoRepository
import id.rizky.anipict.repository.PhotoRepository
import id.rizky.anipict.utils.FilterAnimalAdapter
import id.rizky.anipict.utils.withSelectedValuesAtIndex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotosViewModel @AssistedInject constructor(
    photoRepository: PhotoRepository,
    private val favoritePhotoRepository: FavoritePhotoRepository,
    animalRepository: AnimalRepository,
    @Assisted private val query: String,
) : ViewModel() {

    private val _filterDataFlow: MutableStateFlow<List<FilterAnimalAdapter.Filter>> = MutableStateFlow(
        arrayListOf()
    )

    private val _errorAnimalChannel = Channel<Boolean>(Channel.CONFLATED)
    val errorAnimalChannel = _errorAnimalChannel.receiveAsFlow()

    private val _loadingAnimalChannel = Channel<Boolean>(Channel.CONFLATED)
    val loadingAnimalChannel = _loadingAnimalChannel.receiveAsFlow()

    private val animalFlow = animalRepository.getAnimals(query, onError = {
        _loadingAnimalChannel.trySend(false)
        _errorAnimalChannel.trySend(true)
    }, onStart = {
        _errorAnimalChannel.trySend(false)
        _loadingAnimalChannel.trySend(true)
    }, onComplete = {
    })

    val filterDataFlow: StateFlow<List<FilterAnimalAdapter.Filter>> = _filterDataFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow = filterDataFlow.filter { it.isNotEmpty() }.flatMapLatest { filterData ->
        photoRepository.getPhotos(getActiveFilterQuery(filterData)).cachedIn(viewModelScope)
    }

    init {
        fetchAnimalData()
    }

    fun fetchAnimalData() {
        viewModelScope.launch {
            animalFlow.collectLatest {
                _filterDataFlow.emit(it)
            }
        }
    }

    private fun getActiveFilterQuery(filterData: List<FilterAnimalAdapter.Filter>) =
        if (filterData.isNotEmpty()) filterData.filter { it.isActive }[0].name else query

    fun applyFilter(position: Int) {
        _filterDataFlow.value = _filterDataFlow.value.withSelectedValuesAtIndex(position)
    }

    private fun addPhotoToFavorite(photo: Photo) = viewModelScope.launch {
        favoritePhotoRepository.addPhotoToFavorite(photo, getActiveFilterQuery(_filterDataFlow.value))
    }

    private fun deletePhotoFromFavorite(id: Int) = viewModelScope.launch {
        favoritePhotoRepository.removePhotoFromFavorite(id)
    }

    fun insertOrDeleteFavorite(photo: Photo) {
        if (photo.liked) deletePhotoFromFavorite(photo.id) else addPhotoToFavorite(photo)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(pokemonName: String): PhotosViewModel
    }

    companion object {
        fun provideFactory(
            assistedFactory: AssistedFactory,
            query: String
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {

            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return assistedFactory.create(query) as T
            }
        }
    }
}