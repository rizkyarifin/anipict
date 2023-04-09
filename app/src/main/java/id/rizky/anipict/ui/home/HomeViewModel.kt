package id.rizky.anipict.ui.home

import androidx.lifecycle.ViewModel
import id.rizky.anipict.R
import id.rizky.anipict.ui.home.adapter.AnimalAdapter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HomeViewModel : ViewModel() {

    private val _animalData =
        MutableStateFlow<List<AnimalAdapter.Animal>>(mutableListOf<AnimalAdapter.Animal>().apply {
            add(AnimalAdapter.Animal("Elephant", "Mammalia", R.mipmap.elephant))
            add(AnimalAdapter.Animal("Lion", "Mammalia", R.mipmap.lion))
            add(AnimalAdapter.Animal("Fox", "Mammalia", R.mipmap.fox))
            add(AnimalAdapter.Animal("Dog", "Mammalia", R.mipmap.dog))
            add(AnimalAdapter.Animal("Shark", "Chondrichthyes", R.mipmap.shark))
            add(AnimalAdapter.Animal("Turtle", "Reptilia", R.mipmap.turtle))
            add(AnimalAdapter.Animal("Whale", "Mammalia", R.mipmap.whale))
            add(AnimalAdapter.Animal("Penguin", "Aves", R.mipmap.penguin))
        })

    val animalData: StateFlow<List<AnimalAdapter.Animal>> = _animalData
}