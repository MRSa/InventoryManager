package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusUpdateNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproStatusRequest
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLogDumper
import java.lang.Exception

class PixproStatusChecker : IPixproCommandCallback, ICameraStatusWatcher, ICameraStatus
{
    private val statusHolder = PixproStatusHolder()
    private var whileFetching = false
    private var notifier: ICameraStatusUpdateNotify? = null
    private var pollDuration: Int = EVENT_POLL_QUEUE_MS
    private lateinit var commandPublisher : IPixproCommandPublisher

    fun setCommandPublisher(commandPublisher : IPixproCommandPublisher)
    {
        this.commandPublisher = commandPublisher
        statusHolder.setCommandPublisher(commandPublisher)
    }

    override fun getStatusList(key: String): List<String?>
    {
        try
        {
            return (statusHolder.getAvailableItemList(key))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (ArrayList())
    }

    override fun getStatus(key: String): String
    {
        try
        {
            return (statusHolder.getItemStatus(key))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    override fun getStatusColor(key: String): Int
    {
        try
        {
            return (statusHolder.getItemStatusColor(key))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (Color.WHITE)
    }

    override fun setStatus(key: String, value: String)
    {
        Log.v(TAG, "setStatus($key, $value)")
        try
        {
            return (statusHolder.setItemStatus(key, value))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun startStatusWatch(indicator: IMessageDrawer?, notifier: ICameraStatusUpdateNotify?)
    {
        if (whileFetching)
        {
            Log.v(TAG, "startStatusWatch() already starting.")
            return
        }
        try
        {
            this.notifier = notifier
            whileFetching = true

            val thread = Thread {
                while (whileFetching)
                {
                    try
                    {
                        Thread.sleep(pollDuration.toLong())

                        Log.v(TAG, " === POLL EVENT === ")
                        if (::commandPublisher.isInitialized)
                        {
                            commandPublisher.enqueueCommand(PixproStatusRequest(this))
                        }
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
            }
            try
            {
                thread.start()
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopStatusWatch()
    {
        Log.v(TAG, "stopStatusWatch()")
        whileFetching = false
        notifier = null
    }

    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        if (isDumpLog)
        {
            Log.v(TAG, " RECEIVED EVENT : ${rx_body?.size} bytes.")
            SimpleLogDumper.dumpBytes("EVT[${rx_body?.size}]", rx_body)
        }
        try
        {
            val length = rx_body?.size ?: 0
            if (length == 2744)
            {
                // TAKE MODE
                val takeMode = when (val take0 = rx_body?.get(16 * 136 + 8))
                {
                    0x01.toByte() -> "P"
                    0x04.toByte() -> "M"
                    0x06.toByte() -> "ASCN"
                    0x07.toByte() -> "Video"
                    0x0c.toByte() -> "Cont."
                    else -> "($take0)"
                }
                statusHolder.updateValue(ICameraStatus.TAKE_MODE, takeMode)

                // FLASH Mode
                val flashMode = when (val flash0 = rx_body?.get(16 * 127 + 8))
                {
                    0x01.toByte() -> "OFF"
                    0x02.toByte() -> "AUTO"
                    0x04.toByte() -> "ON"
                    else -> "($flash0)"
                }
                statusHolder.updateValue(ICameraStatus.TORCH_MODE, flashMode)

                // WHITE BALANCE
                val whiteBalance = when (val wb0 = rx_body?.get(16 * 125 + 8))
                {
                    0x01.toByte() -> "AUTO"
                    0x02.toByte() -> "Daylight"
                    0x04.toByte() -> "Cloudy"
                    0x10.toByte() -> "Fluorescent"
                    0x20.toByte() -> "Fluorescent CWF"
                    0x80.toByte() -> "Incandescent"
                    else -> "($wb0)"
                }
                statusHolder.updateValue(ICameraStatus.WHITE_BALANCE, whiteBalance)

                // ISO SENSITIVITY
                val isoSensitivity = when (val iso0 = rx_body?.get(16 * 70 + 0))
                {
                    0x00.toByte() -> "Auto"
                    0x01.toByte() -> "100"
                    0x02.toByte() -> "200"
                    0x03.toByte() -> "400"
                    0x04.toByte() -> "800"
                    0x05.toByte() -> "1600"
                    0x06.toByte() -> "3200"
                    else -> "($iso0)"
                }
                statusHolder.updateValue(ICameraStatus.ISO_SENSITIVITY, isoSensitivity)

                // EXPOSURE Compensation
                val exposureCompensation = when (val exprev0 = rx_body?.get(16 * 61 + 0))
                {
                    0x00.toByte() -> "-3.0"
                    0x01.toByte() -> "-2.7"
                    0x02.toByte() -> "-2.3"
                    0x03.toByte() -> "-2.0"
                    0x04.toByte() -> "-1.7"
                    0x05.toByte() -> "-1.3"
                    0x06.toByte() -> "-1.0"
                    0x07.toByte() -> "-0.7"
                    0x08.toByte() -> "-0.3"
                    0x09.toByte() -> "0.0"
                    0x0a.toByte() -> "+0.3"
                    0x0b.toByte() -> "+0.7"
                    0x0c.toByte() -> "+1.0"
                    0x0d.toByte() -> "+1.3"
                    0x0e.toByte() -> "+1.7"
                    0x0f.toByte() -> "+2.0"
                    0x10.toByte() -> "+2.3"
                    0x11.toByte() -> "+2.7"
                    0x12.toByte() -> "+3.0"

                    else -> "($exprev0)"
                }
                statusHolder.updateValue(ICameraStatus.EXPREV, exposureCompensation)

                // Shutter Speed
                val shutterSpeed = when (val sv = rx_body?.get(16 * 75 + 8))
                {
                    0x01.toByte() -> "1/2000"
                    0x02.toByte() -> "1/1600"
                    0x03.toByte() -> "1/1200"
                    0x04.toByte() -> "1/1000"
                    0x05.toByte() -> "1/800"
                    0x06.toByte() -> "1/600"
                    0x07.toByte() -> "1/500"
                    0x08.toByte() -> "1/400"
                    0x09.toByte() -> "1/320"
                    0x0a.toByte() -> "1/250"
                    0x0b.toByte() -> "1/200"
                    0x0c.toByte() -> "1/160"
                    0x0d.toByte() -> "1/125"
                    0x0e.toByte() -> "1/100"
                    0x0f.toByte() -> "1/80"
                    0x10.toByte() -> "1/60"
                    0x11.toByte() -> "1/50"
                    0x12.toByte() -> "1/40"
                    0x13.toByte() -> "1/30"
                    0x14.toByte() -> "1/25"
                    0x15.toByte() -> "1/20"
                    0x16.toByte() -> "1/15"
                    0x17.toByte() -> "1/13"
                    0x18.toByte() -> "1/10"
                    0x19.toByte() -> "1/8"
                    0x1a.toByte() -> "1/6"
                    0x1b.toByte() -> "1/5"
                    0x1c.toByte() -> "1/4"
                    0x1d.toByte() -> "1/3"
                    0x1e.toByte() -> "1/2.5"
                    0x1f.toByte() -> "1/2"
                    0x20.toByte() -> "1/1.6"
                    0x21.toByte() -> "1/1.3"
                    0x22.toByte() -> "1s"
                    0x23.toByte() -> "1.3s"
                    0x24.toByte() -> "1.5s"
                    0x25.toByte() -> "2s"
                    0x26.toByte() -> "2.5s"
                    0x27.toByte() -> "3s"
                    0x28.toByte() -> "4s"
                    0x29.toByte() -> "5s"
                    0x2a.toByte() -> "6s"
                    0x2b.toByte() -> "8s"
                    0x2c.toByte() -> "10s"
                    0x2d.toByte() -> "13s"
                    0x2e.toByte() -> "15s"
                    0x2f.toByte() -> "20s"
                    0x30.toByte() -> "25s"
                    0x31.toByte() -> "30s"
                    0x00.toByte() -> ""
                    else -> "($sv)"
                }
                statusHolder.updateValue(ICameraStatus.SHUTTER_SPEED, shutterSpeed)

                // IMAGE SIZE
                val imageSize = when (val picSize = (rx_body?.get(16 * 129 + 8) ?: 0).toInt() + (rx_body?.get(16 * 129 + 9) ?: 0) * 256 + ((rx_body?.get(16 * 129 + 10) ?: 0) * 65536) + ((rx_body?.get(16 * 129 + 11) ?: 0) * 16777216))
                {
                    0x00010000 -> "4608x3456"
                    0x00004000 -> "4608x3072"
                    0x00001000 -> "4608x2592"
                    0x00000400 -> "3648x2736"
                    0x00000020 -> "2592x1944"
                    0x00000008 -> "2048x1536"
                    0x00000004 -> "1920x1080"
                    0x00000001 ->  "640x480"
                    0x00000010 -> "2336x1752"
                    else -> "($picSize)"
                }
                statusHolder.updateValue(ICameraStatus.IMAGE_SIZE, imageSize)

                // MOVIE SIZE
                val movieSize = when (val videoSize = (rx_body?.get(16 * 132 + 0) ?: 0).toInt() + (rx_body?.get(16 * 132 + 1) ?: 0) * 256 + ((rx_body?.get(16 * 132 + 2) ?: 0) * 65536) + ((rx_body?.get(16 * 132 + 3) ?: 0) * 16777216))
                {
                    0x00400000 -> "1920×1080 30p"
                    0x00080000 -> "1280x720 60p"
                    0x00040000 -> "1280x720 30p"
                    0x00000100 -> "640x480 120p"
                    0x00000040 -> "640x480 30p"
                    else -> "[$videoSize]"
                }
                statusHolder.updateValue(ICameraStatus.MOVIE_SIZE, movieSize)

            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = PixproStatusChecker::class.java.simpleName
        private const val EVENT_POLL_QUEUE_MS = 550  // 550ms
        private const val isDumpLog = false
    }

}
