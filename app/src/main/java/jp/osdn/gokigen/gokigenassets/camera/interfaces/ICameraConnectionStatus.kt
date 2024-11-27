package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraConnectionStatus
{
    enum class CameraConnectionStatus
    {
        UNKNOWN,  DISCONNECTED, CONNECTING, CONNECTED
    }

    fun getConnectionStatus(): CameraConnectionStatus
}

