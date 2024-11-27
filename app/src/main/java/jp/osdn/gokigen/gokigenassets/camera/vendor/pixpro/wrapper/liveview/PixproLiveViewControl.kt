package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.liveview

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ILiveViewController
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCamera
import java.lang.Exception
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageDataReceiver
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLiveViewSlicer

class PixproLiveViewControl(private val imageDataReceiver: IImageDataReceiver, pixproCamera: IPixproCamera) : ILiveViewController
{
    private val liveViewUrl: String = "http://${pixproCamera.getIpAddress()}:${pixproCamera.getLiveViewPortNumber()}/" // "http://172.16.0.254:9176";
    private var whileFetching = false

    companion object
    {
        private val TAG = PixproLiveViewControl::class.java.simpleName
        private const val FETCH_ERROR_MAX = 30
    }

    override fun startLiveView(isCameraScreen: Boolean)
    {
        Log.v(TAG, "startLiveView() : $isCameraScreen")
        try
        {
            val thread = Thread {
                try
                {
                    start(liveViewUrl)
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
    }

    private fun start(streamUrl: String)
    {
        if (whileFetching)
        {
            Log.v(TAG, "start() already starting.")
            return  // すでにスタートしているので、LVの再スタートはしない。
        }
        whileFetching = true

        // A thread for retrieving liveview data from server.
        try
        {
            val thread = Thread {
                Log.d(TAG, "Starting retrieving streaming data from server.")
                // var slicer: SimpleLiveviewSlicer? = null
                var continuousNullDataReceived = 0
                val startMarker = intArrayOf(0x0a, 0x0a, 0xff, 0xd8)
                try {
                    // Create Slicer to open the stream and parse it.
                    val slicer = SimpleLiveViewSlicer()
                    slicer.setMJpegStartMarker(startMarker)
                    slicer.open(streamUrl)
                    while (whileFetching)
                    {
                        val payload: SimpleLiveViewSlicer.Payload? = slicer.nextPayloadForMotionJpeg()
                        if (payload == null)
                        {
                            //Log.v(TAG, "Liveview Payload is null.");
                            continuousNullDataReceived++
                            if (continuousNullDataReceived > FETCH_ERROR_MAX) {
                                Log.d(TAG, " FETCH ERROR MAX OVER ")
                                break
                            }
                            continue
                        }
                        //if (mJpegQueue.size() == 2)
                        //{
                        //    mJpegQueue.remove();
                        //}
                        //mJpegQueue.add(payload.getJpegData());
                        imageDataReceiver.onUpdateLiveView(payload.getJpegData() ?: ByteArray(0), null)
                        continuousNullDataReceived = 0
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                finally
                {
                    //mJpegQueue.clear();
                    if (!whileFetching && continuousNullDataReceived > FETCH_ERROR_MAX)
                    {
                        // 再度ライブビューのスタートをやってみる。
                        //whileFetching = false;
                        //continuousNullDataReceived = 0;
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

}