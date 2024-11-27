package jp.osdn.gokigen.gokigenassets.camera.vendor.sony.wrapper.eventlistener

import org.json.JSONObject

interface ISonyStatusReceiver
{
    fun updateStatus(jsonObject: JSONObject)
}