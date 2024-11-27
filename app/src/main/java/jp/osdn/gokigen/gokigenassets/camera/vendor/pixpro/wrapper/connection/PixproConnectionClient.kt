package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.connection

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.IPixproInternalInterfaces
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCamera
import java.net.InetAddress

class PixproConnectionClient(private val context: Context, private val interfaceProvider: IPixproInternalInterfaces, private val callback: ISearchResultCallback, private val cameraStatusReceiver: ICameraStatusReceiver, private var sendRepeatCount: Int = 0)
{
    companion object
    {
        private val TAG = PixproConnectionClient::class.java.simpleName
        private const val SEND_TIMES_DEFAULT = 3
        private const val SEND_WAIT_DURATION_MS = 300
        private const val REPLY_RECEIVE_TIMEOUT = 4 * 1000 // msec
        private const val PACKET_BUFFER_SIZE = 4096
        private const val QUERY_STRING = "AOFQUERY:GOKIGEN a01Series,1"
        private const val TARGET_UDP_PORT = 5175
        private const val RECEIVE_UDP_PORT = 5176
    }

    init
    {
        this.sendRepeatCount = if (sendRepeatCount > 0) sendRepeatCount else SEND_TIMES_DEFAULT
    }

    private fun getBroadcastAddress(): InetAddress
    {
/*
        try
        {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
            val dhcp = wifi?.dhcpInfo
            if (dhcp != null)
            {
                //val dhcpServer = dhcp.serverAddress
                val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
                val quads = ByteArray(4)
                for (k in 0..3) quads[k] = (broadcast shr k * 8).toByte()
                Log.v(TAG, " DHCP : $dhcp")
                return (InetAddress.getByAddress(quads))
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
*/
        return (InetAddress.getByName("255.255.255.255"))
    }

    fun search()
    {
        val sendReceiveSocket = DatagramSocket(RECEIVE_UDP_PORT)
        try
        {
            //  要求の送信
            sendReceiveSocket.reuseAddress = true
            val broadcastAddress = getBroadcastAddress()
            val iAddress = InetSocketAddress(broadcastAddress, TARGET_UDP_PORT)
            val sendData = QUERY_STRING.toByteArray()
            val packet = DatagramPacket(sendData, sendData.size, iAddress)

            // 要求を繰り返し送信する
            for (loop in 1..sendRepeatCount)
            {
                cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CAMERA_SEARCH_REQUEST) + " " + loop)
                sendReceiveSocket.send(packet)
                Thread.sleep(SEND_WAIT_DURATION_MS.toLong())
            }
            Log.v(TAG, " UDP QUERY : SEND $QUERY_STRING (${broadcastAddress.hostAddress})")
        }
        catch (e: Exception)
        {
            sendReceiveSocket.close()

            // エラー応答する
            callback.onErrorFinished(" : " + e.localizedMessage)
            return
        }

        try
        {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
            val mLock = wifi?.createMulticastLock("lock")

            val cameraInitializer = interfaceProvider.getIPixproCameraInitializer()
            val pixproCamera = interfaceProvider.getIPixproCamera()

            // 応答の受信
            val startTime = System.currentTimeMillis()
            var currentTime = System.currentTimeMillis()
            val array = ByteArray(PACKET_BUFFER_SIZE)
            cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_WAIT_REPLY_CAMERA))
            while (currentTime - startTime < REPLY_RECEIVE_TIMEOUT)
            {
                val receivePacket = DatagramPacket(array, array.size)
                sendReceiveSocket.soTimeout = REPLY_RECEIVE_TIMEOUT

                try
                {
                    mLock?.acquire()
                    sendReceiveSocket.receive(receivePacket)
                    mLock?.release()
                }
                catch (ee: java.net.SocketTimeoutException)
                {
                    sendReceiveSocket.close()

                    // 受信タイムアウト:　エラー応答する
                    callback.onErrorFinished(" : " + ee.localizedMessage)
                    return
                }
                catch (e: Exception)
                {
                    // そうなじゃなければ例外出す
                    e.printStackTrace()
                }

                cameraInitializer.parseCommunicationParameter(receivePacket.data)
                if (pixproCamera.isAvailable())
                {
                    callback.onDeviceFound(pixproCamera)
                    break
                }
                else
                {
                    // カメラが見つからない...
                    cameraStatusReceiver.onStatusNotify(context.getString(
                        IStringResourceConstantConvert.ID_STRING_CAMERA_NOT_FOUND))
                }
                currentTime = System.currentTimeMillis()
            }
        }
        catch (e: Exception)
        {
            sendReceiveSocket.close()
            e.printStackTrace()

            // エラー応答する
            callback.onErrorFinished(" : " + e.localizedMessage)
            return
        }
        try
        {
            sendReceiveSocket.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        callback.onFinished()
    }

    interface ISearchResultCallback
    {
        fun onDeviceFound(cameraDevice: IPixproCamera) // デバイスが見つかった！
        fun onFinished() // 通常の終了をしたとき
        fun onErrorFinished(reason: String?) // エラーが発生して応答したとき
    }
}
