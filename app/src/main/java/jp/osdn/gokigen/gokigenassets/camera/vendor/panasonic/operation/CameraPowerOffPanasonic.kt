package jp.osdn.gokigen.gokigenassets.camera.vendor.panasonic.operation

import android.content.Context
import androidx.preference.Preference
import jp.osdn.gokigen.constants.IPreferenceConstantConvert.Companion.ID_PREFERENCE_LABEL_EXIT_APPLICATION
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_DIALOG_EXIT_POWER_OFF
import jp.osdn.gokigen.constants.IStringResourceConstantConvert.Companion.ID_DIALOG_TITLE_CONFIRMATION
import jp.osdn.gokigen.gokigenassets.scene.IChangeSceneBasic
import jp.osdn.gokigen.gokigenassets.utils.ConfirmationDialog

class CameraPowerOffPanasonic(private val context: Context, private val changeScene: IChangeSceneBasic) : Preference.OnPreferenceClickListener, ConfirmationDialog.ConfirmationCallback
{
    private var preferenceKey: String? = null

    /**
     * クラスの準備
     *
     */
    fun prepare()
    {
        // 何もしない
    }

    /**
     *
     *
     * @param preference クリックしたpreference
     * @return false : ハンドルしない / true : ハンドルした
     */
    override fun onPreferenceClick(preference: Preference): Boolean
    {
        if (!preference.hasKey())
        {
            return false
        }
        preferenceKey = preference.key
        val isContain = preferenceKey?.contains(ID_PREFERENCE_LABEL_EXIT_APPLICATION)
        if ((isContain != null)&&(isContain))
        {

            // 確認ダイアログの生成と表示
            val dialog: ConfirmationDialog = ConfirmationDialog.newInstance(context)
            dialog.show(
                ID_DIALOG_TITLE_CONFIRMATION,
                ID_DIALOG_EXIT_POWER_OFF, this)
            return true
        }
        return false
    }

    /**
     *
     *
     */
    override fun confirm()
    {
        val isContain = preferenceKey?.contains(ID_PREFERENCE_LABEL_EXIT_APPLICATION)
        if ((isContain != null)&&(isContain))
        {
            // カメラの電源をOFFにしたうえで、アプリケーションを終了する。
            changeScene.exitApplication()
        }
    }
}
