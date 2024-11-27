package jp.osdn.gokigen.gokigenassets.camera.vendor

import android.util.Log
import jp.osdn.gokigen.gokigenassets.scene.IInformationReceiver

class CameraControlCoordinator(private val informationReceiver: IInformationReceiver) : ICameraControlCoordinator
{
    private val cameraDeviceMap = mutableMapOf<Int, String>()
    private val cameraControlInfo = mutableMapOf<Int, String>()
    private var connectingCameraNumber = -1

    override fun startConnectToCamera(number: Int) : Boolean
    {
        Log.v(TAG, "startConnectToCamera($number) : $connectingCameraNumber")
        if (connectingCameraNumber == -1)
        {
            connectingCameraNumber = number
        }
        return (connectingCameraNumber == number)
    }

    override fun isAlreadyAssignedCameraControl(number: Int): Boolean
    {
        try
        {
            return (cameraDeviceMap.containsKey(number))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    override fun releaseCameraControl(number: Int)
    {
        try
        {
            Log.v(TAG, "releaseCameraControl($number)")
            cameraDeviceMap.remove(number)
            cameraControlInfo.remove(number)
            connectingCameraNumber = -1
            updateInformation()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun assignCameraControl(number: Int, deviceId: String)
    {
        try
        {
            Log.v(TAG, "assignCameraControl($number, $deviceId)")
            cameraDeviceMap[number] = deviceId
            cameraControlInfo[number] = "$number "
            connectingCameraNumber = -1
            updateInformation()
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun isAssignedCameraControl(deviceId: String): Boolean
    {
        try
        {
            return (cameraDeviceMap.containsValue(deviceId))
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun updateInformation()
    {
        var message = ""
        for ((_, v) in cameraControlInfo)
        {
            message += v
        }
        informationReceiver.updateMessage(message)

    }

    companion object
    {
        private val TAG = CameraControlCoordinator::class.java.simpleName
    }
}
