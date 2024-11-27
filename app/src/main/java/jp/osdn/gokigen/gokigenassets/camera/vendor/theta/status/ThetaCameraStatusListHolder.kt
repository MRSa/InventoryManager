package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status

import android.util.Log
import jp.osdn.gokigen.gokigenassets.camera.vendor.theta.operation.ThetaOptionSetControl

class ThetaCameraStatusListHolder(private val sessionIdProvider: IThetaSessionIdProvider, executeUrl : String = "http://192.168.1.1")
{
    //private val optionGet = ThetaOptionGetControl(sessionIdProvider, executeUrl)
    private val optionSet = ThetaOptionSetControl(sessionIdProvider, executeUrl)

    companion object
    {
        private val TAG = ThetaCameraStatusListHolder::class.java.simpleName
    }

    fun getAvailableTakeModeList(): List<String?>
    {
        return (listOf("Manual", "Normal", "Aperture", "Shutter", "ISO"))
    }

    fun getAvailableShutterSpeedList(): List<String?>
    {
        return (listOf("1/25000",
            "1/20000",
            "1/16000",
            "1/12500",
            "1/10000",
            "1/8000",
            "1/6400",
            "1/5000",
            "1/4000",
            "1/3200",
            "1/2500",
            "1/2000",
            "1/1600",
            "1/1250",
            "1/1000",
            "1/800",
            "1/640",
            "1/500",
            "1/400",
            "1/320",
            "1/250",
            "1/200",
            "1/160",
            "1/125",
            "1/100",
            "1/80",
            "1/60",
            "1/50",
            "1/40",
            "1/30",
            "1/25",
            "1/20",
            "1/15",
            "1/13",
            "1/10",
            "1/8",
            "1/6",
            "1/5",
            "1/4",
            "1/3",
            "1/2.5",
            "1/2",
            "1/1.6",
            "1/1.3",
            "1",
            "1.3",
            "1.6",
            "2",
            "2.5",
            "3.2",
            "4",
            "5",
            "6",
            "8",
            "10",
            "13",
            "15",
            "20",
            "25",
            "30",
            "60"))
    }

    fun getAvailableApertureList(): List<String?>
    {
        return (listOf("2.0", "0", "2.1", "3.5", "5.6"))
    }

    fun getAvailableExpRevList(): List<String?>
    {
        return (listOf("-2.0", "-1.7", "-1.3", "-1.0", "-0.7", "-0.3", "0.0", "0.3", "0.7", "1.0", "1.3", "1.7", "2.0"))
    }

    fun getAvailableCaptureModeStringList(): List<String?>
    {
        return (listOf("image", "video"))
    }

    fun getAvailableIsoSensitivityList(): List<String?>
    {
        return (listOf("AUTO", "64", "80", "100", "125", "160", "200", "250", "320", "400", "500", "640", "800", "1000", "1250", "1600", "2000", "2500", "3200", "4000", "5000", "6000"))
    }

    fun getAvailableWhiteBalanceList(): List<String?>
    {
        return (listOf("auto","daylight","shade","cloudy-daylight","incandescent","_warmWhiteFluorescent","_dayLightFluorescent","_dayWhiteFluorescent","fluorescent","_bulbFluorescent","_colorTemperature","_underwater"))
    }

    fun getAvailableMeteringModeList(): List<String?>
    {
        return (ArrayList())
    }

    fun getAvailablePictureEffectList(): List<String?>
    {
        return (listOf("off", "DR Comp", "Noise Reduction", "hdr", "Hh hdr"))
    }

    fun setTakeMode(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            val setValue = when (value)
            {
                "Manual" -> 1
                "Normal" -> 2
                "Aperture" -> 3
                "Shutter" -> 4
                "ISO" -> 9
                else -> return
            }
            optionSet.setOptions(" \"exposureProgram\": $setValue", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setShutterSpeed(value: String)
    {
        try
        {
            val setValue = when (value) {
                "1/25000" -> 0.00004
                "1/20000" -> 0.00005
                "1/16000" -> 0.0000625
                "1/12500" -> 0.00008
                "1/10000" -> 0.0001
                "1/8000" -> 0.000125
                "1/6400" -> 0.00015625
                "1/5000" -> 0.0002
                "1/4000" -> 0.00025
                "1/3200" -> 0.0003125
                "1/2500" -> 0.0004
                "1/2000" -> 0.0005
                "1/1600" -> 0.000625
                "1/1250" -> 0.0008
                "1/1000" -> 0.001
                "1/800" -> 0.00125
                "1/640" -> 0.0015625
                "1/500" -> 0.002
                "1/400" -> 0.0025
                "1/320" -> 0.003125
                "1/250" -> 0.004
                "1/200" -> 0.005
                "1/160" -> 0.00625
                "1/125" -> 0.008
                "1/100" -> 0.01
                "1/80" -> 0.0125
                "1/60" -> 0.01666666
                "1/50" -> 0.02
                "1/40" -> 0.025
                "1/30" -> 0.03333333
                "1/25" -> 0.04
                "1/20" -> 0.05
                "1/15" -> 0.06666666
                "1/13" -> 0.07692307
                "1/10" -> 0.1
                "1/8" ->  0.125
                "1/6" -> 0.16666666
                "1/5" -> 0.2
                "1/4" -> 0.25
                "1/3" -> 0.33333333
                "1/2.5" -> 0.4
                "1/2" -> 0.5
                "1/1.6" -> 0.625
                "1/1.3" -> 0.76923076
                "1" -> 1
                "1.3" -> 1.3
                "1.6" -> 1.6
                "2" -> 2
                "2.5" -> 2.5
                "3.2" -> 3.2
                "4" -> 4
                "5" -> 5
                "6" -> 6
                "8" -> 8
                "10" -> 10
                "13" -> 13
                "15" -> 15
                "20" -> 20
                "25" -> 25
                "30" -> 30
                "60" -> 60
                else -> return
            }
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            optionSet.setOptions(" \"shutterSpeed\": $setValue", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setAperture(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            optionSet.setOptions(" \"aperture\": $value", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setExpRev(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            optionSet.setOptions(" \"exposureCompensation\": $value", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setCaptureMode(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            val setValue = if ((!useOSCv2)&&(value == "video")) { "_video" } else { value }
            optionSet.setOptions(" \"captureMode\": \"$setValue\"", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setIsoSensitivity(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            val setValue = if (value == "AUTO") { "0" } else { value }
            optionSet.setOptions(" \"iso\": $setValue", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setWhiteBalance(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            optionSet.setOptions(" \"whiteBalance\": \"$value\"", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setMeteringMode(value: String)
    {
        try
        {
            Log.v(TAG, " setMeteringMode($value)")
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    fun setPictureEffect(value: String)
    {
        try
        {
            val useOSCv2 = sessionIdProvider.sessionId.isEmpty()
            optionSet.setOptions(" \"_filter\": \"$value\"", useOSCv2)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
