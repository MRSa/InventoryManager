package jp.osdn.gokigen.inventorymanager.ui.model

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InventoryViewModel: ViewModel()
{
    private val storageDao = AppSingleton.db.storageDao()
    val dataList = mutableStateListOf<DataContent>()

    private val isRefreshing : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val refreshingData: LiveData<Boolean> = isRefreshing

    private val isExporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataExporting: LiveData<Boolean> = isExporting

    private val exportingFile : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    val exportingFileName: LiveData<String> = exportingFile

    private fun update()
    {
        CoroutineScope(Dispatchers.Main).launch {
            val refresh = isRefreshing.value ?: false
            if (!refresh)
            {
                isRefreshing.value = true
                dataList.clear()
                withContext(Dispatchers.Default) {
                    storageDao.getAll().forEach { data ->
                        dataList.add(data)
                    }
                }
                isRefreshing.value = false
            }
        }
    }

    fun refresh()
    {
        try
        {
            Log.v(TAG, "START REFRESH...")
            val refresh = isRefreshing.value ?: false
            if (!refresh)
            {
                update()
                Log.v(TAG, "DATA REFRESHED : ${dataList.count()}")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = InventoryViewModel::class.java.simpleName
    }
}
