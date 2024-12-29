package jp.osdn.gokigen.inventorymanager.ui.model

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

class DetailInventoryViewModel: ViewModel(), RecognizeFromIsbnCallback {
    private val content: MutableLiveData<DataContent> by lazy { MutableLiveData<DataContent>() }
    val detailData: LiveData<DataContent> = content

    private val isUpdate: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataIsUpdate: LiveData<Boolean> = isUpdate

    private val isQueryEnable: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isEnableQuery: LiveData<Boolean> = isQueryEnable

    private val isSubtitleEdit: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isSubtitleEditing: LiveData<Boolean> = isSubtitleEdit

    private val isIsbnEdit: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isIsbnEditing: LiveData<Boolean> = isIsbnEdit

    private val isCategoryEdit: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isCategoryEditing: LiveData<Boolean> = isCategoryEdit

    fun initializeViewModel() {
        try {
            Log.v(TAG, "DetailInventoryViewModel::initializeViewModel()")
            isUpdate.value = false
            isQueryEnable.value = true
            isSubtitleEdit.value = false
            isIsbnEdit.value = false
            isCategoryEdit.value = false
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

    fun toggleEditButtonStatus(textFieldId: TextFieldId, isEnable: Boolean)
    {
        var update = false
        when (textFieldId)
        {
            TextFieldId.SUBTITLE -> {
                isSubtitleEdit.value = !isEnable
                update = true
            }
            TextFieldId.ISBN -> {
                isIsbnEdit.value = !isEnable
                update = true
            }
            TextFieldId.CATEGORY -> {
                isCategoryEdit.value = !isEnable
                update = true
            }
            else -> { }
        }

        // ----- 編集ボタンを押したら、ISBNでの情報更新は無効にする
        isUpdate.value = update
        isQueryEnable.value = false
    }

    fun updateButtonEnable(isEnableUpdate: Boolean, isEnableQuery: Boolean)
    {
        this.isUpdate.value = isEnableUpdate
        this.isQueryEnable.value = isEnableQuery
    }

    fun updateValueSingle(id: Long, textFieldId: TextFieldId, value: String)
    {
        try
        {
            val newContent = DataContent(
                id = id,
                title = if (textFieldId == TextFieldId.TITLE) { value } else { content.value?.title },
                subTitle = if (textFieldId == TextFieldId.SUBTITLE) { value } else { content.value?.subTitle },
                author = if (textFieldId == TextFieldId.AUTHOR) { value } else { content.value?.author },
                publisher = if (textFieldId == TextFieldId.PUBLISHER) { value } else { content.value?.publisher },
                description = content.value?.description,
                isbn = if (textFieldId == TextFieldId.ISBN) { value } else { content.value?.isbn },
                productId = content.value?.productId,
                urlStr = content.value?.urlStr,
                bcrText = content.value?.bcrText,
                note = if (textFieldId == TextFieldId.TEXT) { value } else { content.value?.note },
                category = if (textFieldId == TextFieldId.CATEGORY) { value } else { content.value?.category },
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
