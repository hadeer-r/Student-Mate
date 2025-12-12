package com.example.studentmate.Activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.Activities.ui.theme.StudentMateTheme
import java.text.SimpleDateFormat
import java.util.*

class AddAssignment : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentMateTheme {
                // Call the main screen composable here
                AddItemScreen(
                    onAddItemClicked = { type, subject, date, score, desc, notify ->
                        // Handle the save action (e.g., save to Room DB)
                        Toast.makeText(this, "Saved: $subject ($type)", Toast.LENGTH_SHORT).show()
                        finish() // Close activity after saving
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onAddItemClicked: (String, String, String, String, String, Boolean) -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // --- State Variables ---
    var selectedType by remember { mutableStateOf("Assignment") }
    var selectedSubject by remember { mutableStateOf("") }
    var expandedSubject by remember { mutableStateOf(false) } // State for dropdown visibility
    var dateText by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notifyEnabled by remember { mutableStateOf(false) }

    // Dummy List of Subjects (You can fetch this from your DB later)
    val subjects = listOf("Data Structures", "Algorithms", "Mobile Programming", "Database", "Operating Systems", "Software Engineering")

    // --- Date Picker Logic ---
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                        dateText = formatter.format(Date(selectedDateMillis))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Item", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(scrollState) // Make screen scrollable
        ) {

            // 1. Type Selector (Assignment / Exam)
            Text("Type", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeButton(
                    text = "Assignment",
                    isSelected = selectedType == "Assignment",
                    modifier = Modifier.weight(1f)
                ) { selectedType = "Assignment" }

                TypeButton(
                    text = "Exam",
                    isSelected = selectedType == "Exam",
                    modifier = Modifier.weight(1f)
                ) { selectedType = "Exam" }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Subject Dropdown (Spinner)
            Text("Subject Name", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedSubject,
                onExpandedChange = { expandedSubject = !expandedSubject },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {}, // ReadOnly, change happens in menu
                    readOnly = true,
                    placeholder = { Text("Select Subject") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(), // Connects text field to menu
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedSubject,
                    onDismissRequest = { expandedSubject = false }
                ) {
                    subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(text = subject) },
                            onClick = {
                                selectedSubject = subject
                                expandedSubject = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Date Input
            Text("Date", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { },
                    readOnly = true,
                    placeholder = { Text("mm/dd/yyyy") },
                    trailingIcon = {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
                // Invisible box to handle click over the text field
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Score / Grade
            Text("Score / Grade (Optional)", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = score,
                onValueChange = { score = it },
                placeholder = { Text("Enter score out of 100") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Description
            Text("Description", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                placeholder = { Text("Add notes or details about this item") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(8.dp),
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 6. Notification Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Notify me before assignment", fontWeight = FontWeight.SemiBold)
                    Text("Get reminders about this item", fontSize = 12.sp, color = Color.Gray)
                }
                Switch(
                    checked = notifyEnabled,
                    onCheckedChange = { notifyEnabled = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 7. Add Item Button
            Button(
                onClick = {
                    if (selectedSubject.isNotEmpty() && dateText.isNotEmpty()) {
                        onAddItemClicked(selectedType, selectedSubject, dateText, score, description, notifyEnabled)
                    } else {
                        Toast.makeText(context, "Please select a Subject and Date", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add Item", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

// Custom Toggle Button Composable
@Composable
fun TypeButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val backgroundColor = if (isSelected) Color(0xFFE3F2FD) else Color.White
    val borderColor = if (isSelected) Color(0xFF2196F3) else Color.LightGray
    val textColor = if (isSelected) Color(0xFF2196F3) else Color.Gray

    Box(
        modifier = modifier
            .height(50.dp)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .background(backgroundColor, RoundedCornerShape(8.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontWeight = if(isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AddItemPreview() {
    StudentMateTheme {
        AddItemScreen { _, _, _, _, _, _ -> }
    }
}