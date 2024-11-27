package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaStatusHolder
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import org.json.JSONObject

class ThetaSingleShotControl(private val sessionIdProvider: IThetaSessionIdProvider, private val showInformation: IVibrator, private val liveViewControl : ILiveViewController, private val statusHolder : IThetaStatusHolder, private val executeUrl : String = "http://192.168.1.1")
{
    private val httpClient = SimpleHttpClient()

    /**
     *
     *
     */
    fun singleShot(useOSCv2 : Boolean)
    {
        Log.v(TAG, "singleShot()")
        try
        {
            val thread = Thread {
                try
                {
                    // 撮影開始を知らせる
                    showInformation.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT_SHORT)
                    val shootUrl = "$executeUrl/osc/commands/execute"
                    val postData = if (useOSCv2) "{\"name\":\"camera.takePicture\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera.takePicture\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\"}}"
                    val result: String? = httpClient.httpPostWithHeader(shootUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        Log.v(TAG, " singleShot() : $result")

                        // 画像処理が終わるまで待つ
                        waitChangeStatus()

                        // 撮影成功をバイブレータで知らせる
                        showInformation.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)

                        //  ライブビュー画像を再起動...
                        liveViewControl.stopLiveView()
                        waitMs(300)
                        liveViewControl.startLiveView()

                        // 画面の再更新を指示する
                        statusHolder.invalidate()
                    }
                    else
                    {
                        Log.v(TAG, "singleShot() reply is null.")
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

    /**
     * 撮影状態が変わるまで待つ。
     * (ただし、タイムアウト時間を超えたらライブビューを再開させる)
     */
    private fun waitChangeStatus()
    {
        val getStateUrl = "$executeUrl/osc/state"
        val maxWaitTimeoutMs = 12000 // 最大待ち時間 (単位: ms)
        var fingerprint = ""
        try
        {
            val result: String? = httpClient.httpPost(getStateUrl, "", timeoutMs)
            if ((result != null)&&(result.isNotEmpty()))
            {
                val jsonObject = JSONObject(result)
                fingerprint = jsonObject.getString("fingerprint")

                //  現在の状態(ログを出す)
                Log.v(TAG, " $getStateUrl $result ($fingerprint)")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }

        try
        {
            val firstTime = System.currentTimeMillis()
            var currentTime = firstTime
            while (currentTime - firstTime < maxWaitTimeoutMs)
            {
                //  ... 状態を見て次に進める
                try
                {
                    val result: String? = httpClient.httpPost(getStateUrl, "", timeoutMs)
                    if ((result != null)&&(result.isNotEmpty()))
                    {
                        val jsonObject = JSONObject(result)
                        val currentFingerprint = jsonObject.getString("fingerprint")

                        //  ログを出してみる
                        // Log.v(TAG, " " + getStateUrl + " ( " + result + " ) " + "(" + fingerprint + " " + current_fingerprint + ")");
                        if (fingerprint != currentFingerprint)
                        {
                            // fingerprintが更新された！
                            break
                        }
                        Log.v(TAG, "  -----  NOW PROCESSING  ----- : $fingerprint")
                    }
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
                waitMs(1500)
                currentTime = System.currentTimeMillis()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     *
     *
     */
    private fun waitMs(waitMs: Int)
    {
        try
        {
            Thread.sleep(waitMs.toLong())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = ThetaSingleShotControl::class.java.simpleName
        private const val timeoutMs = 6000
    }
}
