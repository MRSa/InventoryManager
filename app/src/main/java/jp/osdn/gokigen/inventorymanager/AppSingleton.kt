package jp.osdn.gokigen.inventorymanager

import android.app.Application
import android.util.Log

class AppSingleton : Application()
{
    override fun onCreate()
    {
        super.onCreate()
        Log.v(TAG, "AppSingleton::create()")
        vibrator = MyVibrator()
    }

    interface PreparationCallback
    {
        fun finishedPreparation(result: Boolean, detail: String)
    }

    companion object
    {
        private val TAG = AppSingleton::class.java.simpleName
        lateinit var vibrator: MyVibrator
        var isReadyDatabase = false

        fun prepareApplication(callback: PreparationCallback)
        {
            try
            {

            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }

    }
}
