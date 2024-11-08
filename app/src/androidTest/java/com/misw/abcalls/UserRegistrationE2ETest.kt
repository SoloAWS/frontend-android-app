package com.misw.abcalls

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import com.misw.abcalls.util.ComposeTest
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class UserRegistrationE2ETest : ComposeTest() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testSuccessfulRegistration() {
        // Prepare mock response
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": "1",
                    "email": "test@example.com",
                    "name": "Test User"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // Wait for the registration screen to be displayed
        composeTestRule.waitForIdle()

        // Fill in the registration form
        composeTestRule.onNodeWithText("Nombre")
            .performTextInput("John")
        composeTestRule.onNodeWithText("Apellido")
            .performTextInput("Doe")
        composeTestRule.onNodeWithText("Correo electrónico")
            .performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Contraseña")
            .performTextInput("Password123!")
        composeTestRule.onNodeWithText("Confirmar contraseña")
            .performTextInput("Password123!")

        // Accept terms
        composeTestRule.onNodeWithText("Acepto términos y condiciones")
            .performClick()

        // Click register button
        composeTestRule.onNodeWithText("Registrarse")
            .performClick()

        // Verify navigation to incident list
        composeTestRule.onNodeWithText("Incidentes")
            .assertIsDisplayed()
    }

    @Test
    fun testRegistrationValidation() {
        composeTestRule.waitForIdle()

        // Try to register with empty fields
        composeTestRule.onNodeWithText("Registrarse")
            .performClick()

        // Verify error messages
        composeTestRule.onNodeWithText("El nombre es requerido")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("El apellido es requerido")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("El correo electrónico es requerido")
            .assertIsDisplayed()
    }
}