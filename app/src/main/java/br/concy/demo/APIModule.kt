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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class APIModule {
    @Singleton
    @Provides
    fun provideRetrofit(@ApplicationContext context: Context): Retrofit {
        val config = ConfigLoader.loadConfig(context)
        val baseUrl = config?.baseUrl ?: "http://10.224.1.56/Sistema-Dashboard-Glicose/"

        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder().addInterceptor(logging).build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Singleton
    @Provides
    fun provideEcgApiService(retrofit: Retrofit): APIService {
        return retrofit.create(APIService::class.java)
    }
}