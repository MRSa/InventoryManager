package jp.osdn.gokigen.inventorymanager.ui.model

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraConnectionStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraShutterNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusReceiver
import jp.osdn.gokigen.gokigenassets.liveview.image.IImageProvider
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver
import jp.osdn.gokigen.inventorymanager.R
import jp.osdn.gokigen.inventorymanager.preference.IPreferencePropertyAccessor


class RegisterInformationViewModel: ViewModel(), ICameraStatusReceiver, ICameraShutterNotify, IInformationReceiver
{
    private lateinit var preference : SharedPreferences

    private val category : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData1 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData2 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData3 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData4 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData5 : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val labelData6 : MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val isbnData : MutableLiveData<String> by lazy { MutableLiveData<String>() }
    private val infoData  : MutableLiveData<String> by lazy { MutableLiveData<String>() }

    private val image01 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image02 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image03 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image04 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }
    private val image05 : MutableLiveData<Bitmap> by lazy { MutableLiveData<Bitmap>() }

    private val connectionStatus : MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus> by lazy { MutableLiveData<ICameraConnectionStatus.CameraConnectionStatus>() }
    //val cameraConnectionStatus: LiveData<ICameraConnectionStatus.CameraConnectionStatus> = connectionStatus

    private val isEdited : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isDataEdited : LiveData<Boolean> = isEdited

    private val isShowTextRecognitionEdit : MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() }
    val isShowTextRecognitionEditDialog : LiveData<Boolean> = isShowTextRecognitionEdit

    val registerInformationCategory: LiveData<String> = category
    val registerInformationLabel01: LiveData<String> = labelData1
    val registerInformationLabel02: LiveData<String> = labelData2
    val registerInformationLabel03: LiveData<String> = labelData3
    val registerInformationLabel04: LiveData<String> = labelData4
    val registerInformationLabel05: LiveData<String> = labelData5
    val registerInformationLabel06: LiveData<String> = labelData6

    val registerInformationIsbn: LiveData<String> = isbnData
    val registerInformationData : LiveData<String> = infoData

    val registerInformationImage1 : LiveData<Bitmap> =  image01
    val registerInformationImage2 : LiveData<Bitmap> =  image02
    val registerInformationImage3 : LiveData<Bitmap> =  image03
    val registerInformationImage4 : LiveData<Bitmap> =  image04
    val registerInformationImage5 : LiveData<Bitmap> =  image05

    //private var isbnValue : String = ""
    private var prodValue : String = ""
    private var textValue : String = ""
    private var urlValue : String = ""

    private var isReadImage1 : Boolean = false
    private var isReadImage2 : Boolean = false
    private var isReadImage3 : Boolean = false
    private var isReadImage4 : Boolean = false
    private var isReadImage5 : Boolean = false

    private var isAppendTextRecognition : Boolean = false

    //private val bcrOptions = BarcodeScannerOptions.Builder()
    //    .setBarcodeFormats(
    //        Barcode.FORMAT_ALL_FORMATS,
    //        Barcode.FORMAT_EAN_13,
    //        )
    //    .enableAllPotentialBarcodes()
    //    .build()
    private val scanner = BarcodeScanning.getClient()

    private val recognizerOptions = JapaneseTextRecognizerOptions.Builder().build()
    private var recognizer: TextRecognizer = TextRecognition.getClient(recognizerOptions)

    fun initializeViewModel(activity: AppCompatActivity)
    {
        try
        {
            Log.v(TAG, "RegisterInformationViewModel::initializeViewModel()")
            resetData(activity)

            preference = PreferenceManager.getDefaultSharedPreferences(activity)
            isAppendTextRecognition = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_APPEND_TEXT_RECOGNITION,
                IPreferencePropertyAccessor.PREFERENCE_APPEND_TEXT_RECOGNITION_DEFAULT_VALUE
            )
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

            labelData1.value = ""
            labelData2.value = ""
            labelData3.value = ""
            labelData4.value = ""
            labelData5.value = ""
            labelData6.value = ""
            isbnData.value = ""
            prodValue = ""
            textValue = ""
            urlValue = ""
            isReadImage1 = false
            isReadImage2 = false
            isReadImage3 = false
            isReadImage4 = false
            isReadImage5 = false

            infoData.value = context.getString(R.string.label_explain_register_next)

            isEdited.value = false
            isShowTextRecognitionEdit.value = false
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCategory(value: String)
    {
        category.value = value
        isEdited.value = true
    }

    fun setTextArea1(value: String)
    {
        labelData1.value = value
        isEdited.value = true
    }

    fun setTextArea2(value: String)
    {
        labelData2.value = value
        isEdited.value = true
    }

    fun setTextArea3(value: String)
    {
        labelData3.value = value
        isEdited.value = true
    }

    fun setTextArea4(value: String)
    {
        labelData4.value = value
        isEdited.value = true
    }

    fun setTextReaderArea(value: String)
    {
        labelData5.value = value
        isEdited.value = true
    }

    fun setBarcodeReaderArea(value: String)
    {
        labelData6.value = value
        isEdited.value = true
    }

    fun setIsbnData(value: String)
    {
        isbnData.value = value
        isEdited.value = true
    }

    fun isImage1Read(): Boolean { return(isReadImage1) }
    fun isImage2Read(): Boolean { return(isReadImage2) }
    fun isImage3Read(): Boolean { return(isReadImage3) }
    fun isImage4Read(): Boolean { return(isReadImage4) }
    fun isImage5Read(): Boolean { return(isReadImage5) }

    // fun getIsbnValue(): String { return(isbnValue) }
    fun getProductIdValue(): String { return(prodValue) }
    fun getTextValue(): String { return(textValue) }
    fun getUrlValue(): String { return(urlValue) }

    fun setShowTextRecognitionEditDialog(isShow: Boolean)
    {
        try
        {
            isShowTextRecognitionEdit.value = isShow
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
        connectionStatus.value = ICameraConnectionStatus.CameraConnectionStatus.CONNECTED
    }

    /* ICameraStatusReceiver */
    override fun onCameraDisconnected()
    {
        Log.v(TAG, "onCameraDisconnected()")
        connectionStatus.value = ICameraConnectionStatus.CameraConnectionStatus.DISCONNECTED
    }

    /* ICameraStatusReceiver */
    override fun onCameraConnectError(msg: String?)
    {
        Log.v(TAG, "onCameraConnectError() : $msg")
        if (msg != null)
        {
            infoData.value = msg
        }
        connectionStatus.value = ICameraConnectionStatus.CameraConnectionStatus.UNKNOWN
    }

    override fun doShutter(id: Int, imageProvider: IImageProvider)
    {
        try
        {
            Log.v(TAG, "ICameraShutterNotify::doShutter($id)")
            isEdited.value = true
            when (id)
            {
                1 -> {
                    image01.value = imageProvider.getImage()
                    isReadImage1 = true
                }
                2 -> {
                    image02.value = imageProvider.getImage()
                    isReadImage2 = true
                }
                3 -> {
                    image03.value = imageProvider.getImage()
                    isReadImage3 = true
                }
                4 -> {
                    image04.value = imageProvider.getImage()
                    isReadImage4 = true
                    infoData.value = " TEXT RECOGNITION"
                    val image = image04.value
                    if (image == null)
                    {
                        //  画像が取得できていないので、何もしない
                        Log.v(TAG, "ABORT : textRecognition() : The image is null...")
                        return
                    }
                    val inputImage = InputImage.fromBitmap(image, 0)
                    recognizer.process(inputImage)
                        .addOnSuccessListener { recognitionData -> recognizedText(recognitionData) }
                        .addOnFailureListener { infoData.value = "Failure Text Recognition..." }
                }
                5 -> {
                    image05.value = imageProvider.getImage()
                    isReadImage5 = true
                    infoData.value = " READ BARCODE"
                    val image = image05.value
                    if (image == null)
                    {
                        //  画像が取得できていないので、何もしない
                        Log.v(TAG, "ABORT : readBarcord() : The image is null...")
                        return
                    }
                    val inputImage = InputImage.fromBitmap(image, 0)
                    //val result =
                    scanner.process(inputImage)
                        .addOnSuccessListener { barcodes -> readBarcord(barcodes) }
                        .addOnFailureListener { infoData.value = "Barcode Read Failure..." }
                }
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

    private fun recognizedText(value: Text)
    {
        try
        {
            isAppendTextRecognition = preference.getBoolean(
                IPreferencePropertyAccessor.PREFERENCE_APPEND_TEXT_RECOGNITION,
                IPreferencePropertyAccessor.PREFERENCE_APPEND_TEXT_RECOGNITION_DEFAULT_VALUE
            )
            labelData5.value = if (isAppendTextRecognition) { labelData5.value + value.text } else { value.text }
            infoData.value = "text recognized : ${value.text.length}"
            Log.v(TAG, "recognizedText() : ${value.text}")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun readBarcord(barcodes: List<Barcode>)
    {
        try
        {
            var readData = ""
            for (barcode in barcodes)
            {
                val rawValue = barcode.rawValue
                val valueType = barcode.valueType
                Log.v(TAG, "readBarcord: $valueType")
                when (valueType) {
                    Barcode.TYPE_WIFI -> {
                        val ssid = barcode.wifi!!.ssid
                        val password = barcode.wifi!!.password
                        val type = barcode.wifi!!.encryptionType
                        readData += " WIFI: (SSID:$ssid PASS:$password TYPE:$type)"
                    }
                    Barcode.TYPE_URL -> {
                        readData += " URL:${barcode.url!!.url} (${barcode.url!!.title}) "
                        urlValue = barcode.url!!.url ?: ""
                    }
                    Barcode.TYPE_PRODUCT -> {
                        readData += " PRD:$rawValue "
                        prodValue = rawValue ?: ""
                    }
                    Barcode.TYPE_TEXT -> {
                        readData += " TXT:$rawValue "
                        textValue = rawValue ?: ""
                    }
                    Barcode.TYPE_ISBN -> {
                        readData += " ISBN:$rawValue "
                        isbnData.value = rawValue ?: ""
                    }
                    else -> {
                        readData += " ?[$valueType]:$rawValue "
                    }
                }
            }
            labelData6.value = readData
            infoData.value = "read Barcode : ${barcodes.size}"

        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    /* IInformationReceiver */
    override fun updateMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        infoData.value = message
    }

    /* IInformationReceiver */
    override fun appendMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        infoData.value = "${infoData.value} $message"
    }

    /* IInformationReceiver */
    override fun getCurrentMessage(): String
    {
        return (infoData.value ?: "")
    }

    companion object
    {
        private val TAG = RegisterInformationViewModel::class.java.simpleName
    }
}
