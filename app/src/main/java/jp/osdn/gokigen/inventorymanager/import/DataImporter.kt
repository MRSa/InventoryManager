package jp.osdn.gokigen.inventorymanager.import

import androidx.appcompat.app.AppCompatActivity


class DataImporter(private val activity: AppCompatActivity)
{
    fun doImport()
    {

    }

    companion object
    {
        private val TAG = DataImporter::class.java.simpleName
        private const val FILENAME_FORMAT = "yyyyMMdd_HHmmss"
    }
}
