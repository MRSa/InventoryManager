package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.wrapper

import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICardSlotSelectionReceiver
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICardSlotSelector
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraChangeListener

class PanasonicCardSlotSelector() : ICardSlotSelector, ICameraChangeListener
{
    override fun setupSlotSelector(isEnable: Boolean, slotSelectionReceiver: ICardSlotSelectionReceiver?)
    {
        // TODO("Not yet implemented")
    }

    override fun selectSlot(slotId: String)
    {
        // TODO("Not yet implemented")
    }

    override fun changedCardSlot(slotId: String)
    {
        // TODO("Not yet implemented")
    }

    override fun onApiListModified(apis: List<String?>?)
    {
        // TODO("Not yet implemented")
    }

    override fun onCameraStatusChanged(status: String?)
    {
        // TODO("Not yet implemented")
    }

    override fun onLiveviewStatusChanged(status: Boolean)
    {
        // TODO("Not yet implemented")
    }

    override fun onShootModeChanged(shootMode: String?)
    {
        // TODO("Not yet implemented")
    }

    override fun onZoomPositionChanged(zoomPosition: Int)
    {
        // TODO("Not yet implemented")
    }

    override fun onStorageIdChanged(storageId: String?)
    {
        // TODO("Not yet implemented")
    }

    override fun onFocusStatusChanged(focusStatus: String?)
    {
        // TODO("Not yet implemented")
    }

    override fun onDriveModeChanged(driveMode: String?) {
        // TODO("Not yet implemented")
    }

    override fun onResponseError()
    {
        // TODO("Not yet implemented")
    }
}
