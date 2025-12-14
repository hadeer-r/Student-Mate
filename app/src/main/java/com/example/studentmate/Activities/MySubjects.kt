package com.example.studentmate.Activities

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
//import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import com.example.studentmate.ui.theme.StudentMateTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.lang.Long

class MySubjectsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        var db = AppDatabase.getDatabase(this)
        var bundle = intent.extras
        var student: Student? = null
        if(bundle != null){
            student = Student(
                password = bundle.getString("password")!!,
                name = bundle.getString("name")!!,
                email = bundle.getString("email")!!,
                id = bundle.getInt("id")

            )
        }
        setContent {
            StudentMateTheme {
                MySubjectsScreen(
                    db,
                    onNavigateBack = { finish() },
                    student = student!!
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MySubjectsScreen(
    db: AppDatabase,
    student: Student,
    onNavigateBack: () -> Unit = {},
    onEditSubject: (Subject) -> Unit = {}
) {
    var scope = rememberCoroutineScope()
    var context = LocalContext.current

    // 1. Subject List Data
    var allsubjects by remember { mutableStateOf<List<Subject>>(emptyList()) }

    LaunchedEffect(Unit) {
        allsubjects = db.subjectDao().getAll()
    }

    // 2. Navigation States
    var showAddSubjectScreen by remember { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var subjectToEdit by remember { mutableStateOf<Subject?>(null) }

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
                scope.launch {
                    db.subjectDao().insert(newSubject)
                    Toast.makeText(context, "Added Successfully", Toast.LENGTH_SHORT).show()
                    allsubjects = db.subjectDao().getAll()
                }
                showAddSubjectScreen = false
            }
        )
    } else if (subjectToEdit != null) {
        // --- EDIT MODE (New) ---
        AddOrEditSubjectScreen(
            subjectToEdit = subjectToEdit,
            onBack = { subjectToEdit = null },
            onSave = { updatedSubject ->
                val finalSubject = updatedSubject.copy(id = subjectToEdit!!.id)

                scope.launch {
                    db.subjectDao().update(finalSubject)
                    Toast.makeText(context, "Updated Successfully", Toast.LENGTH_SHORT).show()
                    allsubjects = db.subjectDao().getAll()
                }
                subjectToEdit = null
            },
            onDelete = {
                scope.launch {
                    db.subjectDao().delete(subjectToEdit!!)
                    Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show()
                    allsubjects = db.subjectDao().getAll()
                    subjectToEdit = null

                }
            }
        )
    } else if (selectedSubject != null) {
        // --- DETAILS MODE ---
        SubjectDetailsScreen(
            subject = selectedSubject!!,
            onBack = { selectedSubject = null },
            db = db,
            student = student
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
                                text = "${allsubjects.size} subjects",
                                fontSize = 14.sp,
                                color = Color.Gray // Subtitle gray
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                items(allsubjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onViewDetails = { selectedSubject = subject },
                        onEdit = { subjectToEdit = subject } ,
                        db = db,
                        student = student
                    )
                }
            }
        }
    }
}

// --- ADD OR EDIT SUBJECT SCREEN ---
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
    var selectedColor by remember { mutableStateOf(Color(Long.decode(subjectToEdit?.color ?: "0xFF2196F3"))) }

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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                border = BorderStroke(1.dp, Color.LightGray)
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
//                                total = subjectToEdit?.total ?: 0,
//                                exams = subjectToEdit?.exams ?: 0,
//                                tasks = subjectToEdit?.tasks ?: 0,
                                color = selectedColor.toArgb().toString()
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
    onBack: () -> Unit,
    db: AppDatabase,
    student: Student
) {
    var context = LocalContext.current
    var assessmentsforTheSubject by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    LaunchedEffect(key1 = true) {
        assessmentsforTheSubject = db.assessmentDao().GetAssessmentByStudentIdAndSubjectId(subjectId = subject.id, studentId = student.id)
    }


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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
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
                            .background(Color(Long.decode(subject.color)), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(subject.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text("${assessmentsforTheSubject.size} item total", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Stats Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatBox(assessmentsforTheSubject.filter { it.isExam }.size, "Exams", Color(0xFFE57373), Color(0xFFFFEBEE), Modifier.weight(1f))
                StatBox(assessmentsforTheSubject.filter { !it.isExam }.size, "Assignments", Color(0xFF64B5F6), Color(0xFFE3F2FD), Modifier.weight(1f))
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
                    onClick = { goToAddAsssessment(context, student)},
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp), tint = Color.White)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add", fontSize = 12.sp, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(assessmentsforTheSubject) { item -> AssessmentItemCard(item, Color(0xFF2196F3)) }
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
fun SubjectCard(subject: Subject, onViewDetails: () -> Unit, onEdit: () -> Unit, db: AppDatabase, student: Student) {
    var assessmentsforTheSubject by remember { mutableStateOf<List<Assessment>>(emptyList()) }
    LaunchedEffect(key1 = true) {
        assessmentsforTheSubject = db.assessmentDao().GetAssessmentByStudentIdAndSubjectId(subjectId = subject.id, studentId = student.id)
    }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(48.dp).background(Color(Long.decode(subject.color)), CircleShape), contentAlignment = Alignment.Center) {
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
                StatisticItem(assessmentsforTheSubject.size.toString(), "Total", Color(0xFF2196F3))
                StatisticItem(assessmentsforTheSubject.filter {it.isExam}.size.toString(), "Exams", Color(0xFFF44336))
                StatisticItem(assessmentsforTheSubject.filter {!it.isExam}.size.toString(), "Tasks", Color(0xFF4CAF50))
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
fun AssessmentItemCard(item: Assessment, color: Color) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF5F5F5))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(color = color, shape = RoundedCornerShape(12.dp)) {
                    val type = if(item.isExam) "Exam" else "Assignment"
                    Text(
                        text = type,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.DateRange, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(item.deadline.toString(), fontSize = 13.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.description, fontSize = 14.sp, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp))

    }
}

