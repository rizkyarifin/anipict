package id.rizky.anipict.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val aniBrowseDispatchers: AniBrowseDispatchers)

enum class AniBrowseDispatchers {
    IO
}
