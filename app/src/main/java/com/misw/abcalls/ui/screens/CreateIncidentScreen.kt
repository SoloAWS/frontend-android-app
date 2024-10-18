import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.webkit.MimeTypeMap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.style.TextAlign
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel
import kotlinx.coroutines.launch
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misw.abcalls.ui.viewmodel.CreateIncidentUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIncidentScreen(
    onNavigateBack: () -> Unit,
    onIncidentCreated: () -> Unit,
    viewModel: CreateIncidentViewModel = viewModel()
) {
    var incidentName by remember { mutableStateOf("") }
    var incidentDescription by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var fileError by remember { mutableStateOf<String?>(null) }

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it)
            val fileSize = context.contentResolver.openFileDescriptor(it, "r")?.statSize ?: 0
            val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain")

            when {
                !allowedTypes.contains(mimeType) -> {
                    fileError = "Formato de archivo no soportado. Formatos permitidos: jpg, png, gif, pdf, doc, docx, txt"
                }
                fileSize > 10 * 1024 * 1024 -> {
                    fileError = "El archivo excede el límite de 10 MB"
                }
                else -> {
                    selectedFile = it
                    fileError = null
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Incidente",
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center,
                            )
                        },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = incidentName,
                onValueChange = {
                    if (it.length <= 100) {
                        incidentName = it
                        nameError = null
                    }
                },
                label = { Text("Nombre") },
                isError = nameError != null,
                supportingText = { nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = incidentDescription,
                onValueChange = {
                    if (it.length <= 500) {
                        incidentDescription = it
                        descriptionError = null
                    }
                },
                label = { Text("Descripción") },
                isError = descriptionError != null,
                supportingText = { descriptionError?.let { Text(it) } },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = selectedFile?.lastPathSegment ?: "",
                onValueChange = { },
                label = { Text("Adjuntar archivo (opcional)") },
                isError = fileError != null,
                supportingText = { fileError?.let { Text(it) } },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { launcher.launch("*/*") }) {
                        Icon(Icons.Filled.Add, contentDescription = "Adjuntar archivo")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    nameError = if (incidentName.isBlank()) "El nombre es requerido" else null
                    descriptionError = if (incidentDescription.isBlank()) "La descripción es requerida" else null

                    if (nameError == null && descriptionError == null && fileError == null) {
                        viewModel.createIncident(incidentName, incidentDescription, selectedFile?.toString())
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear", color = MaterialTheme.colorScheme.onPrimary)
            }

            when (val state = uiState) {
                is CreateIncidentUiState.Loading -> {
                    CircularProgressIndicator()
                }
                is CreateIncidentUiState.Success -> {
                    LaunchedEffect(state) {
                        onIncidentCreated()
                    }
                }
                is CreateIncidentUiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
                else -> {} // Initial state, do nothing
            }
        }
    }
}