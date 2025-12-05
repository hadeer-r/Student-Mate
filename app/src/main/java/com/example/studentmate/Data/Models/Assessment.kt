package com.example.studentmate.Data.Models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = "assessments",
    foreignKeys = [
        ForeignKey(
            entity = Subject::class,
            parentColumns = ["id"],
            childColumns = ["subjectId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = Student::class,
            parentColumns = ["id"],
            childColumns = ["studentId"],
            onDelete = CASCADE
        )
    ]
)
data class Assessment(
    @PrimaryKey(autoGenerate = true) val id: Int = 1,
    val subjectId: Int,
    val studentId: Int,
    val title: String,
    val description: String,
    val deadline: Date,
    val score: Int,
    val isExam: Boolean,
    val actualScore: Int,

    )