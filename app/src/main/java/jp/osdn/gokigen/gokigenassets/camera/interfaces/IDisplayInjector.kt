package jp.osdn.gokigen.gokigenassets.camera.interfaces

import jp.osdn.gokigen.gokigenassets.liveview.IIndicatorControl
import jp.osdn.gokigen.gokigenassets.liveview.focusframe.IAutoFocusFrameDisplay


interface IDisplayInjector
{
    fun injectDisplay(
        frameDisplayer: IAutoFocusFrameDisplay,
        indicator: IIndicatorControl,
        focusingModeNotify: IFocusingModeNotify
    )
}
