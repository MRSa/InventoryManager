package jp.osdn.gokigen.inventorymanager.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.inventorymanager.export.DataExporter
import jp.osdn.gokigen.inventorymanager.import.DataImporter
import jp.osdn.gokigen.inventorymanager.liaison.CameraLiaison
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromInternet
import jp.osdn.gokigen.inventorymanager.ui.model.DataMaintenanceViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.DetailInventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.ListViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.PreferenceViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var myViewModel : ListViewModel
    private lateinit var myRegistViewModel : RegisterInformationViewModel
    private lateinit var myPreferenceViewModel : PreferenceViewModel
    private lateinit var myDetailViewModel : DetailInventoryViewModel
    private lateinit var myDataMaintenanceViewModel : DataMaintenanceViewModel
    private lateinit var myLiaison : CameraLiaison
    private lateinit var myExporter : DataExporter
    private lateinit var myImporter : DataImporter
    private lateinit var myRecognizer : RecognizeFromInternet

    fun setLiaisons(
        viewModel : ListViewModel,
        registScreenViewModel: RegisterInformationViewModel,
        preferenceViewModel: PreferenceViewModel,
        detailViewModel: DetailInventoryViewModel,
        dataMaintenanceViewModel: DataMaintenanceViewModel,
        liaison: CameraLiaison,
        exporter: DataExporter,
        importer: DataImporter,
        recognizer: RecognizeFromInternet)
    {
        this.myViewModel = viewModel
        this.myRegistViewModel = registScreenViewModel
        this.myPreferenceViewModel = preferenceViewModel
        this.myDetailViewModel = detailViewModel
        this.myDataMaintenanceViewModel = dataMaintenanceViewModel
        this.myLiaison = liaison
        this.myExporter = exporter
        this.myImporter = importer
        this.myRecognizer = recognizer
        Log.v(TAG, " ...setLiaisons...")
    }

    @Composable
    override fun Content()
    {
        val navController: NavHostController = rememberNavController()
        //Surface(Modifier.safeDrawingPadding()) {  // これだと paddingサイズが大きすぎる...
        //Surface(Modifier.systemBarsPadding()) {   // これも paddingサイズが大きすぎる...
        Surface {
            val cameraControl = this.myLiaison.getCameraControl()
            NavigationMain(
                navController = navController,
                cameraControl = cameraControl,
                registViewModel = this.myRegistViewModel,
                listModel = this.myViewModel,
                preferenceViewModel = this.myPreferenceViewModel,
                detailViewModel = this.myDetailViewModel,
                dataMaintenanceViewModel = myDataMaintenanceViewModel,
                onTouchListener = LiveViewOnTouchListener(cameraControl),
                anotherDrawer = this.myLiaison.getAnotherDrawer(),
                exporter = this.myExporter,
                importer = this.myImporter,
                recognizer = this.myRecognizer)
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(
    navController: NavHostController,
    cameraControl: ICameraControl,
    registViewModel : RegisterInformationViewModel,
    listModel : ListViewModel,
    preferenceViewModel: PreferenceViewModel,
    detailViewModel: DetailInventoryViewModel,
    dataMaintenanceViewModel: DataMaintenanceViewModel,
    onTouchListener: LiveViewOnTouchListener,
    anotherDrawer: IAnotherDrawer?,
    exporter: DataExporter,
    importer: DataImporter,
    recognizer: RecognizeFromInternet
)
{
    MaterialTheme {
        NavHost(
            modifier = Modifier.systemBarsPadding(),
            navController = navController,
            startDestination = "MainScreen"
        ) {
            composable("MainScreen") {
                MainScreen(navController = navController, cameraControl = cameraControl)
            }
            composable("RegistScreen") {
                RegistScreen(navController = navController, cameraControl = cameraControl, viewModel = registViewModel, onTouchListener = onTouchListener, anotherDrawer = anotherDrawer)
            }
            composable("ListScreen") {
                ListScreen(navController = navController, viewModel = listModel, exporter = exporter, recognizer = recognizer)
            }
            composable("DetailScreen/{id}", listOf(navArgument("id") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0
                DetailScreen(navController = navController, viewModel = detailViewModel, id = id, recognizer = recognizer)
            }
            composable("PreferenceScreen") {
                PreferenceScreen(navController = navController, prefsModel = preferenceViewModel)
            }
            composable("DataMaintenanceScreen") {
                DataImportScreen(navController = navController, viewModel = dataMaintenanceViewModel, dataImporter = importer)
            }
        }
    }
}
