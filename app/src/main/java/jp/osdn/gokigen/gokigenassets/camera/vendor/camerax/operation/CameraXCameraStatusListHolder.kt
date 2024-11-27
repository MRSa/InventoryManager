package jp.osdn.gokigen.gokigenassets.camera.vendor.camerax.operation

import android.annotation.SuppressLint
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import kotlin.math.abs

class CameraXCameraStatusListHolder(private val cameraXCameraControl: CameraXCameraControl)
{
    companion object
    {
        private val TAG = CameraXCameraStatusListHolder::class.java.simpleName
    }

    fun getStatusList(key: String): List<String>
    {
        return (when (key) {
            ICameraStatus.TAKE_MODE -> getAvailableTakeMode()
            ICameraStatus.SHUTTER_SPEED -> getAvailableShutterSpeed()
            ICameraStatus.APERTURE -> getAvailableAvailableAperture()
            ICameraStatus.EXPREV -> getAvailableExpRev()
            ICameraStatus.CAPTURE_MODE -> getAvailableCaptureMode()
            ICameraStatus.ISO_SENSITIVITY -> getAvailableIsoSensitivity()
            ICameraStatus.WHITE_BALANCE -> getAvailableWhiteBalance()
            ICameraStatus.AE -> getAvailableMeteringMode()
            ICameraStatus.EFFECT -> getAvailablePictureEffect()
            ICameraStatus.BATTERY -> ArrayList()
            ICameraStatus.TORCH_MODE -> getAvailableTorchMode()
            else -> ArrayList()
        })
    }

    private fun getAvailableTakeMode() : List<String>
    {
        return (listOf("OFF",
            "AUTO",
            "SCENE",
            "OFF_KEEP",
            "exSCENE",
            "DISABLED",
            "FACE",
            "ACTION",
            "PORTRAIT",
            "LANDSCAPE",
            "NIGHT",
            "NIGHT_PORTRAIT",
            "THEATER",
            "BEACH",
            "SNOW",
            "SUNSET",
            "STEADYPHOTO",
            "FIREWORKS",
            "SPORTS",
            "PARTY",
            "CANDLELIGHT",
            "BARCODE",
            "HS_VIDEO",
            "HDR"
        ))
    }

    private fun getAvailableShutterSpeed() : List<String>
    {
        return (ArrayList())
    }

    private fun getAvailableAvailableAperture() : List<String>
    {
        return (ArrayList())
    }

    private fun getAvailableExpRev() : List<String>
    {
        try
        {
            val valueList = ArrayList<String>()
            val exposureState = cameraXCameraControl.getExposureState()
            if ((exposureState != null)&&(exposureState.isExposureCompensationSupported))
            {
                val step = exposureState.exposureCompensationStep
                val range = exposureState.exposureCompensationRange
                for (count in range.lower .. range.upper)
                {
                    valueList.add(String.format("%+1.2f", (count.toDouble() * step.toDouble())))
                }
                return (valueList)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (ArrayList())
    }

    private fun getAvailableCaptureMode() : List<String>
    {
        return (ArrayList())
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getAvailableIsoSensitivity() : List<String>
    {
        return (ArrayList()) // ISO 設定は有効にならない様子なので設定できないようにする
/*
        try
        {
            val sensitivityList = ArrayList<String>()
            val cameraInfo = cameraXCameraControl.getCamera2CameraInfo()
            if (cameraInfo != null)
            {
                val selection = cameraInfo.getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE)
                if (selection != null)
                {
                    //  ここで設定可能なISO値を(追加で)入れる
                    if (selection.lower < 100)
                    {
                        sensitivityList.add(selection.lower.toString())
                    }
                    sensitivityList.add("100")
                    sensitivityList.add("200")
                    sensitivityList.add("400")
                    sensitivityList.add("800")
                    if (selection.upper > 800)
                    {
                        sensitivityList.add(selection.upper.toString())
                    }
                    return (sensitivityList)
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        //return (ArrayList())
        return (listOf("100", "200", "400", "800"))
*/
    }

    private fun getAvailableWhiteBalance() : List<String>
    {
        return (listOf("OFF",
            "AUTO",
            "INCANDESCENT",
            "FLUORESCENT",
            "WARM FLUORESCENT",
            "DAYLIGHT",
            "CLOUDY DAYLIGHT",
            "TWILIGHT",
            "SHADE",
            "TRANSFORM_MATRIX",
            "FAST",
            "HIGH_QUALITY"
        ))
    }

    private fun getAvailableMeteringMode() : List<String>
    {
        return (listOf("OFF",
            "ON",
            "FLASH",
            "ALWAYS FLASH",
            "FLASH REDEYE",
            "EXTERNAL FLASH",
        ))
    }

    private fun getAvailablePictureEffect() : List<String>
    {
        return (listOf("OFF", "AE-L and AWB-L", "AE-L", "AWB-L"))
    }

    private fun getAvailableTorchMode() : List<String>
    {
        return (listOf("OFF", "SINGLE", "TORCH"))
    }

    fun decideMeteringMode(value : String) : Int
    {
        return (when (value)
        {
            "OFF" -> CameraCharacteristics.CONTROL_AE_MODE_OFF
            "ON" -> CameraCharacteristics.CONTROL_AE_MODE_ON
            "FLASH" -> CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH
            "ALWAYS FLASH" -> CameraCharacteristics.CONTROL_AE_MODE_ON_ALWAYS_FLASH
            "FLASH REDEYE" -> CameraCharacteristics.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE
            "EXTERNAL FLASH" -> CameraCharacteristics.CONTROL_AE_MODE_ON_EXTERNAL_FLASH
            else -> -1
        })
    }

    fun decideTorchMode(value : String) : Int
    {
        return (when (value)
        {
            "OFF" -> CaptureRequest.FLASH_MODE_OFF
            "SINGLE" -> CaptureRequest.FLASH_MODE_SINGLE
            "TORCH" -> CaptureRequest.FLASH_MODE_TORCH
            else -> -1
        })
    }

    fun decideTakeMode(value : String) : Int
    {
        return (when (value)
        {
            "OFF" -> CameraCharacteristics.CONTROL_MODE_OFF
            "AUTO" -> CameraCharacteristics.CONTROL_MODE_AUTO
            "SCENE" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "OFF_KEEP" -> CameraCharacteristics.CONTROL_MODE_OFF_KEEP_STATE
            "exSCENE" -> CameraCharacteristics.CONTROL_MODE_USE_EXTENDED_SCENE_MODE
            "DISABLED" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "FACE" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "ACTION" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "PORTRAIT" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "LANDSCAPE" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "NIGHT" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "NIGHT_PORTRAIT" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "THEATER" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "BEACH" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "SNOW" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "SUNSET" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "STEADYPHOTO" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "FIREWORKS" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "SPORTS" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "PARTY" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "CANDLELIGHT" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "BARCODE" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "HS_VIDEO" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            "HDR" -> CameraCharacteristics.CONTROL_MODE_USE_SCENE_MODE
            else -> -1
        })
    }

    fun decideScene(value : String) : Int
    {
        return (when (value)
        {
            "DISABLED" -> CameraCharacteristics.CONTROL_SCENE_MODE_DISABLED
            "FACE" -> CameraCharacteristics.CONTROL_SCENE_MODE_FACE_PRIORITY
            "ACTION" -> CameraCharacteristics.CONTROL_SCENE_MODE_ACTION
            "PORTRAIT" -> CameraCharacteristics.CONTROL_SCENE_MODE_PORTRAIT
            "LANDSCAPE" -> CameraCharacteristics.CONTROL_SCENE_MODE_LANDSCAPE
            "NIGHT" -> CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT
            "NIGHT_PORTRAIT" -> CameraCharacteristics.CONTROL_SCENE_MODE_NIGHT_PORTRAIT
            "THEATER" -> CameraCharacteristics.CONTROL_SCENE_MODE_THEATRE
            "BEACH" -> CameraCharacteristics.CONTROL_SCENE_MODE_BEACH
            "SNOW" -> CameraCharacteristics.CONTROL_SCENE_MODE_SNOW
            "SUNSET" -> CameraCharacteristics.CONTROL_SCENE_MODE_SUNSET
            "STEADYPHOTO" -> CameraCharacteristics.CONTROL_SCENE_MODE_STEADYPHOTO
            "FIREWORKS" -> CameraCharacteristics.CONTROL_SCENE_MODE_FIREWORKS
            "SPORTS" -> CameraCharacteristics.CONTROL_SCENE_MODE_SPORTS
            "PARTY" -> CameraCharacteristics.CONTROL_SCENE_MODE_PARTY
            "CANDLELIGHT" -> CameraCharacteristics.CONTROL_SCENE_MODE_CANDLELIGHT
            "BARCODE" -> CameraCharacteristics.CONTROL_SCENE_MODE_BARCODE
            "HS_VIDEO" -> CameraCharacteristics.CONTROL_SCENE_MODE_HIGH_SPEED_VIDEO
            "HDR" -> CameraCharacteristics.CONTROL_SCENE_MODE_HDR
            else -> -1
        })
    }

    fun decideWhiteBalance(value: String) : Int
    {
        return (when (value)
        {
            "OFF" -> CameraCharacteristics.CONTROL_AWB_MODE_OFF
            "AUTO" -> CameraCharacteristics.CONTROL_AWB_MODE_AUTO
            "INCANDESCENT" -> CameraCharacteristics.CONTROL_AWB_MODE_INCANDESCENT
            "FLUORESCENT" -> CameraCharacteristics.CONTROL_AWB_MODE_FLUORESCENT
            "WARM FLUORESCENT" -> CameraCharacteristics.CONTROL_AWB_MODE_WARM_FLUORESCENT
            "DAYLIGHT" -> CameraCharacteristics.CONTROL_AWB_MODE_DAYLIGHT
            "CLOUDY DAYLIGHT" -> CameraCharacteristics.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT
            "TWILIGHT" -> CameraCharacteristics.CONTROL_AWB_MODE_TWILIGHT
            "SHADE" -> CameraCharacteristics.CONTROL_AWB_MODE_SHADE
            "TRANSFORM_MATRIX" -> CameraCharacteristics.CONTROL_AWB_MODE_OFF
            "FAST" -> CameraCharacteristics.CONTROL_AWB_MODE_OFF
            "HIGH_QUALITY" -> CameraCharacteristics.CONTROL_AWB_MODE_OFF
            else -> -1
        })
    }

    fun decideColorCorrection(value: String) : Int
    {
        return (when (value)
        {
            "TRANSFORM_MATRIX" -> CameraCharacteristics.COLOR_CORRECTION_MODE_TRANSFORM_MATRIX
            "FAST" -> CameraCharacteristics.COLOR_CORRECTION_MODE_FAST
            "HIGH_QUALITY" -> CameraCharacteristics.COLOR_CORRECTION_MODE_HIGH_QUALITY
            else -> -1
        })
    }

    fun decideAeLock(value: String) : Boolean
    {
        return (when (value)
        {
            "AE-L and AWB-L" -> true
            "AE-L" -> true
            else -> false
        })
    }

    fun decideAwbLock(value: String) : Boolean
    {
        return (when (value)
        {
            "AE-L and AWB-L" -> true
            "AWB-L" -> true
            else -> false
        })
    }

    fun decideSensorSensitivity(value: String) : Int
    {
        try
        {
            return (value.toInt())
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (-1)
    }

    fun decideExpRevIndex(value: String) : Int
    {
        try
        {
            val targetValue = value.toDouble()
            Log.v(TAG, " ---------- ExpRev : $targetValue")
            val exposureState = cameraXCameraControl.getExposureState()
            if ((exposureState != null)&&(exposureState.isExposureCompensationSupported))
            {
                val step = exposureState.exposureCompensationStep
                val range = exposureState.exposureCompensationRange
                for (count in range.lower .. range.upper)
                {
                    val checkValue = count.toDouble() * step.toDouble()
                    if (abs(checkValue - targetValue) < step.toDouble())
                    {
                        Log.v(TAG, " ============ ExpRev : $targetValue ($checkValue) -> $count [step:$step]")
                        return (count)
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (0)
    }

}