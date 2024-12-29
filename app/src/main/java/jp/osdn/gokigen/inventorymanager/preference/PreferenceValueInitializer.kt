package jp.osdn.gokigen.inventorymanager.preference

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.preference.PreferenceManager

class PreferenceValueInitializer
{
    fun initializePreferences(context : Context)
    {
        try
        {
            Log.v(TAG, "initializePreferences()")
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

            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE))
            {
                editor.putBoolean(
                    IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE,
                    IPreferencePropertyAccessor.PREFERENCE_OVERWRITE_FROM_ISBN_TO_TITLE_DEFAULT_VALUE
                )
            }

            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE))
            {
                editor.putBoolean(
                    IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE,
                    IPreferencePropertyAccessor.PREFERENCE_EXPORT_ARCHIVE_ONLY_ONE_FILE_DEFAULT_VALUE
                )
            }

            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_METHOD_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_SEQUENCE_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION1_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION2_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION3_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION4_1_DEFAULT_VALUE
                )
            }
            if (!items.containsKey(IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1))
            {
                editor.putString(
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1,
                    IPreferencePropertyAccessor.PREFERENCE_CAMERA_OPTION5_1_DEFAULT_VALUE
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
    }
}