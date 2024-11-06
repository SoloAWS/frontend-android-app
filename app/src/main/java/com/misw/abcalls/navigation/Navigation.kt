import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.abcalls.ui.screens.IncidentListScreen
import com.misw.abcalls.ui.screens.UserRegistrationScreen
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "register") {
        composable("register") {
            UserRegistrationScreen(
                // TODO: Modify the navigation to Login once it is created
                onNavigateToLogin = { navController.navigate("incidentList") }
            )
        }

        composable("incidentList") {
            IncidentListScreen(
                onCreateIncident = { navController.navigate("createIncident") }
            )
        }
        composable("createIncident") {
            val viewModel: CreateIncidentViewModel = hiltViewModel()
            CreateIncidentScreen(
                onNavigateBack = { navController.popBackStack() },
                onIncidentCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}