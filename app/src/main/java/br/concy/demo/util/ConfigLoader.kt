package br.concy.demo.util

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import java.io.InputStreamReader

data class Config(
    @SerializedName("base_url")
    val baseUrl: String,
    val logActive: Boolean
)

object ConfigLoader {

    fun loadConfig(context: Context): Config? {
        return try {
            val assetManager = context.assets
            val inputStream = assetManager.open("config.json")
            val reader = InputStreamReader(inputStream)

            Gson().fromJson(reader, Config::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
