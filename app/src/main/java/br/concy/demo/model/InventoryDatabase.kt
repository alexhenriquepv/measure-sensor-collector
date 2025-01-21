package br.concy.demo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.concy.demo.model.dao.EcgMeasurementDao
import br.concy.demo.model.dao.HeartHateMeasurementDao
import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.entity.HeartHateMeasurement
import br.concy.demo.util.Converters

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        HeartHateMeasurement::class,
        EcgMeasurement::class
    ]
)
@TypeConverters(Converters::class)
abstract class InventoryDatabase: RoomDatabase() {

    abstract fun heartHateMeasurementDao(): HeartHateMeasurementDao
    abstract fun ecgMeasurementDao(): EcgMeasurementDao

    companion object {

        @Volatile
        private var Instance: InventoryDatabase? = null

        fun getDatabase(context: Context): InventoryDatabase {
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    InventoryDatabase::class.java,
                    "measures_db"
                ).fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}