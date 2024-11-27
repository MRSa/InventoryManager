package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.IPixproCommandCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.IPixproMessages
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandBase

class PixproChangeVideoSize(private val callback: IPixproCommandCallback, value: Int) : PixproCommandBase()
{
    private val data00: Byte = (0x000000ff and value).toByte()
    private val data01: Byte = (0x0000ff00 and value shr 8).toByte()
    private val data02: Byte = (0x00ff0000 and value shr 16).toByte()
    private val data03: Byte = (-0x1000000 and value shr 24).toByte()

    override fun getId() : Int
    {
        return (IPixproMessages.SEQ_VIDEO_SIZE)
    }

    override fun commandBody(): ByteArray {
        return byteArrayOf(
            0x2e.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x30.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0xed.toByte(), 0x3.toByte(), 0x0.toByte(), 0x0.toByte(), 0x1.toByte(), 0x0.toByte(), 0x0.toByte(), 0x80.toByte(),
            0x0b.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x1.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x1.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x30.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0b.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x40.toByte(), 0x1.toByte(), 0x4c.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x40.toByte(), 0x1.toByte(), 0x4c.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), data00, data01, data02, data03, 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),
            0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(), 0x0.toByte(),

            0xff.toByte(), 0xff.toByte(), 0xff.toByte(), 0xff.toByte(),
            0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte()

        )
    }

    override fun responseCallback(): IPixproCommandCallback
    {
        return callback
    }
}
