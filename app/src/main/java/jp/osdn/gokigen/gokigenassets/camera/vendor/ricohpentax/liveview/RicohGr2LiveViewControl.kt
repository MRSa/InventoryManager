package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.liveview

import android.content.Context
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.connection.IUseGR2CommandNotify
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLiveViewSlicer


/**
 *
 *
 */
class RicohGr2LiveViewControl(private val liveViewListener: CameraLiveViewListenerImpl, context: Context, executeUrl : String = "http://192.168.0.1") : ILiveViewController, IUseGR2CommandNotify
{
    private val cameraDisplayUrl = "$executeUrl/v1/display" //  カメラの画面をコピーする場合...
    private val liveViewUrl = "$executeUrl/v1/liveview" //  何も表示しない（ライブビューモード）の場合...
    //private var cropScale = 1.0f
    private var whileFetching = false
    private var useGR2command = false
    private var useCameraScreen = false

    companion object
    {
        private val TAG = RicohGr2LiveViewControl::class.java.simpleName
        private const val FETCH_ERROR_MAX = 30
    }

    override fun setUseGR2Command(useGR2Command: Boolean, useCameraScreen: Boolean)
    {
        this.useGR2command = useGR2Command
        this.useCameraScreen = useCameraScreen
    }

    override fun startLiveView(isCameraScreen: Boolean)
    {
        val isCameraScreen0 = useGR2command && useCameraScreen
        Log.v(TAG, "startLiveView() : $isCameraScreen0 ($useGR2command) $isCameraScreen")
        try
        {
            val thread = Thread {
                try
                {
                    if (isCameraScreen0)
                    {
                        start(cameraDisplayUrl)
                    }
                    else
                    {
                        start(liveViewUrl)
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

    override fun stopLiveView()
    {
        Log.v(TAG, "stopLiveView()")
        whileFetching = false
    }

    private fun start(streamUrl: String)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.")
        }
        whileFetching = true

        try
        {
            val thread = Thread {
                Log.d(TAG, "Starting retrieving streaming data from server.")
                var slicer: SimpleLiveViewSlicer? = null
                var continuousNullDataReceived = 0
                try
                {
                    slicer = SimpleLiveViewSlicer()
                    slicer.open(streamUrl)
                    while (whileFetching)
                    {
                        val payload: SimpleLiveViewSlicer.Payload? = slicer.nextPayloadForMotionJpeg()
                        if (payload == null)
                        {
                            Log.v(TAG, "Liveview Payload is null.")
                            continuousNullDataReceived++
                            if (continuousNullDataReceived > FETCH_ERROR_MAX)
                            {
                                Log.d(TAG, " FETCH ERROR MAX OVER ")
                                break
                            }
                            continue
                        }
                        val jpegData = payload.getJpegData()
                        if (jpegData != null)
                        {
                            liveViewListener.onUpdateLiveView(jpegData, null)
                        }
                        else
                        {
                            Log.v(TAG, " jpegData is NULL...")
                        }
                        continuousNullDataReceived = 0
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    try
                    {
                        slicer?.close()
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                    if (!whileFetching && continuousNullDataReceived > FETCH_ERROR_MAX)
                    {
                        // 再度ライブビューのスタートをやってみる。
                        whileFetching = false
                        start(streamUrl)
                    }
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

/*
    fun updateDigitalZoom() {}
*/
    /**
     * デジタルズーム倍率の設定値を応答する
     *
     */
/*
    val digitalZoomScale: Float
        get() = 1.0f
*/

    /**
     * クロップサイズを変更する
     *
     */
/*
    fun updateMagnifyingLiveViewScale(isChangeScale: Boolean) {
        //
        try {
            val httpClient = SimpleHttpClient()
            if (isChangeScale) {
                cropScale = if (cropScale == 1.0f) {
                    1.25f
                } else if (cropScale == 1.25f) {
                    1.68f
                } else {
                    1.0f
                }
            }
            val thread = Thread {
                try {
                    var cropSize = "CROP_SIZE_ORIGINAL"
                    val timeoutMs = 5000
                    val grCmdUrl = "http://192.168.0.1/_gr"
                    var postData: String
                    var result: String?
                    if (isChangeScale) {
                        postData = "mpget=CROP_SHOOTING"
                        result = httpClient.httpPost(grCmdUrl, postData, timeoutMs)
                        if (result == null || result.isEmpty()) {
                            Log.v(TAG, "reply is null.")
                            cropScale = 1.0f
                        } else if (result.contains("SIZE_M")) {
                            cropSize = "CROP_SIZE_S"
                            cropScale = 1.68f
                        } else if (result.contains("SIZE_S")) {
                            cropSize = "CROP_SIZE_ORIGINAL"
                            cropScale = 1.0f
                        } else {
                            cropSize = "CROP_SIZE_M"
                            cropScale = 1.25f
                        }
                    }
                    postData = "mpset=CROP_SHOOTING $cropSize"
                    result = httpClient.httpPost(grCmdUrl, postData, timeoutMs)
                    Log.v(TAG, "RESULT1 : $result")
                    postData = "cmd=mode refresh"
                    result = httpClient.httpPost(grCmdUrl, postData, timeoutMs)
                    Log.v(TAG, "RESULT2 : $result")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            thread.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
*/

    /**
     * ライブビュー拡大倍率の設定値を応答する
     *
     */
/*
    val magnifyingLiveViewScale: Float
        get() = cropScale

    fun getLiveViewListener(): ILiveViewListener {
        return liveViewListener
    }
*/

}
