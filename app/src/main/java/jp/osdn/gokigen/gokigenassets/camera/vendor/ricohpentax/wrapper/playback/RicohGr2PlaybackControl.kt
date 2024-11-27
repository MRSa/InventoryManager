package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.wrapper.playback

import android.graphics.Bitmap
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.playback.*
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import org.json.JSONObject
import java.util.*

/**
 *
 *
 */
class RicohGr2PlaybackControl internal constructor(timeoutMSec: Int, executeUrl : String = "http://192.168.0.1") : IPlaybackControl
{
    private val TAG = toString()
    private val getPhotoUrl = "$executeUrl/v1/photos/"
    private val timeoutValue: Int
    private val httpClient = SimpleHttpClient()

    override fun getRawFileSuffix() : String
    {
        return ".DNG"
    }

    override fun downloadContentList(callback: ICameraContentListCallback) {
        val fileList: MutableList<ICameraFileInfo> = ArrayList<ICameraFileInfo>()
        val imageListUrl = "http://192.168.0.1/v1/photos?limit=3000"
        val contentList: String?
        try {
            contentList = httpClient.httpGet(imageListUrl, timeoutValue)
            if (contentList == null) {
                // ぬるぽ発行
                callback.onErrorOccurred(NullPointerException())
                return
            }
        } catch (e: Exception) {
            // 例外をそのまま転送
            callback.onErrorOccurred(e)
            return
        }
        try {
            val dirsArray = JSONObject(contentList).getJSONArray("dirs")
            // if (dirsArray != null)
            run {
                val size = dirsArray.length()
                for (index in 0 until size) {
                    val `object` = dirsArray.getJSONObject(index)
                    val dirName = `object`.getString("name")
                    val filesArray = `object`.getJSONArray("files")
                    val nofFiles = filesArray.length()
                    for (fileIndex in 0 until nofFiles) {
                        val fileName = filesArray.getString(fileIndex)
                        fileList.add(CameraFileInfo(dirName, fileName))
                    }
                }
            }
        } catch (e: Exception) {
            callback.onErrorOccurred(e)
            return
        }
        callback.onCompleted(fileList)
    }

    override fun updateCameraFileInfo(info: ICameraFileInfo?)
    {
        val url = getPhotoUrl + info?.getDirectoryPath().toString() + "/" + info?.getFilename()
            .toString() + "/info"
        Log.v(TAG, "updateCameraFileInfo() GET URL : $url")
        try {
            val response = httpClient.httpGet(url, timeoutValue)
            if (response.length < 1)
            {
                return
            }
            val `object` = JSONObject(response)

            // データを突っ込む
            val captured = `object`.getBoolean("captured")
            val av = getJSONString(`object`, "av")
            val tv = getJSONString(`object`, "tv")
            val sv = getJSONString(`object`, "sv")
            val xv = getJSONString(`object`, "xv")
            val orientation = `object`.getInt("orientation")
            val aspectRatio = getJSONString(`object`, "aspectRatio")
            val cameraModel = getJSONString(`object`, "cameraModel")
            val latLng = getJSONString(`object`, "latlng")
            val dateTime = `object`.getString("datetime")
            info?.updateValues(
                dateTime,
                av,
                tv,
                sv,
                xv,
                orientation,
                aspectRatio,
                cameraModel,
                latLng,
                captured
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun getJSONString(`object`: JSONObject, key: String): String {
        var value = ""
        try {
            value = `object`.getString(key)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return value
    }

    override fun getContentInfo(path: String?, name: String, callback: IContentInfoCallback) {
        val url = "$getPhotoUrl$name/info"
        Log.v(TAG, "getContentInfo() GET URL : $url")
        try {
            val response: String = httpClient.httpGet(url, timeoutValue)
            if (response == null || response.length < 1) {
                callback.onErrorOccurred(NullPointerException())
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }


    override fun downloadContentScreennail(
        path: String?,
        name: String,
        callback: IDownloadThumbnailImageCallback
    ) {
        //Log.v(TAG, "downloadContentScreennail() : " + path);
        val suffix = "?size=view"
        val url = getPhotoUrl + name + suffix
        Log.v(TAG, "downloadContentScreennail() GET URL : $url")
        try {
            val bmp: Bitmap? = httpClient.httpGetBitmap(url, null, timeoutValue)
            val map = HashMap<String?, Any?>()
            map["Orientation"] = 0
            callback.onCompleted(bmp, map)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun downloadContentThumbnail(
        path: String?,
        name: String,
        callback: IDownloadThumbnailImageCallback
    ) {
        //Log.v(TAG, "downloadContentThumbnail() : " + path);
        var suffix = "?size=view"
        if (name.contains(".JPG")) {
            suffix = "?size=thumb"
        }
        val url = getPhotoUrl + name + suffix
        Log.v(TAG, "downloadContentThumbnail() GET URL : $url")
        try {
            val bmp: Bitmap? = httpClient.httpGetBitmap(url, null, timeoutValue)
            val map = HashMap<String?, Any?>()
            map["Orientation"] = 0
            callback.onCompleted(bmp, map)
        } catch (e: Throwable) {
            e.printStackTrace()
            callback.onErrorOccurred(NullPointerException())
        }
    }

    override fun downloadContent(
        path: String?,
        name: String,
        isSmallSize: Boolean,
        callback: IDownloadContentCallback
    ) {
        Log.v(TAG, "downloadContent() : $name")
        var suffix = "?size=full"
        if (isSmallSize) {
            suffix = "?size=view"
        }
        val url = getPhotoUrl + name + suffix
        Log.v(TAG, "downloadContent() GET URL : $url")
        try {
            httpClient.httpGetBytes(
                url,
                null,
                timeoutValue,
                object : SimpleHttpClient.IReceivedMessageCallback {
                    override fun onCompleted() {
                        callback.onCompleted()
                    }

                    override fun onErrorOccurred(e: Exception?)
                    {
                        callback.onErrorOccurred(e)
                    }

                    override fun onReceive(readBytes: Int, length: Int, size: Int, data: ByteArray?)
                    {
                        val percent =
                            if (length == 0) 0.0f else readBytes.toFloat() / length.toFloat()
                        val event = ProgressEvent(percent, null)
                        callback.onProgress(data, size, event)
                    }
                })
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun showPictureStarted() {}
    override fun showPictureFinished() {}

    companion object {
        private const val DEFAULT_TIMEOUT = 5000
    }

    /*****
     * 操作メモ
     * 画像の一覧をとる            : http://192.168.0.1/v1/photos?limit=3000
     * 画像の情報をとる            ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG/info
     * サムネール画像をとる(JPEG)  ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=thumb
     * サムネール画像をとる(DNG)   ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG?size=view
     * サムネール画像をとる(MOV)   ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.MOV?size=view
     * デバイス表示用画像をとる     :  http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=view
     * 画像(JPEG)をダウンロードする ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.JPG?size=full
     * 画像(DNG)をダウンロードする  ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.DNG?size=full
     * 動画をダウンロードする      ： http://192.168.0.1/v1/photos/yyyRICOH/R0000xxx.MOV?size=full
     */
    init {
        timeoutValue = Math.max(
            DEFAULT_TIMEOUT,
            timeoutMSec
        ) // (timeoutMSec < DEFAULT_TIMEOUT) ? DEFAULT_TIMEOUT : timeoutMSec;
    }
}
