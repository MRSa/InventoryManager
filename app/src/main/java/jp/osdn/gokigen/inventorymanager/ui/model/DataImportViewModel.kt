package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataImportViewModel: ViewModel()
{
    private lateinit var contentResolver: ContentResolver

    private val targetUri : MutableLiveData<Uri> by lazy { MutableLiveData<Uri>() }
    val targetFileUri: LiveData<Uri> = targetUri

    private val isImporting : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val dataImporting: LiveData<Boolean> = isImporting

    fun initializeViewModel(activity: AppCompatActivity)
    {
        try
        {
            Log.v(TAG, "DataImportViewModel::initializeViewModel()")
            contentResolver = activity.contentResolver
            isImporting.value = false
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setTargetFileUri(uri: Uri)
    {
        try
        {
            try
            {
                // ファイルに対し永続的なアクセス権を取得する場合は指定するが、、、今回は不要か？
                // contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                targetUri.value = uri
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

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
