package jp.osdn.gokigen.inventorymanager

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import jp.osdn.gokigen.inventorymanager.liaison.CameraLiaison
import jp.osdn.gokigen.inventorymanager.preference.PreferenceValueInitializer
import jp.osdn.gokigen.inventorymanager.ui.component.ViewRootComponent
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel

class MainActivity : AppCompatActivity(), AppSingleton.PreparationCallback
{
    private lateinit var rootComponent : ViewRootComponent
    private lateinit var myViewModel : InventoryViewModel
    private lateinit var myRegistViewModel : RegisterInformationViewModel
    private lateinit var liaison : CameraLiaison

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        try
        {
            ///////// SHOW SPLASH SCREEN /////////
            installSplashScreen()

            enableEdgeToEdge()

            ///////// INITIALIZATION /////////
            PreferenceValueInitializer().initializePreferences(this)

            myViewModel = ViewModelProvider(this)[InventoryViewModel::class.java]
            myViewModel.initializeViewModel(this)

            myRegistViewModel = ViewModelProvider(this)[RegisterInformationViewModel::class.java]
            myRegistViewModel.initializeViewModel(this)

            liaison = CameraLiaison(this, myViewModel, myRegistViewModel)

            ///////// SET ROOT VIEW /////////
            rootComponent = ViewRootComponent(applicationContext)
            rootComponent.setLiaisons(myViewModel, myRegistViewModel, liaison)
            setContent {
                //Box(Modifier.safeDrawingPadding()) {
                    rootComponent.Content()
                //}
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
        Log.v(TAG, "...MainActivity...")

        supportActionBar?.hide()
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        try
        {
            ///////// SET PERMISSIONS /////////
            if (!allPermissionsGranted())
            {
                val requestPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {

                    ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
                    if(!allPermissionsGranted())
                    {
                        // Abort launch application because required permissions was rejected.
                        Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                        Log.v(TAG, "----- APPLICATION LAUNCH ABORTED -----")
                        AppSingleton.vibrator.vibrate(this, IVibrator.VibratePattern.SIMPLE_LONG)
                        finish()
                    }
                    else
                    {
                        // ----- アプリケーションの初期化を行う
                        AppSingleton.prepareApplication(this)
                    }
                }
                requestPermission.launch(REQUIRED_PERMISSIONS)
            }
            else
            {
                // ----- アプリケーションの初期化を行う
                AppSingleton.prepareApplication(this)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    override fun finishedPreparation(result: Boolean, detail: String)
    {
        try
        {
            Log.v(TAG, "finishedPreparation() : $result [Detail:$detail]")
            if (!result)
            {
                // ----- 起動に失敗...メッセージをToast表示
                val message = "${getString(R.string.permission_not_granted)} : $detail"
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
            if (::myViewModel.isInitialized)
            {
                // ---- DBが初期化できたことを設定する
                myViewModel.setIsReadyDatabase(result)
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    private fun allPermissionsGranted() : Boolean
    {
        var result = true
        for (param in REQUIRED_PERMISSIONS)
        {
            if (ContextCompat.checkSelfPermission(
                    baseContext,
                    param
                ) != PackageManager.PERMISSION_GRANTED
            )
            {
                // Permission Denied...
                if ((param == Manifest.permission.ACCESS_MEDIA_LOCATION)&&(Build.VERSION.SDK_INT < Build.VERSION_CODES.Q))
                {
                    //　この場合は権限付与の判断を除外 (デバイスが (10) よりも古く、ACCESS_MEDIA_LOCATION がない場合）
                }
                else if ((param == Manifest.permission.READ_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになるので...)
                }
                else if ((param == Manifest.permission.WRITE_EXTERNAL_STORAGE)&&(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU))
                {
                    // この場合は、権限付与の判断を除外 (SDK: 33以上はエラーになるので...)
                }
                else
                {
                    // ----- 権限が得られなかった場合...
                    Log.v(TAG, " Permission: $param : ${Build.VERSION.SDK_INT}")
                    result = false
                }
            }
        }
        return (result)
    }

    companion object
    {
        private val TAG = MainActivity::class.java.simpleName
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.INTERNET,
            //Manifest.permission.ACCESS_NETWORK_STATE,
            //Manifest.permission.ACCESS_WIFI_STATE,
            //Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
        )
    }
}
