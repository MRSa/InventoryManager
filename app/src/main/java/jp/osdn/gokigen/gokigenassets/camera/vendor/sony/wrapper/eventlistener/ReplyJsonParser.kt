package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.eventlistener

import android.os.Handler
import android.util.Log
import org.json.JSONObject
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatusHolder
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class ReplyJsonParser(private val uiHandler: Handler) : ICameraStatusHolder
{
    private var cameraStatus = ""
    private var listener: ICameraChangeListener? = null
    private var currentLiveviewStatus = false
    private var currentShootMode = ""
    private var currentAvailableShootModes: List<String?> = Collections.unmodifiableList(ArrayList<String>())
    private var currentZoomPosition = 0
    private var currentStorageId = ""
    private var currentFocusStatus = ""
    private var currentContShootingMode = ""

    fun parse(replyJson: JSONObject)
    {
        try
        {
            // AvailableApis
            val availableApis = findAvailableApiList(replyJson)
            if (availableApis.isNotEmpty())
            {
                uiHandler.post { listener?.onApiListModified(availableApis) }
            }

            // CameraStatus
            val cameraStatus = findCameraStatus(replyJson) //?: ""
            Log.d(TAG, "getEvent cameraStatus: $cameraStatus")
            if (cameraStatus != this.cameraStatus)
            {
                this.cameraStatus = cameraStatus
                uiHandler.post { listener?.onCameraStatusChanged(cameraStatus) }
            }

            // LiveviewStatus
            val liveviewStatus = findLiveviewStatus(replyJson) //?: false
            Log.d(TAG, "getEvent liveviewStatus: $liveviewStatus")
            if (liveviewStatus != currentLiveviewStatus)
            {
                currentLiveviewStatus = liveviewStatus
                uiHandler.post { listener?.onLiveviewStatusChanged(liveviewStatus) }
            }

            // ShootMode
            val shootMode = findShootMode(replyJson) //?: ""
            Log.d(TAG, "getEvent shootMode: $shootMode")
            if (shootMode != currentShootMode)
            {
                currentShootMode = shootMode

                // Available Shoot Modes
                val shootModes = findAvailableShootModes(replyJson)
                currentAvailableShootModes = Collections.unmodifiableList(shootModes)
                uiHandler.post { listener?.onShootModeChanged(shootMode) }
            }

            // zoomPosition
            val zoomPosition = findZoomInformation(replyJson)
            Log.d(TAG, "getEvent zoomPosition: $zoomPosition")
            if (zoomPosition != -1)
            {
                currentZoomPosition = zoomPosition
                uiHandler.post { listener?.onZoomPositionChanged(zoomPosition) }
            }

            // storageId
            val storageId = findStorageId(replyJson) //?: ""
            Log.d(TAG, "getEvent storageId:$storageId")
            if (storageId != currentStorageId)
            {
                currentStorageId = storageId
                uiHandler.post { listener?.onStorageIdChanged(storageId) }
            }

            // focusStatus (v1.1)
            val focusStatus = findFocusStatus(replyJson) //?: ""
            Log.d(TAG, "getEvent focusStatus:$focusStatus")
            if (focusStatus != currentFocusStatus)
            {
                currentFocusStatus = focusStatus
                uiHandler.post { listener?.onFocusStatusChanged(focusStatus) }
            }

            // contShootingMode (v1.2)
            val contShootingMode = findContShootingMode(replyJson) //?: ""
            Log.d(TAG, "getEvent contShootingMode:$contShootingMode")
            if (contShootingMode != currentContShootingMode)
            {
                currentContShootingMode = contShootingMode
                uiHandler.post { listener?.onDriveModeChanged(contShootingMode) }
            }

        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun findAvailableApiList(replyJson: JSONObject): List<String?>
    {
        val availableApis: MutableList<String?> = ArrayList()
        val indexOfAvailableApiList = 0
        try
        {
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfAvailableApiList))
            {
                val availableApiListObj = resultsObj.getJSONObject(indexOfAvailableApiList)
                val type = availableApiListObj.getString("type")
                if ("availableApiList" == type)
                {
                    val apiArray = availableApiListObj.getJSONArray("names")
                    for (i in 0 until apiArray.length())
                    {
                        availableApis.add(apiArray.getString(i))
                    }
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (0: AvailableApiList) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (availableApis)
    }

    private fun findCameraStatus(replyJson: JSONObject): String
    {
        var cameraStatus = ""
        val indexOfCameraStatus = 1
        try
        {
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfCameraStatus))
            {
                val cameraStatusObj = resultsObj.getJSONObject(indexOfCameraStatus)
                val type = cameraStatusObj.getString("type")
                if ("cameraStatus" == type)
                {
                    cameraStatus = cameraStatusObj.getString("cameraStatus")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (1: CameraStatus) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (cameraStatus)
    }

    private fun findLiveviewStatus(replyJson: JSONObject): Boolean
    {
        var liveviewStatus = false
        try
        {
            val indexOfLiveviewStatus = 3
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfLiveviewStatus))
            {
                val liveviewStatusObj = resultsObj.getJSONObject(indexOfLiveviewStatus)
                val type = liveviewStatusObj.getString("type")
                if ("liveviewStatus" == type)
                {
                    liveviewStatus = liveviewStatusObj.getBoolean("liveviewStatus")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (3: LiveviewStatus) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (liveviewStatus)
    }

    private fun findShootMode(replyJson: JSONObject): String
    {
        var shootMode = ""
        try
        {
            val indexOfShootMode = 21
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfShootMode))
            {
                val shootModeObj = resultsObj.getJSONObject(indexOfShootMode)
                val type = shootModeObj.getString("type")
                if ("shootMode" == type)
                {
                    shootMode = shootModeObj.getString("currentShootMode")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (21: ShootMode) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (shootMode)
    }

    private fun findAvailableShootModes(replyJson: JSONObject): List<String>
    {
        val shootModes: MutableList<String> = ArrayList()
        try
        {
            val indexOfShootMode = 21
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfShootMode))
            {
                val shootModesObj = resultsObj.getJSONObject(indexOfShootMode)
                val type = shootModesObj.getString("type")
                if ("shootMode" == type)
                {
                    val shootModesArray = shootModesObj.getJSONArray("shootModeCandidates")
                    for (i in 0 until shootModesArray.length())
                    {
                        shootModes.add(shootModesArray.getString(i))
                    }
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (21: ShootMode) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (shootModes)
    }

    private fun findZoomInformation(replyJson: JSONObject): Int
    {
        var zoomPosition = -1
        try
        {
            val indexOfZoomInformation = 2
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfZoomInformation))
            {
                val zoomInformationObj = resultsObj.getJSONObject(indexOfZoomInformation)
                val type = zoomInformationObj.getString("type")
                if ("zoomInformation" == type)
                {
                    zoomPosition = zoomInformationObj.getInt("zoomPosition")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (2: zoomInformation) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (zoomPosition)
    }

    private fun findStorageId(replyJson: JSONObject): String
    {
        var storageId = ""
        try
        {
            val indexOfStorageInformation = 10
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfStorageInformation))
            {
                val storageInformationArray = resultsObj.getJSONArray(indexOfStorageInformation)
                if (!storageInformationArray.isNull(0))
                {
                    val storageInformationObj = storageInformationArray.getJSONObject(0)
                    val type = storageInformationObj.getString("type")
                    if ("storageInformation" == type)
                    {
                        storageId = storageInformationObj.getString("storageID")
                    }
                    else
                    {
                        Log.w(TAG, "Event reply: Illegal Index (10: storageInformation) $type")
                    }
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (storageId)
    }

    private fun findFocusStatus(replyJson: JSONObject): String
    {
        var focusStatus = ""
        try
        {
            val indexOfFocusStatus = 35
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfFocusStatus))
            {
                val focusStatusObj = resultsObj.getJSONObject(indexOfFocusStatus)
                val type = focusStatusObj.getString("type")
                if ("focusStatus" == type)
                {
                    focusStatus = focusStatusObj.getString("focusStatus")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (35: focusStatus) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (focusStatus)
    }

    private fun findContShootingMode(replyJson: JSONObject): String
    {
        var contShootingMode = ""
        try
        {
            val indexOfContShootingMode = 38
            val resultsObj = replyJson.getJSONArray("result")
            if (!resultsObj.isNull(indexOfContShootingMode))
            {
                val focusStatusObj = resultsObj.getJSONObject(indexOfContShootingMode)
                val type = focusStatusObj.getString("type")
                if ("focusStatus" == type)
                {
                    contShootingMode = focusStatusObj.getString("contShootingMode")
                }
                else
                {
                    Log.w(TAG, "Event reply: Illegal Index (38: contShootingMode) $type")
                }
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (contShootingMode)
    }

    fun setEventChangeListener(listener: ICameraChangeListener?)
    {
        this.listener = listener
    }

    fun clearEventChangeListener()
    {
        listener = null
    }

    fun catchResponseError()
    {
        uiHandler.post { listener?.onResponseError() }
    }

    override fun getCameraStatus(): String
    {
        return cameraStatus
    }

    override fun getLiveviewStatus(): Boolean
    {
        return currentLiveviewStatus
    }

    override fun getShootMode(): String
    {
        return currentShootMode
    }

    override fun getAvailableShootModes(): List<String?>
    {
        return currentAvailableShootModes
    }

    override fun getZoomPosition(): Int
    {
        return currentZoomPosition
    }

    override fun getStorageId(): String
    {
        return currentStorageId
    }

    companion object
    {
        private val TAG = ReplyJsonParser::class.java.simpleName
    }
}
