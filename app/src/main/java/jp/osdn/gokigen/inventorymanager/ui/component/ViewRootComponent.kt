package jp.osdn.gokigen.inventorymanager.ui.component

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.AbstractComposeView
import androidx.navigation.compose.rememberNavController
import jp.osdn.gokigen.inventorymanager.ui.model.InventoryViewModel
import jp.osdn.gokigen.inventorymanager.ui.theme.GokigenComposeAppsTheme

class ViewRootComponent @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : AbstractComposeView(context, attrs, defStyleAttr)
{
    private lateinit var myViewModel : InventoryViewModel
    fun setLiaisons(viewModel : InventoryViewModel)
    {
        this.myViewModel = viewModel
        Log.v(TAG, " ...setLiaisons...")
    }

    @Composable
    override fun Content()
    {
        val navController = rememberNavController()
        GokigenComposeAppsTheme {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Greeting(
                    name = "Android",
                    modifier = Modifier.padding(innerPadding)
                )
            }


/*
            Surface(color = MaterialTheme.colors.background) {
                NavigationMain(navController, liaison.getCameraControl(), liaison.getVibrator(), prefsModel, liaison.getAnotherDrawer())
            }
*/
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}


/*
@Composable
fun NavigationMain(navController: NavHostController, cameraControl: ICameraControl, vibrator: IVibrator, prefsModel : A01fPrefsModel, anotherDrawer: IAnotherDrawer)
{
    GokigenComposeAppsTheme {
        NavHost(navController = navController, startDestination = "LiveViewScreen") {
            composable("LiveViewScreen") { LiveViewScreen(navController = navController, cameraControl, prefsModel, vibrator, LiveViewOnTouchListener(cameraControl), anotherDrawer) }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController, prefsModel, vibrator) }
        }
    }
}
*/
