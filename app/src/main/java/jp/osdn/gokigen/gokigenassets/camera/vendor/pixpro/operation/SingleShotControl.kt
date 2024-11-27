package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICaptureControl
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproExecuteShutter
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay

class SingleShotControl(private val commandPublisher: IPixproCommandPublisher, frameDisplayer: IAutoFocusFrameDisplay?) : ICaptureControl, IPixproCommandCallback
{
    override fun doCapture(kind: Int)
    {
        try
        {
            // シャッター
            Log.v(TAG, " doCapture() : $kind")

            // シャッターを切る
            commandPublisher.enqueueCommand(PixproExecuteShutter(this))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        Log.v(TAG, " SingleShotControl::receivedMessage() : ")
/*
        try
        {
            int responseCode = (rx_body[8] & 0xff) + ((rx_body[9] & 0xff) * 256);
            if ((rx_body.length > 10) && (responseCode != 0x2001))
            {
                Log.v(TAG, String.format(" RECEIVED NG REPLY ID : %d, RESPONSE CODE : 0x%04x ", id, responseCode));
            }
            else
            {
                Log.v(TAG, String.format(" OK REPLY (ID : %d) ", id));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
*/
    }

    companion object
    {
        private val TAG = SingleShotControl::class.java.simpleName
    }
}
