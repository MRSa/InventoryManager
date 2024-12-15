package jp.osdn.gokigen.inventorymanager

import android.app.Application
import android.util.Log
import androidx.room.Room
import jp.osdn.gokigen.inventorymanager.storage.InventoryDataHolder

class AppSingleton : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Log.v(TAG, "AppSingleton::create()")
        vibrator = MyVibrator()

        try
        {
            db = Room.databaseBuilder(
                applicationContext,
                InventoryDataHolder::class.java, "inventory-database"
            ).build()
            isReadyDatabase = true
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        const val APP_NAMESPACE = "inventory"
        private val TAG = AppSingleton::class.java.simpleName
        lateinit var vibrator: MyVibrator
        lateinit var db: InventoryDataHolder
        var isReadyDatabase = false
    }
}
