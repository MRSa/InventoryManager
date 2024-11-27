package jp.osdn.gokigen.gokigenassets.camera.preference

class CameraPreferenceKeySet(private val key1 : String, private val key2 : String, private val key3 : String, private val key4 : String, private val key5 : String)
{
    fun getOption1Key() : String { return (key1) }
    fun getOption2Key() : String { return (key2) }
    fun getOption3Key() : String { return (key3) }
    fun getOption4Key() : String { return (key4) }
    fun getOption5Key() : String { return (key5) }
}
