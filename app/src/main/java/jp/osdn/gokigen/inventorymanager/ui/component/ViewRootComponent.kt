package jp.osdn.gokigen.inventorymanager.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import jp.osdn.gokigen.inventorymanager.liaison.CameraLiaison
import jp.osdn.gokigen.inventorymanager.recognize.RecognizeFromIsbn
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.PreferenceViewModel
import jp.osdn.gokigen.inventorymanager.ui.model.RegisterInformationViewModel

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var myViewModel : InventoryViewModel
    private lateinit var myRegistViewModel : RegisterInformationViewModel
    private lateinit var myPreferenceViewModel : PreferenceViewModel
    private lateinit var myLiaison : CameraLiaison
    private lateinit var myExporter : DataExporter
    private lateinit var myRecognizer : RecognizeFromIsbn


    fun setLiaisons(viewModel : InventoryViewModel, registScreenViewModel: RegisterInformationViewModel, preferenceViewModel: PreferenceViewModel, liaison : CameraLiaison, exporter: DataExporter, recognizer: RecognizeFromIsbn)
    {
        this.myViewModel = viewModel
        this.myRegistViewModel = registScreenViewModel
        this.myPreferenceViewModel = preferenceViewModel
        this.myLiaison = liaison
        this.myExporter = exporter
        this.myRecognizer = recognizer
        Log.v(TAG, " ...setLiaisons...")
    }

    @Composable
    override fun Content()
    {
        val navController: NavHostController = rememberNavController()
        Surface {
            val cameraControl = this.myLiaison.getCameraControl()
            NavigationMain(navController, cameraControl, this.myRegistViewModel, this.myViewModel, this.myPreferenceViewModel, LiveViewOnTouchListener(cameraControl), this.myLiaison.getAnotherDrawer(), this.myExporter, this.myRecognizer)
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl, registViewModel : RegisterInformationViewModel, prefsModel : InventoryViewModel, preferenceViewModel: PreferenceViewModel, onTouchListener: LiveViewOnTouchListener, anotherDrawer: IAnotherDrawer?, exporter: DataExporter, recognizer: RecognizeFromIsbn)
{
    MaterialTheme {
        NavHost(navController = navController, startDestination = "MainScreen") {
            composable("MainScreen") { MainScreen(navController = navController, cameraControl = cameraControl) }
            composable("RegistScreen") { RegistScreen(navController = navController, cameraControl = cameraControl, viewModel = registViewModel, onTouchListener = onTouchListener, anotherDrawer = anotherDrawer) }
            composable("ListScreen") {
                prefsModel.refresh()
                ListScreen(navController = navController, viewModel = prefsModel, exporter = exporter, recognizer = recognizer)
            }
            composable("DetailScreen/{id}", listOf(navArgument("id") { type = NavType.LongType })) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("id") ?: 0
                DetailScreen(navController = navController, viewModel = prefsModel, id = id)
            }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController, prefsModel = preferenceViewModel) }
        }
    }
}
