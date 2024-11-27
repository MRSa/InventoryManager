package jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.status

import jp.osdn.gokigen.gokigenassets.camera.vendor.pixpro.wrapper.command.IPixproCommandPublisher

class PixproStatusConvert(private val statusHolder: PixproStatusHolder)
{

    companion object
    {
        private val TAG = PixproStatusConvert::class.java.simpleName
    }
    private lateinit var commandPublisher : IPixproCommandPublisher

    fun getAvailableTakeMode(): List<String?>
    {
        //return (listOf("P", "M", "ASCN"))
        return (listOf("P", "M", "ASCN", "Video", "Cont. Shot"))
    }

    fun getAvailableShutterSpeed(): List<String?>
    {
        return (listOf(
            "1/2000",
            "1/1600",
            "1/1200",
            "1/1000",
            "1/800",
            "1/600",
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
            "1s",
            "1.3s",
            "1.5s",
            "2s",
            "2.5s",
            "3s",
            "4s",
            "5s",
            "6s",
            "8s",
            "10s",
            "13s",
            "15s",
            "20s",
            "25s",
            "30s",
        ))
    }

    fun getAvailableAperture(): List<String?>
    {
        return (ArrayList())
    }

    fun getAvailableExpRev(): List<String?>
    {
        return (listOf(
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
            "+3.0"
        ))
    }

    fun getAvailableCaptureMode(): List<String?>
    {
        return (ArrayList())
    }

    fun getAvailableIsoSensitivity(): List<String?>
    {
        return (listOf("AUTO", "100", "200", "400", "800",  "1600", "3200"))
    }

    fun getAvailableWhiteBalance(): List<String?>
    {
        return (listOf("AUTO", "Daylight", "Cloudy", "Fluorescent", "Fluorescent CWF", "Incandescent"))
    }

    fun getAvailableMeteringMode(mode: String): List<String?>
    {
        return (when (mode) {
            "Video" -> { listOf("1920×1080 30p", "1280x720 60p", "1280x720 30p", "640x480 30p", "640x480 120p") }
            else -> {  listOf("4608x3456", "4608x3072","4608x2592", "3648x2736", "2592x1944", "2048x1536", "1920x1080", "640x480") }
        })
    }

    fun getAvailablePictureEffect(): List<String?>
    {
        return (listOf("(+) Zoom In", "(-) Zoom Out"))
    }

    fun getAvailableTorchMode(): List<String?>
    {
        return (listOf("OFF", "ON", "AUTO"))
    }

}
