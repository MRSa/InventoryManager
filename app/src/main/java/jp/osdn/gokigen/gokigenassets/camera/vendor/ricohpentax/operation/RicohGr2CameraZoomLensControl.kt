package jp.osdn.gokigen.gokigenassets.camera.vendor.ricohpentax.operation

import jp.osdn.gokigen.gokigenassets.camera.interfaces.IZoomLensControl

/**
 *
 *
 */
class RicohGr2CameraZoomLensControl : IZoomLensControl
{
    /**
     *
     *
     */
    override fun canZoom(): Boolean
    {
        return (false)
    }

    /**
     *
     *
     */
    override fun updateStatus() {}

    /**
     *
     *
     */
    override fun getMaximumFocalLength() : Float
    {
        return (0.0f)
    }

    /**
     *
     *
     */
    override fun getMinimumFocalLength(): Float
    {
        return (0.0f)
    }

    /**
     *
     *
     */
    override fun getCurrentFocalLength(): Float
    {
        return (0.0f)
    }

    /**
     *
     *
     */
    override fun driveZoomLens(targetLength: Float) {}

    /**
     *
     *
     */
    override fun driveZoomLens(isZoomIn: Boolean) {}

    /**
     *
     *
     */
    override fun moveInitialZoomPosition() {}

    /**
     *
     *
     */
    override fun isDrivingZoomLens(): Boolean
    {
        return (false)
    }
}
