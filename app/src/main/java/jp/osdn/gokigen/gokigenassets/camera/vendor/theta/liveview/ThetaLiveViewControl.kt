package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.liveview

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status.IThetaSessionIdProvider
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLiveViewSlicer

class ThetaLiveViewControl(private val liveViewListener: CameraLiveViewListenerImpl, private val executeUrl : String = "http://192.168.1.1") : ILiveViewController
{
    private var whileFetching = false
    private var sessionIdProvider : IThetaSessionIdProvider? = null

    fun setSessionIdProvider(sessionIdProvider: IThetaSessionIdProvider)
    {
        this.sessionIdProvider = sessionIdProvider
    }

    override fun startLiveView(isCameraScreen: Boolean)
    {
        Log.v(TAG, " startLiveView($isCameraScreen)")
        try
        {
            val thread = Thread {
                try
                {
                    if (sessionIdProvider == null)
                    {
                        start("")
                    }
                    else
                    {
                        sessionIdProvider?.sessionId?.let { start(it) }
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
        Log.v(TAG, " stopLiveView()")
        whileFetching = false
    }

    private fun start(sessionId : String)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.")
            return
        }
        whileFetching = true

        try
        {
            val thread = Thread {
                Log.d(TAG, "Starting retrieving streaming data from server.")
                val slicer = SimpleLiveViewSlicer()
                var continuousNullDataReceived = 0
                try
                {
                    val streamUrl = "$executeUrl/osc/commands/execute"
                    val paramData = if (sessionId.isEmpty()) "{\"name\":\"camera.getLivePreview\",\"parameters\":{\"timeout\":0}}" else "{\"name\":\"camera._getLivePreview\",\"parameters\":{\"sessionId\": \"$sessionId\"}}"
                    Log.v(TAG, " >>>>> START THETA PREVIEW($sessionId) : $streamUrl $paramData")

                    // Create Slicer to open the stream and parse it.
                    slicer.open(streamUrl, paramData, "application/json;charset=utf-8")
                    while (whileFetching)
                    {
                        val payload: SimpleLiveViewSlicer.Payload? = slicer.nextPayloadForMotionJpeg()
                        if (payload == null)
                        {
                            //Log.v(TAG, "Liveview Payload is null.");
                            continuousNullDataReceived++
                            if (continuousNullDataReceived > FETCH_ERROR_MAX)
                            {
                                Log.d(TAG, " FETCH ERROR MAX OVER ")
                                break
                            }
                            continue
                        }
                        val jpegData = payload.getJpegData()
                        liveViewListener.onUpdateLiveView(jpegData!!, null)
                        continuousNullDataReceived = 0
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    slicer.close()
                    if (whileFetching && continuousNullDataReceived > FETCH_ERROR_MAX)
                    {
                        // 再度ライブビューのスタートをやってみる。
                        whileFetching = false
                        start(sessionId)
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

    companion object
    {
        private val TAG = ThetaLiveViewControl::class.java.simpleName
        private const val FETCH_ERROR_MAX = 30
    }
}
