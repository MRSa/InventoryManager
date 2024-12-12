package jp.osdn.gokigen.inventorymanager.storage

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

//@Database(entities = [DataContent::class], version = 2,   autoMigrations = [ AutoMigration (from = 1, to = 2) ], exportSchema = true)
@Database(entities = [DataContent::class], version = 1, exportSchema = true)
@TypeConverters(DateConverter::class)
abstract class InventoryDataHolder: RoomDatabase()
{
    abstract fun storageDao(): DataContentDao
}
