package com.example.studentmate.Activities
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
//import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.studentmate.Activities.ui.theme.StudentMateTheme
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.log

class Login : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var db= AppDatabase.getDatabase(this)
        setContent {
            StudentMateTheme {
               StudentMateLoginScreen(db)
            }
        }
    }
}
enum class LoginNav{
    Register,
    Home
}

@Composable
fun StudentMateLoginScreen(db: AppDatabase) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    var navController = rememberNavController()


    // Root container (Box allows us to overlap the Help icon at the bottom)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)

    ) {
        // Main Content (Scrollable Column)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFFAFAFA)), // Makes it scrollable on small screens
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // --- Logo Section ---
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(color = BrandBlue, shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.School,
                    contentDescription = "Logo",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Student Mate",
                fontSize = 22.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Track your academic progress",
                fontSize = 14.sp,
                color =TextGray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- Inputs Section ---

            // Username Field
            Column(modifier = Modifier.padding(16.dp)) {
              LabeledTextFieldClickable(
                    label = "Email",
                    text = email,
                    placeholder = "Enter Your user name",
                    onTextChange = { email = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledTextFieldClickable(
                    label = "UPassword",
                    text = password,
                    placeholder = "Enter your password",
                    onTextChange = { password = it }
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Go to login Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                // 1. This aligns the children (the texts) in the center of the screen
                horizontalArrangement = Arrangement.Center,
                // 2. This ensures the text aligns vertically in the middle of the row
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ", // Added a space at the end so they aren't stuck together
                    color = Color.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = "Register",
                    color =BrandBlue, // Your custom color
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, // Optional: Makes the link stand out
                    // 3. This makes only this text clickable
                    modifier = Modifier.clickable {
                        goToRegister(context )
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Login Button ---
            Button(
                onClick = {
                    if(email.isEmpty()||password.isEmpty())
                        Toast.makeText(context, "all information required", Toast.LENGTH_SHORT).show()
                    else  Login(context, email ,password,db)},
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Log In", fontSize = 16.sp)
            }
        }

        // --- Bottom Right Help Icon ---
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp)
                .size(40.dp) // Size of the black circle
                .background(Color(0xFF212121), CircleShape)
                .clickable { /* Handle Help Click */ },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "?",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// 2. Reusable Component for the Inputs


@Preview(showBackground = true)
@Composable
fun LoginPreview() {
    MaterialTheme {
        StudentMateLoginScreen(db = AppDatabase.getDatabase(LocalContext.current))
    }
}
fun Login(context: Context, email: String, password: String, db: AppDatabase) {

    (context as? LifecycleOwner)?.lifecycleScope?.launch(Dispatchers.IO) {

        val student = db.studentDao().GetByEmailAndPassword(password, email)
        withContext(Dispatchers.Main) {
            Log.d("Login", student.toString())

            if (student != null) {
                goToHome(context, student)
            } else {
                Toast.makeText(context, "Invalid Credentials", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
fun goToRegister(context: Context)
{
    val intent= Intent(context, Register::class.java)
    context.startActivity((intent))
}

fun goToHome(context: Context, student: Student) {
    val bundle = Bundle().apply {
        putString("name", student.name)
        putString("email", student.email)
        putString("password", student.password)
        putBoolean("notificationsEnabled",student.notificationsEnabled)
    }

    val intent = Intent(context, HomeActivity::class.java);
    intent.putExtras(bundle)
    context.startActivity((intent))
}