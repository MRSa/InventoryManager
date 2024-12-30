package jp.osdn.gokigen.inventorymanager.ui.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataImportViewModel: ViewModel()
{
    private val isImporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataImporting: LiveData<Boolean> = isImporting

    fun initializeViewModel()
    {
        try
        {
            Log.v(TAG, "DataImportViewModel::initializeViewModel()")
            isImporting.value = false

        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = DataImportViewModel::class.java.simpleName
    }
}
