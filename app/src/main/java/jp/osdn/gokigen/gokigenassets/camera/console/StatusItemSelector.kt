package jp.osdn.gokigen.gokigenassets.camera.console

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraStatus
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.gokigenassets.utils.ItemSelectionDialog
import java.lang.Exception

class StatusItemSelector(val activity : AppCompatActivity, private val vibrator : IVibrator) : ItemSelectionDialog.ItemSelectedCallback
{
    private var cameraControl : ICameraControl? = null
    companion object
    {
        private val TAG = StatusItemSelector::class.java.simpleName
    }

    fun itemSelected(cameraControl : ICameraControl?, widthPosition: Int, heightPosition: Int): Boolean
    {
        this.cameraControl = cameraControl
        if (cameraControl == null)
        {
            return (false)
        }
        //  タッチ位置をステータスに変換する
        try
        {
            var key = ""
            if (widthPosition == 0)
            {
                if (heightPosition < 2)
                {
                    //Log.v(TAG, " Tapped MODE")
                    key = ICameraStatus.TAKE_MODE
                }
                else if (heightPosition < 4)
                {
                    //Log.v(TAG, " Tapped ISO Sensitivity")
                    key = ICameraStatus.ISO_SENSITIVITY
                }
                else if (heightPosition < 6)
                {
                    //Log.v(TAG, " Tapped White Balance")
                    key = ICameraStatus.WHITE_BALANCE
                }
                else if (heightPosition < 7)
                {
                    //Log.v(TAG, " Tapped Focus Status")
                    key = ICameraStatus.FOCUS_STATUS
                }
            }
            else if (widthPosition == 1)
            {
                if (heightPosition < 2)
                {
                    //Log.v(TAG, " Tapped Shutter Speed")
                    key = ICameraStatus.SHUTTER_SPEED
                }
                else if (heightPosition < 4)
                {
                    //Log.v(TAG, " Tapped Exposure Compensation")
                    key = ICameraStatus.EXPREV
                }
                else if (heightPosition < 6)
                {
                    //Log.v(TAG, " Tapped Picture Effect")
                    key = ICameraStatus.EFFECT
                }
            }
            else if (widthPosition == 2)
            {
                if (heightPosition < 2)
                {
                    //Log.v(TAG, " Tapped Aperture")
                    key = ICameraStatus.APERTURE
                }
                else if (heightPosition < 4)
                {
                    //Log.v(TAG, " Tapped Auto Exposure")
                    key = ICameraStatus.AE
                }
                else if (heightPosition < 6)
                {
                    //Log.v(TAG, " Tapped Capture Mode")
                    key = ICameraStatus.CAPTURE_MODE
                }
                else if (heightPosition < 7)
                {
                    //Log.v(TAG, " Tapped Torch Mode")
                    key = ICameraStatus.TORCH_MODE
                }
            }
            if (key.isNotEmpty())
            {
                val thread = Thread {
                    val currentStatus = cameraControl.getCameraStatus()?.getStatus(key) ?: ""
                    val statusList = cameraControl.getCameraStatus()?.getStatusList(key) ?: ArrayList<String>()
                    if (statusList.isNotEmpty())
                    {
                        try
                        {
                            vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_SHORT)
                        }
                        catch (e: Exception)
                        {
                            e.printStackTrace()
                        }
                        activity.runOnUiThread() {
                            try
                            {
                                // 選択肢がある場合は、アイテム選択ダイアログを表示して選択を促す
                                showItemSelectionDialog(key, statusList, currentStatus)
                            }
                            catch (e: Exception)
                            {
                                e.printStackTrace()
                            }
                        }
                    }
                }
                thread.start()
                return (true)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    private fun showItemSelectionDialog(key: String, selectionItems: List<String?>, currentStatus: String) : Boolean
    {
        try
        {
            // アイテム選択ダイアログを表示する
            // Log.v(TAG, " showItemSelectionDialog($key)")
            val selectionDialog = ItemSelectionDialog.newInstance(activity)
            selectionDialog.show(0, currentStatus, key, selectionItems, this)
            return (false)
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        return (false)
    }

    //　　ItemSelectionDialog.ItemSelectedCallback
    override fun itemSelected(key: String, selectedItem: String)
    {
        // 選択されたアイテムに合わせて、値を設定する
        Log.v(TAG, "itemSelected  $key, $selectedItem")
        try
        {
            val thread = Thread {
                try
                {
                    vibrator.vibrate(IVibrator.VibratePattern.SIMPLE_MIDDLE)
                    cameraControl?.getCameraStatus()?.setStatus(key, selectedItem)
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
                try
                {
                    System.gc()
                }
                catch (e: Exception)
                {
                    e.printStackTrace()
                }
            }
            thread.start()

        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }
}
