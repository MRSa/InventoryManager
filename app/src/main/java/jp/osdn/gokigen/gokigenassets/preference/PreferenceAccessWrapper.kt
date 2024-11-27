package jp.osdn.gokigen.gokigenassets.preference

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceManager

class PreferenceAccessWrapper(context : Context) : PreferenceDataStore()
{
    private val preferences = PreferenceManager.getDefaultSharedPreferences(context)

    override fun getString(key : String, defaultValue : String?) : String
    {
        try
        {
            val value = preferences.getString(key, defaultValue)
            if (value != null)
            {
                return (value)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (defaultValue ?: "")
    }

    override fun getBoolean(key : String, defaultValue : Boolean) : Boolean
    {
        try
        {
            return (preferences.getBoolean(key, defaultValue))
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (defaultValue)
    }

    override fun putString(key: String, value: String?)
    {
        try
        {
            val editor : SharedPreferences.Editor = preferences.edit()
            editor.putString(key, value)
            editor.apply()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}
