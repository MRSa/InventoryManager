package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.status

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.IPanasonicCamera
import jp.osdn.gokigen.gokigenassets.utils.communication.SimpleHttpClient
import java.util.ArrayList

class CameraStatusListHolder(private val remote: IPanasonicCamera)
{
    companion object
    {
        private val TAG = CameraStatusListHolder::class.java.simpleName
        private const val TIMEOUT_MS = 3000
        private const val MAX_RETRY_COUNT = 2
    }

    /**
     *
     *
     */
    fun getAvailableItemList(key: String): List<String>
    {
        try
        {
            return (when (key) {
                ICameraStatus.TAKE_MODE -> getAvailableTakeMode()
                ICameraStatus.SHUTTER_SPEED -> getAvailableShutterSpeed()
                ICameraStatus.APERTURE -> getAvailableAperture()
                ICameraStatus.EXPREV -> getAvailableExpRev()
                ICameraStatus.CAPTURE_MODE -> getAvailableCaptureMode()
                ICameraStatus.ISO_SENSITIVITY -> getAvailableIsoSensitivity()
                ICameraStatus.WHITE_BALANCE -> getAvailableWhiteBalance()
                ICameraStatus.AE -> getAvailableMeteringMode()
                ICameraStatus.EFFECT -> getAvailablePictureEffect()
                ICameraStatus.TORCH_MODE -> getAvailableTorchMode()
                //ICameraStatus.FOCUS_STATUS -> getAvailableDriveMode()
                //ICameraStatus.BATTERY -> getAvailableRemainBattery()
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

    private fun getAvailableTakeMode() : List<String>
    {
        return (ArrayList())
    }

    private fun getAvailableTorchMode() : List<String>
    {
        return (listOf("OFF", "EXP. BRACKET", "FOCAL BRACKET", "WB BRACKET", "FOCUS BRACKET"))
    }

    private fun getAvailableShutterSpeed() : List<String>
    {
        return (listOf(
            "16000" ,
            "13000" ,
            "10000" ,
            "8000" ,
            "6400" ,
            "5000" ,
            "4000" ,
            "3200" ,
            "2500" ,
            "2000" ,
            "1600" ,
            "1300" ,
            "1000" ,
            "800" ,
            "640" ,
            "500" ,
            "400" ,
            "320" ,
            "250" ,
            "200" ,
            "160" ,
            "125" ,
            "100" ,
            "80" ,
            "60" ,
            "50" ,
            "40" ,
            "30" ,
            "25" ,
            "20" ,
            "15" ,
            "13" ,
            "10" ,
            "8" ,
            "6" ,
            "5" ,
            "4" ,
            "3.2" ,
            "2.5" ,
            "2" ,
            "1.6" ,
            "1.3" ,
            "1s" ,
            "1.3s" ,
            "1.6s" ,
            "2s" ,
            "2.5s" ,
            "3.2s" ,
            "4s" ,
            "5s" ,
            "6s" ,
            "8s" ,
            "10s" ,
            "13s" ,
            "15s" ,
            "20s" ,
            "25s" ,
            "30s" ,
            "40s" ,
            "50s" ,
            "60s" ,
            "T" ,
        ))
    }

    private fun getAvailableAperture() : List<String>
    {
        return (listOf(
            "F1.0",
            "F1.1",
            "F1.2",
            "F1.4",
            "F1.6",
            "F1.7",
            "F1.8",
            "F2.0",
            "F2.2",
            "F2.4",
            "F2.5",
            "F2.8",
            "F3.2",
            "F3.5",
            "F4.0",
            "F4.5",
            "F5.0",
            "F5.6",
            "F6.3",
            "F7.1",
            "F8.0",
            "F9.0",
            "F10",
            "F11",
            "F13",
            "F14",
            "F16",
            "F18",
            "F20",
            "F22",
            "F25",
            "F29",
            "F32",
        ))
    }

    private fun getAvailableExpRev() : List<String>
    {
        return (listOf(
            "-5.0",
            "-4.7",
            "-4.3",
            "-4.0",
            "-3.7",
            "-3.3",
            "-3.0",
            "-2.7",
            "-2.3",
            "-2.0",
            "-1.7",
            "-1.3",
            "-1.0",
            "-0.7",
            "-0.3",
            "0.0",
            "+0.3",
            "+0.7",
            "+1.0",
            "+1.3",
            "+1.7",
            "+2.0",
            "+2.3",
            "+2.7",
            "+3.0",
            "+3.3",
            "+3.7",
            "+4.0",
            "+4.3",
            "+4.7",
            "+5.0",
            ))
    }

    private fun getAvailablePictureEffect() : List<String>
    {
        return (listOf("OFF",
            "POP",
            "RETR",
            "OLD",
            "HKEY",
            "LKEY",
            "SEPI",
            "MONO",
            "D.MONO",
            "R.MONO",
            "S.MONO",
            "IART",
            "HDYN",
            "XPRO",
            "TOY",
            "TOYP",
            "BLEA",
            "DIOR",
            "SOFT",
            "FAN",
            "STAR",
            "1CLR",
            "SUN"))
    }

    private fun getAvailableIsoSensitivity() : List<String>
    {
        return (listOf("ISO-i",
            "AUTO",
            "100",
            "125",
            "160",
            "200",
            "250",
            "320",
            "400",
            "500",
            "640",
            "800",
            "1000",
            "1250",
            "1600",
            "2000",
            "2500",
            "3200",
            "4000",
            "5000",
            "6400",
            "8000",
            "10000",
            "12800",
            "16000",
            "20000",
            "25600",
            "32000",
            "40000",
            "50000",
            "51200",
        ))
    }

    private fun getAvailableWhiteBalance() : List<String>
    {
        return (listOf("AWB",
        "Daylight",
        "Cloudy",
        "Shade",
        "Incandescent",
        "Flash",
        "Custom1",
        "Custom2",
        "Custom3",
        "Custom4",
        "Color Temp.",
        "K1",
        "K2",
        "K3",
        "K4",
        "AWBc",
        "AWBw"))
    }

    private fun getAvailableMeteringMode() : List<String>
    {
        return (ArrayList())
        //return (listOf("MULTI", "CENTER", "SPOT", "HIGHLIGHT"))  // SPOT測光に切り替えると、LVがうまく取れないので...
    }

    private fun getAvailableCaptureMode() : List<String>
    {
        return (listOf(
            "STD",
            "VIVID",
            "NATURAL",
            "MONO",
            "L.MONO",
            "L.MONO D",
            "L.MONO S",
            "SCENERY",
            "PORTRAIT",
            "L.CLAS N",
            "FLAT",
            "CUSTOM",
            "CUSTOM1",
            "CUSTOM2",
            "CUSTOM3",
            "CUSTOM4",
            "CINELIKE_D",
            "CINELIKE_V",
            "Like709",
            "VLOG-L",
            "VLOG-G",
            "VLOG-GAMMA",
        ))
    }

    fun setStatus(key: String, value: String)
    {
        Log.v(TAG, " setStatus(key:$key, value:$value)")
        try
        {
            when (key) {
                ICameraStatus.TAKE_MODE -> setTakeMode(value)
                ICameraStatus.SHUTTER_SPEED -> setShutterSpeed(value)
                ICameraStatus.APERTURE -> setAperture(value)
                ICameraStatus.EXPREV -> setExpRev(value)
                ICameraStatus.CAPTURE_MODE -> setCaptureMode(value)
                ICameraStatus.ISO_SENSITIVITY -> setIsoSensitivity(value)
                ICameraStatus.WHITE_BALANCE -> setWhiteBalance(value)
                ICameraStatus.AE -> setMeteringMode(value)
                ICameraStatus.EFFECT -> setPictureEffect(value)
                ICameraStatus.TORCH_MODE -> setTorchMode(value)
                //ICameraStatus.BATTERY -> setRemainBattery(value)
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
        Log.v(TAG, " setTakeMode($value)")

    }

    private fun setTorchMode(value: String)
    {
        val setValue = when (value) {
            "OFF" -> "off"
            "EXP. BRACKET" -> "exposure"
            "FOCAL BRACKET" -> "focal"
            "WB BRACKET" -> "wb"
            "FOCUS BRACKET" -> "focus"
            else -> "off"
        }
        sendCamSetSettingCmd("bracket", setValue, null)
    }

    private fun setShutterSpeed(value: String)
    {
        val setValue = when (value) {
            "16000" -> "3584/256"
                "13000" -> "3499/256"
                "10000" -> "3414/256"
                "8000" -> "3328/256"
                "6400" -> "3243/256"
                "5000" -> "3158/256"
                "4000" -> "3072/256"
                "3200" -> "2987/256"
                "2500" -> "2902/256"
                "2000" -> "2816/256"
                "1600" -> "2731/256"
                "1300" -> "2646/256"
                "1000" -> "2560/256"
                "800" -> "2475/256"
                "640" -> "2390/256"
                "500" -> "2304/256"
                "400" -> "2219/256"
                "320" -> "2134/256"
                "250" -> "2048/256"
                "200" -> "1963/256"
                "160" -> "1878/256"
                "125" -> "1792/256"
                "100" -> "1707/256"
                "80" -> "1622/256"
                "60" -> "1536/256"
                "50" -> "1451/256"
                "40" -> "1366/256"
                "30" -> "1280/256"
                "25" -> "1195/256"
                "20" -> "1110/256"
                "15" -> "1024/256"
                "13" -> "939/256"
                "10" -> "854/256"
                "8" -> "768/256"
                "6" -> "683/256"
                "5" -> "598/256"
                "4" -> "512/256"
                "3.2" -> "427/256"
                "2.5" -> "342/256"
                "2" -> "256/256"
                "1.6" -> "171/256"
                "1.3" -> "86/256"
                "1s" -> "0/256"
                "1.3s" -> "-85/256"
                "1.6s" -> "-170/256"
                "2s" -> "-256/256"
                "2.5s" -> "-341/256"
                "3.2s" -> "-426/256"
                "4s" -> "-512/256"
                "5s" -> "-597/256"
                "6s" -> "-682/256"
                "8s" -> "-768/256"
                "10s" -> "-853/256"
                "13s" -> "-938/256"
                "15s" -> "-1024/256"
                "20s" -> "-1109/256"
                "25s" -> "-1194/256"
                "30s" -> "-1280/256"
                "40s" -> "-1365/256"
                "50s" -> "-1450/256"
                "60s" -> "-1536/256"
                "T" -> "256/256"
                else -> "1792/256"
        }
        sendCamSetSettingCmd("shtrspeed", setValue, null)
    }

    private fun setAperture(value: String)
    {
        val setValue = when (value) {
            "F1.0" -> "0/256"
            "F1.1" -> "85/256"
            "F1.2" -> "171/256"
            "F1.4" -> "256/256"
            "F1.6" -> "341/256"
            "F1.7" -> "392/256"
            "F1.8" -> "427/256"
            "F2.0" -> "512/256"
            "F2.2" -> "598/256"
            "F2.4" -> "640/256"
            "F2.5" -> "683/256"
            "F2.8" -> "768/256"
            "F3.2" -> "854/256"
            "F3.5" -> "938/256"
            "F4.0" -> "1024/256"
            "F4.5" -> "1110/256"
            "F5.0" -> "1195/256"
            "F5.6" -> "1280/256"
            "F6.3" -> "1366/256"
            "F7.1" -> "1451/256"
            "F8.0" -> "1536/256"
            "F9.0" -> "1622/256"
            "F10" -> "1707/256"
            "F11" -> "1792/256"
            "F13" -> "1878/256"
            "F14" -> "1963/256"
            "F16" -> "2048/256"
            "F18" -> "2133/256"
            "F20" -> "2219/256"
            "F22" -> "2304/256"
            "F25" -> "2389/256"
            "F29" -> "2474/256"
            "F32" -> "2560/256"
            else -> "1024/256"
        }
        sendCamSetSettingCmd("focal", setValue, null)
    }

    private fun setExpRev(value: String)
    {
        val setValue = when (value) {
            "-5.0" -> "-5"
            "-4.7" -> "-14/3"
            "-4.3" -> "-13/3"
            "-4.0" -> "-4"
            "-3.7" -> "-11/3"
            "-3.3" -> "-10/3"
            "-3.0" -> "-3"
            "-2.7" -> "-8/3"
            "-2.3" -> "-7/3"
            "-2.0" -> "-2"
            "-1.7" -> "-5/3"
            "-1.3" -> "-4/3"
            "-1.0" -> "-1"
            "-0.7" -> "-2/3"
            "-0.3" -> "-1/3"
            "0.0" -> "0"
            "+0.3" -> "1/3"
            "+0.7" -> "2/3"
            "+1.0" -> "1"
            "+1.3" -> "4/3"
            "+1.7" -> "5/3"
            "+2.0" -> "2"
            "+2.3" -> "7/3"
            "+2.7" -> "8/3"
            "+3.0" -> "3"
            "+3.3" -> "10/3"
            "+3.7" -> "11/3"
            "+4.0" -> "4"
            "+4.3" -> "13/3"
            "+4.7" -> "14/3"
            "+5.0" -> "5"
            else -> "0"
        }
        sendCamSetSettingCmd("exposure", setValue, null)
    }

    private fun setCaptureMode(value: String)
    {
        //Log.v(TAG, " ===== setCaptureMode($value) =====")
        val setValue = when (value) {
            "STD" -> "standard"
            "VIVID" -> "vivid"
            "NATURAL" -> "natural"
            "MONO" -> "bw"
            "L.MONO" -> "l_bw"
            "SCENERY" -> "scenery"
            "PORTRAIT" -> "portrait"
            "CUSTOM" -> "custom"
            "CUSTOM1" -> "custom1"
            "CUSTOM2" -> "custom2"
            "CUSTOM3" -> "custom3"
            "CUSTOM4" -> "custom4"
            "CINELIKE_D" -> "cinelike_d"
            "CINELIKE_V" -> "cinelike_v"
            "L.MONO D" -> "l_bw_d"
            "Like709" -> "709like"
            "VLOG-L" -> "vlog-l"
            "VLOG-G" -> "vlog-g"
            "VLOG-GAMMA" -> "vlog-gamma"
            "FLAT" -> "flat"
            "L.CLAS N" -> "l_cla_neo"
            "L.MONO S" -> "l_bw_s"
            else -> return
        }
        sendCamSetSettingCmd("colormode", setValue, null)
    }

    private fun setIsoSensitivity(value: String)
    {
        val setValue = when (value) {
            "ISO-i" -> "i_auto"
            "AUTO" -> "auto"
            else -> value
        }
        sendCamSetSettingCmd("iso", setValue, null)
    }

    private fun setWhiteBalance(value: String)
    {
        val setValue =  when (value) {
            "AWB" -> "auto"
            "Daylight" -> "daylight"
            "Cloudy" -> "cloudy"
            "Shade" -> "shade"
            "Incandescent" -> "halogen"
            "Flash" -> "flash"
            "Custom1" -> "white_set1"
            "Custom2" -> "white_set2"
            "Custom3" -> "white_set3"
            "Custom4" -> "white_set4"
            "Color Temp." -> "color_temp"
            "K1" -> "color_temp1"
            "K2" -> "color_temp2"
            "K3" -> "color_temp3"
            "K4" -> "color_temp4"
            "AWBc" -> "auto_cool"
            "AWBw" -> "auto_warm"
            else -> "auto"
        }
        sendCamSetSettingCmd("whitebalance", setValue, null)
    }

    private fun setMeteringMode(value: String)
    {
        val setValue = when (value) {
            "MULTI" -> "multi"
            "CENTER" -> "center"
            "SPOT" -> "spot"
            "HIGHLIGHT" -> "highlight"
            else -> "multi"
        }
        sendCamSetSettingCmd("lightmetering", setValue, null)
    }

    private fun setPictureEffect(value: String)
    {
        val setValue = when (value) {
            "POP" -> "pop"
            "RETR" -> "retro"
            "OLD" -> "old_days"
            "HKEY" -> "high_key"
            "LKEY" -> "low_key"
            "SEPI" -> "sepia"
            "MONO" -> "monochro"
            "D.MONO" -> "dynamic_monochro"
            "R.MONO" -> "rough_monochro"
            "S.MONO" -> "silky_monochro"
            "IART" -> "impressive_art"
            "HDYN" -> "high_dynamic"
            "XPRO" -> "cross_proc"
            "TOY" -> "toy_photo"
            "TOYP" -> "toy_pop"
            "BLEA" -> "bleach_bypass"
            "DIOR" -> "diorama"
            "SOFT" -> "soft_focus"
            "FAN" -> "fantasy"
            "STAR" -> "cross_filter"
            "1CLR" -> "one_point_color"
            "SUN" -> "sunshine"
            else -> "noeffect"
        }
        sendCamSetSettingCmd("filter_setting", setValue, null)
    }

    private fun sendCamSetSettingCmd(msgType: String, msgValue: String, value2: String?)
    {
        try
        {
            val http = SimpleHttpClient()

            //Log.v(TAG, "  ------- sendCamSetSettingCmd($msgType, $msgValue, $value2)")
            val sendMessage = if (value2 != null) { "cam.cgi?mode=setsetting&type=$msgType&value=$msgValue&value2=$value2" } else { "cam.cgi?mode=setsetting&type=$msgType&value=$msgValue" }
            val thread = Thread {
                try
                {
                    var retryCount = 0
                    var loop = true
                    while (loop)
                    {
                        val urlToSend = remote.getCmdUrl() + sendMessage
                        val sessionId = remote.getCommunicationSessionId()
                        val reply = if (!sessionId.isNullOrEmpty())
                        {
                            val headerMap: MutableMap<String, String> = HashMap()
                            headerMap["X-SESSION_ID"] = sessionId
                            http.httpGetWithHeader(urlToSend, headerMap, null,
                                TIMEOUT_MS
                            ) ?: ""
                        }
                        else
                        {
                            http.httpGet(urlToSend, TIMEOUT_MS)
                        }
                        if (reply.indexOf("<result>ok</result>") > 0)
                        {
                            loop = false
                            // Log.v(TAG, " --- SET SETTING ($msgType,$msgValue): $reply")
                        }
                        else
                        {
                            // エラー発生時は何回か(MAX_RETRY_COUNT分)再送する
                            Log.v(TAG, " $urlToSend (NG) : $reply ")
                            Thread.sleep(1000) // 1秒待つ
                            retryCount++
                            if(retryCount >= MAX_RETRY_COUNT)
                            {
                                loop = false
                            }
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
}
