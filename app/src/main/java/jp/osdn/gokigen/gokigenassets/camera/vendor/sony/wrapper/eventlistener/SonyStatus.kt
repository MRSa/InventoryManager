package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.eventlistener

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.*
import jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.ISonyCameraApi
import org.json.JSONObject
import java.lang.Exception

class SonyStatus(jsonObject : JSONObject) : ICameraStatus, ICameraChangeListener, ISonyStatusReceiver {
    private val statusCandidates = SonyStatusCandidates()

    private lateinit var focusControl: IFocusingControl

    private var currentExposureMode = ""
    private var currentShootMode = ""
    private var currentExposureCompensation = ""
    private var currentFlashMode = ""
    private var currentFNumber = ""
    private var currentFocusMode = ""
    private var currentIsoSpeedRate = ""
    private var currentShutterSpeed = ""
    private var currentWhiteBalanceMode = ""
    private var currentCameraStatus = ""
    private var currentPictureEffect = ""
    private var currentMeteringMode = ""
    private var currentRemainBattery = ""
    private var currentRemainBatteryPercent = 0.0
    private var currentFocusStatus = ""
    private var currentDriveMode = ""


    fun setCameraApi(sonyCameraApi: ISonyCameraApi) {
        statusCandidates.setCameraApi(sonyCameraApi)
    }

    fun setFocusControl(focusControl: IFocusingControl) {
        this.focusControl = focusControl
    }

    private var jsonObject: JSONObject

    init {
        this.jsonObject = jsonObject
    }

    override fun setStatus(key: String, value: String) {
        when (key) {
            ICameraStatus.TAKE_MODE -> statusCandidates.setTakeMode(value)
            ICameraStatus.SHUTTER_SPEED -> statusCandidates.setShutterSpeed(value)
            ICameraStatus.APERTURE -> statusCandidates.setAperture(value)
            ICameraStatus.EXPREV -> statusCandidates.setExpRev(value)
            ICameraStatus.CAPTURE_MODE -> statusCandidates.setCaptureMode(value)
            ICameraStatus.ISO_SENSITIVITY -> statusCandidates.setIsoSensitivity(value)
            ICameraStatus.WHITE_BALANCE -> statusCandidates.setWhiteBalance(value)
            ICameraStatus.AE -> statusCandidates.setMeteringMode(value)
            ICameraStatus.EFFECT -> statusCandidates.setPictureEffect(value)
            ICameraStatus.BATTERY -> statusCandidates.setRemainBattery(value)
            ICameraStatus.TORCH_MODE -> statusCandidates.setTorchMode(value)
            ICameraStatus.FOCUS_STATUS -> setFocusStatus(value)
            ICameraStatus.DRIVE_MODE -> setDriveMode(value)
            else -> {
                return
            }
        }
    }

    override fun getStatusList(key: String): List<String?> {
        val statusList: List<String?> = (when (key) {
            ICameraStatus.TAKE_MODE -> statusCandidates.getAvailableTakeMode()
            ICameraStatus.SHUTTER_SPEED -> statusCandidates.getAvailableShutterSpeed()
            ICameraStatus.APERTURE -> statusCandidates.getAvailableAperture()
            ICameraStatus.EXPREV -> statusCandidates.getAvailableExpRev()
            ICameraStatus.CAPTURE_MODE -> statusCandidates.getAvailableCaptureMode()
            ICameraStatus.ISO_SENSITIVITY -> statusCandidates.getAvailableIsoSensitivity()
            ICameraStatus.WHITE_BALANCE -> statusCandidates.getAvailableWhiteBalance()
            ICameraStatus.AE -> statusCandidates.getAvailableMeteringMode()
            ICameraStatus.EFFECT -> statusCandidates.getAvailablePictureEffect()
            ICameraStatus.BATTERY -> statusCandidates.getAvailableRemainBattery()
            ICameraStatus.TORCH_MODE -> statusCandidates.getAvailableTorchMode()
            ICameraStatus.FOCUS_STATUS -> getAvailableFocusStatus()
            ICameraStatus.DRIVE_MODE -> getAvailableDriveMode()
            else -> {
                ArrayList()
            }
        })
        return (statusList)
    }

    override fun getStatus(key: String): String {
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
            ICameraStatus.FOCUS_STATUS -> getFocusStatus()
            ICameraStatus.DRIVE_MODE -> getDriveMode()
            else -> ""
        })
    }

    override fun getStatusColor(key: String): Int {
        return (when (key) {
            ICameraStatus.BATTERY -> getRemainBatteryColor()
            ICameraStatus.FOCUS_STATUS -> getFocusStatusColor()
            else -> Color.WHITE
        })
    }

    private fun getAvailableFocusStatus(): List<String?> {
/*
        if (currentFocusStatus == "Not Focusing")
        {
            return (listOf("Do Focus", "Cancel Focus"))
        }
        return (listOf("Cancel Focus"))
*/
        return (listOf("Do Focus", "Cancel Focus"))
    }

    private fun getAvailableDriveMode(): List<String?> {
/*
        if (currentFocusStatus == "Not Focusing")
        {
            return (listOf("Do Focus", "Cancel Focus"))
        }
        return (listOf("Cancel Focus"))
*/
        return (listOf("Single", "Continuous", "Spd Priority Cont.", "Burst", "MotionShot"))
    }

    private fun setFocusStatus(value: String) {
        try {
            if (!::focusControl.isInitialized) {
                Log.v(TAG, " FOCUS CONTROL IS NOT INITIALIZED.")
                return
            }
            when (value) {
                "Do Focus" -> focusControl.halfPressShutter(true)
                else -> {
                    focusControl.unlockAutoFocus()
                    focusControl.halfPressShutter(false)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setDriveMode(value: String) {
        try {
            if (!::focusControl.isInitialized) {
                Log.v(TAG, " FOCUS CONTROL IS NOT INITIALIZED.")
                return
            }
            when (value) {
                "Single" -> nop()
                "Continuous" -> nop()
                "Spd Priority Cont." -> nop()
                "Burst" -> nop()
                "MotionShot" -> nop()
                else -> nop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun nop()
    {

    }

    private fun getRemainBatteryColor() : Int
    {
        var color = Color.WHITE
        try
        {
            if (currentRemainBatteryPercent <= 30.1)
            {
                color = Color.RED
            }
            else if (currentRemainBatteryPercent <= 50.1)
            {
                color = Color.YELLOW
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return (color)
    }

    private fun getFocusStatusColor() : Int
    {
        var color = Color.WHITE
        try
        {
            color = when (currentFocusStatus)
            {
                "Not Focusing" -> Color.WHITE
                "Focusing" -> Color.LTGRAY
                "Focused" -> Color.GREEN
                "Failed" -> Color.RED
                else -> Color.WHITE
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (color)
    }

    private fun getTakeMode() : String
    {
        return (currentExposureMode)
    }

    private fun getShutterSpeed() : String
    {
        return (currentShutterSpeed)
    }

    private fun getAperture() : String
    {
        if (currentFNumber.length > 1)
        {
            return ("F$currentFNumber")
        }
        return (currentFNumber)
    }

    private fun getExpRev() : String
    {
        return (currentExposureCompensation)
    }

    private fun getCaptureMode() : String
    {
        return (currentShootMode)
    }

    private fun getIsoSensitivity() : String
    {
        if (currentIsoSpeedRate.length > 1)
        {
            return ("ISO:$currentIsoSpeedRate")
        }
        return (currentIsoSpeedRate)
    }

    private fun getWhiteBalance() : String
    {
        return (currentWhiteBalanceMode)
    }

    private fun getMeteringMode() : String
    {
        return (currentMeteringMode)
    }

    private fun getPictureEffect() : String
    {
        return (currentPictureEffect)
    }

    private fun getRemainBattery() : String
    {
        return (currentRemainBattery)
    }

    private fun getTorchMode() : String
    {
        return (currentCameraStatus)
    }

    private fun getFocusStatus() : String
    {
        var status = ""
        try
        {
            status = when (currentFocusStatus)
            {
                "Not Focusing" -> ""
                "Focusing" -> "Focusing"
                "Focused" -> "Focused"
                "Failed" -> "Failed"
                else -> ""
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (status)
    }

    private fun getDriveMode() : String
    {
        return (currentDriveMode)
    }

    override fun onApiListModified(apis: List<String?>?)
    {
        //TODO("Not yet implemented")
    }

    override fun onCameraStatusChanged(status: String?)
    {
        //TODO("Not yet implemented")
    }

    override fun onLiveviewStatusChanged(status: Boolean)
    {
        //TODO("Not yet implemented")
    }

    override fun onShootModeChanged(shootMode: String?)
    {
        //TODO("Not yet implemented")
    }

    override fun onZoomPositionChanged(zoomPosition: Int)
    {
        //TODO("Not yet implemented")
    }

    override fun onStorageIdChanged(storageId: String?)
    {
        //TODO("Not yet implemented")
    }

    override fun onFocusStatusChanged(focusStatus: String?)
    {
        //TODO("Not yet implemented")
    }

    override fun onDriveModeChanged(driveMode: String?) {
        // TODO("Not yet implemented")
    }

    override fun onResponseError()
    {
        //TODO("Not yet implemented")
    }

    override fun updateStatus(jsonObject: JSONObject)
    {
        this.jsonObject = jsonObject

        currentCameraStatus = parseEventStatus(currentCameraStatus, jsonObject, "cameraStatus", "cameraStatus", 1)
        currentExposureMode = parseEventStatus(currentExposureMode, jsonObject, "exposureMode", "currentExposureMode", 18)
        currentShootMode = parseEventStatus(currentShootMode, jsonObject, "shootMode", "currentShootMode",21)
        currentExposureCompensation = parseExposureCompensation(currentExposureCompensation, jsonObject)

        currentFlashMode = parseEventStatus(currentFlashMode, jsonObject, "flashMode", "currentFlashMode",26)
        currentFNumber = parseEventStatus(currentFNumber, jsonObject, "fNumber", "currentFNumber",27)
        currentFocusMode = parseEventStatus(currentFocusMode, jsonObject, "focusMode", "currentFocusMode",28)

        currentIsoSpeedRate = parseEventStatus(currentIsoSpeedRate, jsonObject, "isoSpeedRate", "currentIsoSpeedRate", 29)
        currentShutterSpeed = parseEventStatus(currentShutterSpeed, jsonObject, "shutterSpeed", "currentShutterSpeed", 32)
        currentWhiteBalanceMode = parseEventStatus(currentWhiteBalanceMode, jsonObject, "whiteBalance", "currentWhiteBalanceMode", 33)
        currentRemainBattery = parseBatteryInfo(currentRemainBattery, jsonObject)
        currentFocusStatus = parseEventStatus(currentFocusStatus, jsonObject, "focusStatus", "focusStatus", 35)
        currentDriveMode = parseEventStatus(currentDriveMode, jsonObject, "contShootingMode", "contShootingMode", 38)
    }

    private fun parseExposureCompensation(currentStatus: String, replyJson: JSONObject) : String
    {
        val indexOfCameraStatus = 25
        var eventStatus = currentStatus
        try
        {
            val resultsObj = replyJson.getJSONArray("result")
            if ((resultsObj.length() > indexOfCameraStatus)&&(!resultsObj.isNull(indexOfCameraStatus)))
            {
                val cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus)
                val type = cameraStatusObj.getString("type")
                if ("exposureCompensation" == type)
                {
                    val currentIndex = cameraStatusObj.getInt("currentExposureCompensation")
                    val currentStep = cameraStatusObj.getInt("stepIndexOfExposureCompensation")
                    val showValue = currentIndex.toDouble() / if (currentStep == 2) { 2.0 } else { 3.0 }
                    eventStatus = String.format("%+1.1f", showValue)
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index ($indexOfCameraStatus) $type : $cameraStatusObj")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (eventStatus)
    }

    private fun parseBatteryInfo(currentStatus: String, replyJson: JSONObject) : String
    {
        val indexOfCameraStatus = 56
        var eventStatus = currentStatus
        try
        {
            val resultsObj = replyJson.getJSONArray("result")
            if ((resultsObj.length() > indexOfCameraStatus)&&(!resultsObj.isNull(indexOfCameraStatus)))
            {
                val cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus)
                val batteryInfoObj = cameraStatusObj.getJSONArray("batteryInfo").getJSONObject(0)
                val type = cameraStatusObj.getString("type")
                if ("batteryInfo" == type)
                {
                    //Log.v(TAG, "  =====> $batteryInfoObj")
                    //eventStatus = cameraStatusObj.getString(key)
                    val numerator = batteryInfoObj.getInt("levelNumer")
                    val denominator =  batteryInfoObj.getInt("levelDenom")
                    if ((numerator <= 0)||(denominator <= 0))
                    {
                        eventStatus = "Batt.: ???%"
                        currentRemainBatteryPercent = 0.0
                    }
                    else
                    {
                        //currentRemainBatteryPercent = denominator.toDouble() / numerator.toDouble()  * 100.0
                        currentRemainBatteryPercent = numerator.toDouble() / denominator.toDouble()  * 100.0
                        eventStatus = "Batt.: " + String.format("%2.0f", currentRemainBatteryPercent) + "%"
                    }
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index ($indexOfCameraStatus:) $type : $cameraStatusObj")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (eventStatus)
    }

    private fun parseEventStatus(currentStatus: String, replyJson: JSONObject, item: String, key: String, indexOfCameraStatus: Int): String
    {
        var eventStatus = currentStatus
        try
        {
            val resultsObj = replyJson.getJSONArray("result")
            if ((resultsObj.length() > indexOfCameraStatus)&&(!resultsObj.isNull(indexOfCameraStatus)))
            {
                val cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus)
                val type = cameraStatusObj.getString("type")
                if (item == type)
                {
                    eventStatus = cameraStatusObj.getString(key)
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index ($indexOfCameraStatus: $key) $type : $cameraStatusObj")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (eventStatus)
    }

    companion object
    {
        private val TAG = SonyStatus::class.java.simpleName
    }
}
