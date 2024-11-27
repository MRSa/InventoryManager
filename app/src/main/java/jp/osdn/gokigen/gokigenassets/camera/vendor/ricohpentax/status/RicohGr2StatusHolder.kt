package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.status

import android.graphics.Color
import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusUpdateNotify
import org.json.JSONObject
import java.util.*

/**
 *
 *
 */
class RicohGr2StatusHolder(private val notifier: ICameraStatusUpdateNotify?)
{
    private var latestResultObject: JSONObject? = null
    private var focused = false
    private var focusLocked = false
    private var avStatus = ""
    private var tvStatus = ""
    private var xvStatus = ""
    private var exposureModeStatus = ""
    private var meteringModeStatus = ""
    private var wbModeStatus = ""
    private var batteryStatus = ""
    private var shootModeStatus = ""

    /**
     *
     *
     */
    fun getAvailableItemList(key: String): List<String> {
        val itemList: MutableList<String> = ArrayList()
        try {
            val array = latestResultObject?.getJSONArray(key) ?: return itemList
            val nofItems = array.length()
            for (index in 0 until nofItems) {
                try {
                    itemList.add(array.getString(index))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return itemList
    }

    fun getItemStatus(key: String): String
    {
        try
        {
            val statusValue = when (key) {
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
                ICameraStatus.DRIVE_MODE -> getDriveMode()
                else -> ""
            }
            return (statusValue)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    fun getItemStatusColor(key: String): Int
    {
        var color : Int = Color.WHITE
        try
        {
            color = when (key) {
                ICameraStatus.BATTERY -> getRemainBatteryColor()
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
        return (latestResultObject?.getString("exposureMode") ?: "")
    }

    private fun getDriveMode() : String
    {
        return (latestResultObject?.getString("shootMode") ?: "")
    }

    private fun getShutterSpeed() : String
    {
        try
        {
            val value = latestResultObject?.getString("tv") ?: ""
            return (value.replace(".", "/"))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    private fun getAperture() : String
    {
        val value =  latestResultObject?.getString("av") ?: ""
        return ("F$value")
    }

    private fun getExpRev() : String
    {
        try
        {
            val value = latestResultObject?.getString("xv") ?: ""
            return (value)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    private fun getCaptureMode() : String
    {
        return (latestResultObject?.getString("captureMode") ?: "")
    }

    private fun getIsoSensitivity() : String
    {
        val value = latestResultObject?.getString("sv")
        return ("ISO:$value")
    }

    private fun getWhiteBalance() : String
    {
        val value = latestResultObject?.getString("WBMode")
        return ("WB:$value")
    }

    private fun getMeteringMode() : String
    {
        return (latestResultObject?.getString("meteringMode") ?: "")
    }

    private fun getPictureEffect() : String
    {
        return (latestResultObject?.getString("effect") ?: "")
    }

    private fun getTorchMode() : String
    {
        return ("")
    }

    private fun getRemainBattery() : String
    {
        try
        {
            val remain = latestResultObject?.getString("battery")?.toInt()
            return ("Batt.:$remain%")
        }
        catch (e : Exception)
        {
            e.printStackTrace()
        }
        return ("")
    }

    private fun getRemainBatteryColor() : Int
    {
        var color = Color.WHITE
        try
        {
            val percentage = latestResultObject?.getString("battery")?.toInt() ?: 100
           if (percentage < 30)
           {
               color = Color.RED
           }
           else if (percentage < 50)
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

    private fun getStatusString(obj: JSONObject?, name: String): String
    {
        try
        {
            if (obj == null)
            {
                return ""
            }
            return obj.getString(name)
        }
        catch (e: Exception)
        {
            //e.printStackTrace();
        }
        return ""
    }

    private fun getBooleanStatus(obj: JSONObject?, name: String): Boolean
    {
        try
        {
            if (obj == null)
            {
                return false
            }
            return obj.getBoolean(name)
        }
        catch (e: Exception)
        {
            //e.printStackTrace();
        }
        return false
    }

    /**
     *
     *
     */
    fun updateStatus(replyString: String?)
    {
        if (replyString == null || replyString.isEmpty())
        {
            Log.v(TAG, "httpGet() reply is null. ")
            return
        }
        try {
            latestResultObject = JSONObject(replyString)
            val result = getStatusString(latestResultObject, "errMsg")
            val av = getStatusString(latestResultObject, "av")
            val tv = getStatusString(latestResultObject, "tv")
            val xv = getStatusString(latestResultObject, "xv")
            val shootMode = getStatusString(latestResultObject, "shootMode")
            val exposureMode = getStatusString(latestResultObject, "exposureMode")
            val meteringMode = getStatusString(latestResultObject, "meteringMode")
            val wbMode = getStatusString(latestResultObject, "WBMode")
            val battery = getStatusString(latestResultObject, "battery")
            val focus = getBooleanStatus(latestResultObject, "focused")
            val focusLock = getBooleanStatus(latestResultObject, "focusLocked")
            if (result.contains("OK"))
            {
                if (avStatus != av)
                {
                    avStatus = av
                    notifier?.updatedAperture(avStatus)
                }
                if (tvStatus != tv) {
                    tvStatus = tv
                    notifier?.updatedShutterSpeed(tvStatus)
                }
                if (xvStatus != xv) {
                    xvStatus = xv
                    notifier?.updatedExposureCompensation(xvStatus)
                }
                if (exposureModeStatus != exposureMode) {
                    exposureModeStatus = exposureMode
                    notifier?.updatedTakeMode(exposureModeStatus)
                }
                if (meteringModeStatus != meteringMode) {
                    meteringModeStatus = meteringMode
                    notifier?.updatedMeteringMode(meteringModeStatus)
                }
                if (wbModeStatus != wbMode) {
                    wbModeStatus = wbMode
                    notifier?.updatedWBMode(wbModeStatus)
                }
                if (batteryStatus != battery) {
                    batteryStatus = battery
                    notifier?.updateRemainBattery(batteryStatus.toInt())
                }
                if (focus != focused || focusLock != focusLocked) {
                    focused = focus
                    focusLocked = focusLock
                    notifier?.updateFocusedStatus(focused, focusLocked)
                }
                if (shootModeStatus != shootMode)
                {
                    shootModeStatus = shootMode
                    notifier?.updateShootMode(shootModeStatus)
                    Log.v(TAG, " Shoot Mode : $shootModeStatus")
                }
            }
            System.gc()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     *
     *
     */
    companion object
    {
        private val TAG = RicohGr2StatusHolder::class.java.simpleName
    }
}
