package jp.osdn.gokigen.gokigenassets.camera.interfaces.playback


/**
*   画像再生・取得用インタフェース
*
*/
interface IPlaybackControl
{
    fun getRawFileSuffix() : String?

    fun downloadContentList(callback: ICameraContentListCallback)
    fun getContentInfo(path: String?, name: String, callback: IContentInfoCallback)

    fun updateCameraFileInfo(info: ICameraFileInfo?)

    fun downloadContentScreennail(
        path: String?,
        name: String,
        callback: IDownloadThumbnailImageCallback
    )

    fun downloadContentThumbnail(
        path: String?,
        name: String,
        callback: IDownloadThumbnailImageCallback
    )

    fun downloadContent(
        path: String?,
        name: String,
        isSmallSize: Boolean,
        callback: IDownloadContentCallback
    )

    fun showPictureStarted()
    fun showPictureFinished()

}
