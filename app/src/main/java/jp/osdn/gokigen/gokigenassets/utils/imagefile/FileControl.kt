package jp.osdn.gokigen.gokigenassets.utils.imagefile

import android.os.Build
import android.util.Log
import androidx.camera.core.ImageCapture
import androidx.fragment.app.FragmentActivity
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SAVE_LOCAL_LOCATION
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE
import jp.osdn.gokigen.gokigenassets.liveview.storeimage.IStoreImage
import jp.osdn.gokigen.gokigenassets.preference.PreferenceAccessWrapper
import jp.osdn.gokigen.gokigenassets.scene.IVibrator

class FileControl(private val context: FragmentActivity, private val storeImage : IStoreImage, private val vibrator : IVibrator)
{
    private val storeLocal = ImageStoreLocal(context)
    private val storeExternal = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { ImageStoreExternal(context) } else { ImageStoreExternalLegacy(context) }
    private var imageCapture: ImageCapture? = null

    fun prepare() : ImageCapture?
    {
        try
        {
            imageCapture = ImageCapture.Builder().build()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return imageCapture
    }

    fun finish()
    {

    }

    fun takePhoto(cameraId : Int, position : Float = 0.0f)
    {
        Log.v(TAG, " takePhoto()")
        try
        {
            //  preferenceから設定を取得する
            val preference = PreferenceAccessWrapper(context)
            val isLocalLocation  = preference.getBoolean(ID_PREFERENCE_SAVE_LOCAL_LOCATION, ID_PREFERENCE_SAVE_LOCAL_LOCATION_DEFAULT_VALUE)
            val captureBothCamera  = preference.getBoolean(ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW, ID_PREFERENCE_CAPTURE_BOTH_CAMERA_AND_LIVE_VIEW_DEFAULT_VALUE)
            val notUseShutter = preference.getBoolean(ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE, ID_PREFERENCE_CAPTURE_ONLY_LIVEVIEW_IMAGE_DEFAULT_VALUE)

            if (captureBothCamera)
            {
                // ライブビュー画像を保管する場合...
                val thread = Thread { storeImage.doStore(cameraId, false, position) }
                try
                {
                    thread.start()
                }
                catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }

            if (notUseShutter)
            {
                //  シャッターを駆動させない(けど、バイブレーションで通知する)
                vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                return
            }

            // 保管用クラスが準備できていない場合は、何もしない。
            if (imageCapture == null)
            {
                Log.v(TAG, " ImageCapture is NULL, so do nothing.")
                return
            }

            var isStoreLocal = isLocalLocation
            if (!isLocalLocation)
            {
                // 共用フォルダに保存
                isStoreLocal = !storeExternal.takePhoto(cameraId, imageCapture)
            }
            if (isStoreLocal)
            {
                // アプリ専用フォルダに登録
                storeLocal.takePhoto(cameraId, imageCapture)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = FileControl::class.java.simpleName
    }
}
