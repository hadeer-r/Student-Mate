package com.example.studentmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import com.example.studentmate.ui.theme.StudentMateTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val db = AppDatabase.getDatabase(this)
        val studentDao = db.studentDao()
        val subjectDao = db.subjectDao()
        val assessmentDao = db.assessmentDao()
        var scope = this.lifecycleScope
        lateinit var students: List<Student>
        lateinit var subjects: List<Subject>
        lateinit var assessments: List<Assessment>
        scope.launch {
            students = studentDao.GetAll();
             subjects = subjectDao.getAll();
             assessments =assessmentDao.getAllAssessments();
        }
        enableEdgeToEdge()
        setContent {
            StudentMateTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    TestScreen(
                        students = students,
                        subjects = subjects,
                        assessments = assessments,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun TestScreen(
    students: List<Student>,
    subjects: List<Subject>,
    assessments: List<Assessment>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = "🎉 Database Status:")
        Text(text = "------------------")
        Text(text = "Students Found: ${students.size}")

        if (students.isNotEmpty()) {
            Text(text = "First Student: ${students[0].name}")
        }

        Text(text = "Subjects Found: ${subjects.size}")
        Text(text = "Assessments Found: ${assessments.size}")

    }
}
