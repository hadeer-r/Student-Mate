package com.example.studentmate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.studentmate.ui.theme.StudentMateTheme



data class EditItem(
    val id: Int = 0,
    val type: String,
    val subject: String,
    val date: String,
    val score: String,
    val description: String,
    val notify: Boolean
)

class EditItemActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentMateTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color(0xFFFAFAFA)
                ) {
                    EditItemScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditItemScreen() {
    var selectedType by remember { mutableStateOf("Assignment") }

    var subjectName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var score by remember { mutableStateOf("0") }
    var description by remember { mutableStateOf("") }
    var isNotificationEnabled by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Item", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { /* Handle Back */ }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { /*  Handle Delete */ }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFFF5252))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = Color.White) {
                Button(
                    onClick = {
                        val itemToSave = EditItem(
                            type = selectedType,
                            subject = subjectName,
                            date = date,
                            score = score,
                            description = description,
                            notify = isNotificationEnabled
                        )
                        // TODO:Save 'itemToSave' to the database
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 32.dp)
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                ) {
                    Text("Update Item", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            TypeSelector(selectedType) { selectedType = it }

            CustomTextField(
                label = "Subject Name",
                value = subjectName,
                placeholder = "Enter Subject Name"
            ) { subjectName = it }

            CustomTextField(
                label = "Date",
                value = date,
                placeholder = "mm/dd/yyyy"
            ) { date = it }

            ScoreTextField(
                label = "Score / Grade (Optional)",
                value = score,
                onValueChange = { score = it }
            )

            CustomTextField(
                label = "Description",
                value = description,
                isSingleLine = false,
                placeholder = "Add notes or details about this item"
            ) { description = it }

            NotificationSwitch(isNotificationEnabled, selectedType) { isNotificationEnabled = it }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}


@Composable
fun ScoreTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { newValue ->

                if (newValue.all { it.isDigit() }) {
                    onValueChange(newValue)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            placeholder = { Text("0", color = Color.LightGray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            ),
            trailingIcon = {
                Column(
                    modifier = Modifier.padding(end = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropUp,
                        contentDescription = "Increase",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val currentScore = value.toIntOrNull() ?: 0
                                onValueChange((currentScore + 1).toString())
                            },
                        tint = Color.Gray
                    )
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Decrease",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable {
                                val currentScore = value.toIntOrNull() ?: 0
                                if (currentScore > 0) {
                                    onValueChange((currentScore - 1).toString())
                                }
                            },
                        tint = Color.Gray
                    )
                }
            }
        )
    }
}

@Composable
fun CustomTextField(
    label: String,
    value: String,
    placeholder: String = "",
    isSingleLine: Boolean = true,
    onChange: (String) -> Unit
) {
    Column {
        Text(label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            singleLine = isSingleLine,
            minLines = if (isSingleLine) 1 else 3,
            placeholder = { Text(text = placeholder, color = Color.LightGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun TypeSelector(currentType: String, onTypeSelected: (String) -> Unit) {
    Text("Type", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        TypeButton("Assignment", currentType == "Assignment", Color(0xFF2196F3), Modifier.weight(1f)) { onTypeSelected("Assignment") }
        TypeButton("Exam", currentType == "Exam", Color(0xFFFF5252), Modifier.weight(1f)) { onTypeSelected("Exam") }
    }
}

@Composable
fun TypeButton(text: String, isSelected: Boolean, color: Color, modifier: Modifier, onClick: () -> Unit) {
    val bgColor = if (isSelected) color.copy(alpha = 0.1f) else Color.White
    val borderColor = if (isSelected) color else Color.LightGray.copy(alpha = 0.5f)
    val textColor = if (isSelected) color else Color.Gray

    Surface(
        modifier = modifier.height(50.dp).clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderColor),
        color = bgColor
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(text = text, color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal)
        }
    }
}

@Composable
fun NotificationSwitch(isEnabled: Boolean, type: String, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.LightGray.copy(0.5f), RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text("Notify me before ${type.lowercase()}", fontWeight = FontWeight.Medium)
            Text("Get reminders", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF2196F3))
        )
    }
}