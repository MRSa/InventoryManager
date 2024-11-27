package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.connection

import android.graphics.Color
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnection
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.IPixproInternalInterfaces
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_01
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_02
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_03
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_04
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_05
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_06
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_07
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_08
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_09
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_10
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_CONNECT_11
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_FLASH_AUTO
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_FLASH_OFF
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_FLASH_ON
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.connection.*
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproFlashAuto
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproFlashOff
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.PixproFlashOn
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status.PixproStatusChecker
import jp.osdn.gokigen.constants.IStringResourceConstantConvert

class PixproCameraConnectSequence(private val context: AppCompatActivity, private val cameraStatusReceiver: ICameraStatusReceiver, private val cameraConnection : ICameraConnection, private val interfaceProvider : IPixproInternalInterfaces, private val statusChecker: PixproStatusChecker) : Runnable, IPixproCommandCallback, IPixproMessages, PixproConnectionClient.ISearchResultCallback
{
    private var client = PixproConnectionClient(context, interfaceProvider, this, cameraStatusReceiver, 1)

    private val commandIssuer: IPixproCommandPublisher
    private var flashMode: String = "OFF"

    companion object
    {
        private val TAG = PixproCameraConnectSequence::class.java.simpleName
    }

    init
    {
        Log.v(TAG, " PixproCameraConnectSequence")
        //this.cameraConnection = cameraConnection
        commandIssuer = interfaceProvider.getIPixproCommandPublisher()
    }

    override fun run()
    {
        Log.v(TAG, "search()")
        try
        {
            cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_START))
            client.search()
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    private fun startConnect()
    {
        try
        {
            // カメラとTCP接続
            //val issuer: IPixproCommandPublisher = interfaceProvider.getCommandPublisher()
            if (!commandIssuer.isConnected())
            {
                if (!interfaceProvider.getIPixproCommunication().connect())
                {
                    // 接続失敗...
                    interfaceProvider.getInformationReceiver().updateMessage(
                        context.getString(IStringResourceConstantConvert.ID_STRING_DIALOG_TITLE_CONNECT_FAILED),
                        isBold = false,
                        isColor = true,
                        color = Color.RED
                    )
                    onConnectError(context.getString(IStringResourceConstantConvert.ID_STRING_DIALOG_TITLE_CONNECT_FAILED))
                    return
                }
            }
            else
            {
                Log.v(TAG, "SOCKET IS ALREADY CONNECTED...")
            }
            // コマンドタスクの実行開始
            commandIssuer.start()

            // 接続シーケンスの開始
            startConnectSequence()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            interfaceProvider.getInformationReceiver().updateMessage(
                context.getString(IStringResourceConstantConvert.ID_STRING_DIALOG_TITLE_CONNECT_FAILED),
                isBold = false,
                isColor = true,
                color = Color.RED
            )
            onConnectError(e.message?: "")
        }
    }

    private fun onConnectError(reason: String)
    {
        cameraConnection.alertConnectingFailed(reason)
    }

    override fun receivedMessage(id: Int, rx_body: ByteArray?)
    {
        when (id) {
            SEQ_CONNECT_01 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "1",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence02(this))
            }
            SEQ_CONNECT_02 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "2",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence03(this))
            }
            SEQ_CONNECT_03 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "3",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence04(this))
            }
            SEQ_CONNECT_04 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "4",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                // ここで、パスワードの Base64情報を切り出す(FC 03 の応答、 0x0058 ～ 64バイトの文字列を切り出して、Base64エンコードする)
                commandIssuer.enqueueCommand(PixproConnectSequence05(this))
            }
            SEQ_CONNECT_05 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "5",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                // ここで、パスワードの情報を切り出す (FE 03 の応答、 0x0078 ～ 文字列を切り出す。)
                commandIssuer.enqueueCommand(PixproConnectSequence06(this))
            }
            SEQ_CONNECT_06 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "6",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence07(this))
            }
            SEQ_CONNECT_07 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "7",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence08(this))
            }
            SEQ_CONNECT_08 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "8",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence09(this))
            }
            SEQ_CONNECT_09 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "9",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence10(this))
            }
            SEQ_CONNECT_10 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "10",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                commandIssuer.enqueueCommand(PixproConnectSequence11(this))
            }
            SEQ_CONNECT_11 -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTING) + "11",
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                when {
                    flashMode.contains("AUTO") -> {
                        commandIssuer.enqueueCommand(PixproFlashAuto(this))
                    }
                    flashMode.contains("ON") -> {
                        commandIssuer.enqueueCommand(PixproFlashOn(this))
                    }
                    else -> {
                        commandIssuer.enqueueCommand(PixproFlashOff(this))
                    }
                }
            }
            SEQ_FLASH_AUTO, SEQ_FLASH_OFF, SEQ_FLASH_ON -> {
                interfaceProvider.getInformationReceiver().updateMessage(
                    context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECT_FINISHED),
                    isBold = false,
                    isColor = false,
                    color = 0
                )
                connectFinished()
                Log.v(TAG, "  CONNECT TO CAMERA : DONE.")
            }
            else -> {
                Log.v(TAG, " RECEIVED UNKNOWN ID : $id")
                onConnectError(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_UNKNOWN_MESSAGE))
            }
        }
    }

    private fun startConnectSequence()
    {
        interfaceProvider.getInformationReceiver().updateMessage(context.getString(
            IStringResourceConstantConvert.ID_STRING_CONNECT_START),
            isBold = false,
            isColor = false,
            color = 0
        )
        cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_START))
        commandIssuer.enqueueCommand(PixproConnectSequence01(this))
    }

    private fun connectFinished()
    {
        try
        {
            // 接続成功のメッセージを出す
            interfaceProvider.getInformationReceiver()
                .updateMessage(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTED),
                    isBold = false,
                    isColor = false,
                    color = 0
                )

            // ちょっと待つ
            Thread.sleep(1000)

            //interfaceProvider.getAsyncEventCommunication().connect();
            //interfaceProvider.getCameraStatusWatcher().startStatusWatch(interfaceProvider.getStatusListener());  ステータスの定期確認は実施しない

            // 接続成功！のメッセージを出す
            interfaceProvider.getInformationReceiver()
                .updateMessage(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTED),
                    isBold = false,
                    isColor = false,
                    color = 0
                )
            onConnectNotify()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun onConnectNotify()
    {
        try
        {
            val thread = Thread { // カメラとの接続確立を通知する
                cameraStatusReceiver.onStatusNotify(context.getString(IStringResourceConstantConvert.ID_STRING_CONNECT_CONNECTED))
                cameraStatusReceiver.onCameraConnected()
                interfaceProvider.getIPixproCommunicationNotify().readyToCommunicate()
                Log.v(TAG, " onConnectNotify()")
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onDeviceFound(cameraDevice: IPixproCamera)
    {
        Log.v(TAG, " onDeviceFound()")
        startConnect()
    }

    override fun onFinished()
    {
        Log.v(TAG, " ------ onFinished()")
    }

    override fun onErrorFinished(reason: String?)
    {
        Log.v(TAG, " onErrorFinished() : $reason")
        cameraConnection.alertConnectingFailed(reason)
    }
}
