package jp.osdn.gokigen.gokigenassets.liveview

import android.util.Log

class IndicatorControl() : IIndicatorControl
{
    companion object
    {
        private val TAG = IndicatorControl::class.java.simpleName
    }

    override fun onAfLockUpdate(isAfLocked: Boolean)
    {
        Log.v(TAG, "onAfLockUpdate($isAfLocked)")
    }

    override fun onShootingStatusUpdate(status: IIndicatorControl.shootingStatus?)
    {
        Log.v(TAG, "onShootingStatusUpdate($status)")
    }

    override fun onMovieStatusUpdate(status: IIndicatorControl.shootingStatus?)
    {
        Log.v(TAG, "onMovieStatusUpdate($status)")
    }

    override fun onBracketingStatusUpdate(message: String?)
    {
        Log.v(TAG, "onBracketingStatusUpdate : $message")
    }
}
