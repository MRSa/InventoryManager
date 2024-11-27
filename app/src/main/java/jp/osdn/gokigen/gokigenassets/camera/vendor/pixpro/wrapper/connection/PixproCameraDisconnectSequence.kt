package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.connection

import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.IPixproInternalInterfaces
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status.PixproStatusChecker
import java.lang.Exception


class PixproCameraDisconnectSequence(private val context: AppCompatActivity, private val powerOff: Boolean, private val interfaceProvider : IPixproInternalInterfaces, private val statusChecker: PixproStatusChecker) : Runnable
{
    override fun run()
    {
        try
        {
            statusChecker.stopStatusWatch()
            interfaceProvider.getIPixproCommunication().disconnect()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
