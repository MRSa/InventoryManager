package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.util.Log
import android.view.KeyEvent
import android.view.View
import jp.osdn.gokigen.gokigenassets.camera.interfaces.IKeyDown
import jp.osdn.gokigen.constants.IApplicationConstantConvert
import jp.osdn.gokigen.gokigenassets.liveview.ICachePositionProvider
import jp.osdn.gokigen.gokigenassets.utils.imagefile.FileControl

class CameraClickKeyDownListener(private val cameraId : Int,  private val fileControl: FileControl, private val cachePositionProvider : ICachePositionProvider?) : View.OnClickListener, View.OnLongClickListener, IKeyDown
{
    override fun onClick(v: View?)
    {
        try
        {
            Log.v(TAG, " onClick : $v?.id ")
            when (v?.id)
            {
                IApplicationConstantConvert.ID_PREVIEW_VIEW_BUTTON_SHUTTER -> takePhoto()
                IApplicationConstantConvert.ID_BUTTON_SHUTTER -> takePhoto()
                else -> Log.v(TAG, " Unknown ID : " + v?.id)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    fun takePhoto()
    {
        if (cachePositionProvider != null)
        {
            fileControl.takePhoto(cameraId, cachePositionProvider.getCachePosition())

        }
        else
        {
            fileControl.takePhoto(cameraId)
        }
    }

    override fun handleKeyDown(keyCode: Int, event: KeyEvent): Boolean
    {
        try
        {
            if (event.action == KeyEvent.ACTION_DOWN && (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_CAMERA))
            {
                takePhoto()
                return (true)
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun onLongClick(v: View?): Boolean
    {
        return (false)
    }

    companion object
    {
        private val TAG = CameraClickKeyDownListener::class.java.simpleName
    }
}
