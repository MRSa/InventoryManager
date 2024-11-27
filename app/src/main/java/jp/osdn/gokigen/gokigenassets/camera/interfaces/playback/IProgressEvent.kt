package jp.osdn.gokigen.gokigenassets.camera.interfaces.playback

interface IProgressEvent
{
    val progress: Float
    val isCancellable: Boolean

    fun requestCancellation()
    interface CancelCallback
    {
        fun requestCancellation()
    }
}
