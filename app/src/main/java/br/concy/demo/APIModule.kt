package br.concy.demo

import android.content.Context
import br.concy.demo.health.APIService
import br.concy.demo.util.ConfigLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class APIModule {
    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val config = ConfigLoader.loadConfig(context)

        // Define a URL base com prioridade para a carregada da config, mas com fallback duplo
        val baseUrl = config?.baseUrl ?: "http://localhost:8000/api"

        // Habilita logs apenas se a configuração permitir
        val logActive = config?.logActive ?: false
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (logActive) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
        }
        
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideEcgApiService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }
}
