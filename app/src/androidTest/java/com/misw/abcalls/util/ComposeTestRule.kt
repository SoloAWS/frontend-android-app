package com.misw.abcalls.util

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import com.misw.abcalls.MainActivity
import org.junit.Rule

abstract class ComposeTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
}