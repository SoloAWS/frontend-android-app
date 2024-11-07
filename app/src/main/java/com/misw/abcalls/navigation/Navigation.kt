import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.abcalls.ui.screens.IncidentListScreen
import com.misw.abcalls.ui.screens.UserRegistrationScreen
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel
import com.misw.abcalls.ui.viewmodel.IncidentListViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val incidentListViewModel: IncidentListViewModel = hiltViewModel()

    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            UserRegistrationScreen(
                onNavigateToLogin = { navController.navigate("incidentList") }
            )
        }

        composable("incidentList") {
            LaunchedEffect(Unit) {
                incidentListViewModel.refresh()
            }

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