package jp.osdn.gokigen.gokigenassets.camera.preference

import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper

class CameraPreference(private val id : Int, private val wrapper : PreferenceAccessWrapper, method0 : String, private val isReadonly : Boolean = false, sequence0 : String = "0", option1_ : String = "", option2_ : String = "", option3_ : String = "", option4_ : String = "", option5_ : String = "", private val keySet : CameraPreferenceKeySet? = null) : ICameraPreferenceProvider,
    ICameraPreferenceUpdater
{
    private var method = method0
    private var sequence = sequence0
    private var option1 = option1_
    private var option2 = option2_
    private var option3 = option3_
    private var option4 = option4_
    private var option5 = option5_

    override fun getId(): Int
    {
        return (id)
    }

    override fun getCameraMethod(): String
    {
        return (method)
    }

    override fun getConnectionSequence(): String
    {
        return (sequence)
    }

    override fun getCameraOption1(): String
    {
        return (option1)
    }

    override fun getCameraOption2(): String
    {
        return (option2)
    }

    override fun getCameraOption3(): String
    {
        return (option3)
    }

    override fun getCameraOption4(): String
    {
        return (option4)
    }

    override fun getCameraOption5(): String
    {
        return (option5)
    }

    override fun getUpdater(): ICameraPreferenceUpdater?
    {
        if (isReadonly)
        {
            return (null)
        }
        return (this)
    }

    override fun setCameraOption1(value: String)
    {
        option1 = value
        if (keySet != null)
        {
            wrapper.putString(keySet.getOption1Key(), option1)
        }
    }

    override fun setCameraOption2(value: String)
    {
        option2 = value
        if (keySet != null)
        {
            wrapper.putString(keySet.getOption2Key(), option2)
        }
    }

    override fun setCameraOption3(value: String)
    {
        option3 = value
        if (keySet != null)
        {
            wrapper.putString(keySet.getOption3Key(), option3)
        }
    }

    override fun setCameraOption4(value: String)
    {
        option4 = value
        if (keySet != null)
        {
            wrapper.putString(keySet.getOption4Key(), option4)
        }
    }

    override fun setCameraOption5(value: String)
    {
        option5 = value
        if (keySet != null)
        {
            wrapper.putString(keySet.getOption5Key(), option5)
        }
    }
}
