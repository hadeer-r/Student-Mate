package com.example.studentmate.Data.Relations

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Junction
import androidx.room.Relation
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject

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
data class SubjectsAndStudents(
    val subjectId: Int,
    val studentId: Int
)

data class StudentWithSubjects(
    @Embedded val student: Student,
    @Relation(
        parentColumn = "studentId",
        entityColumn = "subjectId",
        associateBy = Junction(SubjectsAndStudents::class) // This tells Room to look at the middle table
    )
    val subjects: List<Subject>
)