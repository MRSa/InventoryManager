package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.PixproCommandCommunicator
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLogDumper

class PixproCommandOnlyCallback(private val isDumpReceiveMessage: Boolean = false) : IPixproCommandCallback
{
    companion object
    {
        private val TAG = PixproCommandOnlyCallback::class.java.simpleName
    }

    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        Log.v(TAG, " RECEIVED MESSAGE : $id (${rx_body?.size} bytes.)")
        try
        {
            if (rx_body != null)
            {
                if (isDumpReceiveMessage)
                {
                    // 受信データをログに出力する
                    SimpleLogDumper.dumpBytes("[${rx_body.size}]", rx_body)
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
