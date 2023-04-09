package id.rizky.anipict.utils

import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val aniPictDispatchers: AniPictDispatchers)

enum class AniPictDispatchers {
    IO
}
