package com.misw.abcalls

import androidx.compose.ui.test.*
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
class IncidentManagementE2ETest : ComposeTest() {
    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun setup() {
        hiltRule.inject()
        mockWebServer = MockWebServer()
        mockWebServer.start(8080)

        // Login mock response
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
            .setBody("""{"access_token": "test-token"}"""))
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun testCreateAndViewIncident() {
        // Mock responses
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
            .setBody("""
                {
                    "userId": "1",
                    "companies": [
                        {"id": "1", "name": "Test Company"}
                    ]
                }
            """.trimIndent()))

        mockWebServer.enqueue(MockResponse().setResponseCode(200)
            .setBody("""
                {
                    "id": "1",
                    "user_id": "1",
                    "company_id": "1",
                    "description": "Test incident",
                    "state": "open",
                    "channel": "mobile",
                    "priority": "high",
                    "creation_date": "2024-03-08T12:00:00Z"
                }
            """.trimIndent()))

        // Navigate to create incident
        composeTestRule.onNodeWithContentDescription("Crear incidente")
            .performClick()

        // Select company
        composeTestRule.onNodeWithText("Nombre Compañía")
            .performClick()
        composeTestRule.onNodeWithText("Test Company")
            .performClick()

        // Fill description
        composeTestRule.onNodeWithText("Descripción")
            .performTextInput("Test incident")

        // Create incident
        composeTestRule.onNodeWithText("Crear")
            .performClick()

        // Verify success dialog
        composeTestRule.onNodeWithText("El incidente se ha creado exitosamente.")
            .assertIsDisplayed()

        // Confirm dialog
        composeTestRule.onNodeWithText("OK")
            .performClick()

        // Verify incident in list
        composeTestRule.onNodeWithText("Test incident")
            .assertIsDisplayed()
    }

    @Test
    fun testSearchIncident() {
        // Mock incident list response
        mockWebServer.enqueue(MockResponse().setResponseCode(200)
            .setBody("""
                {
                    "incidents": [
                        {
                            "id": "1",
                            "description": "Test incident 1",
                            "state": "open",
                            "priority": "high"
                        },
                        {
                            "id": "2",
                            "description": "Test incident 2",
                            "state": "closed",
                            "priority": "low"
                        }
                    ]
                }
            """.trimIndent()))

        // Search for incident
        composeTestRule.onNodeWithText("Buscador")
            .performTextInput("incident 1")

        // Verify filtered results
        composeTestRule.onNodeWithText("Test incident 1")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Test incident 2")
            .assertDoesNotExist()
    }
}