package ru.granatblack.menu

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

object MenuRepository {
    fun loadMenu(context: Context): MenuData {
        context.assets.open("menu_data.json").use { stream ->
            return Gson().fromJson(InputStreamReader(stream, Charsets.UTF_8), MenuData::class.java)
        }
    }
}
