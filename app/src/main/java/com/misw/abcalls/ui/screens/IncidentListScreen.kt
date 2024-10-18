import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IncidentListScreen(onCreateIncident: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incidentes",
                    color = MaterialTheme.colorScheme.onPrimary,
                    textAlign = TextAlign.Center,
                    ) },
                actions = {
                    IconButton(onClick = { /* TODO: Handle menu */ }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateIncident,
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add Incident", tint = MaterialTheme.colorScheme.onPrimary)
            }
        }
    ) { innerPadding ->
        // Here you would typically have a LazyColumn to display the list of incidents
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            Text("", modifier = Modifier.padding(16.dp))
        }
    }
}


@Preview(showBackground = true)
@Composable
fun IncidentListScreenPreview() {
    MaterialTheme {
        IncidentListScreen(onCreateIncident = {})
    }
}