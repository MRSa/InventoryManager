package jp.osdn.gokigen.gokigenassets.camera.vendor.theta.status

import android.util.Log

class ThetaSessionHolder() : IThetaSessionIdProvider, IThetaSessionIdNotifier
{
    override fun receivedSessionId(sessionId: String?)
    {
        if (!(sessionId.isNullOrEmpty()))
        {
            Log.v(TAG, " SESSION ID : $sessionId")
            this.sessionId = sessionId
        }
    }

    fun isApiLevelV21() : Boolean
    {
        return (sessionId.isEmpty())
    }

    companion object
    {
        private val TAG = ThetaSessionHolder::class.java.simpleName
    }

    override var sessionId: String = ""

}
