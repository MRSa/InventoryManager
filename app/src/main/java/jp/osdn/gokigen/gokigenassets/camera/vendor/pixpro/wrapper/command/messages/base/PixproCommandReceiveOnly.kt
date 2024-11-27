package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback


class PixproCommandReceiveOnly(private val id: Int, private val callback: IPixproCommandCallback, private val isDumpLog:Boolean = false) : PixproCommandBase()
{
    override fun responseCallback(): IPixproCommandCallback
    {
        return (callback)
    }

    override fun getId(): Int
    {
        return (id)
    }

    override fun dumpLog(): Boolean
    {
        return (isDumpLog)
    }
}
