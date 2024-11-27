package jp.osdn.gokigen.gokigenassets.preference

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.preference.*
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_LAYOUT_PREFERENCE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_METHOD
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_SHOW_GRID
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_ARRAY_SHOW_GRID_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_METHOD_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION1_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION2_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION3_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION4_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_OPTION5_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_1
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_2
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_3
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_4
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_5
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_6
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_7
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAMERA_SEQUENCE_8
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_LABEL_DEBUG_INFO
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_LABEL_EXIT_APPLICATION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_LABEL_SELECT_CAMERA_CONNECTION_METHOD
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_LABEL_WIFI_SETTINGS
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SELF_TIMER_ARRAY
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SELF_TIMER_ARRAY_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SELF_TIMER_SECONDS
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SHOW_GRID
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_CAMERAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_EXAMPLE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_NONE
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_OMDS
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PANASONIC
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PENTAX
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_PIXPRO
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_SONY
import jp.osdn.gokigen.gokigenassets.constants.ICameraConnectionMethods.Companion.PREFERENCE_CAMERA_METHOD_THETA

class MainPreferenceFragment : PreferenceFragmentCompat(), IPreferenceViewUpdater
{
    private lateinit var actionReceiver : IActionReceiver
    private lateinit var valueInitializer : IPreferenceValueInitializer

    private fun setActionReceiver(receiver : IActionReceiver)
    {
        actionReceiver = receiver
        actionReceiver.setPreferenceViewUpdater(this)
    }

    private fun setValueInitializer(initializer : IPreferenceValueInitializer)
    {
        valueInitializer = initializer
    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)
        Log.v(TAG, " onAttach() : ")

        prepareChangeListener(context)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?)
    {
        setPreferencesFromResource(ID_LAYOUT_PREFERENCE, rootKey)

        prepareClickListener(ID_PREFERENCE_LABEL_EXIT_APPLICATION)
        prepareClickListener(ID_PREFERENCE_LABEL_WIFI_SETTINGS)
        prepareClickListener(ID_PREFERENCE_LABEL_DEBUG_INFO)
        prepareClickListener(ID_PREFERENCE_LABEL_SELECT_CAMERA_CONNECTION_METHOD)

        setMethodSummary(ID_PREFERENCE_SELF_TIMER_SECONDS, ID_PREFERENCE_SELF_TIMER_ARRAY, ID_PREFERENCE_SELF_TIMER_ARRAY_VALUE)

        setMethodSummary(ID_PREFERENCE_SHOW_GRID, ID_PREFERENCE_ARRAY_SHOW_GRID, ID_PREFERENCE_ARRAY_SHOW_GRID_VALUE)

        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_1, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_2, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_3, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_4, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_5, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_6, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_7, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_METHOD_8, ID_PREFERENCE_ARRAY_CAMERA_METHOD, ID_PREFERENCE_ARRAY_CAMERA_METHOD_VALUE)

        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_1, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_2, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_3, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_4, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_5, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_6, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_7, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)
        setMethodSummary(ID_PREFERENCE_CAMERA_SEQUENCE_8, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE, ID_PREFERENCE_ARRAY_CAMERA_SEQUENCE_VALUE)

        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_1, ID_PREFERENCE_CAMERA_SEQUENCE_1, ID_PREFERENCE_CAMERA_OPTION1_1, ID_PREFERENCE_CAMERA_OPTION2_1, ID_PREFERENCE_CAMERA_OPTION3_1, ID_PREFERENCE_CAMERA_OPTION4_1, ID_PREFERENCE_CAMERA_OPTION5_1)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_2, ID_PREFERENCE_CAMERA_SEQUENCE_2, ID_PREFERENCE_CAMERA_OPTION1_2, ID_PREFERENCE_CAMERA_OPTION2_2, ID_PREFERENCE_CAMERA_OPTION3_2, ID_PREFERENCE_CAMERA_OPTION4_2, ID_PREFERENCE_CAMERA_OPTION5_2)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_3, ID_PREFERENCE_CAMERA_SEQUENCE_3, ID_PREFERENCE_CAMERA_OPTION1_3, ID_PREFERENCE_CAMERA_OPTION2_3, ID_PREFERENCE_CAMERA_OPTION3_3, ID_PREFERENCE_CAMERA_OPTION4_3, ID_PREFERENCE_CAMERA_OPTION5_3)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_4, ID_PREFERENCE_CAMERA_SEQUENCE_4, ID_PREFERENCE_CAMERA_OPTION1_4, ID_PREFERENCE_CAMERA_OPTION2_4, ID_PREFERENCE_CAMERA_OPTION3_4, ID_PREFERENCE_CAMERA_OPTION4_4, ID_PREFERENCE_CAMERA_OPTION5_4)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_5, ID_PREFERENCE_CAMERA_SEQUENCE_5, ID_PREFERENCE_CAMERA_OPTION1_5, ID_PREFERENCE_CAMERA_OPTION2_5, ID_PREFERENCE_CAMERA_OPTION3_5, ID_PREFERENCE_CAMERA_OPTION4_5, ID_PREFERENCE_CAMERA_OPTION5_5)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_6, ID_PREFERENCE_CAMERA_SEQUENCE_6, ID_PREFERENCE_CAMERA_OPTION1_6, ID_PREFERENCE_CAMERA_OPTION2_6, ID_PREFERENCE_CAMERA_OPTION3_6, ID_PREFERENCE_CAMERA_OPTION4_6, ID_PREFERENCE_CAMERA_OPTION5_6)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_7, ID_PREFERENCE_CAMERA_SEQUENCE_7, ID_PREFERENCE_CAMERA_OPTION1_7, ID_PREFERENCE_CAMERA_OPTION2_7, ID_PREFERENCE_CAMERA_OPTION3_7, ID_PREFERENCE_CAMERA_OPTION4_7, ID_PREFERENCE_CAMERA_OPTION5_7)
        updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_8, ID_PREFERENCE_CAMERA_SEQUENCE_8, ID_PREFERENCE_CAMERA_OPTION1_8, ID_PREFERENCE_CAMERA_OPTION2_8, ID_PREFERENCE_CAMERA_OPTION3_8, ID_PREFERENCE_CAMERA_OPTION4_8, ID_PREFERENCE_CAMERA_OPTION5_8)

    }

    private fun setMethodSummary(itemKey : String, listArray : Int, listArrayValue : Int)
    {
        try
        {
            val preferenceItem = findPreference<DropDownPreference>(itemKey)
            if (preferenceItem != null)
            {
                preferenceItem.summary = preferenceItem.entry
                preferenceItem.onPreferenceChangeListener = Preference.OnPreferenceChangeListener { _, newValue ->
                    try
                    {
                        val listArrayValues = context?.resources!!.getStringArray(listArray)
                        val listArrays = context?.resources!!.getStringArray(listArrayValue)
                        val item = newValue.toString()
                        var summaryIsSet = false
                        var targetIndex = -1
                        for (checkItem in listArrays)
                        {
                            targetIndex++
                            Log.v(TAG, " $checkItem : $item $targetIndex ${listArrayValues[targetIndex]}")
                            if (item == checkItem)
                            {
                                preferenceItem.summary = listArrayValues[targetIndex]
                                summaryIsSet = true
                                break
                            }
                        }
                        if (!summaryIsSet)
                        {
                            preferenceItem.summary = (newValue  as CharSequence)
                        }
                    }
                    catch (e : Exception)
                    {
                        e.printStackTrace()
                        preferenceItem.summary = (newValue  as CharSequence) // 例外発生時には、とりあえず番号を表示した
                    }
                    true
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun updatePreferenceVisibility(methodKey: String, sequenceKey: String, option1Key: String, option2Key: String, option3Key: String, option4Key: String, option5Key: String)
    {
        try
        {
            val myContext = context
            if (myContext != null)
            {
                val preference = PreferenceAccessWrapper(myContext)
                when (preference.getString(methodKey, "")) {
                    PREFERENCE_CAMERA_METHOD_NONE -> updatePreferenceItemVisibility(false, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_EXAMPLE -> updatePreferenceItemVisibility(false, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_CAMERAX -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_THETA -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_PENTAX -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_PANASONIC  -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_SONY  -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_PIXPRO  -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    PREFERENCE_CAMERA_METHOD_OMDS  -> updatePreferenceItemVisibility(true, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                    else -> updatePreferenceItemVisibility(false, sequenceKey, option1Key, option2Key, option3Key, option4Key, option5Key)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun updatePreferenceItemVisibility(visibility: Boolean, sequenceKey: String, option1Key: String, option2Key: String, option3Key: String, option4Key: String, option5Key: String)
    {
        try
        {
            val myContext = context
            if (myContext != null)
            {
                val sequencePreference: DropDownPreference? = findPreference(sequenceKey)
                sequencePreference?.isVisible = visibility

                val option1Preference: EditTextPreference? = findPreference(option1Key)
                option1Preference?.isVisible = visibility

                val option2Preference: EditTextPreference? = findPreference(option2Key)
                option2Preference?.isVisible = visibility

                val option3Preference: EditTextPreference? = findPreference(option3Key)
                option3Preference?.isVisible = visibility

                val option4Preference: EditTextPreference? = findPreference(option4Key)
                option4Preference?.isVisible = visibility

                val option5Preference: EditTextPreference? = findPreference(option5Key)
                option5Preference?.isVisible = visibility
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun prepareChangeListener(context: Context)
    {
        try
        {
            val preferences = PreferenceManager.getDefaultSharedPreferences(context)
            valueInitializer.initializePreferences(context)
            preferences.registerOnSharedPreferenceChangeListener(actionReceiver.getPreferenceChangeListener())
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun prepareClickListener(label: String)
    {
        try
        {
            val settings : Preference? = findPreference(label)
            settings?.onPreferenceClickListener = actionReceiver.getPreferenceClickListener()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onPreferenceUpdated(key: String)
    {
        Log.v(TAG, " onPreferenceUpdated($key)")
        try
        {
            when (key)
            {
                ID_PREFERENCE_CAMERA_METHOD_1 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_1, ID_PREFERENCE_CAMERA_SEQUENCE_1, ID_PREFERENCE_CAMERA_OPTION1_1, ID_PREFERENCE_CAMERA_OPTION2_1, ID_PREFERENCE_CAMERA_OPTION3_1, ID_PREFERENCE_CAMERA_OPTION4_1, ID_PREFERENCE_CAMERA_OPTION5_1)
                ID_PREFERENCE_CAMERA_METHOD_2 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_2, ID_PREFERENCE_CAMERA_SEQUENCE_2, ID_PREFERENCE_CAMERA_OPTION1_2, ID_PREFERENCE_CAMERA_OPTION2_2, ID_PREFERENCE_CAMERA_OPTION3_2, ID_PREFERENCE_CAMERA_OPTION4_2, ID_PREFERENCE_CAMERA_OPTION5_2)
                ID_PREFERENCE_CAMERA_METHOD_3 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_3, ID_PREFERENCE_CAMERA_SEQUENCE_3, ID_PREFERENCE_CAMERA_OPTION1_3, ID_PREFERENCE_CAMERA_OPTION2_3, ID_PREFERENCE_CAMERA_OPTION3_3, ID_PREFERENCE_CAMERA_OPTION4_3, ID_PREFERENCE_CAMERA_OPTION5_3)
                ID_PREFERENCE_CAMERA_METHOD_4 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_4, ID_PREFERENCE_CAMERA_SEQUENCE_4, ID_PREFERENCE_CAMERA_OPTION1_4, ID_PREFERENCE_CAMERA_OPTION2_4, ID_PREFERENCE_CAMERA_OPTION3_4, ID_PREFERENCE_CAMERA_OPTION4_4, ID_PREFERENCE_CAMERA_OPTION5_4)
                ID_PREFERENCE_CAMERA_METHOD_5 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_5, ID_PREFERENCE_CAMERA_SEQUENCE_5, ID_PREFERENCE_CAMERA_OPTION1_5, ID_PREFERENCE_CAMERA_OPTION2_5, ID_PREFERENCE_CAMERA_OPTION3_5, ID_PREFERENCE_CAMERA_OPTION4_5, ID_PREFERENCE_CAMERA_OPTION5_5)
                ID_PREFERENCE_CAMERA_METHOD_6 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_6, ID_PREFERENCE_CAMERA_SEQUENCE_6, ID_PREFERENCE_CAMERA_OPTION1_6, ID_PREFERENCE_CAMERA_OPTION2_6, ID_PREFERENCE_CAMERA_OPTION3_6, ID_PREFERENCE_CAMERA_OPTION4_6, ID_PREFERENCE_CAMERA_OPTION5_6)
                ID_PREFERENCE_CAMERA_METHOD_7 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_7, ID_PREFERENCE_CAMERA_SEQUENCE_7, ID_PREFERENCE_CAMERA_OPTION1_7, ID_PREFERENCE_CAMERA_OPTION2_7, ID_PREFERENCE_CAMERA_OPTION3_7, ID_PREFERENCE_CAMERA_OPTION4_7, ID_PREFERENCE_CAMERA_OPTION5_7)
                ID_PREFERENCE_CAMERA_METHOD_8 -> updatePreferenceVisibility(ID_PREFERENCE_CAMERA_METHOD_8, ID_PREFERENCE_CAMERA_SEQUENCE_8, ID_PREFERENCE_CAMERA_OPTION1_8, ID_PREFERENCE_CAMERA_OPTION2_8, ID_PREFERENCE_CAMERA_OPTION3_8, ID_PREFERENCE_CAMERA_OPTION4_8, ID_PREFERENCE_CAMERA_OPTION5_8)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        fun newInstance(receiver : IActionReceiver, initializer : IPreferenceValueInitializer) = MainPreferenceFragment().apply {
            setActionReceiver(receiver)
            setValueInitializer(initializer)
        }
        private val TAG = MainPreferenceFragment::class.java.simpleName
    }

}
