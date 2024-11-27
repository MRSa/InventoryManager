package jp.osdn.gokigen.gokigenassets.liveview

import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer

interface ILiveView
{
    fun setImageProvider(provider : IImageProvider)
    fun setAnotherDrawer(drawer : IAnotherDrawer?, drawer2 : IAnotherDrawer?)
    fun getMessageDrawer() : IMessageDrawer
    fun invalidate()
}
