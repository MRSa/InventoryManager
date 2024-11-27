package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICameraInformation
{
    val isManualFocus: Boolean
    val isElectricZoomLens: Boolean
    val isExposureLocked: Boolean
}
