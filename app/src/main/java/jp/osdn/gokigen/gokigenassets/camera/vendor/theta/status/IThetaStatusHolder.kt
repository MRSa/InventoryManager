package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status


interface IThetaStatusHolder
{
    var captureMode: String
    var captureStatus : String

    fun invalidate()
}
