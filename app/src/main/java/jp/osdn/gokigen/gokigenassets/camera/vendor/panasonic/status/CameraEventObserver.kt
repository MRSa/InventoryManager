package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status

import android.content.Context
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusUpdateNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICardSlotSelector
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class CameraEventObserver(context: Context, private val remote: IPanasonicCamera, cardSlotSelector: ICardSlotSelector, private val sleepMs : Long = 500) : ICameraStatusWatcher
{
    private val statusHolder = CameraStatusHolder(context, remote, cardSlotSelector)
    private val statusConvert = CameraStatusConvert(statusHolder, remote)
    private var isEventMonitoring = false
    private var isActive = false

    override fun startStatusWatch(indicator : IMessageDrawer?, notifier: ICameraStatusUpdateNotify?)
    {
        try
        {
            isActive = true
            start()
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stopStatusWatch()
    {
        try
        {
            isEventMonitoring = false
            isActive = false
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun start(): Boolean
    {
        if (!isActive)
        {
            Log.w(TAG, "start() observer is not active.")
            return false
        }
        if (isEventMonitoring)
        {
            Log.w(TAG, "start() already starting.")
            return false
        }
        isEventMonitoring = true
        try
        {
            val http = SimpleHttpClient()
            val thread: Thread = object : Thread() {
                override fun run() {
                    Log.d(TAG, "start() exec.")
                    while (isEventMonitoring)
                    {
                        try
                        {
                            val sessionId = remote.getCommunicationSessionId()
                            val urlToSend = "${remote.getCmdUrl()}cam.cgi?mode=getstate"
                            val reply = if (!sessionId.isNullOrEmpty())
                            {
                                val headerMap: MutableMap<String, String> = HashMap()
                                headerMap["X-SESSION_ID"] = sessionId
                                http.httpGetWithHeader(urlToSend, headerMap, null, TIMEOUT_MS) ?: ""
                            }
                            else
                            {
                                http.httpGet(urlToSend, TIMEOUT_MS)
                            }
                            // parse reply message
                            statusHolder.parse(reply)
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                        try
                        {
                            sleep(sleepMs)
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                    }
                    isEventMonitoring = false
                }
            }
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    fun setEventListener(listener: ICameraChangeListener)
    {
        try
        {
            statusHolder.setEventChangeListener(listener)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

/*
    private fun clearEventListener()
    {
        try
        {
            statusHolder.clearEventChangeListener()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
*/

    fun getCameraStatusConvert(): ICameraStatus
    {
        return (statusConvert)
    }

    fun getCameraStatusEventObserver(): ICameraEventObserver
    {
        return (statusConvert)
    }

/*
    private fun activate()
    {
        isActive = true
    }
*/

    companion object
    {
        private val TAG = CameraEventObserver::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }
}
