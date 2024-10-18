import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
            CreateIncidentScreen(
                onNavigateBack = { navController.popBackStack() },
                onIncidentCreated = {
                    navController.popBackStack()
                }
            )
        }
    }
}