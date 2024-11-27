package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.connection

import android.content.Context
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.vendor.ICameraControlCoordinator
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.SonyCameraDeviceProvider
import jp.osdn.gokigen.constants.IStringResourceConstantConvert
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import jp.osdn.gokigen.gokigenassets.utils.communication.XmlElement
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.charset.Charset

class SonySsdpClient(private val context: Context, private val callback: ISearchResultCallback, private val cameraStatusReceiver: ICameraStatusReceiver, private val cameraCoordinator: ICameraControlCoordinator, private val number : Int, private var sendRepeatCount: Int = 0)
{
    companion object
    {
        private val TAG = SonySsdpClient::class.java.simpleName
        private const val SEND_TIMES_DEFAULT = 3
        private const val SEND_WAIT_DURATION_MS = 300
        private const val SSDP_RECEIVE_TIMEOUT = 4 * 1000 // msec
        private const val PACKET_BUFFER_SIZE = 4096
        private const val SSDP_PORT = 1900
        private const val SSDP_MX = 2
        private const val SSDP_ADDR = "239.255.255.250"
        private const val SSDP_ST = "urn:schemas-sony-com:service:ScalarWebAPI:1"
        private const val SSDP_ST2 = "urn:schemas-upnp-org:service:ContentDirectory:1"
        private const val SSDP_ST3 = "urn:schemas-upnp-org:device:MediaServer:1"
    }

    private val ssdpRequest: String
    private val useSmartphoneTransfer = false

    init
    {
        this.sendRepeatCount = if (sendRepeatCount > 0) sendRepeatCount else SEND_TIMES_DEFAULT
        ssdpRequest = "M-SEARCH * HTTP/1.1\r\nHOST: $SSDP_ADDR:$SSDP_PORT\r\nMAN: \"ssdp:discover\"\r\nMX: $SSDP_MX\r\nST: $SSDP_ST\r\n\r\n"
    }

    fun search()
    {
        val sendData = ssdpRequest.toByteArray()
        val detailString = ""
        var socket: DatagramSocket? = null
        var receivePacket: DatagramPacket
        val packet: DatagramPacket

        //  要求の送信
        try
        {
            socket = DatagramSocket()
            socket.reuseAddress = true
            val iAddress = InetSocketAddress(SSDP_ADDR, SSDP_PORT)
            packet = DatagramPacket(sendData, sendData.size, iAddress)

            // 要求を繰り返し送信する
            for (loop in 1..sendRepeatCount)
            {
                cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CAMERA_SEARCH_REQUEST) + " " + loop)
                socket.send(packet)
                Thread.sleep(SEND_WAIT_DURATION_MS.toLong())
            }
            Log.v(TAG, " SSDP : SEND")
        }
        catch (e: Exception)
        {
            if (socket != null && !socket.isClosed)
            {
                socket.close()
            }
            e.printStackTrace()

            // エラー応答する
            callback.onErrorFinished(detailString + " : " + e.localizedMessage)
            return
        }

        // 応答の受信
        val startTime = System.currentTimeMillis()
        var currentTime = System.currentTimeMillis()
        val foundDevices: MutableList<String?> = ArrayList()
        val array = ByteArray(PACKET_BUFFER_SIZE)

        try
        {
            cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_WAIT_REPLY_CAMERA))
            while (currentTime - startTime < SSDP_RECEIVE_TIMEOUT)
            {
                receivePacket = DatagramPacket(array, array.size)
                socket.soTimeout = SSDP_RECEIVE_TIMEOUT
                socket.receive(receivePacket)
                val ssdpReplyMessage = String(receivePacket.getData(), 0, receivePacket.length, Charset.forName("UTF-8"))
                var ddUsn: String
                if (ssdpReplyMessage.contains("HTTP/1.1 200"))
                {
                    ddUsn = findParameterValue(ssdpReplyMessage, "USN")
                    Log.v(TAG, "- - - - - - - USN : $ddUsn")
                    cameraStatusReceiver.onStatusNotify(context.getString(
                        IStringResourceConstantConvert.ID_STRING_CONNECT_CAMERA_RECEIVED_REPLY))
                    if ((ddUsn.isNotEmpty())&&(!foundDevices.contains(ddUsn))&&(!cameraCoordinator.isAssignedCameraControl(ddUsn)))
                    {
                        val ddLocation = findParameterValue(ssdpReplyMessage, "LOCATION")
                        foundDevices.add(ddUsn)

                        //// Fetch Device Description XML and parse it.
                        if (ddLocation.isNotEmpty())
                        {
                            cameraStatusReceiver.onStatusNotify("LOCATION : $ddLocation")
                            val device: ISonyCamera? = searchSonyCameraDevice(ddLocation)
                            if ((device != null)&&(device.hasApiService("camera")))
                            {
                                cameraStatusReceiver.onStatusNotify(context.getString(
                                    IStringResourceConstantConvert.ID_STRING_CONNECT_CAMERA_FOUND) + " " + device.getFriendlyName())
                                cameraCoordinator.assignCameraControl(number, ddUsn)
                                callback.onDeviceFound(device)
                                // カメラが見つかった場合は breakする
                                Log.v(TAG, "  assignCameraControl execution Result: " + cameraCoordinator.isAssignedCameraControl(ddUsn))
                                break
                            }
                            else
                            {
                                if (useSmartphoneTransfer && device != null)
                                {
                                    cameraStatusReceiver.onStatusNotify(context.getString(
                                        IStringResourceConstantConvert.ID_STRING_CONNECT_CAMERA_FOUND) + " " + device.getFriendlyName() + " (Smartphone Transfer)")
                                    Log.v(TAG, " SMARTPHONE TRANSFER : $ssdpReplyMessage")
                                    callback.onDeviceFound(device)
                                    break
                                }

                                // カメラが見つからない...
                                cameraStatusReceiver.onStatusNotify(context.getString(
                                    IStringResourceConstantConvert.ID_STRING_CAMERA_NOT_FOUND))
                            }
                        }
                    }
                    else
                    {
                        Log.v(TAG, "Already received. : $ddUsn")
                    }
                }
                else
                {
                    Log.v(TAG, " SSDP REPLY MESSAGE (ignored) : $ssdpReplyMessage")
                }
                currentTime = System.currentTimeMillis()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()

            // エラー応答する
            callback.onErrorFinished(detailString + " : " + e.localizedMessage)
            return
        }
        finally
        {
            try
            {
                if (!socket.isClosed())
                {
                    socket.close()
                }
            }
            catch (ee: Exception)
            {
                ee.printStackTrace()
            }
        }
        callback.onFinished()
    }

    private fun findParameterValue(ssdpMessage: String, paramName: String): String
    {
        var name = paramName
        if (!name.endsWith(":"))
        {
            name = "$name:"
        }
        var start = ssdpMessage.indexOf(name)
        val end = ssdpMessage.indexOf("\r\n", start)
        if (start != -1 && end != -1)
        {
            start += name.length
            try
            {
                return ssdpMessage.substring(start, end).trim { it <= ' ' }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        return ("")
    }

    private fun searchSonyCameraDevice(ddUrl: String): ISonyCamera?
    {
        val httpClient = SimpleHttpClient()
        var device: SonyCameraDeviceProvider? = null
        val ddXml: String
        try
        {
            ddXml = httpClient.httpGet(ddUrl, -1)
            Log.d(TAG, "fetch () httpGet done. : " + ddXml.length)
            if (ddXml.length < 2)
            {
                // 内容がないときは...終了する
                Log.v(TAG, "NO BODY")
                return (null)
            }
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
            return (null)
        }
        try
        {
            //Log.v(TAG, "ddXml : " + ddXml);
            val rootElement = XmlElement.parse(ddXml)

            // "root"
            if ("root" == rootElement.tagName)
            {
                // "device"
                val deviceElement = rootElement.findChild("device")
                val friendlyName = deviceElement.findChild("friendlyName").value
                val modelName = deviceElement.findChild("modelName").value
                val udn = deviceElement.findChild("UDN").value

                // "iconList"
                var iconUrl = ""
                val iconListElement = deviceElement.findChild("iconList")
                val iconElements = iconListElement.findChildren("icon")
                for (iconElement in iconElements) {
                    // Choose png icon to show Android UI.
                    if ("image/png" == iconElement.findChild("mimetype").value) {
                        val uri = iconElement.findChild("url").value
                        val hostUrl = toSchemeAndHost(ddUrl)
                        iconUrl = hostUrl + uri
                    }
                }
                device = SonyCameraDeviceProvider(ddUrl, friendlyName, modelName, udn, iconUrl)

                // "av:X_ScalarWebAPI_DeviceInfo"
                val wApiElement = deviceElement.findChild("X_ScalarWebAPI_DeviceInfo")
                val wApiServiceListElement = wApiElement.findChild("X_ScalarWebAPI_ServiceList")
                val wApiServiceElements =
                    wApiServiceListElement.findChildren("X_ScalarWebAPI_Service")
                for (wApiServiceElement in wApiServiceElements) {
                    val serviceName =
                        wApiServiceElement.findChild("X_ScalarWebAPI_ServiceType").value
                    val actionUrl =
                        wApiServiceElement.findChild("X_ScalarWebAPI_ActionList_URL").value
                    device.addApiService(serviceName, actionUrl)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        Log.d(TAG, "fetch () parsing XML done.")
        if (device == null) {
            Log.v(TAG, "device is null.")
        }
        return device
    }

    private fun toSchemeAndHost(url: String): String {
        val i = url.indexOf("://") // http:// or https://
        if (i == -1) {
            return ""
        }
        val j = url.indexOf("/", i + 3)
        return if (j == -1) {
            ""
        } else url.substring(0, j)
    }

    private fun toHost(url: String): String
    {
        val i = url.indexOf("://") // http:// or https://
        if (i == -1) {
            return ""
        }
        val j = url.indexOf(":", i + 3)
        return if (j == -1) {
            ""
        } else url.substring(i + 3, j)
    }

    /**
     * 検索結果のコールバック
     *
     */
    interface ISearchResultCallback
    {
        fun onDeviceFound(cameraDevice: ISonyCamera) // デバイスが見つかった！
        fun onFinished() // 通常の終了をしたとき
        fun onErrorFinished(reason: String?) // エラーが発生して応答したとき
    }
}
