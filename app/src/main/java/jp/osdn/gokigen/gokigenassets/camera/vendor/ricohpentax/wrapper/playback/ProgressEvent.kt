package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.wrapper.playback

import jp.osdn.gokigen.gokigenassets.camera.interfaces.playback.IProgressEvent

class ProgressEvent(private val percent: Float, private val callback: CancelCallback?) : IProgressEvent
{
    override val progress: Float
        get() = percent
    override val isCancellable: Boolean
        get() = callback != null

    override fun requestCancellation() {
        callback?.requestCancellation()
    }

    interface CancelCallback {
        fun requestCancellation()
    }
}
