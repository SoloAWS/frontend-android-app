import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.abcalls.ui.screens.IncidentListScreen
import com.misw.abcalls.ui.screens.UserRegistrationScreen
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel
import com.misw.abcalls.ui.viewmodel.IncidentListViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val incidentListViewModel: IncidentListViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()

    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            UserRegistrationScreen(
                onNavigateToLogin = { navController.navigate("incidentList") }
            )
        }

        composable("incidentList") {
            IncidentListScreen(
                onCreateIncident = { navController.navigate("createIncident") }
            )
        }

        composable("createIncident") {
            CreateIncidentScreen(
                onNavigateBack = { navController.popBackStack() },
                onIncidentCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}