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
import kotlinx.coroutines.delay

class Profile : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentMateTheme {
                ProfileScreen()
            }
        }
    }
}


@Composable
fun ProfileScreen() {
    var context= LocalContext.current
    var fullName by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var currentPassword by remember { mutableStateOf("") }
    var trueCurrentPassword by remember { mutableStateOf("") }
    var isExamReminderEnabled by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        val data = loadData()
        Log.d("data",data.name)
        fullName = data.name
        trueCurrentPassword=data.password
        isExamReminderEnabled = data.notificationsEnabled
    }

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

        Text(text = fullName, fontSize = 18.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(8.dp))

        // --- 2. Input Fields ---
        LabeledTextFieldClickable(
            label = "Full Name",
            text = fullName,
            placeholder = fullName,
            onTextChange = { fullName = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        LabeledTextFieldClickable(
            label = "Current Password",
            text = currentPassword,
            placeholder = currentPassword,
            onTextChange = { currentPassword = it }
        )
        Spacer(modifier = Modifier.height(8.dp))
        LabeledTextFieldClickable(
            label = "New Password",
            text = password,
            placeholder = password,
            onTextChange = { password = it }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // --- 3. Save Button (Blue) ---
        Button(
            onClick = {
                if(currentPassword.equals(trueCurrentPassword))
                    saveChanges(context,fullName,password)
                else
                    Toast.makeText(context,"Current password is Wrong",Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Text("Save Changes")
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 4. Notifications Card ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, contentDescription = null, tint = Color(0xFF2196F3))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Notifications", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Enable exam reminders", fontSize = 14.sp)
                        Text("Get notified about upcoming exams", fontSize = 12.sp, color = Color.Gray)
                    }
                    Switch(
                        checked = isExamReminderEnabled,
                        onCheckedChange = { isExamReminderEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2196F3))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- 5. Logout Button (Red) ---
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
        ProfileScreen()
    }
}
// --- Helper Composable for Text Fields ---
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

data class UserData(val name: String, val id: String, val password: String, val notificationsEnabled: Boolean)

suspend fun loadData(): UserData {
    delay(1000)
    return UserData("Sarah Johnson", "ST2023456","1234", true)
}
fun saveChanges(context: Context,fullName: String,password: String)
{

}
fun logOut(context: Context) {
    var intent = Intent(context, Login::class.java)
    context.startActivity(intent)
}
