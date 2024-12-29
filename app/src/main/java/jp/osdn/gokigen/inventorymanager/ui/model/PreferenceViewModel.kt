package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor

class PreferenceViewModel: ViewModel()
{
    private lateinit var preference : SharedPreferences

    private val readIsbnImmediately : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val overwriteTitleFromIsbn : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    private val archiveOnlyOneFile : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }

    val overwriteInfoFromIsbn: LiveData<Boolean> = overwriteTitleFromIsbn
    val checkIsbnImmediately: LiveData<Boolean> = readIsbnImmediately
    val archiveOneFile: LiveData<Boolean> = archiveOnlyOneFile

    fun initializeViewModel(activity: AppCompatActivity)
    {
        try
        {
            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            readIsbnImmediately.value = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY,
                IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE
            )
            overwriteTitleFromIsbn.value = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
            )

            archiveOnlyOneFile.value = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE,
                IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE_DEFAULT_VALUE
            )
            Log.v(TAG, "PreferenceViewModel::initializeViewModel() isbn: ${readIsbnImmediately.value} overwriteTitle: ${overwriteTitleFromIsbn.value}")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCheckIsbnImmediately(value: Boolean)
    {
        try
        {
            if (!::preference.isInitialized)
            {
                Log.v(TAG, " Preference Manager is unknown...")
                return
            }
            val editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY, value)
            editor.apply()
            readIsbnImmediately.value = value
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setOverwriteInformation(value: Boolean)
    {
        try
        {
            if (!::preference.isInitialized)
            {
                Log.v(TAG, " Preference Manager is unknown...")
                return
            }
            val editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE, value)
            editor.apply()
            overwriteTitleFromIsbn.value = value
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setArchiveOneFile(value: Boolean)
    {
        try
        {
            if (!::preference.isInitialized)
            {
                Log.v(TAG, " Preference Manager is unknown...")
                return
            }
            val editor = preference.edit()
            editor.putBoolean(IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE, value)
            editor.apply()
            archiveOnlyOneFile.value = value
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = PreferenceViewModel::class.java.simpleName
    }
}
