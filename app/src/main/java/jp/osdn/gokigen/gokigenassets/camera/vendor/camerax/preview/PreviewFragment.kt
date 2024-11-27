package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.preview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_LAYOUT_CAMERA_CAPTURE
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_PREVIEW_VIEW_BUTTON_SHUTTER
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl

class PreviewFragment(private val contentLayoutId: Int = ID_LAYOUT_CAMERA_CAPTURE) : Fragment(contentLayoutId)
{
    private lateinit var previewView : View
    private lateinit var cameraControl: ICameraControl

    companion object
    {
        fun newInstance(cameraControl : ICameraControl) = PreviewFragment().apply { this.cameraControl = cameraControl }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        if (::previewView.isInitialized)
        {
            return (previewView)
        }
        previewView = inflater.inflate(contentLayoutId, null, false)

        if (::cameraControl.isInitialized)
        {
            previewView.findViewById<ImageButton>(ID_PREVIEW_VIEW_BUTTON_SHUTTER)?.setOnClickListener(cameraControl.captureButtonReceiver())
        }
        return (previewView)
    }
}
