package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandBase


class PixproExecuteFocus(private val callback: IPixproCommandCallback, posX: Int, posY: Int) : PixproCommandBase()
{
    private val data00: Byte = (0x000000ff and posX).toByte()
    private val data01: Byte = (0x0000ff00 and posX shr 8).toByte()
    private val data02: Byte = (0x00ff0000 and posX shr 16).toByte()
    private val data03: Byte = (-0x1000000 and posX shr 24).toByte()
    private val data10: Byte = (0x000000ff and posY).toByte()
    private val data11: Byte = (0x0000ff00 and posY shr 8).toByte()
    private val data12: Byte = (0x00ff0000 and posY shr 16).toByte()
    private val data13: Byte = (-0x1000000 and posY shr 24).toByte()

    override fun getId() : Int
    {
        return (IPixproMessages.SEQ_FOCUS)
    }

    override fun commandBody(): ByteArray
    {
        return byteArrayOf(
            0x2e.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x18.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0xf6.toByte(), 0x03.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x80.toByte(),

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x18.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            data00, data01, data02, data03,
            data10, data11, data12, data13,

            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),

            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()
        )
    }

    override fun responseCallback(): IPixproCommandCallback
    {
        return (callback)
    }

}
