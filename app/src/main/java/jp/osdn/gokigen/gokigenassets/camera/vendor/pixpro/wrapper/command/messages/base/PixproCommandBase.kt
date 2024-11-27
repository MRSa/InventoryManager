package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback

open class PixproCommandBase: IPixproCommand
{
    override fun getId() : Int
    {
        return (IPixproMessages.SEQ_DUMMY)
    }

    override fun receiveDelayMs(): Int
    {
        return 30
    }

    override fun commandBody(): ByteArray
    {
        return ByteArray(0)
    }

    override fun commandBody2(): ByteArray?
    {
        return null
    }

    override fun maxRetryCount(): Int
    {
        return 50
    }

    override fun sendRetry(): Boolean
    {
        return false
    }

    override fun responseCallback(): IPixproCommandCallback?
    {
        return null
    }

    override fun dumpLog(): Boolean
    {
        return false
    }
}
