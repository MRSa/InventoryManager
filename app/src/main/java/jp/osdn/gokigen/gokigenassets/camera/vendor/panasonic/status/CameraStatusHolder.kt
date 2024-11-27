package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status

import android.content.Context
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class CameraStatusHolder(private val context: Context, private val remote: IPanasonicCamera, private val cardSlotSelector: ICardSlotSelector) : ICardSlotSelectionReceiver, ICameraStatusHolder
{
    private var listener: ICameraChangeListener? = null
    private var currentSd = "sd1"
    private var isInitialized = false
    private var isDualSlot = false
    private var remainBattery : Int = 0

    fun parse(reply: String)
    {
        try
        {
            parseReceivedStatus(reply)
            var isEnableDualSlot = false
            if (reply.contains("<sd_memory>set</sd_memory>") && reply.contains("<sd2_memory>set</sd2_memory>")) {
                // カードが2枚刺さっている場合...
                isEnableDualSlot = true
            }
            if (!isInitialized || isDualSlot != isEnableDualSlot)
            {
                // 初回だけの実行...
                if (isEnableDualSlot)
                {
                    // カードが2枚刺さっている場合...
                    cardSlotSelector.setupSlotSelector(true, this)
                }
                else
                {
                    // カードが１つしか刺さっていない場合...
                    cardSlotSelector.setupSlotSelector(false, null)
                }
                isInitialized = true
                isDualSlot = isEnableDualSlot
            }
            checkCurrentSlot(reply)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkCurrentSlot(reply: String)
    {
        try
        {
            val header = "<current_sd>"
            val indexStart = reply.indexOf(header)
            val indexEnd = reply.indexOf("</current_sd>")
            if (indexStart > 0 && indexEnd > 0 && indexStart < indexEnd)
            {
                val currentSlot = reply.substring(indexStart + header.length, indexEnd)
                if (currentSd != currentSlot)
                {
                    currentSd = currentSlot
                    cardSlotSelector.changedCardSlot(currentSd)
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setEventChangeListener(listener: ICameraChangeListener)
    {
        this.listener = listener
    }

    fun clearEventChangeListener()
    {
        listener = null
    }

    override fun getCameraStatus(): String?
    {
        return null
    }

    override fun getLiveviewStatus(): Boolean
    {
        return false
    }

    override fun getShootMode(): String?
    {
        return null
    }

    override fun getAvailableShootModes(): List<String?>?
    {
        return null
    }

    override fun getZoomPosition(): Int
    {
        return 0
    }

    override fun getStorageId(): String
    {
        return currentSd
    }

    override fun slotSelected(slotId: String)
    {
        Log.v(TAG, " slotSelected : $slotId")
        if (currentSd != slotId)
        {
            // スロットを変更したい！
            requestToChangeSlot(slotId)
        }
    }

    private fun requestToChangeSlot(slotId: String)
    {
        try
        {
            val thread = Thread {
                try
                {
                    var loop = true
                    val http = SimpleHttpClient()
                    val sessionId = remote.getCommunicationSessionId()
                    val urlToSend = "${remote.getCmdUrl()}cam.cgi?mode=setsetting&type=current_sd&value=$slotId"
                    while (loop)
                    {
                        val reply = if (!sessionId.isNullOrEmpty())
                        {
                            val headerMap: MutableMap<String, String> = HashMap()
                            headerMap["X-SESSION_ID"] = sessionId
                            http.httpGetWithHeader(urlToSend, headerMap, null,
                                TIMEOUT_MS
                            ) ?: ""
                        }
                        else
                        {
                            http.httpGet(urlToSend, TIMEOUT_MS)
                        }
                        if (reply.indexOf("<result>ok</result>") > 0)
                        {
                            loop = false
                            cardSlotSelector.selectSlot(slotId)
                        }
                        else
                        {
                            Thread.sleep(1000) // 1秒待つ
                        }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun parseReceivedStatus(eventData : String)
    {
        try
        {
            //Log.v(TAG, " parseReceivedStatus : $eventData")
            parseBatteryInfo(eventData)
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun parseBatteryInfo(eventData : String)
    {
        try
        {
            val header = "<batt>"
            val indexStart = eventData.indexOf(header)
            val indexEnd = eventData.indexOf("</batt>")
            if (indexStart > 0 && indexEnd > 0 && indexStart < indexEnd)
            {
                val batteryInfo = eventData.substring(indexStart + header.length, indexEnd)
                val indexMiddle = batteryInfo.indexOf("/")
                val numerator  = batteryInfo.substring(0, indexMiddle).toFloat()
                val denominator = batteryInfo.substring(indexMiddle + 1, batteryInfo.length).toFloat()
                remainBattery = ((numerator/denominator) * 100.0f).toInt()
                // Log.v(TAG, "  ======  BATTERY INFORMATION :  $numerator / $denominator  ($remainBattery%)  ====== ")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun getCurrentStatus(key: String) : String
    {
        return (when (key)
        {
            ICameraStatus.BATTERY -> "$remainBattery"
            else -> ""
        })
    }

    companion object
    {
        private val TAG = CameraStatusHolder::class.java.simpleName
        private const val TIMEOUT_MS = 3000
    }
}
