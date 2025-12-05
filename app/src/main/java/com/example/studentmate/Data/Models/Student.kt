package com.example.studentmate.Data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val name: String,
    val email: String,
    val password: String
)