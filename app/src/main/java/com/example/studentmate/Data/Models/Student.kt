package com.example.studentmate.Data.Models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true) var  id: Int = 0,
    var name: String,
    var email: String,
    var password: String,
)