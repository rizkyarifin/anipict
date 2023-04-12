package id.rizky.anipict.ui.photos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import id.rizky.anipict.data.model.Photo
import id.rizky.anipict.repository.AnimalRepository
import id.rizky.anipict.repository.FavoritePhotoRepository
import id.rizky.anipict.repository.PhotoRepository
import id.rizky.anipict.utils.FilterAnimalAdapter
import id.rizky.anipict.utils.Resource
import id.rizky.anipict.utils.withSelectedValuesAtIndex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PhotosViewModel @AssistedInject constructor(
    photoRepository: PhotoRepository,
    private val favoritePhotoRepository: FavoritePhotoRepository,
    animalRepository: AnimalRepository,
    @Assisted private val query: String,
) : ViewModel() {

    private val _filterDataFlow: MutableStateFlow<List<FilterAnimalAdapter.Filter>> =
        MutableStateFlow(
            arrayListOf()
        )

    private val _emptyPhotoEvents = Channel<Boolean>(Channel.CONFLATED)
    val emptyPhotoEvents = _emptyPhotoEvents.receiveAsFlow()

    private val _animalFlow = animalRepository.getAnimals(query)
    private val _animalState = MutableStateFlow<AnimalEvents>(AnimalEvents.Loading)
    val animalState: StateFlow<AnimalEvents> = _animalState

    val filterDataFlow: StateFlow<List<FilterAnimalAdapter.Filter>> = _filterDataFlow

    @OptIn(ExperimentalCoroutinesApi::class)
    val photosFlow: StateFlow<PagingData<Photo>> =
        filterDataFlow.filter {
            _emptyPhotoEvents.send(true)
            delay(100)
            it.isNotEmpty()
        }.flatMapLatest { filterData ->
            photoRepository.getPhotos(getActiveFilterQuery(filterData)).cachedIn(viewModelScope)
        }.stateIn(
            viewModelScope,
            SharingStarted.Lazily, PagingData.empty()
        )

    init {
        fetchAnimalData()
    }

    private fun fetchAnimalData() {
        viewModelScope.launch {
            _animalState.value = AnimalEvents.Loading
            _animalFlow.collectLatest {
                when (it) {
                    is Resource.Success -> {
                        _filterDataFlow.value = it.data!!
                    }
                    is Resource.Error -> {
                        _animalState.value = AnimalEvents.Error(it.msg.orEmpty())
                    }
                }
            }
        }
    }

    fun retry() {
        _animalState.value = AnimalEvents.Loading
        fetchAnimalData()
    }

    private fun getActiveFilterQuery(filterData: List<FilterAnimalAdapter.Filter>) =
        if (filterData.isNotEmpty()) filterData.single { it.isActive }.name else query

    fun applyFilter(position: Int) {
        _filterDataFlow.value = _filterDataFlow.value.withSelectedValuesAtIndex(position)
    }

    private fun addPhotoToFavorite(photo: Photo) = viewModelScope.launch {
        favoritePhotoRepository.addPhotoToFavorite(
            photo,
            getActiveFilterQuery(_filterDataFlow.value)
        )
    }

    private fun deletePhotoFromFavorite(id: Int) = viewModelScope.launch {
        favoritePhotoRepository.removePhotoFromFavorite(id)
    }

    fun insertOrDeleteFavorite(photo: Photo) {
        if (photo.liked) deletePhotoFromFavorite(photo.id) else addPhotoToFavorite(photo)
    }

    @dagger.assisted.AssistedFactory
    interface AssistedFactory {
        fun create(query: String): PhotosViewModel
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

    sealed class AnimalEvents {
        data class Error(val message: String) : AnimalEvents()
        object Loading : AnimalEvents()
    }
}