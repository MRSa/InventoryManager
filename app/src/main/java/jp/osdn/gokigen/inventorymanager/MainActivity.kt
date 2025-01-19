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
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import jp.osdn.gokigen.inventorymanager.liaison.CameraLiaison
import jp.osdn.gokigen.inventorymanager.preference.PreferenceValueInitializer
import jp.osdn.gokigen.inventorymanager.ui.component.ViewRootComponent
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel
import jp.osdn.gokigen.gokigenassets.scene.IVibrator
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromInternet
import jp.osdn.gokigen.inventorymanager.storage.DataContent
import jp.osdn.gokigen.inventorymanager.storage.InventoryDataHolder
import jp.osdn.gokigen.inventorymanager.ui.model.DataMaintenanceViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.DetailInventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.PreferenceViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel

class MainActivity : AppCompatActivity()
{
    private lateinit var db : InventoryDataHolder
    private lateinit var rootComponent : ViewRootComponent
    private lateinit var myViewModel : ListViewModel
    private lateinit var myRegistViewModel : RegisterInformationViewModel
    private lateinit var myPreferenceViewModel : PreferenceViewModel
    private lateinit var myDetailViewModel : DetailInventoryViewModel
    private lateinit var myDataMaintenanceViewModel : DataMaintenanceViewModel
    private lateinit var liaison : CameraLiaison
    private val dataExporter =  DataExporter(this)
    private val dataImporter =  DataImporter(this)
    private val recognizerFromInternet = RecognizeFromInternet(this)

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

            db = AppSingleton.db

            myRegistViewModel = ViewModelProvider(this)[RegisterInformationViewModel::class.java]
            myRegistViewModel.initializeViewModel(this)

            myPreferenceViewModel = ViewModelProvider(this)[PreferenceViewModel::class.java]
            myPreferenceViewModel.initializeViewModel(this)

            liaison = CameraLiaison(this, myRegistViewModel, myRegistViewModel, myRegistViewModel)

            myViewModel = ViewModelProvider(this)[ListViewModel::class.java]
            myViewModel.initializeViewModel()

            myDetailViewModel = ViewModelProvider(this)[DetailInventoryViewModel::class.java]
            myDetailViewModel.initializeViewModel()

            myDataMaintenanceViewModel = ViewModelProvider(this)[DataMaintenanceViewModel::class.java]
            myDataMaintenanceViewModel.initializeViewModel(this)

            ///////// SET ROOT VIEW /////////
            rootComponent = ViewRootComponent(applicationContext)
            rootComponent.setLiaisons(
                viewModel = myViewModel,
                registScreenViewModel = myRegistViewModel,
                preferenceViewModel = myPreferenceViewModel,
                detailViewModel = myDetailViewModel,
                dataMaintenanceViewModel = myDataMaintenanceViewModel,
                liaison = liaison,
                exporter = dataExporter,
                importer = dataImporter,
                recognizer = recognizerFromInternet)

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
                        // 必要な権限がそろっていないことを通知する
                        Toast.makeText(this, getString(R.string.permission_not_granted), Toast.LENGTH_SHORT).show()
                        Log.v(TAG, "----- APPLICATION LAUNCH ABORTED -----")
                        AppSingleton.vibrator.vibrate(this, IVibrator.VibratePattern.SIMPLE_LONG)
                        finish()
                    }
                    else
                    {
                        // ----- for debug
                        dumpDatabase()
                    }
                }
                requestPermission.launch(REQUIRED_PERMISSIONS)
            }
            else
            {
                // ----- for debug
                dumpDatabase()
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

    private fun dumpDatabase()
    {
        try
        {
            if ((DEBUG_LOG)&&(AppSingleton.isReadyDatabase))
            {
                Thread {
                    try
                    {
                        val contents: List<DataContent> = db.storageDao().getAll()
                        Log.v(TAG, " = = = = = number of contents : ${contents.count()} = = = = =")
                        var index = 1
                        for (value in contents)
                        {
                            Log.v(TAG, "  $value")
                            index++
                        }
                    }
                    catch (e: Exception)
                    {
                        e.printStackTrace()
                    }
                }.start()
            }
        }
        catch (e: Exception)
        {
            e.printStackTrace()
        }
    }

    companion object
    {
        private val TAG = MainActivity::class.java.simpleName
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.VIBRATE,
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            //Manifest.permission.ACCESS_NETWORK_STATE,
            //Manifest.permission.ACCESS_WIFI_STATE,
            //Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
        )
        private const val DEBUG_LOG = false
    }
}
