package jp.osdn.gokigen.gokigenassets.liveview

interface IIndicatorControl
{
    // 撮影状態の記録
    enum class shootingStatus {
        Unknown, Starting, Stopping
    }

    fun onAfLockUpdate(isAfLocked: Boolean)
    fun onShootingStatusUpdate(status: shootingStatus?)
    fun onMovieStatusUpdate(status: shootingStatus?)
    fun onBracketingStatusUpdate(message: String?)
}
