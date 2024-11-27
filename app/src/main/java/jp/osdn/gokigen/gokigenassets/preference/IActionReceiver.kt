package jp.osdn.gokigen.gokigenassets.preference

import android.content.SharedPreferences
import androidx.preference.Preference

interface IActionReceiver
{
    fun getPreferenceChangeListener() : SharedPreferences.OnSharedPreferenceChangeListener
    fun getPreferenceClickListener() : Preference.OnPreferenceClickListener
    fun setPreferenceViewUpdater(updater: IPreferenceViewUpdater)
}
