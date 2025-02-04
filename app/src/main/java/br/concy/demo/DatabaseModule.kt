package br.concy.demo

import android.content.Context
import br.concy.demo.model.InventoryDatabase
import br.concy.demo.model.dao.AccelMeasurementDao
import br.concy.demo.model.dao.EcgMeasurementDao
import br.concy.demo.model.dao.GyroMeasurementDao
import br.concy.demo.model.repository.AccelRepository
import br.concy.demo.model.repository.EcgRepository
import br.concy.demo.model.repository.GyroRepository
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
    fun provideEcgDao(database: InventoryDatabase) = database.ecgMeasurementDao()

    @Singleton
    @Provides
    fun provideEcgRepository(dao: EcgMeasurementDao) = EcgRepository(dao)

    @Singleton
    @Provides
    fun provideAccelDao(database: InventoryDatabase) = database.accelMeasurementDao()

    @Singleton
    @Provides
    fun provideAccelRepository(dao: AccelMeasurementDao) = AccelRepository(dao)

    @Singleton
    @Provides
    fun provideGyroDao(database: InventoryDatabase) = database.gyroMeasurementDao()

    @Singleton
    @Provides
    fun provideGyroRepository(dao: GyroMeasurementDao) = GyroRepository(dao)
}