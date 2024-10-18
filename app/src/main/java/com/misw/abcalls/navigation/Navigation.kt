import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "incidentList") {
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