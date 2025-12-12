package com.example.studentmate.Activities
import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Dao.StudentDao_Impl
import com.example.studentmate.Data.Models.Student
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

//import com.example.studentmate.ui.theme
val BrandBlue = Color(0xFF2196F3)
val TextGray = Color(0xFF757575)
class Register : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db= AppDatabase.getDatabase(this)
        enableEdgeToEdge()
        setContent {
                StudentMateRegisterScreen(db)

        }
    }
}

@Composable
fun StudentMateRegisterScreen(db: AppDatabase) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope ()
    val context = LocalContext.current

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

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Track your academic progress",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(10.dp))

            // --- Inputs Section ---
            //FullNameField
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledTextFieldClickable(
                    label = "FullName",
                    text = fullName,
                    placeholder = "Enter Your full name",
                    onTextChange = { fullName = it }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.padding(16.dp)) {
                LabeledTextFieldClickable(
                    label = "Email",
                    text = email,
                    placeholder = "Enter your email",
                    onTextChange = { email = it }
                )}

            Spacer(modifier = Modifier.height(8.dp))

            // Password Field
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledTextFieldClickable(
                    label = "Password",
                    text = password,
                    placeholder = "Enter your password",
                    onTextChange = { password = it }
                )}


            Spacer(modifier = Modifier.height(8.dp))
            //ConfirmPasswordField
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledTextFieldClickable(
                    label = "confirm Password",
                    text = confirmPassword,
                    placeholder = "confirm Password ",
                    onTextChange = { confirmPassword = it }
                )}


            Spacer(modifier = Modifier.height(8.dp))

            // go to login  Link
            Row(
                modifier = Modifier.fillMaxWidth(),
                // 1. This aligns the children (the texts) in the center of the screen
                horizontalArrangement = Arrangement.Center,
                // 2. This ensures the text aligns vertically in the middle of the row
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Already have an account? ", // Added a space at the end so they aren't stuck together
                    color = Color.Black,
                    fontSize = 14.sp
                )
                Text(
                    text = "Login",
                    color = BrandBlue, // Your custom color
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold, // Optional: Makes the link stand out
                    // 3. This makes only this text clickable
                    modifier = Modifier.clickable {
                        goToLogin(context)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Login Button ---
            Button(
                onClick = {
//                      Log.d("password",password)
//                    Log.d("confirmPassword",confirmPassword)
                    if(email.isEmpty()||fullName.isEmpty()||password.isEmpty())
                        Toast.makeText(context, "all information required", Toast.LENGTH_SHORT).show()
                    else if(password.equals(confirmPassword))
                        RegisterF(context,scope,fullName,email,password,db)
                    else
                        Toast.makeText(context, "Confirm Password does not match", Toast.LENGTH_SHORT).show()                    },
                modifier = Modifier
                    .fillMaxWidth(.9f)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(text = "Register", fontSize = 16.sp)
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



@Composable
fun LabeledTextFieldClickable(
    label: String,
    text: String,
    placeholder: String,
    onTextChange: (String) -> Unit
) {
    // FocusRequester for the TextField
    val focusRequester = remember { FocusRequester() }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Clickable label
        Text(
            text = label,

            color = Color.Black,
            fontSize = 14.sp,
            modifier = Modifier
                .padding(bottom = 4.dp)
                .clickable { focusRequester.requestFocus() } // Focus TextField on click
        )

        // TextField
        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester), // attach the focus requester
            placeholder = { Text(placeholder) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
        )
    }
}
fun RegisterF(context: Context,scope: CoroutineScope,name: String,email: String,password: String,db: AppDatabase) {
    scope.launch {
        db.studentDao().insert(Student(name=name,email=email,password=password, notificationsEnabled = false))
        Log.d("studentT","true")

        Log.d("student",db.studentDao().GetAll().toString())
        goToLogin(context)
    }
}
@Preview(showBackground = true)
@Composable
fun RegisterPreview() {
    MaterialTheme {
        StudentMateRegisterScreen(AppDatabase.getDatabase(LocalContext.current))
    }
}
fun goToLogin(context: Context)
{
    val intent= Intent(context, Login::class.java)
    context.startActivity((intent))
}