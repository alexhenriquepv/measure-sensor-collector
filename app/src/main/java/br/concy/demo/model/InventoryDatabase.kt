package br.concy.demo.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.concy.demo.model.dao.AccelMeasurementDao
import br.concy.demo.model.dao.EcgMeasurementDao
import br.concy.demo.model.dao.GyroMeasurementDao
import br.concy.demo.model.dao.HrMeasurementDao
import br.concy.demo.model.dao.IbiMeasurementDao
import br.concy.demo.model.entity.AccelMeasurement
import br.concy.demo.model.entity.EcgMeasurement
import br.concy.demo.model.entity.GyroscopeMeasurement
import br.concy.demo.model.entity.HrMeasurement
import br.concy.demo.model.entity.IbiMeasurement
import br.concy.demo.util.Converters

@Database(
    version = 1,
    exportSchema = false,
    entities = [
        EcgMeasurement::class,
        AccelMeasurement::class,
        GyroscopeMeasurement::class,
        HrMeasurement::class,
        IbiMeasurement::class
    ]
)
@TypeConverters(Converters::class)
abstract class InventoryDatabase: RoomDatabase() {

    abstract fun ecgMeasurementDao(): EcgMeasurementDao
    abstract fun accelMeasurementDao(): AccelMeasurementDao
    abstract fun gyroMeasurementDao(): GyroMeasurementDao
    abstract fun hrMeasurementDao(): HrMeasurementDao
    abstract fun ibiMeasurementDao(): IbiMeasurementDao

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