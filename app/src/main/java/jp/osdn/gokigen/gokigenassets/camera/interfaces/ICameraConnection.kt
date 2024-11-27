package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraConnection
{
    fun alertConnectingFailed(message: String?)
    fun forceUpdateConnectionStatus(status: ICameraConnectionStatus.CameraConnectionStatus)
}
