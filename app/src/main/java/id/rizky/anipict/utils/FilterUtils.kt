package id.rizky.anipict.utils


fun List<FilterAnimalAdapter.Filter>.withSelectedValuesAtIndex(
    index: Int,
): MutableList<FilterAnimalAdapter.Filter> =
    toMutableList().also {
        for (i in it.indices) {
            it[i] = it[i].copy(isActive = false)
        }
        it[index] = it[index].copy(isActive = true)
    }