package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandBase

class PixproWhiteBalance(private val callback: IPixproCommandCallback, whiteBalance: Int) : PixproCommandBase()
{
    private val data0: Byte = whiteBalance.toByte()

    override fun getId() : Int
    {
        return (IPixproMessages.SEQ_WHITE_BALANCE)
    }

    override fun commandBody(): ByteArray
    {
        return byteArrayOf(
            0x2e.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            0x20.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            0xed.toByte(),
            0x03.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            0x01.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x80.toByte(),

            0x08.toByte(),
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

            0x20.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            0x08.toByte(),
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

            0xb7.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            0xb7.toByte(),
            0x00.toByte(),
            0x00.toByte(),
            0x00.toByte(),

            data0,
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

            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
        )
    }

    override fun responseCallback(): IPixproCommandCallback
    {
        return callback
    }
}
