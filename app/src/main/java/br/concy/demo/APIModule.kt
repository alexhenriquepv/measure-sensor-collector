package br.concy.demo

import android.content.Context
import br.concy.demo.health.EcgAPIService
import br.concy.demo.health.HeartRateAPIService
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
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.224.1.28/Sistema-Embarcado-de-Aquisicao-de-Sinais-de-ECG/Model_Web_IA_Arritmias/backend/API/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Singleton
    @Provides
    fun provideHeartRateApiService(retrofit: Retrofit): HeartRateAPIService {
        return retrofit.create(HeartRateAPIService::class.java)
    }

    @Singleton
    @Provides
    fun provideEcgApiService(retrofit: Retrofit): EcgAPIService {
        return retrofit.create(EcgAPIService::class.java)
    }
}