package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraHardwareStatus


class RicohGr2HardwareStatus : ICameraHardwareStatus
{
    override fun isAvailableHardwareStatus(): Boolean { return false }
    override fun getLensMountStatus(): String? { return null }
    override fun getMediaMountStatus(): String? { return null }
    override fun getMinimumFocalLength(): Float { return 0.0f }
    override fun getMaximumFocalLength(): Float { return 0.0f }
    override fun getActualFocalLength(): Float { return 0.0f }
    override fun inquireHardwareInformation(): Map<String, Any?>? { return null }
}
