package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient


class ThetaOptionSetControl(private val sessionIdProvider: IThetaSessionIdProvider, private val executeUrl : String = "http://192.168.1.1")
{
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun setOptions(options: String, useOSCv2: Boolean, callBack: IOperationCallback? = null)
    {
        Log.v(TAG, "setOptions() OSCv2:$useOSCv2 MSG : $options")
        try
        {
            val thread = Thread {
                try
                {
                    val setOptionsUrl = "${executeUrl}/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.setOptions\",\"parameters\":{\"timeout\":0, \"options\": {$options}}}" else "{\"name\":\"camera.setOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"options\": { $options }}}"
                    val result: String? = httpClient.httpPostWithHeader(setOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null) && (result.isNotEmpty()))
                    {
                        Log.v(TAG, " setOptions() : $result")
                        callBack?.operationExecuted(0, result)
                    }
                    else
                    {
                        Log.v(TAG, "setOptions() reply is null or empty.  $postData")
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
        private val TAG = ThetaOptionSetControl::class.java.simpleName
        private const val timeoutMs = 1500
    }
}
