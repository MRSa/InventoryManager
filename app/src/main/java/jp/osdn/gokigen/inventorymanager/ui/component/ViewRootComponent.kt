package jp.osdn.gokigen.inventorymanager.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.AbstractComposeView
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.gokigenassets.camera.interfaces.ICameraControl
import jp.osdn.gokigen.gokigenassets.liveview.IAnotherDrawer
import jp.osdn.gokigen.gokigenassets.liveview.LiveViewOnTouchListener
import jp.osdn.gokigen.inventorymanager.liaison.CameraLiaison
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var myViewModel : InventoryViewModel
    private lateinit var myLiaison : CameraLiaison
    fun setLiaisons(viewModel : InventoryViewModel, liaison : CameraLiaison)
    {
        this.myViewModel = viewModel
        this.myLiaison = liaison
        Log.v(TAG, " ...setLiaisons...")
    }

    @Composable
    override fun Content()
    {
        val navController: NavHostController = rememberNavController()
        Surface {
            val cameraControl = this.myLiaison.getCameraControl()
            NavigationMain(navController, cameraControl, this.myViewModel, LiveViewOnTouchListener(cameraControl), this.myLiaison.getAnotherDrawer())
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl, prefsModel : InventoryViewModel, onTouchListener: LiveViewOnTouchListener, anotherDrawer: IAnotherDrawer?)
{
    GokigenComposeAppsTheme {
        NavHost(navController = navController, startDestination = "MainScreen") {
            composable("MainScreen") { MainScreen(navController = navController, prefsModel = prefsModel) }
            composable("RegistScreen") { RegistScreen(navController = navController, cameraControl = cameraControl, prefsModel = prefsModel, onTouchListener = onTouchListener, anotherDrawer = anotherDrawer) }
            composable("ListScreen") { ListScreen(navController = navController, prefsModel = prefsModel) }
            composable("DetailScreen") { DetailScreen(navController = navController, prefsModel = prefsModel) }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController, prefsModel = prefsModel) }
        }
    }
}
