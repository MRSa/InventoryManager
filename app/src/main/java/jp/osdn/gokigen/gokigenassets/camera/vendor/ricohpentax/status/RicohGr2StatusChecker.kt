package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.status

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusUpdateNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import kotlin.collections.ArrayList


/**
 *
 *
 */
class RicohGr2StatusChecker
/**
 *
 *
 */ internal constructor(private val sleepMs: Int, executeUrl : String = "http://192.168.0.1") : ICameraStatusWatcher, ICameraStatus
{
    private val statusCheckUrl = "$executeUrl/v1/props"
    private val statusSetUrl = "$executeUrl/v1/params/camera"
    private val grCommandUrl = "$executeUrl/_gr"
    private val timeoutMs = 5000
    private var whileFetching = false
    private var statusHolder: RicohGr2StatusHolder? = null
    private var useGR2command = false
    private val httpClient = SimpleHttpClient()

    companion object
    {
        private val TAG = RicohGr2StatusChecker::class.java.simpleName
    }

    fun setUseGR2Command(useGR2command: Boolean)
    {
        this.useGR2command = useGR2command
    }

    override fun startStatusWatch(indicator : IMessageDrawer?, notifier: ICameraStatusUpdateNotify?)
    {
        Log.v(TAG, "startStatusWatch()")
        try {
            statusHolder = RicohGr2StatusHolder(notifier)
            val thread = Thread {
                try {
                    start(statusCheckUrl)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *
     *
     */
    override fun stopStatusWatch() {
        Log.v(TAG, "stoptStatusWatch()")
        whileFetching = false
    }

    /**
     *
     *
     */
    private fun start(watchUrl: String) {
        if (whileFetching) {
            Log.v(TAG, "start() already starting.")
            return
        }
        try {
            whileFetching = true
            val thread = Thread {
                Log.d(TAG, "Start status watch.")
                while (whileFetching)
                {
                    try
                    {
                        statusHolder?.updateStatus(httpClient.httpGet(watchUrl, timeoutMs))
                        Thread.sleep(sleepMs.toLong())
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
                Log.v(TAG, "STATUS WATCH STOPPED.")
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getStatusList(key: String): List<String> {
        try {
            if (statusHolder == null) {
                return ArrayList()
            }
            val listKey = key + "List"
            return statusHolder?.getAvailableItemList(listKey) ?: ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ArrayList()
    }

    override fun getStatus(key: String): String
    {
        try
        {
            return if (statusHolder == null) { "" } else { statusHolder!!.getItemStatus(key) }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ""
    }

    override fun getStatusColor(key: String): Int
    {
        try
        {
            return (statusHolder?.getItemStatusColor(key) ?: Color.WHITE)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (Color.WHITE)
    }


    override fun setStatus(key: String, value: String) {
        val thread = Thread {
            try {
                var response: String?
                var postData = "$key=$value"
                if (useGR2command && key == "exposureMode") {
                    //  撮影モードを変更するときは、GR専用コマンドを送ることにする。
                    postData = "cmd=" + decideButtonCode(value)
                    response = httpClient.httpPost(grCommandUrl, postData, timeoutMs)
                    Log.v(
                        TAG,
                        "CHANGE MODE : " + postData + " resp. (" + response?.length + "bytes.)"
                    )
                } else {
                    // 通常の変更コマンド
                    response = httpClient.httpPut(statusSetUrl, postData, timeoutMs)
                    Log.v(
                        TAG,
                        "SET PROPERTY : " + postData + " resp. (" + response?.length + "bytes.)"
                    )
                }
                if (useGR2command) {
                    //  GR専用コマンドで、画面表示をリフレッシュ
                    response =
                        httpClient.httpPost(grCommandUrl, "cmd=mode refresh", timeoutMs)
                    Log.v(TAG, "refresh resp. (" + response?.length + "bytes.)")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        try {
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 撮影モードをGRのダイアルコマンドに変更する
     *
     */
    private fun decideButtonCode(exposureMode: String?): String {
        var buttonCode = "bdial AUTO"
        if (exposureMode == null) {
            return buttonCode
        }
        when (exposureMode) {
            "movie" -> buttonCode = "bdial MOVIE"
            "M" -> buttonCode = "bdial M"
            "TAV" -> buttonCode = "bdial TAV"
            "AV" -> buttonCode = "bdial AV"
            "TV" -> buttonCode = "bdial TV"
            "P" -> buttonCode = "bdial P"
            "auto" -> buttonCode = "bdial AUTO"
        }
        return buttonCode
    }
}
