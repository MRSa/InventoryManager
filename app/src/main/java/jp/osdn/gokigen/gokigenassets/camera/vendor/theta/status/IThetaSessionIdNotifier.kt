package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status


interface IThetaSessionIdNotifier
{
    fun receivedSessionId(sessionId: String?)
}
