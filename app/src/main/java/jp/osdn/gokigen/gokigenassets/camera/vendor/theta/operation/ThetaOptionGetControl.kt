package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient

class ThetaOptionGetControl(private val sessionIdProvider: IThetaSessionIdProvider, private val executeUrl : String = "http://192.168.1.1")
{
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun getOptions(options: String, useOSCv2: Boolean, callBack: IOperationCallback? = null)
    {
        Log.v(TAG, "getOptions() OSCv2:$useOSCv2 MSG : $options")
        try
        {
            val thread = Thread {
                try
                {
                    val getOptionsUrl = "${executeUrl}/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\": $options}}" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\": $options}}"
                    val result: String? = httpClient.httpPostWithHeader(getOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null) && (result.isNotEmpty()))
                    {
                        Log.v(TAG, " getOptions() : $result")
                        callBack?.operationExecuted(0, result)
                    }
                    else
                    {
                        Log.v(TAG, "getOptions() reply is null or empty.  $postData")
                        callBack?.operationExecuted(-1, "")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                    callBack?.operationExecuted(-1, e.localizedMessage)
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            callBack?.operationExecuted(-1, e.localizedMessage)
        }
    }

    companion object
    {
        private val TAG = ThetaOptionGetControl::class.java.simpleName
        private const val timeoutMs = 1500
    }
}
