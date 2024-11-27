package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraStatusReceiver
{
    fun onStatusNotify(message: String?)
    fun onCameraConnected()
    fun onCameraDisconnected()
    fun onCameraConnectError(msg: String?)
}
