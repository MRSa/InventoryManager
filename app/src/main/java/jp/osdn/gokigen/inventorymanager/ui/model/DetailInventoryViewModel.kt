package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.inventorymanager.AppSingleton
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbnCallback
import jp.osdn.gokigen.inventorymanager.recognize.UpdateRecordInformation
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailInventoryViewModel: ViewModel(), RecognizeFromIsbnCallback
{
    private val content : MutableLiveData<DataContent> by lazy { MutableLiveData<DataContent>() }
    val detailData: LiveData<DataContent> = content

    private val isUpdate : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataIsUpdate: LiveData<Boolean> = isUpdate

    private val isQueryEnable : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isEnableQuery: LiveData<Boolean> = isQueryEnable

    fun initializeViewModel(context: Context)
    {
        try
        {
            Log.v(TAG, "DetailInventoryViewModel::initializeViewModel()")
            isUpdate.value = false
            isQueryEnable.value = true
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun initializeData(id: Long)
    {
        try
        {
            val coroutineScope = CoroutineScope(Dispatchers.Main)
            Thread {
                val storageDao = AppSingleton.db.storageDao()
                val value = storageDao.findById(id)
                coroutineScope.launch {
                    content.value = value
                    isUpdate.value = false
                    isQueryEnable.value = true
                }
                Log.v(TAG, "Update Detail Data : $id")
            }.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun startQueryUsingIsbn()
    {
        isQueryEnable.value = false
    }

    override fun finishRecognizedDataFromIsbn(needUpdate: Boolean)
    {
        isUpdate.value = needUpdate
        isQueryEnable.value = true
    }

    override fun recognizedDataFromIsbnCallback(data: UpdateRecordInformation, isOverwrite: Boolean)
    {
        try
        {
            val newContent = DataContent(
                id = data.id,
                title = data.title,
                subTitle = data.subTitle,
                author = data.author,
                publisher = data.publisher,
                description = content.value?.description,
                isbn = content.value?.isbn,
                productId = content.value?.productId,
                urlStr = content.value?.urlStr,
                bcrText = content.value?.bcrText,
                note = content.value?.note,
                category = content.value?.category,
                imageFile1 = content.value?.imageFile1,
                imageFile2 = content.value?.imageFile2,
                imageFile3 = content.value?.imageFile3,
                imageFile4 = content.value?.imageFile4,
                imageFile5 = content.value?.imageFile5,
                checked = content.value?.checked ?: false,
                informMessage = content.value?.informMessage,
                informDate = content.value?.informDate,
                isDelete = content.value?.isDelete ?: false,
                deleteDate = content.value?.deleteDate,
                updateDate = content.value?.updateDate,
                createDate = content.value?.createDate
            )
            content.value = newContent
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = DetailInventoryViewModel::class.java.simpleName
    }


}