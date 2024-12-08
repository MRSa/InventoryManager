package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel.Companion

class RegisterInformationViewModel: ViewModel(), ICameraStatusReceiver
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

    companion object
    {
        private val TAG = RegisterInformationViewModel::class.java.simpleName
    }
}
