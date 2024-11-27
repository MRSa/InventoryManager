package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback

interface IPixproCommand
{
    // メッセージの識別子
    fun getId(): Int

    // コマンドの受信待ち時間(単位:ms)
    fun receiveDelayMs(): Int

    // 送信するメッセージボディ
    fun commandBody(): ByteArray

    // 送信するメッセージボディ(連続送信する場合)
    fun commandBody2(): ByteArray?

    // 受信待ち再試行回数
    fun maxRetryCount(): Int

    // コマンドの受信が失敗した場合、再送する（再送する場合は true）
    fun sendRetry(): Boolean

    // コマンド送信結果（応答）の通知先
    fun responseCallback(): IPixproCommandCallback?

    // デバッグ用： ログ(logcat)に通信結果を残すかどうか
    fun dumpLog(): Boolean

}
