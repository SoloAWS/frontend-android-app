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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.abcalls.ui.viewmodel.CreateIncidentViewModel
import com.misw.abcalls.R
import com.misw.abcalls.data.model.Company

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateIncidentScreen(
    onNavigateBack: () -> Unit,
    onIncidentCreated: () -> Unit,
    viewModel: CreateIncidentViewModel = hiltViewModel()
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

    LaunchedEffect(Unit) {
        viewModel.loadCompanies()
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
    val fileNotSupportedMessage = stringResource(R.string.file_not_supported)
    val fileTooLarge = stringResource(R.string.file_too_large)

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val mimeType = context.contentResolver.getType(it)
            val fileSize = context.contentResolver.openFileDescriptor(it, "r")?.statSize ?: 0
            val allowedTypes = listOf("image/jpeg", "image/png", "image/gif", "application/pdf", "application/msword", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "text/plain")

            when {
                !allowedTypes.contains(mimeType) -> {
                    fileError = fileNotSupportedMessage
                }
                fileSize > 10 * 1024 * 1024 -> {
                    fileError = fileTooLarge
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
                title = { Text(stringResource(R.string.incident_create_title), color = MaterialTheme.colorScheme.onPrimary, textAlign = TextAlign.Center) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(R.string.back), tint = MaterialTheme.colorScheme.onPrimary)
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
                    label = { Text(stringResource(R.string.company_name)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
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
                label = { Text(stringResource(R.string.description)) },
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
                label = { Text(stringResource(R.string.attach_file_optional)) },
                isError = fileError != null,
                supportingText = { fileError?.let { Text(it) } },
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { launcher.launch("*/*") }) {
                        Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.attach_file))
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
             val descriptionRequired = stringResource(id = R.string.description_required)
            Button(
                onClick = {
                    descriptionError = if (incidentDescription.isBlank()) descriptionRequired else null

                    if (selectedCompany != null && descriptionError == null && fileError == null) {
                        viewModel.createIncident(incidentDescription, selectedCompany!!.id, selectedFile)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedCompany != null
            ) {
                Text(stringResource(R.string.create), color = MaterialTheme.colorScheme.onPrimary)
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
            title = { Text(stringResource(R.string.success)) },
            text = { Text(stringResource(R.string.incident_success_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showSuccessDialog = false
                    viewModel.resetState()
                    onIncidentCreated()
                }) {
                    Text(stringResource(R.string.ok))
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
            title = { Text(stringResource(R.string.error)) },
            text = { Text(stringResource(R.string.error_creating_incident)) },
            confirmButton = {
                TextButton(onClick = {
                    showErrorDialog = false
                    viewModel.resetState()
                }) {
                    Text(stringResource(R.string.ok))
                }
            }
        )
    }
}