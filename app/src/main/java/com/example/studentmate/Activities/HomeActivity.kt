package com.example.studentmate.Activities

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.ui.theme.StudentMateTheme
import com.example.studentmate.Data.Models.Assessment
import java.util.Calendar
import java.util.Date

class HomeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this);
        enableEdgeToEdge()
        setContent {
            StudentMateTheme {
                HomeScreen();
            }
        }
    }
}

@Composable
fun HomeScreen() {
    val assessments = listOf(
        Assessment(
            1,
            1,
            1,
            "Web development",
            "Assignment",
            Calendar.getInstance().apply { set(2025, Calendar.NOVEMBER, 30) }.time,
            100,
            false,
    false,
            actualScore = 100
        ),
        Assessment(
            2,
            2,
            1,
            "andriod exam",
            "Assignment"
            , Calendar.getInstance().apply { set(2025, Calendar.NOVEMBER, 30) }.time,
            0,
            true,
            false,
            actualScore = 150
        )
    )

    Scaffold(
        containerColor = Color(0xFFF9FAFB),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Handle Add */ },
                containerColor = Color(0xFF2196F3),
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Section
            item {
                Header()
                Spacer(modifier = Modifier.height(20.dp))
            }

            // 2. Action Buttons
            item {
                DashboardButton(
                    text = "My Subjects",
                    icon = Icons.Default.Menu,
                    backgroundColor = Color(0xFF8E24AA) // Purple
                )
                Spacer(modifier = Modifier.height(12.dp))
                DashboardButton(
                    text = "Calculate My GPA",
                    icon = Icons.Default.Check,
                    backgroundColor = Color(0xFF1E88E5) // Blue
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
            items(assessments) { item ->
                AssessmentCard(item)
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}
@Composable
fun Header(){
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Column {
            Text(
                text = "Hello, Sarah Johnson",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Track your progress",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Surface(
            shape = CircleShape,
            color = Color(0xFF2196F3),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center){
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    tint = Color.White,
                )
            }
        }
    }
}

@Composable
fun DashboardButton(
    text: String,
    icon: ImageVector,
    backgroundColor: Color,
){
    Button(
        onClick = { /* Handle button click */ },
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp)
    ){
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, fontSize = 16.sp)
    }
}

@Composable
fun AssessmentCard(item: Assessment){
    var assessmentColor: Color
    var assessmentType: String

    if(item.isExam)
    {
        assessmentColor = Color.Red
        assessmentType="Exam"
    }
    else {
        assessmentColor = Color.Green
        assessmentType = "Assignment"
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ){
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top

            ){
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Surface(
                    color = assessmentColor,
                    shape = RoundedCornerShape(16.dp)

                ) {
                    Text(
                        text = assessmentType,
                        color = Color.White,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            // Row 2
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = item.deadline.toString(),
                    fontSize = 14.sp,
                    color = Color.Gray
                )


            }
            Spacer(modifier = Modifier.height(8.dp))

            // row 3
            Text(
                text = item.description,
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))


            Surface(
                color = Color(0xFFE8F5E9),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Score: ${item.score}",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}