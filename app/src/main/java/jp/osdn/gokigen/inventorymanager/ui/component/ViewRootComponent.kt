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
        val navController: NavHostController = rememberNavController()
        Surface {
            NavigationMain(navController, this.myViewModel)
        }
        Log.v(TAG, " ...NavigationRootComponent...")
    }

    companion object
    {
        private val TAG = ViewRootComponent::class.java.simpleName
    }
}

@Composable
fun NavigationMain(navController: NavHostController, prefsModel : InventoryViewModel)
{
    GokigenComposeAppsTheme {
        NavHost(navController = navController, startDestination = "MainScreen") {
            composable("MainScreen") { MainScreen(navController = navController, prefsModel = prefsModel) }
            composable("RegistScreen") { RegistScreen(navController = navController, prefsModel = prefsModel) }
            composable("ListScreen") { ListScreen(navController = navController, prefsModel = prefsModel) }
            composable("DetailScreen") { DetailScreen(navController = navController, prefsModel = prefsModel) }
            composable("PreferenceScreen") { PreferenceScreen(navController = navController, prefsModel = prefsModel) }
        }
    }
}
