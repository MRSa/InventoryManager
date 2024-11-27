package jp.osdn.gokigen.gokigenassets.preference

import android.content.Context

interface IPreferenceValueInitializer
{
    fun initializePreferences(context : Context)
}
