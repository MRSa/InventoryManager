package jp.osdn.gokigen.inventorymanager.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceValueInitializer
{
    fun initializePreferences(context : Context)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context) ?: return
            val items : Map<String, *> = preferences.all
            val editor : SharedPreferences.Editor = preferences.edit()

            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY))
            {
                editor.putBoolean(
                    IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY,
                    IPreferencePropertyAccessor.PREFERENCE_CHECK_ISBN_IMMEDIATELY_DEFAULT_VALUE
                )
            }
            editor.apply()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = PreferenceValueInitializer::class.java.simpleName
        private const val REQUEST_CODE_PERMISSIONS = 10
    }
}