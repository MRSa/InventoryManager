package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.ContentResolver
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor

class InventoryViewModel: ViewModel(), IInformationReceiver
{
    private lateinit var preference : SharedPreferences
    private lateinit var contentResolver: ContentResolver
    private val connectionStatus : MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus> by lazy { MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus>() }
    private val readIsbnImmediately : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val databaseInitialize : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    val isReadyDatabase: LiveData<Boolean> = databaseInitialize
    val checkIsbnImmediately: LiveData<Boolean> = readIsbnImmediately
    val cameraConnectionStatus: LiveData<ICameraConnectionStatus.CameraConnectionStatus> = connectionStatus

    fun initializeViewModel(activity: AppCompatActivity)
    {
        try
        {
            contentResolver = activity.contentResolver
            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            readIsbnImmediately.value = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY,
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE
            )
            Log.v(TAG, "initializeViewModel()")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCheckIsbnImmediately(value: Boolean)
    {
        readIsbnImmediately.value = value
        try
        {
            val editor: SharedPreferences.Editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY, value)
            editor.apply()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setIsReadyDatabase(value: Boolean)
    {
        databaseInitialize.value = value
    }

    /* IInformationReceiver */
    override fun updateMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        Log.v(TAG, "updateMessage(): $message")
    }

    /* IInformationReceiver */
    override fun appendMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        Log.v(TAG, "appendMessage(): $message")
    }

    /* IInformationReceiver */
    override fun getCurrentMessage(): String
    {
        Log.v(TAG, "getCurrentMessage()")
        return ""
    }



    companion object
    {
        private val TAG = InventoryViewModel::class.java.simpleName
    }
}
