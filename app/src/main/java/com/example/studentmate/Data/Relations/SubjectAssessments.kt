package com.example.studentmate.Data.Relations

import androidx.room.Embedded
import androidx.room.Relation
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Subject

data class SubjectAssessments(
    @Embedded val subject: Subject,
    @Relation(parentColumn = "id", entityColumn = "subjectId")
    val assessments: List<Assessment>
)
