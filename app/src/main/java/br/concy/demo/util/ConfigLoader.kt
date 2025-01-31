package br.concy.demo.util

import android.content.Context
import com.google.gson.Gson
import java.io.InputStreamReader

data class Config(val base_url: String)

object ConfigLoader {

    fun loadConfig(context: Context): Config? {
        return try {
            // Abrindo o arquivo de configuração da pasta assets
            val assetManager = context.assets
            val inputStream = assetManager.open("config.json")
            val reader = InputStreamReader(inputStream)

            // Usando Gson para deserializar o arquivo JSON
            Gson().fromJson(reader, Config::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
