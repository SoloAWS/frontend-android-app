package com.misw.abcalls.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.misw.abcalls.data.model.DocumentType
import com.misw.abcalls.ui.viewmodel.UserRegistrationUiState
import com.misw.abcalls.ui.viewmodel.UserRegistrationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserRegistrationScreen(
    onNavigateToLogin: () -> Unit,
    viewModel: UserRegistrationViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val isFormValid by viewModel.isFormValid.collectAsState()
    var isDocumentTypeExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.registrationSuccess) {
        if (uiState.registrationSuccess) {
            onNavigateToLogin()
            viewModel.resetSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Usuario") },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = { Text("Nombre completo") },
                isError = uiState.nameError != null,
                supportingText = { uiState.nameError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
            ExposedDropdownMenuBox(
                expanded = isDocumentTypeExpanded,
                onExpandedChange = { isDocumentTypeExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = uiState.documentType.displayName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Tipo de documento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDocumentTypeExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = isDocumentTypeExpanded,
                    onDismissRequest = { isDocumentTypeExpanded = false }
                ) {
                    DocumentType.entries.forEach { documentType ->
                        DropdownMenuItem(
                            text = { Text(documentType.displayName) },
                            onClick = {
                                viewModel.updateDocumentType(documentType)
                                isDocumentTypeExpanded = false
                            }
                        )
                    }
                }
            }

            // Document ID TextField
            OutlinedTextField(
                value = uiState.documentId,
                onValueChange = { viewModel.updateDocumentId(it) },
                label = { Text("Número de documento") },
                isError = uiState.documentIdError != null,
                supportingText = { uiState.documentIdError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = { Text("Correo electrónico") },
                isError = uiState.emailError != null,
                supportingText = { uiState.emailError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.updatePassword(it) },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.passwordError != null,
                supportingText = { uiState.passwordError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = { Text("Confirmar contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                isError = uiState.confirmPasswordError != null,
                supportingText = { uiState.confirmPasswordError?.let { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = uiState.termsAccepted,
                    onCheckedChange = { viewModel.updateTermsAccepted(it) }
                )
                Text("Acepto términos y condiciones")
            }

            Button(
                onClick = { viewModel.register() },
                enabled = isFormValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Registrarse")
                }
            }

            TextButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("¿Ya tienes una cuenta? Iniciar sesión")
            }
        }
    }

    if (uiState.error != null) {
        AlertDialog(
            onDismissRequest = { viewModel.resetError() },
            title = { Text("Error") },
            text = { Text(uiState.error!!) },
            confirmButton = {
                TextButton(onClick = { viewModel.resetError() }) {
                    Text("OK")
                }
            }
        )
    }
}