package jp.osdn.gokigen.gokigenassets.camera.interfaces.playback

import java.util.*

interface ICameraFileInfo
{

    fun getDatetime(): Date?
    fun getDirectoryPath(): String?
    fun getFilename(): String?
    fun getOriginalFilename(): String?

    fun getAperature(): String?
    fun getShutterSpeed(): String?
    fun getIsoSensitivity(): String?
    fun getExpRev(): String?
    fun getOrientation(): Int
    fun getAspectRatio(): String?
    fun getModel(): String?
    fun getLatLng(): String?
    fun getCaptured(): Boolean

    fun updateValues(
        dateTime: String,
        av: String,
        tv: String,
        sv: String,
        xv: String,
        orientation: Int,
        aspectRatio: String,
        model: String,
        LatLng: String,
        captured: Boolean
    )
}
