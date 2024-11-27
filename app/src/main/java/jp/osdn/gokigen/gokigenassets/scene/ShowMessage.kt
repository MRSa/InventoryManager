package jp.osdn.gokigen.gokigenassets.scene

import android.graphics.Color
import android.graphics.Typeface
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.constants.IApplicationConstantConvert.Companion.ID_AREA_MESSAGE

class ShowMessage(private val activity : AppCompatActivity) : IInformationReceiver
{
    private var currentMessage : String = ""
    override fun updateMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        currentMessage = message
        val messageArea = activity.findViewById<TextView>(ID_AREA_MESSAGE)
        activity.runOnUiThread {
            messageArea?.text = message
            if (isBold)
            {
                messageArea?.typeface = Typeface.DEFAULT_BOLD
            }
            if (isColor)
            {
                messageArea?.setTextColor(color)
            }
            else
            {
                messageArea?.setTextColor(Color.DKGRAY)
            }
            messageArea?.invalidate()
        }
    }

    override fun appendMessage(message: String, isBold: Boolean, isColor: Boolean, color: Int)
    {
        updateMessage(currentMessage + message, isBold, isColor, color)
    }

    override fun getCurrentMessage(): String
    {
        return (currentMessage)
    }

}
