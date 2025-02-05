package br.concy.demo

import android.content.Context
import br.concy.demo.health.APIService
import br.concy.demo.util.ConfigLoader
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class APIModule {
    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val config = ConfigLoader.loadConfig(context)
        val baseUrl = config?.base_url ?: "http://localhost:8000/api/"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideEcgApiService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }
}