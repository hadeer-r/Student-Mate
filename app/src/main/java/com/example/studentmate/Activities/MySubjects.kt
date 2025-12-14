package com.example.studentmate.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.ui.theme.StudentMateTheme

class MySubjectsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentMateTheme {
                MySubjectsScreen(
                    onNavigateBack = { finish() }
                )
            }
        }
    }
}

data class Subject(
    val name: String,
    val credits: Int,
    val total: Int = 0,
    val exams: Int = 0,
    val tasks: Int = 0,
    val color: Color
)

data class AssessmentItem(
    val type: String,
    val date: String,
    val description: String,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubjectsScreen(
    onNavigateBack: () -> Unit = {},
    onEditSubject: (Subject) -> Unit = {}
) {
    // 1. Subject List Data
    val subjects = remember {
        mutableStateListOf(
            Subject("Database Systems", 3, 1, 1, 0, Color(0xFFFF9800)),
            Subject("Data Structures", 4, 1, 1, 0, Color(0xFF2196F3)),
            Subject("Web Development", 3, 1, 0, 1, Color(0xFF9C27B0))
        )
    }

    // 2. Navigation States
    var showAddSubjectScreen by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) } // Viewing Details
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }   // Editing

    // 3. Back Handler Logic
    BackHandler(enabled = showAddSubjectScreen || selectedSubject != null || subjectToEdit != null) {
        if (showAddSubjectScreen) showAddSubjectScreen = false
        else if (selectedSubject != null) selectedSubject = null
        else if (subjectToEdit != null) subjectToEdit = null
    }

    // 4. Screen Switching Logic
    if (showAddSubjectScreen) {
        // --- CREATE MODE ---
        AddOrEditSubjectScreen(
            subjectToEdit = null, // Null means Create Mode
            onBack = { showAddSubjectScreen = false },
            onSave = { newSubject ->
                subjects.add(newSubject)
                showAddSubjectScreen = false
            }
        )
    } else if (subjectToEdit != null) {
        // --- EDIT MODE (New) ---
        AddOrEditSubjectScreen(
            subjectToEdit = subjectToEdit,
            onBack = { subjectToEdit = null },
            onSave = { updatedSubject ->
                // Find and replace
                val index = subjects.indexOf(subjectToEdit)
                if (index != -1) {
                    subjects[index] = updatedSubject
                }
                subjectToEdit = null
            },
            onDelete = {
                subjects.remove(subjectToEdit)
                subjectToEdit = null
            }
        )
    } else if (selectedSubject != null) {
        // --- DETAILS MODE ---
        SubjectDetailsScreen(
            subject = selectedSubject!!,
            onBack = { selectedSubject = null }
        )
    } else {
        // --- LIST MODE ---
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "My Subjects",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black // Main text black
                            )
                            Text(
                                text = "${subjects.size} subjects",
                                fontSize = 14.sp,
                                color = Color.Gray // Subtitle gray
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                        }
                    },
                    actions = {
                        IconButton(onClick = { showAddSubjectScreen = true }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Subject",
                                tint = Color(0xFF2196F3)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onViewDetails = { selectedSubject = subject },
                        onEdit = { subjectToEdit = subject } // Trigger Edit Mode
                    )
                }
            }
        }
    }
}

// --- REFACTORED: ADD OR EDIT SUBJECT SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddOrEditSubjectScreen(
    subjectToEdit: Subject? = null, // If null, we are adding. If set, we are editing.
    onBack: () -> Unit,
    onSave: (Subject) -> Unit,
    onDelete: () -> Unit = {}
) {
    // Initialize state with existing data if editing, or defaults if creating
    var subjectName by remember { mutableStateOf(subjectToEdit?.name ?: "") }
    var creditHours by remember { mutableStateOf(subjectToEdit?.credits?.toString() ?: "3") }
    var selectedColor by remember { mutableStateOf(subjectToEdit?.color ?: Color(0xFF2196F3)) }

    val isEditMode = subjectToEdit != null
    val screenTitle = if (isEditMode) "Edit Subject" else "Create Subject"
    val buttonText = if (isEditMode) "Update Subject" else "Create Subject"

    val colors = listOf(
        Color(0xFF2196F3), Color(0xFF9C27B0), Color(0xFFFF9800), Color(0xFF4CAF50),
        Color(0xFFF44336), Color(0xFF00BCD4), Color(0xFFE91E63), Color(0xFF3F51B5)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(screenTitle, fontSize = 18.sp, color = Color.Black)
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                actions = {
                    // Show Delete icon only in Edit Mode
                    if (isEditMode) {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFE57373) // Light Red
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Preview Section
            Text(
                text = "Preview",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Live Preview Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(selectedColor, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        // Display first letter of name or Book icon
                        if (subjectName.isNotEmpty()) {
                            Text(
                                text = subjectName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Book,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (subjectName.isEmpty()) "Subject Name" else subjectName,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Black // Main text Black
                        )
                        Text(
                            text = "${creditHours.toIntOrNull() ?: 0} credits",
                            fontSize = 14.sp,
                            color = Color.Gray // Subtitle Gray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Form Inputs
            Text("Subject Name", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = subjectName,
                onValueChange = { subjectName = it },
                placeholder = { Text("e.g., Data Structures") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black), // Input text Black
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF2196F3),
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text("Credit Hours", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = creditHours,
                onValueChange = { if (it.all { char -> char.isDigit() }) creditHours = it },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                textStyle = LocalTextStyle.current.copy(color = Color.Black), // Input text Black
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = Color.LightGray,
                    focusedBorderColor = Color(0xFF2196F3),
                    cursorColor = Color.Black,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Color Picker
            Text("Subject Color", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(colors) { color ->
                    ColorOption(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = { selectedColor = color }
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // 4. Update/Create Button
            Button(
                onClick = {
                    if (subjectName.isNotEmpty()) {
                        // Preserve stats if editing, or set to 0 if new
                        onSave(
                            Subject(
                                name = subjectName,
                                credits = creditHours.toIntOrNull() ?: 0,
                                total = subjectToEdit?.total ?: 0,
                                exams = subjectToEdit?.exams ?: 0,
                                tasks = subjectToEdit?.tasks ?: 0,
                                color = selectedColor
                            )
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(buttonText, fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

// --- SUBJECT DETAILS SCREEN ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubjectDetailsScreen(
    subject: Subject,
    onBack: () -> Unit
) {
    val assessments = listOf(
        AssessmentItem("Exam", "Dec 10, 2025", "Midterm exam on SQL queries", Color(0xFFF44336))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(subject.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("${subject.credits} credits", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.Black)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(16.dp)
        ) {
            // 1. Main Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEEEEEE))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(subject.color, RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(subject.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("${subject.total} item total", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBox(subject.exams, "Exams", Color(0xFFE57373), Color(0xFFFFEBEE), Modifier.weight(1f))
                StatBox(subject.tasks, "Assignments", Color(0xFF64B5F6), Color(0xFFE3F2FD), Modifier.weight(1f))
                StatBox(0, "Completed", Color(0xFF81C784), Color(0xFFE8F5E9), Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Exams & Assignments", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                Button(
                    onClick = { /* Add Logic */ },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(assessments) { item -> AssessmentItemCard(item) }
            }
        }
    }
}

// --- HELPER COMPONENTS ---

@Composable
fun ColorOption(color: Color, isSelected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(50.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .clickable(onClick = onClick)
            .then(
                if (isSelected) Modifier.border(3.dp, Color.White, RoundedCornerShape(8.dp)) else Modifier
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) Icon(Icons.Default.Check, null, tint = Color.White)
    }
}

@Composable
fun SubjectCard(subject: Subject, onViewDetails: () -> Unit, onEdit: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(subject.color, CircleShape), contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Book, null, tint = Color.White, modifier = Modifier.size(24.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(subject.name, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Black)
                    Text("${subject.credits} credits", fontSize = 14.sp, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                StatisticItem(subject.total.toString(), "Total", Color(0xFF2196F3))
                StatisticItem(subject.exams.toString(), "Exams", Color(0xFFF44336))
                StatisticItem(subject.tasks.toString(), "Tasks", Color(0xFF4CAF50))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onViewDetails,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    shape = RoundedCornerShape(8.dp)
                ) { Text("View Details", fontSize = 16.sp, color = Color.White) }
                OutlinedButton(
                    onClick = onEdit,
                    modifier = Modifier.weight(0.5f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Gray)
                ) { Text("Edit", fontSize = 16.sp, color = Color.Gray) }
            }
        }
    }
}

@Composable
fun StatisticItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 14.sp, color = Color.Gray)
    }
}

@Composable
fun StatBox(count: Int, label: String, textColor: Color, backgroundColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.height(60.dp).background(backgroundColor, RoundedCornerShape(8.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(count.toString(), fontWeight = FontWeight.Bold, color = textColor, fontSize = 16.sp)
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun AssessmentItemCard(item: AssessmentItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Surface(color = item.color, shape = RoundedCornerShape(12.dp)) {
                Text(item.type, color = Color.White, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.date, fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.description, fontSize = 14.sp, color = Color.Gray)
        }
    }
}
