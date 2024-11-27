package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.wrapper

import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraRunMode
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraRunModeCallback

class RicohGr2RunMode : ICameraRunMode
{
    private var recordingMode = true
    override fun changeRunMode(isRecording: Boolean, callback: ICameraRunModeCallback)
    {
        // モードレスなので、絶対成功する
        recordingMode = isRecording
        callback.onCompleted(isRecording)
    }

    override fun isRecordingMode(): Boolean
    {
        return recordingMode
    }
}
