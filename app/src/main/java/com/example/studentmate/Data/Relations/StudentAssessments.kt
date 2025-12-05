package com.example.studentmate.Data.Relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student


data class StudentAssessments(
    @Embedded val student: Student,
    @Relation(parentColumn = "id", entityColumn = "studentId")
    val assessments: List<Assessment>,
)
