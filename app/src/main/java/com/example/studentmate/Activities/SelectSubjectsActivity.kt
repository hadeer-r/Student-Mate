package com.example.studentmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//   (Data Model)
data class Subject(
    val id: Int,
    val name: String,
    val type: String,
    var isSelected: Boolean = false
)

class SelectSubjectsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SelectSubjectsScreen()
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectSubjectsScreen() {
    //    (State)
    val subjects = remember {
        mutableStateListOf(
            Subject(1, "Data Structures", "Exam"),
            Subject(2, "Web Development", "Assignment"),
            Subject(3, "Database Systems", "Exam"),
            Subject(4, "Mobile Computing", "Assignment"),

        )
    }

    val selectedCount = subjects.count { it.isSelected }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(text = "Calculate GPA", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                        Text(text = "Step 1: Select subjects", fontSize = 14.sp, color = Color.Gray)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { /* Handle Back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Button(
                onClick = { /* move to gpacalc screen later */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(top = 16.dp, bottom = 32.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
            ) {
                Text(text = "Next: Enter Grades", fontSize = 16.sp, color = Color.White)
            }
        },
        containerColor = Color(0xFFFAFAFA)
    ) { paddingValues ->

        Column(modifier = Modifier.padding(paddingValues).padding(16.dp)) {
            Text(
                text = "Choose subjects to include",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select the subjects you want to calculate your GPA for",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )


            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { subject ->
                    SubjectItem(
                        subject = subject,
                        onClick = {
                            //  reflect the choice
                            val index = subjects.indexOf(subject)
                            if (index != -1) {
                                subjects[index] = subjects[index].copy(isSelected = !subject.isSelected)
                            }
                        }
                    )
                }
            }

            if (selectedCount > 0) {
                Spacer(modifier = Modifier.height(16.dp))
                Surface(
                    color = Color(0xFFE3F2FD),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "$selectedCount subjects selected",
                        color = Color(0xFF2196F3),
                        modifier = Modifier.padding(8.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectItem(
    subject: Subject,
    onClick: () -> Unit
) {
    val borderColor = if (subject.isSelected) Color(0xFF2196F3) else Color.LightGray
    val backgroundColor = if (subject.isSelected) Color(0xFFE3F2FD) else Color.White

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (subject.isSelected) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Selected",
                    tint = Color(0xFF2196F3),
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .border(2.dp, Color.LightGray, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = subject.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = subject.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}