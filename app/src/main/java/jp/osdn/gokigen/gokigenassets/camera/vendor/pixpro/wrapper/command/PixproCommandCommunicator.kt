package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.IPixproCamera
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproCommand
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages.Companion.SEQ_RECEIVE_ONLY
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandOnlyCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandReceiveOnly
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleLogDumper
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.net.SocketException
import java.util.*

class PixproCommandCommunicator(private val pixproCamera: IPixproCamera, private val notifier: IPixproCommunicationNotify, private val statusChecker: IPixproCommandCallback) : IPixproCommandPublisher, IPixproCommunication
{
    private var isStart = false
    private var isConnected = false
    private var waitForever: Boolean = false
    private val commandQueue : Queue<IPixproCommand> = ArrayDeque()

    private var socket: Socket? = null
    private var dataOutputStream: DataOutputStream? = null
    private var bufferedReader: BufferedReader? = null

    init
    {
        commandQueue.clear()
    }

    companion object
    {
        private val TAG = PixproCommandCommunicator::class.java.simpleName
        private const val BUFFER_SIZE = 1024 * 1024 + 16 // 受信バッファは 1MB
        private const val COMMAND_SEND_RECEIVE_DURATION_MS = 30
        private const val COMMAND_SEND_RECEIVE_DURATION_MAX = 3000
        private const val COMMAND_POLL_QUEUE_MS = 15
    }

    override fun connect(): Boolean
    {
        if (!pixproCamera.isAvailable())
        {
            Log.v(TAG, " pixpro camera is not ready...")
            return (false)
        }
        try
        {
            val tcpNoDelay = pixproCamera.getTcpNoDelay()
            Log.v(TAG, " connect() $tcpNoDelay")
            socket = Socket()
            socket?.reuseAddress = true
            socket?.keepAlive = true
            socket?.tcpNoDelay = tcpNoDelay
            if (tcpNoDelay)
            {
                socket?.keepAlive = false
                socket?.setPerformancePreferences(0, 2, 0)
                socket?.oobInline = true
                socket?.reuseAddress = false
                socket?.trafficClass = 0x80
            }
            socket?.connect(InetSocketAddress(pixproCamera.getIpAddress(), pixproCamera.getPortNumber()), 0)
            isConnected = true
            return (true)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
            socket = null
        }
        return (false)
    }

    private fun closeOutputStream()
    {
        try
        {
            dataOutputStream?.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        dataOutputStream = null
    }

    private fun closeBufferedReader()
    {
        try
        {
            bufferedReader?.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        bufferedReader = null
    }

    private fun closeSocket()
    {
        try
        {
            socket?.close()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        socket = null
    }

    override fun disconnect()
    {
        try
        {
            // 通信関連のクローズ
            closeOutputStream()
            closeBufferedReader()
            closeSocket()
            isStart = false
            isConnected = false
            commandQueue.clear()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        System.gc()
    }

    override fun start()
    {
        if (isStart)
        {
            // すでにコマンドのスレッド動作中なので抜ける
            return
        }
        isStart = true
        Log.v(TAG, " start()")
        val thread = Thread {
            try
            {
                val inputStream = socket?.getInputStream()
                dataOutputStream = DataOutputStream(socket?.getOutputStream())
                while (isStart)
                {
                    try
                    {
                        val command = commandQueue.poll()
                        if (command != null)
                        {
                            issueCommand(command)

                            Thread.sleep(COMMAND_POLL_QUEUE_MS.toLong())
                            Log.v(TAG, " --- RECEIVE FOR REPLY --- ")
                            receiveFromCamera(command)
                        }
                        Thread.sleep(COMMAND_POLL_QUEUE_MS.toLong())

                        if (inputStream != null && inputStream.available() > 0)
                        {
                            Log.v(TAG, " --- RECEIVE FOR (PRIMARY) MSG --- ")
                            receiveFromCamera(PixproCommandReceiveOnly(SEQ_RECEIVE_ONLY, PixproCommandOnlyCallback()))
                        }
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }
            }
            catch (e: Exception)
            {
                Log.v(TAG, "<<<<< IP : ${pixproCamera.getIpAddress()} port : ${pixproCamera.getPortNumber()} >>>>>")
                e.printStackTrace()
            }
        }
        try
        {
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun stop()
    {
        isStart = false
        commandQueue.clear()
    }

    override fun isConnected(): Boolean
    {
        return (isConnected)
    }

    override fun enqueueCommand(command: IPixproCommand): Boolean
    {
        try
        {
            //Log.v(TAG, "Enqueue : "  + command.getId());
            return commandQueue.offer(command)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return false
    }

    private fun issueCommand(command: IPixproCommand)
    {
        try
        {
/*
            var retryOver = true
            while (retryOver)
            {
*/
                //Log.v(TAG, "issueCommand : " + command.getId());
                val commandBody: ByteArray = command.commandBody()
                // コマンドボディが入っていた場合には、コマンド送信（入っていない場合は受信待ち）
                sendToCamera(command.dumpLog(), commandBody)

                val commandBody2: ByteArray? = command.commandBody2()
                if (commandBody2 != null)
                {
                    // コマンドボディの２つめが入っていた場合には、コマンドを連続送信する
                    sendToCamera(command.dumpLog(), commandBody2)
                }
/*
                retryOver = receiveFromCamera(command)
                if (retryOver)
                {
                    retryOver = command.sendRetry()
                }
            }
*/
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラにコマンドを送信する（メイン部分）
     *
     */
    private fun sendToCamera(isDumpReceiveLog: Boolean, byte_array: ByteArray)
    {
        try
        {
            if (dataOutputStream == null)
            {
                Log.v(TAG, " DataOutputStream is null.")
                return
            }
            if (byte_array.isEmpty())
            {
                // メッセージボディがない。終了する
                Log.v(TAG, " SEND BODY IS NOTHING.")
                return
            }
            if (isDumpReceiveLog)
            {
                // ログに送信メッセージを出力する
                SimpleLogDumper.dumpBytes("SEND[" + byte_array.size + "] ", byte_array)
            }

            // (データを)送信
            dataOutputStream?.write(byte_array)
            dataOutputStream?.flush()
        }
        catch (socketException: SocketException)
        {
            socketException.printStackTrace()
            try
            {
                // 回線切断を通知する
                notifier.detectDisconnect()
            }
            catch (ee: Exception)
            {
                ee.printStackTrace()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun sleep(delayMs: Int)
    {
        try
        {
            Thread.sleep(delayMs.toLong())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /**
     * カメラからにコマンドの結果を受信する（メイン部分）
     *
     */
    private fun receiveFromCamera(command: IPixproCommand): Boolean
    {
        var delayMs: Int = command.receiveDelayMs()
        if (delayMs < 0 || delayMs > COMMAND_SEND_RECEIVE_DURATION_MAX)
        {
            delayMs = COMMAND_SEND_RECEIVE_DURATION_MS
        }
        return receiveSingle(command, delayMs)
    }

    private fun receiveSingle(command: IPixproCommand, delayMs: Int): Boolean
    {
        val isDumpReceiveLog: Boolean = command.dumpLog()
        val id: Int = command.getId()
        val callback: IPixproCommandCallback? = command.responseCallback()
        try
        {
            val receiveMessageBufferSize = BUFFER_SIZE
            val byteArray = ByteArray(receiveMessageBufferSize)
            val inputStream: InputStream? = socket?.getInputStream()
            if (inputStream == null)
            {
                Log.v(TAG, " InputStream is NULL... RECEIVE ABORTED.")
                receivedAllMessage(isDumpReceiveLog, id, null, callback)
                return (false)
            }

            // 初回データが受信バッファにデータが溜まるまで待つ...
            var readBytes = waitForReceive(inputStream, delayMs, command.maxRetryCount())
            if (readBytes < 0)
            {
                // リトライオーバー検出
                Log.v(TAG, "  ----- DETECT RECEIVE RETRY OVER... -----")
                return (true)
            }

            // 受信したデータをバッファに突っ込む
            val byteStream = ByteArrayOutputStream()
            while (readBytes > 0)
            {
                readBytes = inputStream.read(byteArray, 0, receiveMessageBufferSize)
                if (readBytes <= 0)
                {
                    Log.v(TAG," RECEIVED MESSAGE FINISHED ($readBytes)")
                    break
                }
                byteStream.write(byteArray, 0, readBytes)
                sleep(delayMs)
                readBytes = inputStream.available()
            }
            receivedAllMessage(isDumpReceiveLog, id, byteStream.toByteArray(), callback)
            System.gc()
        }
        catch (e: Throwable)
        {
            e.printStackTrace()
            System.gc()
        }
        return (false)
    }

    private fun receivedAllMessage(isDumpReceiveLog: Boolean, id: Int, body: ByteArray?, callback: IPixproCommandCallback?)
    {
        Log.v(TAG, " receivedAllMessage() : " + (body?.size ?: 0) + " bytes.")
        if (isDumpReceiveLog && body != null)
        {
            // ログに受信メッセージを出力する
            SimpleLogDumper.dumpBytes("RX[" + body.size + "] ", body)
        }
        if (checkReceiveStatusMessage(body))
        {
            sendSecondaryMessage(isDumpReceiveLog, body)
        }
        callback?.receivedMessage(id, body)
    }

    private fun sendSecondaryMessage(isDumpReceiveLog: Boolean, received_body: ByteArray?)
    {
        if (received_body == null)
        {
            Log.v(TAG, " send_secondary_message : NULL ")
            return
        }
        Log.v(TAG, " sendSecondaryMessage : [" + received_body[8] + "] [" + received_body[9] + "] ")
        try
        {
            var messageToSend: ByteArray? = null
            if (received_body[8] == 0xd2.toByte() && received_body[9] == 0x07.toByte())
            {
                messageToSend = byteArrayOf(
                    0x2e.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xd2.toByte(),
                    0x07.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x10.toByte(),
                    0x00.toByte(),
                    0x80.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }
            if (received_body[8] == 0xd2.toByte() && received_body[9] == 0xd7.toByte())
            {
                messageToSend = byteArrayOf(
                    0x2e.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xd2.toByte(),
                    0xd7.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x10.toByte(),
                    0x00.toByte(),
                    0x80.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }
            if (received_body[8] == 0xb9.toByte() && received_body[9] == 0x0b.toByte()) {
                messageToSend = byteArrayOf(
                    0x2e.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xb9.toByte(),
                    0x0b.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x10.toByte(),
                    0x00.toByte(),
                    0x80.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }
            if (received_body[8] == 0xba.toByte() && received_body[9] == 0x0b.toByte()) {
                messageToSend = byteArrayOf(
                    0x2e.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xba.toByte(),
                    0x0b.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x10.toByte(),
                    0x00.toByte(),
                    0x80.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }
            if (received_body[8] == 0xbb.toByte() && received_body[9] == 0x0b.toByte()) {
                messageToSend = byteArrayOf(
                    0x2e.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xbb.toByte(),
                    0x0b.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x01.toByte(),
                    0x10.toByte(),
                    0x00.toByte(),
                    0x80.toByte(),
                    0x1f.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x90.toByte(),
                    0x01.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0x00.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(),
                    0xff.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
                )
            }
            if (isDumpReceiveLog && messageToSend != null)
            {
                // ログに受信メッセージを出力する
                SimpleLogDumper.dumpBytes("SND2[" + messageToSend.size + "] ", messageToSend)
            }
            if (dataOutputStream != null && messageToSend != null)
            {
                // (データを)送信
                dataOutputStream?.write(messageToSend)
                dataOutputStream?.flush()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkReceiveStatusMessage(receive_body: ByteArray?): Boolean
    {
        var isReceivedStatusMessage = false
        try
        {
            if (receive_body == null)
            {
                return (false)
            }
            if (receive_body.size < 16)
            {
                Log.v(TAG, " BODY SIZE IS SMALL. : " + receive_body.size)
                return false
            }
            if (receive_body[8] == 0xd2.toByte() && receive_body[9] == 0x07.toByte() ||
                receive_body[8] == 0xd2.toByte() && receive_body[9] == 0xd7.toByte() ||
                receive_body[8] == 0xb9.toByte() && receive_body[9] == 0x0b.toByte() ||
                receive_body[8] == 0xba.toByte() && receive_body[9] == 0x0b.toByte() ||
                receive_body[8] == 0xbb.toByte() && receive_body[9] == 0x0b.toByte()
            )
            {
                isReceivedStatusMessage = true
                Log.v(TAG, "  >>> RECEIVED HOST PRIMARY MESSAGE. (${receive_body.size} bytes.)<<<")
                statusChecker.receivedMessage(0, receive_body)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (isReceivedStatusMessage)
    }

    private fun waitForReceive(inputStream: InputStream, delayMs: Int, retry_count: Int): Int
    {
        var retryCount = retry_count
        var isLogOutput = true
        var readBytes = 0
        try
        {
            while (readBytes <= 0)
            {
                // Log.v(TAG, "  --- waitForReceive : " + retry_count + " delay : " + delayMs + "ms");
                sleep(delayMs)
                readBytes = inputStream.available()
                if (readBytes <= 0)
                {
                    if (isLogOutput)
                    {
                        Log.v(TAG, "  waitForReceive:: is.available() WAIT... : " + delayMs + "ms")
                        isLogOutput = false
                    }
                    retryCount--
                    if (!waitForever && retry_count < 0)
                    {
                        return (-1)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (readBytes)
    }
}
