package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraRunModeCallback
{
    fun onCompleted(isRecording: Boolean)
    fun onErrorOccurred(isRecording: Boolean)
}
