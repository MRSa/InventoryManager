package jp.osdn.gokigen.gokigenassets.liveview.message

import android.graphics.Color

class MessageHolder(private var message: String = "", private var color: Int = Color.LTGRAY, private var size: Int = 20) : IMessageHolder
{
    override fun getSize() : Int { return (size) }
    override fun getColor(): Int { return (color) }
    override fun getMessage(): String { return (message) }

    fun setValue(message : String = "", color : Int = Color.BLACK, size : Int = 8)
    {
        this.message = message
        this.color = color
        this.size = size
    }
}
