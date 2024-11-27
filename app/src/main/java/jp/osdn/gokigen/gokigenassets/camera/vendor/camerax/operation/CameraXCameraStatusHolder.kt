package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.annotation.SuppressLint
import android.graphics.Color
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.util.Log
import android.util.Range
import androidx.camera.camera2.interop.CaptureRequestOptions
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus

class CameraXCameraStatusHolder(private val cameraXCameraControl: CameraXCameraControl) : ICameraStatus
{
    private val statusListHolder = CameraXCameraStatusListHolder(cameraXCameraControl)
    companion object
    {
        private val TAG = CameraXCameraStatusHolder::class.java.simpleName
        private const val isDumpStatus = false
    }

    override fun getStatusList(key: String): List<String?>
    {
        return (statusListHolder.getStatusList(key))
    }

    override fun setStatus(key: String, value: String)
    {
        try
        {
            Log.v(TAG, "  ----- setStatus($key, $value)")
            when (key)
            {
                ICameraStatus.TAKE_MODE -> setTakeMode(value)
                // ICameraStatus.SHUTTER_SPEED -> setShutterSpeed(value)
                //ICameraStatus.APERTURE -> setAperture(value)
                ICameraStatus.EXPREV -> setExpRev(value)
                // ICameraStatus.CAPTURE_MODE -> setCaptureMode(value)
                ICameraStatus.ISO_SENSITIVITY -> setIsoSensitivity(value)
                ICameraStatus.WHITE_BALANCE -> setWhiteBalance(value)
                ICameraStatus.AE -> setMeteringMode(value)
                ICameraStatus.EFFECT -> setPictureEffect(value)
                // ICameraStatus.BATTERY -> setRemainBattery(value)
                ICameraStatus.TORCH_MODE -> setTorchMode(value)
                else -> return
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun getStatus(key: String): String
    {
        return (when (key) {
            ICameraStatus.TAKE_MODE -> getTakeMode()
            ICameraStatus.SHUTTER_SPEED -> getShutterSpeed()
            ICameraStatus.APERTURE -> getAperture()
            ICameraStatus.EXPREV -> getExpRev()
            ICameraStatus.CAPTURE_MODE -> getCaptureMode()
            ICameraStatus.ISO_SENSITIVITY -> getIsoSensitivity()
            ICameraStatus.WHITE_BALANCE -> getWhiteBalance()
            ICameraStatus.AE -> getMeteringMode()
            ICameraStatus.EFFECT -> getPictureEffect()
            ICameraStatus.BATTERY -> getRemainBattery()
            ICameraStatus.TORCH_MODE -> getTorchMode()
          else -> ""
        })
    }

    override fun getStatusColor(key: String): Int
    {
        return (Color.WHITE)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getTakeMode() : String
    {
        var takeMode = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                // https://developer.android.com/reference/android/hardware/camera2/CaptureRequest
                val controlMode = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_MODE)
                val sceneMode = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_SCENE_MODE)
                takeMode = when (controlMode?: 0) {
                    CameraCharacteristics.CONTROL_MODE_OFF -> "OFF"
                    CameraCharacteristics.CONTROL_MODE_AUTO -> "AUTO"
                    CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE -> "SCENE"
                    CameraCharacteristics.CONTROL_MODE_OFF_KEEP_STATE -> "OFF_KEEP"
                    CameraCharacteristics.CONTROL_MODE_USE_EXTENDED_SCENE_MODE -> "exSCENE"
                    else -> "($controlMode)"
                }
                val sceneText = when (sceneMode ?: 0)
                {
                    CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED -> "DISABLED"
                    CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY -> "FACE"
                    CameraCharacteristics.CONTROL_SCENE_MODE_ACTION -> "ACTION"
                    CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT -> "PORTRAIT"
                    CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE -> "LANDSCAPE"
                    CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT -> "NIGHT"
                    CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT -> "NIGHT_PORTRAIT"
                    CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE -> "THEATRE"
                    CameraCharacteristics.CONTROL_SCENE_MODE_BEACH -> "BEACH"
                    CameraCharacteristics.CONTROL_SCENE_MODE_SNOW -> "SNOW"
                    CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET -> "SUNSET"
                    CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO -> "STEADYPHOTO"
                    CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS -> "FIREWORKS"
                    CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS -> "SPORTS"
                    CameraCharacteristics.CONTROL_SCENE_MODE_PARTY -> "PARTY"
                    CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT -> "CANDLELIGHT"
                    CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE -> "BARCODE"
                    CameraCharacteristics.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO -> "HS_VIDEO"
                    CameraCharacteristics.CONTROL_SCENE_MODE_HDR -> "HDR"
                    else -> "[$sceneMode]"
                }
                if ((controlMode == CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE)||(controlMode == CameraCharacteristics.CONTROL_MODE_USE_EXTENDED_SCENE_MODE))
                {
                    takeMode = sceneText
                }

                if (isDumpStatus)
                {
                    dumpAvailableValuesIntArray("CONTROL_AF_AVAILABLE_MODES", CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)
                    dumpAvailableValuesIntArray("CONTROL_AVAILABLE_SCENE_MODES", CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES)
                    dumpAvailableValuesIntArray("CONTROL_AVAILABLE_EFFECTS", CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS)
                    dumpAvailableValuesIntArray("CONTROL_AWB_AVAILABLE_MODES", CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES)
                    dumpAvailableValuesIntArray("NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES", CameraCharacteristics.NOISE_REDUCTION_AVAILABLE_NOISE_REDUCTION_MODES)
                    dumpAvailableValuesIntArray("LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION", CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION)
                    dumpAvailableValuesIntArray("REQUEST_AVAILABLE_CAPABILITIES", CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                    dumpAvailableValuesIntArray("TONEMAP_AVAILABLE_TONE_MAP_MODES", CameraCharacteristics.TONEMAP_AVAILABLE_TONE_MAP_MODES)
                    dumpAvailableValuesIntArray("STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES", CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES)
                    dumpAvailableValuesFloatArray("LENS_INFO_AVAILABLE_APERTURES", CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)
                    dumpAvailableValuesFloatArray("LENS_INFO_AVAILABLE_FILTER_DENSITIES", CameraCharacteristics.LENS_INFO_AVAILABLE_FILTER_DENSITIES)
                    dumpAvailableValuesRangeLong("SENSOR_INFO_EXPOSURE_TIME_RANGE", CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE)
                    dumpAvailableValuesRangeInt("SENSOR_INFO_SENSITIVITY_RANGE", CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
                    dumpAvailableValuesRangeInt("CONTROL_AE_COMPENSATION_RANGE", CameraCharacteristics.CONTROL_AE_COMPENSATION_RANGE)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (takeMode)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getShutterSpeed() : String
    {
        var exposureTime = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val exposure = captureOptions.getCaptureRequestOption(CaptureRequest.SENSOR_EXPOSURE_TIME) ?: 0
                if (exposure != 0L)
                {
                    //  単位 : us → 秒に変換して文字列化
                    exposureTime = convertShutterSpeedString((exposure.toDouble() / 1000000.0f))
                }
                //Log.v(TAG, " Exposure Time : $exposure [$exposureTime]")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (exposureTime)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getAperture() : String
    {
        var aperture = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                // https://developer.android.com/reference/android/hardware/camera2/CaptureRequest
                val lensAperture = captureOptions.getCaptureRequestOption(CaptureRequest.LENS_APERTURE) ?: 0.0f
                if (lensAperture != 0.0f)
                {
                    aperture = String.format("F%2.1f", lensAperture)
                }
                // Log.v(TAG, " Aperture : $lensAperture")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (aperture)
    }

    private fun getExpRev() : String
    {
        try
        {
            // ExposureCompensation : ExpRev
            val exposureState = cameraXCameraControl.getExposureState()
            if ((exposureState != null)&&(exposureState.isExposureCompensationSupported))
            {
                val index = exposureState.exposureCompensationIndex
                val step = exposureState.exposureCompensationStep
                val value = index.toDouble() * step.toDouble()
                return (String.format("%+1.1f", value))
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getCaptureMode() : String
    {
        var focalLength = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                // https://developer.android.com/reference/android/hardware/camera2/CaptureRequest
                val focal = captureOptions.getCaptureRequestOption(CaptureRequest.LENS_FOCAL_LENGTH) ?: 0.0f
                if (focal != 0.0f)
                {
                    focalLength = String.format("%2.1fmm", focal)
                }
                // Log.v(TAG, " Focal Length : $focalLength")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (focalLength)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getIsoSensitivity() : String
    {
        var isoSensitivity = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val sensitivity = captureOptions.getCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY) ?: 0
                if (sensitivity != 0)
                {
                    isoSensitivity = String.format("ISO:%d", sensitivity)
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (isoSensitivity)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getWhiteBalance() : String
    {
        var wb = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val awbMode = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE) ?: 0
                val colorCorrection = captureOptions.getCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_MODE) ?: 0
                wb = when (awbMode) {
                    CameraCharacteristics.CONTROL_AWB_MODE_OFF -> "WB:OFF"
                    CameraCharacteristics.CONTROL_AWB_MODE_AUTO -> "WB:AUTO"
                    CameraCharacteristics.CONTROL_AWB_MODE_INCANDESCENT -> "WB:INCANDESCENT"
                    CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT -> "WB:FLUORESCENT"
                    CameraCharacteristics.CONTROL_AWB_MODE_WARM_FLUORESCENT -> "WB:WARM FLUORESCENT"
                    CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT -> "WB:DAYLIGHT"
                    CameraCharacteristics.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT -> "WB:CLOUDY DAYLIGHT"
                    CameraCharacteristics.CONTROL_AWB_MODE_TWILIGHT -> "WB:TWILIGHT"
                    CameraCharacteristics.CONTROL_AWB_MODE_SHADE -> "WB:SHADE"
                    else -> "WB:[$awbMode]"
                }
                if (awbMode == CameraCharacteristics.CONTROL_AWB_MODE_OFF)
                {
                    wb = when (colorCorrection) {
                        CameraCharacteristics.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX -> "wb:MATRIX"
                        CameraCharacteristics.COLOR_CORRECTION_MODE_FAST -> "wb:FAST"
                        CameraCharacteristics.COLOR_CORRECTION_MODE_HIGH_QUALITY -> "wb:HQ"
                        else -> "WB:[$awbMode/$colorCorrection]"
                    }
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (wb)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getMeteringMode() : String
    {
        var aeMode = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val autoExposure = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE) ?: 0
                aeMode = when (autoExposure) {
                    CameraCharacteristics.CONTROL_AE_MODE_OFF -> "AE:OFF"
                    CameraCharacteristics.CONTROL_AE_MODE_ON -> "AE:ON"
                    CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH -> "Flash"
                    CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH -> "Always FLASH"
                    CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE -> "Flash(RedEye)"
                    CameraCharacteristics.CONTROL_AE_MODE_ON_EXTERNAL_FLASH -> "FLASH:EXTERNAL"
                    else -> "FLASH:[$autoExposure]"
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (aeMode)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getPictureEffect() : String
    {
        var effect = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val aeLock = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_AE_LOCK) ?: false
                val awbLock = captureOptions.getCaptureRequestOption(CaptureRequest.CONTROL_AWB_LOCK) ?: false
                if (aeLock)
                {
                    effect = "$effect AE-L"
                }
                if (awbLock)
                {
                    effect = "$effect WB-L"
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (effect)
    }


    @SuppressLint("UnsafeOptInUsageError")
    private fun getRemainBattery() : String
    {
        try
        {
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                val hwLevel = cameraInfo.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
                return ("ID: ${cameraInfo.cameraId}  (L$hwLevel)")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getTorchMode() : String
    {
        var flash = ""
        try
        {
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                val captureOptions = cameraControl.captureRequestOptions
                val flashState = captureOptions.getCaptureRequestOption(CaptureRequest.FLASH_MODE) ?: -1
                flash = when (flashState)
                {
                    CaptureRequest.FLASH_MODE_OFF -> "FLASH: OFF"
                    CaptureRequest.FLASH_MODE_SINGLE -> "FLASH: SINGLE"
                    CaptureRequest.FLASH_MODE_TORCH -> "FLASH: TORCH"
                    -1 -> ""
                    else -> "FLASH: $flashState"
                }
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (flash)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setMeteringMode(value : String)
    {
        try
        {
            val setValue = statusListHolder.decideMeteringMode(value)
            if (setValue == -1)
            {
                return
            }
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_MODE,setValue)
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setIsoSensitivity(value : String)
    {
        try
        {
            val setValue = statusListHolder.decideSensorSensitivity(value)
            if (setValue == -1)
            {
                return
            }
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY,setValue)
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setExpRev(value : String)
    {
        try
        {
            cameraXCameraControl.setExposureCompensation(statusListHolder.decideExpRevIndex(value))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setPictureEffect(value : String)
    {
        try
        {
            val setAeLock = statusListHolder.decideAeLock(value)
            val setAwbLock = statusListHolder.decideAwbLock(value)
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.CONTROL_AE_LOCK,setAeLock)
                builder.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_LOCK,setAwbLock)
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setTorchMode(value : String)
    {
        try
        {
            val setValue = statusListHolder.decideTorchMode(value)
            if (setValue == -1)
            {
                return
            }
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.FLASH_MODE,setValue)
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setWhiteBalance(value : String)
    {
        try
        {
            val setValue = statusListHolder.decideWhiteBalance(value)
            val setColorCorrection = statusListHolder.decideColorCorrection(value)
            if (setValue == -1)
            {
                return
            }
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.CONTROL_AWB_MODE,setValue)
                if ((setValue == CameraCharacteristics.CONTROL_AWB_MODE_OFF)&&(setColorCorrection != -1))
                {
                    builder.setCaptureRequestOption(CaptureRequest.COLOR_CORRECTION_MODE, setColorCorrection)
                }
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun setTakeMode(value : String)
    {
        try
        {
            val setValue = statusListHolder.decideTakeMode(value)
            val setScene = statusListHolder.decideScene(value)
            val cameraControl = cameraXCameraControl.getCamera2CameraControl()
            if (cameraControl != null)
            {
                // cameraControl.setCaptureRequestOptions()
                val builder = CaptureRequestOptions.Builder()
                builder.setCaptureRequestOption(CaptureRequest.CONTROL_MODE,setValue)
                if (((setValue == CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE)||(setValue == CameraCharacteristics.CONTROL_MODE_USE_EXTENDED_SCENE_MODE))&&(setScene != -1))
                {
                    builder.setCaptureRequestOption(CaptureRequest.CONTROL_SCENE_MODE, setScene)
                }
                cameraControl.captureRequestOptions = builder.build()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun dumpAvailableValuesIntArray(name : String, id: CameraCharacteristics.Key<IntArray>)
    {
        try
        {
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                Log.v(TAG, "  -----  $name  -----")
                val selection = cameraInfo.getCameraCharacteristic(id)
                if (selection != null)
                {
                    for (value in selection)
                    {
                        Log.v(TAG, "    $value")
                    }
                }
                Log.v(TAG, "  -----  $name  -----")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun convertShutterSpeedString(shutterSpeed : Double) : String
    {
        var inv = 0.0
        var stringValue = ""
        try
        {
            if (shutterSpeed  < 1.0)
            {
                inv = 1.0 / shutterSpeed
            }
            if (inv < 2.0) // if (inv < 10.0)
            {
                inv = 0.0
            }
            if (inv > 0.0f)
            {
                // シャッター速度を分数で表示する
                var intValue = inv.toInt()
                val modValue = intValue % 10
                if (modValue == 9 || modValue == 4)
                {
                    // ちょっと格好が悪いけど...切り上げ
                    intValue++
                }
                stringValue = "1/$intValue"
            }
            else
            {
                // シャッター速度を数値(秒数)で表示する
                stringValue = "${shutterSpeed}s "
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (stringValue)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun dumpAvailableValuesFloatArray(name : String, id: CameraCharacteristics.Key<FloatArray>)
    {
        try
        {
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                Log.v(TAG, "  -----  $name  -----")
                val selection = cameraInfo.getCameraCharacteristic(id)
                if (selection != null)
                {
                    for (value in selection)
                    {
                        Log.v(TAG, "    $value")
                    }
                }
                Log.v(TAG, "  -----  $name  -----")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun dumpAvailableValuesRangeLong(name : String, id: CameraCharacteristics.Key<Range<Long>>)
    {
        try
        {
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                Log.v(TAG, "  -----  $name  -----")
                val selection = cameraInfo.getCameraCharacteristic(id)
                if (selection != null)
                {
                    Log.v(TAG, "    lower: ${selection.lower}  upper: ${selection.upper}")
                }
                Log.v(TAG, "  -----  $name  -----")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun dumpAvailableValuesRangeInt(name : String, id: CameraCharacteristics.Key<Range<Int>>)
    {
        try
        {
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                Log.v(TAG, "  -----  $name  -----")
                val selection = cameraInfo.getCameraCharacteristic(id)
                if (selection != null)
                {
                    Log.v(TAG, "    lower: ${selection.lower} upper: ${selection.upper}")
                }
                Log.v(TAG, "  -----  $name  -----")
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }
}
