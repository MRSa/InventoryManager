package jp.osdn.gokigen.gokigenassets.camera.interfaces

interface ICardSlotSelector
{
    fun setupSlotSelector(isEnable: Boolean, slotSelectionReceiver: ICardSlotSelectionReceiver?)
    fun selectSlot(slotId: String)
    fun changedCardSlot(slotId: String)
}
