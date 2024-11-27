package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusUpdateNotify
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusWatcher
import jp.osdn.gokigen.gokigenassets.liveview.message.IMessageDrawer
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import org.json.JSONObject

class ThetaCameraStatusWatcher(private val sessionIdProvider: IThetaSessionIdProvider, private val captureModeReceiver : ICaptureModeReceiver, private val executeUrl : String = "http://192.168.1.1") : ICameraStatusWatcher, IThetaStatusHolder, ICameraStatus
{
    private val httpClient = SimpleHttpClient()
    private val statusListHolder = ThetaCameraStatusListHolder(sessionIdProvider, executeUrl)
    private var whileFetching = false
    private var currentIsoSensitivity : Int = 0
    private var currentBatteryLevel : Double = 0.0
    private var currentAperture : Double = 0.0
    private var currentShutterSpeed : Double = 0.0
    private var currentExposureCompensation : Double = 0.0
    private var currentCaptureMode : String = ""
    private var currentExposureProgram : String = ""
    private var currentCaptureStatus : String = ""
    private var currentWhiteBalance : String = ""
    private var currentFilter : String = ""

    private var showInformation: IMessageDrawer? = null

    override fun startStatusWatch(indicator : IMessageDrawer?, notifier: ICameraStatusUpdateNotify?)
    {
        if (whileFetching)
        {
            Log.v(TAG, "startStatusWatch() already starting.")
            return
        }
        showInformation = indicator
        whileFetching = true

        try
        {
            setMessage(IMessageDrawer.MessageArea.UPLEFT, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.UPRIGHT, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.CENTER, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.LOWLEFT, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.LOWRIGHT, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.UPCENTER, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.LOWCENTER, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.CENTERLEFT, Color.WHITE, "")
            setMessage(IMessageDrawer.MessageArea.CENTERRIGHT, Color.WHITE, "")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }

        try
        {
            val thread = Thread {
                try
                {
                    val getOptionsUrl = "$executeUrl/osc/commands/execute"
                    val getStateUrl = "$executeUrl/osc/state"

                    val postDataCaptureMode = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"captureMode\"] }}" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"captureMode\" ] }}"
                    val postDataImage = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }}" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }}"
                    val postDataVideo = if (sessionIdProvider.sessionId.isEmpty()) "{\"name\":\"camera.getOptions\",\"parameters\":{\"timeout\":0, \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"whiteBalance\"] }}" else "{\"name\":\"camera.getOptions\",\"parameters\":{\"sessionId\": \"" + sessionIdProvider.sessionId + "\", \"optionNames\" : [ \"aperture\",\"captureMode\",\"exposureCompensation\",\"exposureProgram\",\"iso\",\"shutterSpeed\",\"_filter\",\"whiteBalance\"] }}"
                    Log.v(TAG, " >>>>> START STATUS WATCH : $getOptionsUrl")
                    while (whileFetching)
                    {
                        val response0: String? = httpClient.httpPostWithHeader(getOptionsUrl, postDataCaptureMode, null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response0.isNullOrEmpty()))
                        {
                            // 設定データ受信、解析する
                            checkStatus0(response0)
                        }
                        val postData = if (currentCaptureMode != "image") { postDataVideo } else { postDataImage }
                        val response1: String? = httpClient.httpPostWithHeader(getOptionsUrl, postData, null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response1.isNullOrEmpty()))
                        {
                            // 設定データ受信、解析する
                            checkStatus1(response1)
                        }
                        val response2: String? = httpClient.httpPostWithHeader(getStateUrl, "", null, "application/json;charset=utf-8", timeoutMs)
                        if (!(response2.isNullOrEmpty()))
                        {
                            // ステータスデータ受信、解析する
                            checkStatus2(response2)
                        }
                        try
                        {
                            // 表示を更新する
                            updateMessage()

                            // ちょっと休む
                            Thread.sleep(loopWaitMs)
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkStatus0(response: String)
    {
        try
        {
            //Log.v(TAG, " STATUS0 : $response")
            val stateObject = JSONObject(response).getJSONObject("results").getJSONObject("options")
            try
            {
                val captureMode = stateObject.getString(THETA_CAPTURE_MODE)
                if (captureMode != currentCaptureMode)
                {
                    Log.v(TAG, " CapMode : $currentCaptureMode -> $captureMode")
                    currentCaptureMode = captureMode
                    captureModeReceiver.changedCaptureMode(captureMode)
                    setMessage(IMessageDrawer.MessageArea.UPLEFT, Color.WHITE, captureMode)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
    }

    private fun checkStatus1(response: String)
    {
        try
        {
            //Log.v(TAG, " STATUS1 : $response")
            val stateObject = JSONObject(response).getJSONObject("results").getJSONObject("options")
            try
            {
                val exposureCompensation = stateObject.getDouble(THETA_EXPOSURE_COMPENSATION)
                if (exposureCompensation != currentExposureCompensation)
                {
                    Log.v(TAG, " XV : $currentExposureCompensation => $exposureCompensation")
                    currentExposureCompensation = exposureCompensation
                    if (currentExposureCompensation == 0.0)
                    {
                        // 補正なしの時には数値を表示しない
                        setMessage(IMessageDrawer.MessageArea.CENTERLEFT, Color.WHITE, "")
                    }
                    else
                    {
                        setMessage(IMessageDrawer.MessageArea.CENTERLEFT, Color.WHITE, String.format("%1.1f", currentExposureCompensation))
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val whiteBalance = stateObject.getString(THETA_WHITE_BALANCE)
                if (whiteBalance != currentWhiteBalance)
                {
                    Log.v(TAG, " WB : $currentWhiteBalance => $whiteBalance")
                    currentWhiteBalance = whiteBalance
                    setMessage(IMessageDrawer.MessageArea.LOWLEFT, Color.WHITE, "WB : $currentWhiteBalance")
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val exposureProgram = stateObject.getString(THETA_EXPOSURE_PROGRAM)
                if (exposureProgram != currentExposureProgram)
                {
                    Log.v(TAG, " ExpPrg : $currentExposureProgram -> $exposureProgram")
                    currentExposureProgram = exposureProgram

                    var mode = ""
                    when (currentExposureProgram) {
                        "1" -> mode = "Manual"
                        "2" -> mode = "Normal"
                        "3" -> mode = "Aperture"
                        "4" -> mode = "Shutter"
                        "9" -> mode = "ISO"
                    }
                    setMessage(IMessageDrawer.MessageArea.UPRIGHT, Color.WHITE, "Exposure Program : $mode")
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            if (currentCaptureMode == "image")
            {
                try
                {
                    val filterValue = stateObject.getString(THETA_FILTER)
                    if (filterValue != currentFilter)
                    {
                        Log.v(TAG, " FILTER : $currentFilter -> $filterValue")
                        currentFilter = filterValue
                        setMessage(IMessageDrawer.MessageArea.UPRIGHT, Color.WHITE, "FILTER : $currentFilter")
                    }
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            else
            {
                currentFilter = ""
            }

            try
            {
                val isoSensitivity = stateObject.getInt(THETA_ISO_SENSITIVITY)
                if (isoSensitivity != currentIsoSensitivity)
                {
                    Log.v(TAG, " ISO : $currentIsoSensitivity -> $isoSensitivity")
                    currentIsoSensitivity = isoSensitivity
                    if (currentIsoSensitivity == 0)
                    {
                        setMessage(IMessageDrawer.MessageArea.LOWRIGHT, Color.WHITE, "ISO : AUTO")
                    }
                    else
                    {
                        setMessage(IMessageDrawer.MessageArea.LOWRIGHT, Color.WHITE, "ISO : $currentIsoSensitivity")
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val aperture = stateObject.getDouble(THETA_APERTURE)
                if (aperture != currentAperture)
                {
                    Log.v(TAG, " A : $currentAperture -> $aperture")
                    currentAperture = aperture
/*
                    if ((currentExposureProgram == "1")||(currentExposureProgram == "3"))
                    {
                        if (currentAperture == 0.0)
                        {
                            setMessage(IMessageDrawer.MessageArea.CENTERRIGHT, Color.WHITE, "F:auto")
                        }
                        else
                        {
                            setMessage(IMessageDrawer.MessageArea.CENTERRIGHT, Color.WHITE, "F$currentAperture")
                        }
                    }
*/
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

            try
            {
                val shutterSpeed = stateObject.getDouble(THETA_SHUTTER_SPEED)
                if (shutterSpeed != currentShutterSpeed)
                {
                    Log.v(TAG, " SS : $currentShutterSpeed -> $shutterSpeed")
                    currentShutterSpeed = shutterSpeed
                    if (currentShutterSpeed == 0.0)
                    {
                        setMessage(IMessageDrawer.MessageArea.LOWCENTER, Color.WHITE, "")
                    }
                    else
                    {
                        setMessage(IMessageDrawer.MessageArea.LOWCENTER, Color.WHITE, convertShutterSpeedString(currentShutterSpeed))
                    }
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }
        catch (ee: Exception)
        {
            ee.printStackTrace()
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

    private fun checkStatus2(response: String)
    {
        try
        {
            //Log.v(TAG, " STATUS2 : $response")
            val stateObject = JSONObject(response).getJSONObject("state")
            try
            {
                val captureStatus = stateObject.getString(THETA_CAPTURE_STATUS)
                if (captureStatus != currentCaptureStatus)
                {
                    Log.v(TAG, " CapStatus : $currentCaptureStatus -> $captureStatus")
                    setMessage(IMessageDrawer.MessageArea.UPCENTER, Color.WHITE, "STATUS : $captureStatus")
                    currentCaptureStatus = captureStatus
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
            try
            {
                val batteryLevel = stateObject.getDouble(THETA_BATTERY_LEVEL)
                if (batteryLevel != currentBatteryLevel)
                {
                    Log.v(TAG, " BATTERY : $currentBatteryLevel => $batteryLevel")
                    currentBatteryLevel = batteryLevel
                    updateRemainBattery(currentBatteryLevel)
                }
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }

        }
        catch (ee: Exception)
        {
            ee.printStackTrace()
        }
    }

    private fun updateRemainBattery(percentageDouble: Double)
    {
        var color = Color.WHITE
        if (percentageDouble < 0.5)
        {
            color = Color.YELLOW
        }
        if (percentageDouble < 0.3)
        {
                color = Color.RED
        }
        try
        {
            val percentage = kotlin.math.ceil(percentageDouble * 100.0).toInt()
            setMessage(IMessageDrawer.MessageArea.CENTERRIGHT, color, "BATTERY : $percentage%")
        }
        catch (ee: java.lang.Exception)
        {
            ee.printStackTrace()
        }
    }

    private fun setMessage(area: IMessageDrawer.MessageArea, color: Int, message: String)
    {
        if (showInformation != null)
        {
            showInformation?.setMessageToShow(message, area, color)
        }
    }

    private fun updateMessage()
    {
        if (showInformation != null)
        {
            showInformation?.invalidate()
        }
    }

    override fun stopStatusWatch()
    {
        whileFetching = false
    }


    override var captureMode: String
        get() = currentCaptureMode
        set(value) { currentCaptureMode = value }

    override var captureStatus: String
        get() = currentCaptureStatus
        set(value) { currentCaptureStatus = value }

    override fun invalidate()
    {
        updateMessage()
    }

    override fun getStatusList(key: String): List<String?>
    {
        try
        {
            return (when (key) {
                ICameraStatus.TAKE_MODE -> statusListHolder.getAvailableTakeModeList()
                ICameraStatus.SHUTTER_SPEED -> statusListHolder.getAvailableShutterSpeedList()
                ICameraStatus.APERTURE -> statusListHolder.getAvailableApertureList()
                ICameraStatus.EXPREV -> statusListHolder.getAvailableExpRevList()
                ICameraStatus.CAPTURE_MODE -> statusListHolder.getAvailableCaptureModeStringList()
                ICameraStatus.ISO_SENSITIVITY -> statusListHolder.getAvailableIsoSensitivityList()
                ICameraStatus.WHITE_BALANCE -> statusListHolder.getAvailableWhiteBalanceList()
                ICameraStatus.AE -> statusListHolder.getAvailableMeteringModeList()
                ICameraStatus.EFFECT -> statusListHolder.getAvailablePictureEffectList()
                //ICameraStatus.BATTERY -> // 設定不可
                //ICameraStatus.TORCH_MODE -> // 設定不可
                else -> ArrayList<String>()
            })
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (ArrayList<String>())
    }

    override fun getStatus(key: String): String
    {
        return (when (key) {
            ICameraStatus.TAKE_MODE -> getTakeMode()
            ICameraStatus.SHUTTER_SPEED -> getShutterSpeed()
            ICameraStatus.APERTURE -> getAperture()
            ICameraStatus.EXPREV -> getExpRev()
            ICameraStatus.CAPTURE_MODE -> getCaptureModeString()
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
        return (when (key) {
            ICameraStatus.BATTERY -> getRemainBatteryColor()
            else -> Color.WHITE
        })
    }

    override fun setStatus(key: String, value: String)
    {
        try
        {
            when (key) {
                ICameraStatus.TAKE_MODE -> statusListHolder.setTakeMode(value)
                ICameraStatus.SHUTTER_SPEED -> statusListHolder.setShutterSpeed(value)
                ICameraStatus.APERTURE -> statusListHolder.setAperture(value)
                ICameraStatus.EXPREV -> statusListHolder.setExpRev(value)
                ICameraStatus.CAPTURE_MODE -> statusListHolder.setCaptureMode(value)
                ICameraStatus.ISO_SENSITIVITY -> statusListHolder.setIsoSensitivity(value)
                ICameraStatus.WHITE_BALANCE -> statusListHolder.setWhiteBalance(value)
                ICameraStatus.AE -> statusListHolder.setMeteringMode(value)
                ICameraStatus.EFFECT -> statusListHolder.setPictureEffect(value)
                //ICameraStatus.BATTERY -> // 設定不可
                //ICameraStatus.TORCH_MODE -> // 設定不可
                else -> Log.v(TAG, "  ----- setStatus($key, $value) : Unknown -----")
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun getTakeMode() : String
    {
        return (
        when (currentExposureProgram) {
            "1" -> "Manual"
            "2" -> "Normal"
            "3" -> "Aperture"
            "4" -> "Shutter"
            "9" -> "ISO"
            else -> ""
        })
    }

    private fun getShutterSpeed() : String
    {
        return (if (currentShutterSpeed == 0.0) { "" } else { convertShutterSpeedString(currentShutterSpeed) })
    }

    private fun getAperture() : String
    {
        if ((currentExposureProgram == "1")||(currentExposureProgram == "3"))
        {
            return (if (currentAperture == 0.0) { "F:auto" }  else { "F$currentAperture" })
        }
        return ("F$currentAperture")
    }

    private fun getExpRev() : String
    {
        return (if (currentExposureCompensation == 0.0) { "" } else { String.format("%1.1f", currentExposureCompensation) })
    }

    private fun getCaptureModeString() : String
    {
        return (currentCaptureMode)
    }

    private fun getIsoSensitivity() : String
    {
        return (if (currentIsoSensitivity == 0) { "ISO:auto" } else { "ISO:$currentIsoSensitivity" })
    }

    private fun getWhiteBalance() : String
    {
        return (currentWhiteBalance)
    }

    private fun getMeteringMode() : String
    {
        return ("")
    }

    private fun getPictureEffect() : String
    {
        return (when (currentFilter) {
            "off" -> ""
            else -> currentFilter
        })
    }

    private fun getTorchMode() : String
    {
        return ("")
    }

    private fun getRemainBattery() : String
    {
        try
        {
            if (currentBatteryLevel >= 0.0f)
            {
                val percentage = kotlin.math.ceil(currentBatteryLevel * 100.0).toInt()
                return ("Batt. : $percentage%")
            }
        }
        catch (ee: java.lang.Exception)
        {
            ee.printStackTrace()
        }
        return ("Batt. : ???%")
    }

    private fun getRemainBatteryColor() : Int
    {
        var color = Color.WHITE
        try
        {
            if (currentBatteryLevel >= 0.0f)
            {
                val percentage = kotlin.math.ceil(currentBatteryLevel * 100.0).toInt()
                if (percentage < 30)
                {
                    color = Color.RED
                }
                else if (percentage < 50)
                {
                    color = Color.YELLOW
                }
            }
        }
        catch (ee: java.lang.Exception)
        {
            ee.printStackTrace()
        }
        return (color)
    }

    companion object
    {
        private val TAG = ThetaCameraStatusWatcher::class.java.simpleName
        private const val timeoutMs = 3300
        private const val loopWaitMs : Long = 400

        private const val THETA_BATTERY_LEVEL = "batteryLevel"
        private const val THETA_CAPTURE_STATUS = "_captureStatus"

        private const val THETA_APERTURE = "aperture"
        private const val THETA_CAPTURE_MODE = "captureMode"
        private const val THETA_EXPOSURE_COMPENSATION = "exposureCompensation"
        private const val THETA_EXPOSURE_PROGRAM = "exposureProgram"
        private const val THETA_ISO_SENSITIVITY = "iso"
        private const val THETA_SHUTTER_SPEED = "shutterSpeed"
        private const val THETA_WHITE_BALANCE = "whiteBalance"
        private const val THETA_FILTER = "_filter"
    }
}
