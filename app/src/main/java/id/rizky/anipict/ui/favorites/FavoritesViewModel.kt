package id.rizky.anipict.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import id.rizky.anipict.repository.FavoritePhotoRepository
import id.rizky.anipict.utils.FilterAnimalAdapter
import id.rizky.anipict.utils.withSelectedValuesAtIndex
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val favoritePhotoRepository: FavoritePhotoRepository
) : ViewModel() {

    private val _filterData =
        MutableStateFlow(mutableListOf<FilterAnimalAdapter.Filter>().apply {
            add(FilterAnimalAdapter.Filter("All", true))
            add(FilterAnimalAdapter.Filter("Elephant", false))
            add(FilterAnimalAdapter.Filter("Lion", false))
            add(FilterAnimalAdapter.Filter("Fox", false))
            add(FilterAnimalAdapter.Filter("Dog", false))
            add(FilterAnimalAdapter.Filter("Shark", false))
            add(FilterAnimalAdapter.Filter("Turtle", false))
            add(FilterAnimalAdapter.Filter("Whale", false))
            add(FilterAnimalAdapter.Filter("Penguin", false))
        })

    val filterData: Flow<List<FilterAnimalAdapter.Filter>> = _filterData

    @OptIn(ExperimentalCoroutinesApi::class)
    var favoritePhotoFlow = filterData.flatMapLatest { filterData ->
        favoritePhotoRepository.getAllFavouritePhotos(getActiveFilterQuery(filterData))
    }

    private fun getActiveFilterQuery(filterData: List<FilterAnimalAdapter.Filter>): String {
        val query = filterData.filter { it.isActive }[0].name
        return if (query == "All") "" else query
    }

    fun applyFilter(position: Int) {
        _filterData.value = _filterData.value.withSelectedValuesAtIndex(position)
    }

    fun deletePhotoFromFavorite(id: Int) = viewModelScope.launch {
        favoritePhotoRepository.removePhotoFromFavorite(id)
    }

}