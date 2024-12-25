package jp.osdn.gokigen.inventorymanager.export

import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.storage.DataContent

class DataExporter(private val activity: AppCompatActivity)
{
    private val storageDao = AppSingleton.db.storageDao()

    fun doExport(baseDirectory: String = "inventory", fileName: String = "inventoryDataExport.xml")
    {
        try
        {
            Thread { exportImpl(baseDirectory, fileName) }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun exportImpl(baseDirectory: String, fileName: String)
    {
        Log.v(TAG, "DataExporter::export $baseDirectory/$fileName")

        try
        {
            Log.v(TAG, " Export: $baseDirectory/$fileName  START")
            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.label_data_start_export)} : $baseDirectory/$fileName", Toast.LENGTH_SHORT).show()
            }

            // ----- 出力するデータをここで確保。
            val exportTarget = ArrayList<DataContent>()
            storageDao.getAll().forEach { data ->
                exportTarget.add(data)
            }

            ///////  ここでファイルに出力する！



            Thread.sleep(5000)

            Log.v(TAG, " Export: $baseDirectory/$fileName  FINISHED.")

            activity.runOnUiThread {
                Toast.makeText(activity, "${activity.getString(R.string.label_data_exported)} : $baseDirectory/$fileName", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = DataExporter::class.java.simpleName
    }
}