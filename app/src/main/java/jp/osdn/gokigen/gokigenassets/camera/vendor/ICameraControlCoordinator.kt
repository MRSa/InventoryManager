package jp.osdn.gokigen.gokigenassets.camera.vendor

interface ICameraControlCoordinator
{
    fun startConnectToCamera(number: Int) : Boolean
    //fun isOtherCameraConnecting(number: Int) : Boolean

    fun isAlreadyAssignedCameraControl(number: Int) : Boolean
    fun releaseCameraControl(number: Int)

    fun assignCameraControl(number: Int, deviceId: String)
    fun isAssignedCameraControl(deviceId: String) : Boolean
}
