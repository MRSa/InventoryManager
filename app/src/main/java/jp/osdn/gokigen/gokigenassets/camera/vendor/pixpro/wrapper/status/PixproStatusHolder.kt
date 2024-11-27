package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.base.PixproCommandOnlyCallback
import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.messages.specific.*
import java.lang.Exception
import java.util.*

class PixproStatusHolder
{
    companion object
    {
        private val TAG = PixproStatusHolder::class.java.simpleName
    }

    private var statusConvert = PixproStatusConvert(this)
    private lateinit var commandPublisher : IPixproCommandPublisher

    private var currentTakeMode = ""
    private var currentFlashMode = ""
    private var currentWhiteBalance = ""
    private var currentIsoSensitivity = ""
    private var currentExposureCompensation = ""
    private var currentShutterSpeed = ""
    private var currentRemainBattery = ""
    private var currentCaptureMode = ""
    private var currentImageSize = ""
    private var currentMovieSize = ""

    fun setCommandPublisher(commandPublisher : IPixproCommandPublisher)
    {
        this.commandPublisher = commandPublisher
    }

    fun updateValue(key: String, value: String)
    {
        when (key)
        {
            ICameraStatus.TAKE_MODE -> { currentTakeMode = value }
            ICameraStatus.SHUTTER_SPEED -> { currentShutterSpeed = value }
            ICameraStatus.EXPREV -> { currentExposureCompensation = value }
            ICameraStatus.ISO_SENSITIVITY -> { currentIsoSensitivity = value }
            ICameraStatus.WHITE_BALANCE -> { currentWhiteBalance = value }
            ICameraStatus.TORCH_MODE -> { currentFlashMode = value }
            ICameraStatus.BATTERY -> { currentRemainBattery = value }
            ICameraStatus.CAPTURE_MODE -> { currentCaptureMode = value }
            ICameraStatus.IMAGE_SIZE -> { currentImageSize = value }
            ICameraStatus.MOVIE_SIZE -> { currentMovieSize = value }

            //ICameraStatus.APERTURE -> { }
            //ICameraStatus.AE -> { }
            //ICameraStatus.EFFECT -> { }
            //ICameraStatus.FOCUS_STATUS -> { }
            else -> { }
        }
    }

    fun getAvailableItemList(key: String?): List<String?>
    {
        try
        {
            return (when (key) {
                ICameraStatus.TAKE_MODE -> statusConvert.getAvailableTakeMode()
                ICameraStatus.SHUTTER_SPEED -> statusConvert.getAvailableShutterSpeed()
                ICameraStatus.APERTURE -> statusConvert.getAvailableAperture()
                ICameraStatus.EXPREV -> statusConvert.getAvailableExpRev()
                ICameraStatus.CAPTURE_MODE -> statusConvert.getAvailableCaptureMode()
                ICameraStatus.ISO_SENSITIVITY -> statusConvert.getAvailableIsoSensitivity()
                ICameraStatus.WHITE_BALANCE -> statusConvert.getAvailableWhiteBalance()
                ICameraStatus.AE -> statusConvert.getAvailableMeteringMode(currentTakeMode)
                ICameraStatus.EFFECT -> statusConvert.getAvailablePictureEffect()
                ICameraStatus.TORCH_MODE -> statusConvert.getAvailableTorchMode()
                //ICameraStatus.BATTERY -> statusConvert.getAvailableRemainBattery()
                //ICameraStatus.FOCUS_STATUS -> statusConvert.getAvailableFocusStatus()
                else -> ArrayList()
            })
            //Log.v(TAG, " ----- getAvailableItemList($key) ")
            //sendCamGetSettingCmd("colormode")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (ArrayList())
    }

    fun getItemStatusColor(key: String): Int
    {
        return (when (key) {
            ICameraStatus.BATTERY -> getRemainBatteryColor()
            ICameraStatus.CAPTURE_MODE -> getCaptureModeColor()
            else -> Color.WHITE
        })
    }

    fun getItemStatus(orgKey: String): String
    {
        return (when (orgKey) {
            ICameraStatus.TAKE_MODE -> getTakeMode()
            ICameraStatus.SHUTTER_SPEED -> getShutterSpeed()
            //ICameraStatus.APERTURE -> getAperture()
            ICameraStatus.EXPREV -> getExpRev()
            ICameraStatus.CAPTURE_MODE -> getCaptureMode()
            ICameraStatus.ISO_SENSITIVITY -> getIsoSensitivity()
            ICameraStatus.WHITE_BALANCE -> getWhiteBalance()
            ICameraStatus.AE -> getMeteringMode()
            ICameraStatus.EFFECT -> getPictureEffect()
            ICameraStatus.BATTERY -> getRemainBattery()
            ICameraStatus.TORCH_MODE -> getTorchMode()
            //ICameraStatus.FOCUS_STATUS -> getfocusStatus()
            else -> ""
        })
    }

    private fun getCaptureModeColor() : Int
    {
        if (currentCaptureMode.length > 1)
        {
            return (Color.RED)
        }
        return (Color.WHITE)
    }

    private fun getRemainBatteryColor() : Int
    {
        return (Color.WHITE)
    }

    private fun getTakeMode() : String
    {
        return (currentTakeMode)
    }

    private fun getShutterSpeed() : String
    {
        return (currentShutterSpeed)
    }

    private fun getExpRev() : String
    {
        return (currentExposureCompensation)
    }

    private fun getIsoSensitivity() : String
    {
        return ("ISO:$currentIsoSensitivity")
    }

    private fun getRemainBattery() : String
    {
        return (currentRemainBattery)
    }

    private fun getWhiteBalance() : String
    {
        return ("WB: $currentWhiteBalance")
    }

    private fun getMeteringMode() : String
    {
        return (when (currentTakeMode) {
            "Video" -> { currentMovieSize }
            else -> { currentImageSize }
        })
        // return ("Size")
    }

    private fun getPictureEffect() : String
    {
        return ("Zoom")
    }

    private fun getCaptureMode() : String
    {
        return (currentCaptureMode)
    }

    private fun getTorchMode() : String
    {
        return ("Flash: $currentFlashMode")
    }

    fun setItemStatus(key: String, value: String)
    {
        Log.v(TAG, " setItemStatus(key:$key, value:$value)")
        try
        {
            when (key) {
                ICameraStatus.TAKE_MODE -> setTakeMode(value)
                ICameraStatus.SHUTTER_SPEED -> setShutterSpeed(value)
                //ICameraStatus.APERTURE -> setAperture(value)
                ICameraStatus.EXPREV -> setExpRev(value)
                ICameraStatus.CAPTURE_MODE -> updateValue(key, value)
                ICameraStatus.ISO_SENSITIVITY -> setIsoSensitivity(value)
                ICameraStatus.WHITE_BALANCE -> setWhiteBalance(value)
                ICameraStatus.AE -> setMeteringMode(value)
                ICameraStatus.EFFECT -> setPictureEffect(value)
                ICameraStatus.TORCH_MODE -> setTorchMode(value)
                //ICameraStatus.BATTERY -> setRemainBattery(value)
                //ICameraStatus.FOCUS_STATUS -> setfocusStatus(value)
                else -> return
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setTakeMode(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setTakeMode($value)")
            when (value)
            {
                "P" -> commandPublisher.enqueueCommand(PixproChangeMode(PixproCommandOnlyCallback(), 0x01))
                "M" -> commandPublisher.enqueueCommand(PixproChangeMode(PixproCommandOnlyCallback(), 0x08))
                "ASCN" -> commandPublisher.enqueueCommand(PixproChangeMode(PixproCommandOnlyCallback(), 0x20))
                "Video" -> commandPublisher.enqueueCommand(PixproChangeVideoMode(PixproCommandOnlyCallback()))
                "Cont. Shot" -> commandPublisher.enqueueCommand(PixproChangeMode(PixproCommandOnlyCallback(), 0x00, 0x08))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setShutterSpeed(value: String)
    {
        try
        {

            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setShutterSpeed($value)")
            when (value)
            {
                "1/2000" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x01))
                "1/1600" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x02))
                "1/1200" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x03))
                "1/1000" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x04))
                "1/800" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x05))
                "1/600" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x06))
                "1/500" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x07))
                "1/400" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x08))
                "1/320" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x09))
                "1/250" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0a))
                "1/200" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0b))
                "1/160" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0c))
                "1/125" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0d))
                "1/100" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0e))
                "1/80" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x0f))
                "1/60" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x10))
                "1/50" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x11))
                "1/40" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x12))
                "1/30" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x13))
                "1/25" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x14))
                "1/20" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x15))
                "1/15" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x16))
                "1/13" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x17))
                "1/10" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x18))
                "1/8" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x19))
                "1/6" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1a))
                "1/5" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1b))
                "1/4" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1c))
                "1/3" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1d))
                "1/2.5" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1e))
                "1/2" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x1f))
                "1/1.6" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x20))
                "1/1.3" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x21))
                "1s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x22))
                "1.3s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x23))
                "1.5s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x24))
                "2s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x25))
                "2.5s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x26))
                "3s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x27))
                "4s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x28))
                "5s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x29))
                "6s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2a))
                "8s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2b))
                "10s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2c))
                "13s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2d))
                "15s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2e))
                "20s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x2f))
                "25s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x30))
                "30s" -> commandPublisher.enqueueCommand(PixproChangeShutterSpeed(PixproCommandOnlyCallback(), 0x31))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }


    private fun setMeteringMode(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setMeteringMode($value)")
            when (value)
            {
                "4608x3456" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00010000))  // 4608x3456
                "4608x3072" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00004000))  // 4608x3072
                "4608x2592" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00001000))  // 4608x2592
                "3648x2736" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00000400))  // 3648x2736
                "2592x1944" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00000020))  // 2592x1944
                "2048x1536" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00000008))  // 2048x1536
                "1920x1080" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00000004))  // 1920x1080
                "640x480" -> commandPublisher.enqueueCommand(PixproChangeImageSize(PixproCommandOnlyCallback(), 0x00000001))  // 640x480
                "1280x720 30p" -> commandPublisher.enqueueCommand(PixproChangeVideoSize(PixproCommandOnlyCallback(), 0x00040000))  // 1280x720 30p
                "640x480 30p" -> commandPublisher.enqueueCommand(PixproChangeVideoSize(PixproCommandOnlyCallback(), 0x00000040))  // 640x480 30p
                "640x480 120p" -> commandPublisher.enqueueCommand(PixproChangeVideoSize(PixproCommandOnlyCallback(), 0x00000100))  // 640x480  120p
                "1920×1080 30p" -> commandPublisher.enqueueCommand(PixproChangeVideoSize(PixproCommandOnlyCallback(), 0x00400000))  // 1920x1080 30p
                "1280x720 60p" -> commandPublisher.enqueueCommand(PixproChangeVideoSize(PixproCommandOnlyCallback(), 0x00080000))  // 1280x720  60p
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }


    private fun setIsoSensitivity(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setIsoSensitivity($value)")
            when (value)
            {
                "AUTO" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x00))
                "100" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x01))
                "200" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x02))
                "400" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x03))
                "800" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x04))
                "1600" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x05))
                "3200" -> commandPublisher.enqueueCommand(PixproChangeIsoSensitivity(PixproCommandOnlyCallback(), 0x06))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setExpRev(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setExpRev($value)")
            when (value)
            {
                "-3.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x00))
                "-2.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x01))
                "-2.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x02))
                "-2.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x03))
                "-1.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x04))
                "-1.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x05))
                "-1.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x06))
                "-0.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x07))
                "-0.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x08))
                "0.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x09))
                "+0.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0a))
                "+0.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0b))
                "+1.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0c))
                "+1.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0d))
                "+1.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0e))
                "+2.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x0f))
                "+2.3" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x10))
                "+2.7" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x11))
                "+3.0" -> commandPublisher.enqueueCommand(PixproChangeExposureCompensation(PixproCommandOnlyCallback(), 0x12))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setWhiteBalance(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setWhiteBalance($value)")
            when (value)
            {
                "AUTO" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x01))
                "Daylight" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x02))
                "Cloudy" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x04))
                "Fluorescent" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x10))
                "Fluorescent CWF" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x20)) //
                "Incandescent" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x80))    //
                "Unknown" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x40))         //
                "Other" -> commandPublisher.enqueueCommand(PixproWhiteBalance(PixproCommandOnlyCallback(), 0x08))           // 不明...
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun setPictureEffect(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setPictureEffect($value)")
            when (value)
            {
                "(+) Zoom In" -> commandPublisher.enqueueCommand(PixproExecuteZoom(PixproCommandOnlyCallback(), 1))
                "(-) Zoom Out" -> commandPublisher.enqueueCommand(PixproExecuteZoom(PixproCommandOnlyCallback(), -1))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }


    private fun setTorchMode(value: String)
    {
        try
        {
            if (!::commandPublisher.isInitialized)
            {
                // 未初期化の場合はコマンドを送らない
                return
            }
            Log.v(TAG, " setTorchMode($value)")
            when (value)
            {
                "OFF" -> commandPublisher.enqueueCommand(PixproFlashOff(PixproCommandOnlyCallback()))
                "ON" -> commandPublisher.enqueueCommand(PixproFlashOn(PixproCommandOnlyCallback()))
                "AUTO" -> commandPublisher.enqueueCommand(PixproFlashAuto(PixproCommandOnlyCallback()))
                else -> { }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
