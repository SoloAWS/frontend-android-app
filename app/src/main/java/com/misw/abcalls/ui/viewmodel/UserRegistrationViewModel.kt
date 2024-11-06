package com.misw.abcalls.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.misw.abcalls.data.model.DocumentType
import com.misw.abcalls.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.regex.Pattern

@HiltViewModel
class UserRegistrationViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(UserRegistrationUiState())
    val uiState: StateFlow<UserRegistrationUiState> = _uiState

    private val emailPattern = Pattern.compile(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun updateName(name: String) {
        _uiState.update { currentState ->
            currentState.copy(
                name = name,
                nameError = validateName(name)
            )
        }
    }

    fun updateEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email,
                emailError = validateEmail(email)
            )
        }
    }

    fun updatePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password,
                passwordError = validatePassword(password),
                confirmPasswordError = validatePasswordMatch(password, currentState.confirmPassword)
            )
        }
    }

    fun updateConfirmPassword(confirmPassword: String) {
        _uiState.update { currentState ->
            currentState.copy(
                confirmPassword = confirmPassword,
                confirmPasswordError = validatePasswordMatch(currentState.password, confirmPassword)
            )
        }
    }

    fun updateTermsAccepted(accepted: Boolean) {
        _uiState.update { currentState ->
            currentState.copy(termsAccepted = accepted)
        }
    }

    fun register() {
        val currentState = _uiState.value
        if (!isFormValid(currentState)) return

        viewModelScope.launch {
            try {
                val registrationResult = userRepository.registerUser(
                    name = currentState.name,
                    email = currentState.email,
                    password = currentState.password,
                    documentType = currentState.documentType.backendValue,
                    documentId = currentState.documentId
                )

                if (registrationResult.isSuccess) {
                    // Automatic login after successful registration
                    val loginResult = userRepository.login(
                        email = currentState.email,
                        password = currentState.password
                    )

                    if (loginResult.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                registrationSuccess = true
                            )
                        }
                    } else {
                        throw loginResult.exceptionOrNull() ?: Exception("Login failed")
                    }
                } else {
                    throw registrationResult.exceptionOrNull() ?: Exception("Registration failed")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = when {
                            e.message?.contains("400") == true -> "Este correo ya está en uso"
                            e.message?.contains("connection") == true -> "Error de conexión. Intenta nuevamente"
                            else -> "Error en el registro. Por favor intenta nuevamente"
                        }
                    )
                }
            }
        }
    }

    private fun validateName(name: String): String? {
        return when {
            name.isBlank() -> "El nombre es requerido"
            name.length < 2 -> "El nombre debe tener al menos 2 caracteres"
            name.length > 100 -> "El nombre no puede exceder 100 caracteres"
            !name.matches(Regex("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) -> "El nombre solo puede contener letras y espacios"
            else -> null
        }
    }

    private fun validateEmail(email: String): String? {
        return when {
            email.isBlank() -> "El correo electrónico es requerido"
            !emailPattern.matcher(email).matches() -> "Ingresa un correo electrónico válido"
            else -> null
        }
    }

    private fun validatePassword(password: String): String? {
        return when {
            password.isBlank() -> "La contraseña es requerida"
            password.length < 8 -> "La contraseña debe tener al menos 8 caracteres"
            !password.matches(Regex(".*[A-Z].*")) -> "La contraseña debe incluir al menos una mayúscula"
            !password.matches(Regex(".*[a-z].*")) -> "La contraseña debe incluir al menos una minúscula"
            !password.matches(Regex(".*\\d.*")) -> "La contraseña debe incluir al menos un número"
            else -> null
        }
    }

    private fun validatePasswordMatch(password: String, confirmPassword: String): String? {
        return when {
            confirmPassword.isBlank() -> "Confirma tu contraseña"
            password != confirmPassword -> "Las contraseñas no coinciden"
            else -> null
        }
    }

    fun updateDocumentType(documentType: DocumentType) {
        _uiState.update { currentState ->
            currentState.copy(
                documentType = documentType,
                documentId = "", // Clear document ID when type changes
                documentIdError = null
            )
        }
    }

    fun updateDocumentId(documentId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                documentId = documentId,
                documentIdError = validateDocumentId(documentId, currentState.documentType)
            )
        }
    }

    private fun validateDocumentId(documentId: String, documentType: DocumentType): String? {
        return when {
            documentId.isBlank() -> "El número de documento es requerido"
            else -> null
        }
    }

    fun resetError() {
        _uiState.update { it.copy(error = null) }
    }

    fun resetSuccess() {
        _uiState.update { it.copy(registrationSuccess = false) }
    }

    private fun isFormValid(state: UserRegistrationUiState): Boolean {
        return state.name.isNotBlank() &&
                state.email.isNotBlank() &&
                state.password.isNotBlank() &&
                state.confirmPassword.isNotBlank() &&
                state.documentId.isNotBlank() &&
                state.termsAccepted &&
                state.nameError == null &&
                state.emailError == null &&
                state.passwordError == null &&
                state.confirmPasswordError == null &&
                state.documentIdError == null &&
                !state.isLoading
    }

    val isFormValid = _uiState.map { state ->
        isFormValid(state)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = false
    )
}

data class UserRegistrationUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val documentType: DocumentType = DocumentType.ID_CARD,
    val documentId: String = "",
    val termsAccepted: Boolean = false,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val documentIdError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val registrationSuccess: Boolean = false
)

