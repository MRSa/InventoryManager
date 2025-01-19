package jp.osdn.gokigen.inventorymanager.recognize

import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor

class RecognizeFromInternet(private val activity: ComponentActivity)
{
    private val recognizerFromIsbn = RecognizeFromIsbn(activity)

    fun doRecognizeAllFromInternet(callback: IRecognizeDataFromInternetCallback?)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isOverwrite = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )
            val useProductIdSearch = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_CHECK_PRODUCT_ID,
                IPreferencePropertyAccessor.PREFERENCE_CHECK_PRODUCT_ID_DEFAULT_VALUE
            )
            Thread {
                try
                {
                    activity.runOnUiThread {
                        callback?.startRecognizeFromInternet()
                        Toast.makeText(activity, activity.getString(R.string.label_data_start_update_record), Toast.LENGTH_SHORT).show()
                    }

                    val updateCount = recognizerFromIsbn.recognizeAllFromIsbn(isOverwrite, useProductIdSearch, callback)

                    activity.runOnUiThread {
                        callback?.finishRecognizeFromInternet()
                        Toast.makeText(activity, "${activity.getString(R.string.label_data_finish_update_record)} $updateCount ${activity.getString(R.string.label_data_finish_update_record_counts)}", Toast.LENGTH_SHORT).show()
                    }
                }
                catch (ee: Exception)
                {
                    ee.printStackTrace()
                }
            }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun doRecognizeFromInternet(id: Long, callback: IRecognizedDataCallback)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val isOverwrite = preferences.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )

            Log.v(TAG, "doRecognizeFromIsbn($isOverwrite, $id)")
            Thread {
                try
                {
                    recognizerFromIsbn.recognizeFromIsbn(id, isOverwrite, callback)
                }
                catch (ee: Exception)
                {
                    ee.printStackTrace()
                }
            }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = RecognizeFromInternet::class.java.simpleName
    }
}
