package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation


interface IRicohGr2CameraStatus
{
    fun getStatusList(key: String): List<String?>
    fun getStatus(key: String): String
    fun setStatus(key: String, value: String)

    companion object
    {
        var BATTERY = "battery"
        var STATE = "state"
        var FOCUS_MODE = "focusMode"
        var AF_MODE = "AFMode"

        var RESOLUTION = "reso"
        var DRIVE_MODE = "shootMode"
        var WHITE_BALANCE = "WBMode"

        var AE = "meteringMode"

        var AE_STATUS_MULTI = "multi"
        var AE_STATUS_ESP = "ESP"
        var AE_STATUS_SPOT = "spot"
        var AE_STATUS_PINPOINT = "Spot"
        var AE_STATUS_CENTER = "center"
        var AE_STATUS_CENTER2 = "Ctr-Weighted"

        var EFFECT = "effect"
        var TAKE_MODE = "exposureMode"
        var IMAGESIZE = "stillSize"
        var MOVIESIZE = "movieSize"

        var APERATURE = "av"
        var SHUTTER_SPEED = "tv"
        var ISO_SENSITIVITY = "sv"
        var EXPREV = "xv"
        var FLASH_XV = "flashxv"
        var SELF_TIMER = "selftimer"

        var TAKE_MODE_MOVIE = "movie"
    }
}