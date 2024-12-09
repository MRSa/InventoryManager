package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraShutterNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.inventorymanager.R

class RegisterInformationViewModel: ViewModel(), ICameraStatusReceiver, ICameraShutterNotify
{
    private val labelData1 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData2 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData3 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData4 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData5 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData6 : MutableLiveData<String> by lazy { MutableLiveData<String>() }


    private val infoData  : MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val image01 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image02 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image03 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image04 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image05 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }

    private val connectionStatus : MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus> by lazy { MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus>() }

    val cameraConnectionStatus: LiveData<ICameraConnectionStatus.CameraConnectionStatus> = connectionStatus

    val registerInformationLabel01: LiveData<String> = labelData1
    val registerInformationLabel02: LiveData<String> = labelData2
    val registerInformationLabel03: LiveData<String> = labelData3
    val registerInformationLabel04: LiveData<String> = labelData4
    val registerInformationLabel05: LiveData<String> = labelData5
    val registerInformationLabel06: LiveData<String> = labelData6

    val registerInformationData : LiveData<String> = infoData

    val registerInformationImage1 : LiveData<Bitmap> =  image01
    val registerInformationImage2 : LiveData<Bitmap> =  image02
    val registerInformationImage3 : LiveData<Bitmap> =  image03
    val registerInformationImage4 : LiveData<Bitmap> =  image04
    val registerInformationImage5 : LiveData<Bitmap> =  image05

    fun initializeViewModel(context: Context)
    {
        try
        {
            Log.v(TAG, "initializeViewModel()")
            resetData(context)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun resetData(context: Context)
    {
        try
        {
            image01.value = AppCompatResources.getDrawable(context, R.drawable.baseline_looks_one_24)?.toBitmap()
            image02.value = AppCompatResources.getDrawable(context, R.drawable.baseline_looks_two_24)?.toBitmap()
            image03.value = AppCompatResources.getDrawable(context, R.drawable.baseline_looks_3_24)?.toBitmap()
            image04.value = AppCompatResources.getDrawable(context, R.drawable.baseline_image_24)?.toBitmap()
            image05.value = AppCompatResources.getDrawable(context, R.drawable.baseline_image_24)?.toBitmap()

            labelData1.value = "${context.getString(R.string.label_register_item)} 1 "
            labelData2.value = "${context.getString(R.string.label_register_item)} 2 "
            labelData3.value = "${context.getString(R.string.label_register_item)} 3 "
            labelData4.value = "${context.getString(R.string.label_register_item)} 4 "
            labelData5.value = context.getString(R.string.label_register_text)
            labelData6.value = context.getString(R.string.label_register_bcr)

            infoData.value = context.getString(R.string.label_explain_register_next)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setTextArea1(value: String)
    {
        labelData1.value = value
    }

    fun setTextArea2(value: String)
    {
        labelData2.value = value
    }

    fun setTextArea3(value: String)
    {
        labelData3.value = value
    }

    fun setTextArea4(value: String)
    {
        labelData4.value = value
    }

    fun setTextReaderArea(value: String)
    {
        labelData5.value = value
    }

    fun setBarcodeReaderArea(value: String)
    {
        labelData6.value = value
    }

    /* ICameraStatusReceiver */
    override fun onStatusNotify(message: String?) {
        Log.v(TAG, "onStatusNotify() : $message")
        if (message != null)
        {
            infoData.value = message
        }
    }
    /* ICameraStatusReceiver */
    override fun onCameraConnected()
    {
        Log.v(TAG, "onCameraConnected()")
    }

    /* ICameraStatusReceiver */
    override fun onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()")
    }

    /* ICameraStatusReceiver */
    override fun onCameraConnectError(msg: String?)
    {
        Log.v(TAG, "onCameraConnectError() : $msg")
        if (msg != null)
        {
            infoData.value = msg
        }
    }

    override fun doShutter(id: Int, imageProvider: IImageProvider)
    {
        try
        {
            Log.v(TAG, "ICameraShutterNotify::doShutter($id)")
            when (id)
            {
                1 -> { image01.value = imageProvider.getImage() }
                2 -> { image02.value = imageProvider.getImage() }
                3 -> { image03.value = imageProvider.getImage() }
                4 -> { image04.value = imageProvider.getImage() }
                5 -> { image05.value = imageProvider.getImage() }
                else -> {
                    Log.v(TAG, "Unknown ID (no operation for $id)")
                }

            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }


    }


    companion object
    {
        private val TAG = RegisterInformationViewModel::class.java.simpleName
    }
}
