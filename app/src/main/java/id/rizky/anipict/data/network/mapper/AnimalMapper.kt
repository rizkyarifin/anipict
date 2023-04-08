package id.rizky.anipict.data.network.mapper

import id.rizky.anipict.data.network.AnimalResponse
import id.rizky.anipict.utils.FilterAnimalAdapter

fun AnimalResponse.mapToFilterModel(): List<FilterAnimalAdapter.Filter> {
    return arrayListOf<FilterAnimalAdapter.Filter>().apply {
        this@mapToFilterModel.forEach {
            if (this@mapToFilterModel.indexOf(it) == 0) {
                add(FilterAnimalAdapter.Filter(it.name, true))
            } else
                add(FilterAnimalAdapter.Filter(it.name, false))
        }
    }
}