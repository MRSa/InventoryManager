package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproCommand

interface IPixproCommandPublisher
{
    fun isConnected(): Boolean
    fun enqueueCommand(command: IPixproCommand): Boolean

    fun start()
    fun stop()
}
