package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.liveview

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status.ICameraEventObserver
import jp.osdn.gokigen.gokigenassets.liveview.image.CameraLiveViewListenerImpl
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.net.DatagramPacket
import java.net.DatagramSocket

class PanasonicLiveViewControl(private val liveViewListener : CameraLiveViewListenerImpl, private val camera: IPanasonicCamera, private val eventObserver: ICameraEventObserver, private val informationReceiver: IInformationReceiver, private val portNumber: Int)
{
    private val liveViewPortNumber = 49151 + portNumber  // Base port number : 49152
    private var receiverSocket: DatagramSocket? = null
    private var whileStreamReceive = false
    private var errorOccur = 0

    companion object
    {
        private val TAG =  PanasonicLiveViewControl::class.java.simpleName
        private const val TIMEOUT_MAX = 3
        private const val ERROR_MAX = 30
        private const val RECEIVE_BUFFER_SIZE = 1024 * 1024 * 4
        private const val TIMEOUT_MS = 3500
        private const val LIVEVIEW_START_REQUEST = "cam.cgi?mode=startstream&value="
        private const val LIVEVIEW_STOP_REQUEST = "cam.cgi?mode=stopstream"
    }

    fun startLiveView()
    {
        Log.v(TAG, "startLiveView() port: $liveViewPortNumber")
        try
        {
            val thread = Thread(Runnable {
                try
                {
                    startReceiveStream()
                    if (!whileStreamReceive)
                    {
                        Log.v(TAG, "CANNOT OPEN : UDP RECEIVE SOCKET")
                        return@Runnable
                    }
                    val http = SimpleHttpClient()
                    val requestUrl = camera.getCmdUrl() + LIVEVIEW_START_REQUEST + liveViewPortNumber
                    val sessionId = camera.getCommunicationSessionId()
                    val reply = if (!sessionId.isNullOrEmpty())
                    {
                        val headerMap: MutableMap<String, String> = HashMap()
                        headerMap["X-SESSION_ID"] = sessionId
                        http.httpGetWithHeader(requestUrl, headerMap, null, TIMEOUT_MS) ?: ""
                    }
                    else
                    {
                        http.httpGet(requestUrl, TIMEOUT_MS)
                    }
                    if (!reply.contains("<result>ok</result>"))
                    {
                        try
                        {
                            // エラー回数のカウントアップ
                            errorOccur++

                            // 少し待つ...
                            Thread.sleep(TIMEOUT_MS.toLong())
                            if (errorOccur < ERROR_MAX)
                            {
                                Log.v(TAG, "RETRY START LIVEVIEW... : $errorOccur")
                                startLiveView()
                            }
                            else
                            {
                                Log.v(TAG, "RETRY OVER : START LIVEVIEW")
                            }
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                    }
                    else
                    {
                        Log.v(TAG, "   ----- START LIVEVIEW ----- : $requestUrl")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            })
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun stopLiveView()
    {
        Log.v(TAG, "stopLiveView()")
        try
        {
            val thread = Thread {
                try
                {
                    val http = SimpleHttpClient()
                    val sessionId = camera.getCommunicationSessionId()
                    val urlToSend = camera.getCmdUrl() + LIVEVIEW_STOP_REQUEST
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
                    if (!reply.contains("<result>ok</result>"))
                    {
                        Log.v(TAG, "stopLiveview() reply is fail... $reply")
                    }
                    else
                    {
                        Log.v(TAG, "stopLiveview() is issued.")
                    }
                    //  ライブビューウォッチャーを止める
                    whileStreamReceive = false
                    closeReceiveSocket()
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

/*
    fun changeLiveViewSize(size: String?) {}
    fun updateDigitalZoom() {}
    fun updateMagnifyingLiveViewScale(isChangeScale: Boolean) {}
    fun getMagnifyingLiveViewScale(): Float { return 1.0f }
    fun getDigitalZoomScale(): Float { return 1.0f }
*/

    private fun startReceiveStream()
    {
        if (whileStreamReceive)
        {
            Log.v(TAG, "startReceiveStream() : already starting.")
            return
        }

        // ソケットをあける (UDP)
        try
        {
            receiverSocket = DatagramSocket(liveViewPortNumber)
            whileStreamReceive = true
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            whileStreamReceive = false
            receiverSocket = null
        }

        // 受信スレッドを動かす
        val thread = Thread { receiverThread() }
        try
        {
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkReceiveImage(packet: DatagramPacket)
    {
        val dataLength: Int = packet.length
        var searchIndex = 0
        var startPosition = 0
        //val startmarker = intArrayOf(0xff, 0xd8)
        val startMarker = byteArrayOf(0xff.toByte(), 0xd8.toByte())
        val receivedData: ByteArray = packet.data
        //if (receivedData == null)
        //{
        //    // 受信データが取れなかったので終了する
        //    Log.v(TAG, "RECEIVED DATA IS NULL...")
        //    return
        //}
        // Log.v(TAG, "RECEIVED PACKET : " + dataLength);
        while (startPosition < dataLength)
        {
            // 先頭のjpegマーカーが出てくるまで読み飛ばす
            try
            {
                if (receivedData[startPosition++] == startMarker[searchIndex])
                {
                    searchIndex++
                    if (searchIndex >= startMarker.size)
                    {
                        break
                    }
                }
                else
                {
                    searchIndex = 0
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                return
            }
        }
        val offset = startPosition - startMarker.size
        if (offset > 0)
        {
            eventObserver.receivedEvent(receivedData.copyOfRange(0, offset))
        }
        liveViewListener.onUpdateLiveView(receivedData.copyOfRange(offset, dataLength), null)
    }

    private fun receiverThread()
    {
        var exceptionCount = 0
        val buffer = ByteArray(RECEIVE_BUFFER_SIZE)
        while (whileStreamReceive)
        {
            try
            {
                val receivePacket = DatagramPacket(buffer, buffer.size)
                if (receiverSocket != null)
                {
                    receiverSocket?.soTimeout = TIMEOUT_MS
                    receiverSocket?.receive(receivePacket)
                    checkReceiveImage(receivePacket)
                    exceptionCount = 0
                }
                else
                {
                    Log.v(TAG, "receiveSocket is NULL...")
                }
            }
            catch (e: Exception)
            {
                showExceptionInformation()
                exceptionCount++
                e.printStackTrace()
                if (exceptionCount > TIMEOUT_MAX)
                {
                    try
                    {
                        Log.v(TAG, "LV : RETRY REQUEST")
                        exceptionCount = 0
                        val http = SimpleHttpClient()
                        val urlToSend = camera.getCmdUrl() + LIVEVIEW_START_REQUEST + liveViewPortNumber
                        val sessionId = camera.getCommunicationSessionId()
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
                        if (!reply.contains("ok"))
                        {
                            Log.v(TAG, "LV : RETRY COMMAND FAIL...")
                        }
                        else
                        {
                            clearExceptionInformation()
                        }
                    }
                    catch (ee: Exception)
                    {
                        ee.printStackTrace()
                    }
                }
            }
        }
        closeReceiveSocket()
        Log.v(TAG, "  ----- startReceiveStream() : Finished.")
        System.gc()
    }

    private fun showExceptionInformation()
    {
        try
        {
            val msgToShow = "l${portNumber}e"
            if (!informationReceiver.getCurrentMessage().contains(msgToShow))
            {
                informationReceiver.appendMessage(" $msgToShow")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun clearExceptionInformation()
    {
        try
        {
            val msgToShow = "l${portNumber}e"
            val currentMessage = informationReceiver.getCurrentMessage()
            if (currentMessage.contains(msgToShow))
            {
                informationReceiver.updateMessage(currentMessage.replace(msgToShow, ""))
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun closeReceiveSocket()
    {
        Log.v(TAG, "closeReceiveSocket()")
        try
        {
            if (receiverSocket != null)
            {
                Log.v(TAG, "  ----- SOCKET CLOSE -----  ")
                receiverSocket?.close()
                receiverSocket = null
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
