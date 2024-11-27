package jp.osdn.gokigen.gokigenassets.camera.preference

interface ICameraPreferenceProvider
{
    fun getId() : Int
    fun getCameraMethod() : String
    fun getConnectionSequence() : String

    fun getCameraOption1() : String
    fun getCameraOption2() : String
    fun getCameraOption3() : String
    fun getCameraOption4() : String
    fun getCameraOption5() : String

    fun getUpdater() : ICameraPreferenceUpdater?
}
