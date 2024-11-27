package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation

import android.util.Log

import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproExecuteZoom
import java.lang.Exception

class ZoomControl(private val commandPublisher: IPixproCommandPublisher) : IZoomLensControl, IPixproCommandCallback
{
    override fun canZoom(): Boolean { return true }
    override fun updateStatus() { }
    override fun getMaximumFocalLength(): Float { return 0.0f }
    override fun getMinimumFocalLength(): Float { return 0.0f }
    override fun getCurrentFocalLength(): Float { return 0.0f }
    override fun driveZoomLens(targetLength: Float) { }
    override fun driveZoomLens(isZoomIn: Boolean)
    {
        try
        {
            Log.v(TAG, " Zoom in : $isZoomIn")
            commandPublisher.enqueueCommand(PixproExecuteZoom(this, if (isZoomIn) 1 else -1))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
    override fun moveInitialZoomPosition() { }
    override fun isDrivingZoomLens(): Boolean { return false }
    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        Log.v(TAG, " KodakFocusingControl::receivedMessage() : ")
    }

    companion object
    {
        private val TAG = ZoomControl::class.java.simpleName
    }
}
