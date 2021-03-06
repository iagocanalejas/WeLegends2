package com.andiag.welegends.remake.models.database.static_data

import android.util.Log
import com.andiag.welegends.remake.WeLegendsDatabase
import com.andiag.welegends.remake.common.utils.CallbackData
import com.andiag.welegends.remake.models.api.RestClient
import com.andiag.welegends.remake.models.database.converters.ConverterJsonArray
import com.andiag.welegends.remake.models.database.converters.ConverterJsonObject
import com.andiag.welegends.remake.models.database.static_data.generics.OrmBaseModel
import com.andiag.welegends.remake.models.database.static_data.utils.CallbackSemaphore
import com.andiag.welegends.remake.models.database.static_data.utils.CallbackStaticData
import com.andiag.welegends.remake.models.database.static_data.utils.KeyInMapTypeAdapter
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.annotations.Expose
import com.raizlabs.android.dbflow.annotation.*
import java.io.Serializable

/**
 * Created by Canalejas on 13/12/2016.
 */
@Table(database = WeLegendsDatabase::class)
class Rune : OrmBaseModel(), Serializable, KeyInMapTypeAdapter {

    @Expose(serialize = false, deserialize = false) @PrimaryKey(autoincrement = true) var mid: Int = 0
    @Expose(serialize = false, deserialize = false) @Unique @Column var riotId: Long? = null
    @Column var name: String? = null
    @Column var description: String? = null
    @ForeignKey(tableClass = Image::class) var image: Image? = null
    @ForeignKey(tableClass = RuneType::class) var rune: RuneType? = null
    @Column var colloq: String? = null
    @Column var plaintext: String? = null
    @Column(typeConverter = ConverterJsonArray::class) var tags: JsonArray? = null
    @Column(typeConverter = ConverterJsonObject::class) var stats: JsonObject? = null

    override fun setKey(key: String) {
        this.riotId = key.toLong()
    }

    companion object {
        private val TAG: String = Rune::class.java.simpleName

        fun loadFromServer(callback: CallbackData<*>, semaphore: CallbackSemaphore, version: String, locale: String) {
            val call = RestClient.getDdragonStaticData(version, locale).runes()
            call.enqueue(CallbackStaticData<Rune>(locale, semaphore, callback, Runnable {
                Log.i(TAG, "Reloading %s Locale From onResponse To: %s".format(Rune::class.java.simpleName, RestClient.DEFAULT_LOCALE))
                loadFromServer(callback, semaphore, version, RestClient.DEFAULT_LOCALE)
            }))
        }

    }

}
