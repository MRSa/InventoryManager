package jp.osdn.gokigen.gokigenassets.utils.preferences

import androidx.preference.PreferenceDataStore

class MyPreferenceDataStore: PreferenceDataStore()
{
    override fun putString(key: String, value: String?)
    {
        // Save the value somewhere
    }

    override fun getString(key: String, defValue: String?): String
    {
        // Retrieve the value

        return ("")
    }

}