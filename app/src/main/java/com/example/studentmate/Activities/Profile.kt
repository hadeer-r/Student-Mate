package com.example.studentmate.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.Activities.ui.theme.StudentMateTheme
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val db = AppDatabase.getDatabase(this);
        var bundle = intent.extras
        var student: Student? = null
        if(bundle != null){
            student = Student(
                password = bundle.getString("password")!!,
                name = bundle.getString("name")!!,
                email = bundle.getString("email")!!,

                )
        }
        setContent {
            StudentMateTheme {
                ProfileScreen(db, student)
            }
        }
    }
}


@Composable
fun ProfileScreen(db: AppDatabase, student: Student?) {
    var context= LocalContext.current
    var scope = rememberCoroutineScope()
    var fullname by remember { mutableStateOf(student?.name ?: "") }
    var currentPassword by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // --- 1. Header (Circle Image & Name) ---
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color(0xFF2196F3)), // Brand Blue
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(text = student?.name ?: "", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))
        LabeledTextFieldClickable(
            label = "Full Name",
            text = fullname,
            placeholder = "Enter full name",
            onTextChange = { fullname = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabeledTextFieldClickable(
            label = "Current Password",
            text = currentPassword,
            placeholder = "Enter current password",
            onTextChange = { currentPassword = it }
        )

        Spacer(modifier = Modifier.height(8.dp))

        LabeledTextFieldClickable(
            label = "New Password",
            text = password,
            placeholder = "Enter new password",
            onTextChange = { password = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if(currentPassword == student?.password && password.isNotEmpty()){
                    student?.password = password
                    student?.name = fullname
                    saveChanges(context, scope, db, student)
                }
                else if(currentPassword.isEmpty()) {
                    student?.name = fullname
                    saveChanges(context, scope, db, student)
                }
                else {
                    Toast.makeText(context, "Current password is Wrong", Toast.LENGTH_LONG).show()
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { logOut(context) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)), // Red Color
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.ExitToApp, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Log Out")
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}


@Preview(showBackground = true)
@Composable
fun ProfileScreenPreview() {
    MaterialTheme {
        ProfileScreen(db = AppDatabase.getDatabase(LocalContext.current), student = null)
    }
}

@Composable
fun LabelledInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    readOnly: Boolean = false,
    placeholder: String = ""
) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            placeholder = { Text(placeholder, fontSize = 14.sp, color = Color.LightGray) },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedContainerColor = Color.White,
                focusedContainerColor = Color.White,
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray
            )
        )
    }
}



fun saveChanges(context: Context,scope: CoroutineScope,db: AppDatabase, student: Student?)
{
    scope.launch {
        if(student != null)
        {
            db.studentDao().update(student)
            Log.d("studentT","Student Updated")
        }

    }
}
fun logOut(context: Context) {
    var intent = Intent(context, Login::class.java)
    context.startActivity(intent)
}
