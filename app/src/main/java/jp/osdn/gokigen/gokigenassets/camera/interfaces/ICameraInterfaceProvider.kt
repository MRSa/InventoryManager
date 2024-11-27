package jp.osdn.gokigen.gokigenassets.camera.interfaces

import jp.osdn.gokigen.gokigenassets.camera.interfaces.playback.IPlaybackControl
import jp.osdn.gokigen.gokigenassets.liveview.image.ILiveViewListener

interface ICameraInterfaceProvider
{
    fun getCameraInterfaceName() : String
    fun getCameraControl() : ICameraControl
    fun getCameraConnection(): ICameraConnection
    fun getFocusingControl(): IFocusingControl
    fun getZoomLensControl(): IZoomLensControl
    fun getCaptureControl(): ICaptureControl
    fun getCameraStatusListHolder(): ICameraStatus
    fun getHardwareStatus(): ICameraHardwareStatus
    fun getCameraRunMode(): ICameraRunMode
    fun getCameraStatusWatcher(): ICameraStatusWatcher
    fun getLiveViewControl(): ILiveViewController
    fun getLiveViewListener(): ILiveViewListener
    fun getCameraInformation(): ICameraInformation

    fun getDisplayInjector(): IDisplayInjector?
    fun getButtonControl(): ICameraButtonControl?

    fun getPlaybackControl(): IPlaybackControl?
}
