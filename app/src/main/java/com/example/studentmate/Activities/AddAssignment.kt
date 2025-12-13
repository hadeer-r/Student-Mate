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
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.emptyList

class AddAssignment : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.getDatabase(this)
        val bundle = intent.extras
        var loggedUser: Student? = null
        if (bundle != null) {
            loggedUser = Student(
                password = bundle.getString("password")!!,
                name = bundle.getString("name")!!,
                email = bundle.getString("email")!!,
                id = bundle.getInt("id")
            )
        }
        setContent {
            StudentMateTheme {
                AddItemScreen(db, loggedUser)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(db: AppDatabase, loggedUser: Student?) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Form State
    var selectedType by remember { mutableStateOf("Assignment") }
    var selectedSubject by remember { mutableStateOf("") }
    var expandedSubject by remember { mutableStateOf(false) }

    // Date State
    var dateText by remember { mutableStateOf("") }
    var selectedDateValue by remember { mutableStateOf(Date()) } // Stores the actual Date object
    var showDatePicker by remember { mutableStateOf(false) }

    // Input Fields State
    var score by remember { mutableStateOf("") }
    var actualScore by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var subjectId by remember { mutableStateOf(0) }
    var title by remember { mutableStateOf("") }

    var allSubjects by remember { mutableStateOf<List<Subject>>(emptyList()) }

    LaunchedEffect(key1 = true) {
        allSubjects = db.subjectDao().getAll()
    }

    // Date Picker Logic
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDateMillis = datePickerState.selectedDateMillis
                    if (selectedDateMillis != null) {
                        // 1. Create the actual Date object
                        val dateObj = Date(selectedDateMillis)
                        selectedDateValue = dateObj

                        // 2. Format it just for display in the text box
                        val formatter = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
                        dateText = formatter.format(dateObj)
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
                .verticalScroll(scrollState)
        ) {
            // 1. Title Input (Using Reusable Function)
            Text("Title", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            StudentMateInput(
                value = title,
                onValueChange = { title = it },
                placeholder = "Enter title"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Type Selector
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

            // 3. Subject Dropdown
            Text("Subject Name", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))

            ExposedDropdownMenuBox(
                expanded = expandedSubject,
                onExpandedChange = { expandedSubject = !expandedSubject },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {},
                    readOnly = true,
                    placeholder = { Text("Select Subject") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSubject) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(8.dp)
                )

                ExposedDropdownMenu(
                    expanded = expandedSubject,
                    onDismissRequest = { expandedSubject = false }
                ) {
                    allSubjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(text = subject.name) },
                            onClick = {
                                selectedSubject = subject.name
                                subjectId = subject.id
                                expandedSubject = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Date Input
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

            // 5. Score Input (Using Reusable Function)
            Text("Assessment Score", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            StudentMateInput(
                value = score,
                onValueChange = { score = it },
                placeholder = "Enter score"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Actual Score Input (Using Reusable Function)
            Text("Your Score (optional)", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            StudentMateInput(
                value = actualScore,
                onValueChange = { actualScore = it },
                placeholder = "Enter your score"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 7. Description Input (Using Reusable Function with custom height)
            Text("Description", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            StudentMateInput(
                value = description,
                onValueChange = { description = it },
                placeholder = "Add notes or details",
                singleLine = false,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    if (title.isNotEmpty() && selectedSubject.isNotEmpty() && dateText.isNotEmpty() && score.isNotEmpty()) {

                        // Parse numbers safely to avoid crash if empty
                        val scoreInt = score.toIntOrNull() ?: 0
                        val actualScoreInt = actualScore.toIntOrNull() ?: 0

                        val newAssessment = Assessment(
                            subjectId = subjectId,
                            studentId = loggedUser?.id ?: 0,
                            title = title,
                            description = description,
                            deadline = selectedDateValue, // Passing the actual Date Object
                            score = scoreInt,
                            isExam = selectedType == "Exam",
                            isDone = false,
                            actualScore = actualScoreInt
                        )

                        CoroutineScope(Dispatchers.IO).launch {
                            db.assessmentDao().insertAssessment(newAssessment)
                        }

                        // Optional: Finish activity or show success message
                        Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                        (context as? ComponentActivity)?.finish()

                    } else {
                        Toast.makeText(context, "Please fill required fields", Toast.LENGTH_SHORT).show()
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

// --- NEW REUSABLE INPUT FUNCTION ---
@Composable
fun StudentMateInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier.fillMaxWidth(),
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        modifier = modifier,
        singleLine = singleLine,
        shape = RoundedCornerShape(8.dp)
    )
}

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
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AddItemPreview() {
    StudentMateTheme {
        AddItemScreen(db = AppDatabase.getDatabase(LocalContext.current),null)
    }
}