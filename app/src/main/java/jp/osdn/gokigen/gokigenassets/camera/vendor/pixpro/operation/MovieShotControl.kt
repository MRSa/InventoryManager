package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproExecuteVideo
import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay

class MovieShotControl(private val commandPublisher: IPixproCommandPublisher, private val frameDisplayer: IAutoFocusFrameDisplay, private val cameraStatus: ICameraStatus) : ICaptureControl, IPixproCommandCallback
{
    private var isMovieRecording = false

    override fun doCapture(kind: Int)
    {
        try
        {
            // シャッター
            Log.v(TAG, " doCapture($kind) : $isMovieRecording ")

            isMovieRecording = if ((kind <= -1)||(isMovieRecording))
            {
                // ビデオ録画終了
                Log.v(TAG, " FINISH VIDEO")
                commandPublisher.enqueueCommand(PixproExecuteVideo(this, true))
                cameraStatus.setStatus(ICameraStatus.CAPTURE_MODE, "")
                frameDisplayer.hideFocusFrame()
                false
            }
            else
            {
                // ビデオ録画開始
                Log.v(TAG, " START VIDEO")
                commandPublisher.enqueueCommand(PixproExecuteVideo(this, false))
                cameraStatus.setStatus(ICameraStatus.CAPTURE_MODE, " REC ")
                frameDisplayer.hideFocusFrame()
                true
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        Log.v(TAG, " MovieShotControl::receivedMessage() : ${rx_body?.size} bytes")
    }

    companion object
    {
        private val TAG = MovieShotControl::class.java.simpleName
    }
}
