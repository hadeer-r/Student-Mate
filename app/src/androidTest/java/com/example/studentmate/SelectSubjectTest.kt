package com.example.studentmate

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SelectSubjectTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<SelectSubjectsActivity>()

    @Test
    fun selectSubject_showsSelectionCount() {
        composeTestRule.onNodeWithTag("subject_item_1").performClick()

        composeTestRule.onNodeWithTag("selection_count_text").assertIsDisplayed()

        composeTestRule.onNodeWithTag("subject_item_1").performClick()

        composeTestRule.onNodeWithTag("selection_count_text").assertDoesNotExist()
}
    @Test
    fun selectMultipleSubjects_updatesCountCorrectly() {
        composeTestRule.onNodeWithTag("subject_item_1").performClick()

        composeTestRule.onNodeWithTag("subject_item_2").performClick()

        composeTestRule.onNodeWithTag("selection_count_text").assertIsDisplayed()
    }
}