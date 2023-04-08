package id.rizky.anipict.data.db

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import id.rizky.anipict.data.model.Src
import javax.inject.Inject


@ProvidedTypeConverter
class TypeResponseConverters @Inject constructor(
    private val gson: Gson
) {

    @TypeConverter
    fun srcToString(src: Src): String = gson.toJson(src)

    @TypeConverter
    fun stringToSrc(string: String): Src = gson.fromJson(string, Src::class.java)

}