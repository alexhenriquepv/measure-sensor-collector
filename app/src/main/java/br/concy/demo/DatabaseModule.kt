package br.concy.demo

import android.content.Context
import br.concy.demo.model.InventoryDatabase
import br.concy.demo.model.dao.HeartHateMeasurementDao
import br.concy.demo.model.repository.HeartRateRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    @Singleton
    fun provideInventoryDatabase(@ApplicationContext context: Context): InventoryDatabase {
        return InventoryDatabase.getDatabase(context)
    }

    @Singleton
    @Provides
    fun providesHeartHateDao(database: InventoryDatabase) = database.heartHateMeasurementDao()

    @Singleton
    @Provides
    fun provideHeartHateRepository(dao: HeartHateMeasurementDao) = HeartRateRepository(dao)
}