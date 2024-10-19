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
import androidx.compose.ui.text.style.TextAlign
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.misw.abcalls.data.model.Company

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIncidentScreen(
    onNavigateBack: () -> Unit,
    onIncidentCreated: () -> Unit,
    viewModel: CreateIncidentViewModel = viewModel()
) {
    var selectedCompany by remember { mutableStateOf<Company?>(null) }
    var incidentDescription by remember { mutableStateOf("") }
    var selectedFile by remember { mutableStateOf<Uri?>(null) }
    var descriptionError by remember { mutableStateOf<String?>(null) }
    var fileError by remember { mutableStateOf<String?>(null) }
    var isDropdownExpanded by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val uiState by viewModel.uiState.collectAsState()

    val context = LocalContext.current

    // Fixed user UUID for now
    val userId = "a1b2c3d4-e5f6-4a7b-8c9d-0e1f2a3b4c5d"

    LaunchedEffect(userId) {
        viewModel.loadCompanies(userId)
    }

    LaunchedEffect(uiState.createdIncident) {
        if (uiState.createdIncident != null) {
            showSuccessDialog = true
        }
    }

    LaunchedEffect(uiState.error) {
        if (uiState.error != null) {
            showErrorDialog = true
        }
    }

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
                title = { Text("Crear Incidente", color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onPrimary)
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = MaterialTheme.colorScheme.primary)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            ExposedDropdownMenuBox(
                expanded = isDropdownExpanded,
                onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = selectedCompany?.name ?: "",
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Nombre Compañía") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = isDropdownExpanded,
                    onDismissRequest = { isDropdownExpanded = false }
                ) {
                    uiState.companies.forEach { company ->
                        DropdownMenuItem(
                            text = { Text(company.name) },
                            onClick = {
                                selectedCompany = company
                                isDropdownExpanded = false
                            }
                        )
                    }
                }
            }

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
                    descriptionError = if (incidentDescription.isBlank()) "La descripción es requerida" else null

                    if (selectedCompany != null && descriptionError == null && fileError == null) {
                        viewModel.createIncident(incidentDescription, selectedCompany!!.id, userId, selectedFile)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCompany != null
            ) {
                Text("Crear", color = MaterialTheme.colorScheme.onPrimary)
            }

            if (uiState.isLoading) {
                CircularProgressIndicator()
            }
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                showSuccessDialog = false
                viewModel.resetState()
                onIncidentCreated()
            },
            title = { Text("Éxito") },
            text = { Text("El incidente se ha creado exitosamente.") },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.resetState()
                    onIncidentCreated()
                }) {
                    Text("OK")
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = {
                showErrorDialog = false
                viewModel.resetState()
            },
            title = { Text("Error") },
            text = { Text("Ha ocurrido un error al crear el incidente. Por favor, inténtelo de nuevo.") },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.resetState()
                }) {
                    Text("OK")
                }
            }
        )
    }
}