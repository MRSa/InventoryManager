package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.wrapper.playback

import jp.osdn.gokigen.gokigenassets.camera.interfaces.playback.ICameraFileInfo
import jp.osdn.gokigen.gokigenassets.camera.interfaces.playback.ICameraFileInfoSetter
import java.text.SimpleDateFormat
import java.util.*


class CameraFileInfo(private val path: String, private val name: String) : ICameraFileInfo, ICameraFileInfoSetter
{
    private var dateTime : Date? = Date()
    private var captured = false
    private var av: String? = null
    private var sv: String? = null
    private var tv: String? = null
    private var xv: String? = null
    private var orientation = 0
    private var aspectRatio: String? = null
    private var cameraModel: String? = null
    private var latlng: String? = null
    private val fileSize: Long = 0

    override fun getDatetime(): Date?
    {
        return (dateTime)
    }

    override fun getDirectoryPath(): String
    {
        return (path)
    }

    override fun getFilename(): String
    {
        return (name)
    }

    override fun getOriginalFilename(): String
    {
        return (name)
    }

    override fun getAperature(): String?
    {
        return (av)
    }

    override fun getShutterSpeed(): String?
    {
        return (sv)
    }

    override fun getIsoSensitivity(): String? {
        TODO("Not yet implemented")
    }

    override fun getExpRev(): String? {
        TODO("Not yet implemented")
    }

    override fun getOrientation(): Int {
        TODO("Not yet implemented")
    }

    override fun getAspectRatio(): String? {
        TODO("Not yet implemented")
    }

    override fun getModel(): String? {
        TODO("Not yet implemented")
    }

    override fun getLatLng(): String? {
        TODO("Not yet implemented")
    }

    override fun getCaptured(): Boolean {
        TODO("Not yet implemented")
    }


    override fun updateValues(
        dateTime: String,
        av: String,
        tv: String,
        sv: String,
        xv: String,
        orientation: Int,
        aspectRatio: String,
        model: String,
        latLng: String,
        captured: Boolean
    )
    {
        this.av = av
        this.tv = tv
        this.sv = sv
        this.xv = xv
        this.orientation = orientation
        this.aspectRatio = aspectRatio
        cameraModel = model
        latlng = latLng
        this.captured = captured
        try
        {
            val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            this.dateTime = df.parse(dateTime)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun setDate(datetime: Date)
    {
        dateTime = datetime
    }
}
