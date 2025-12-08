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
    primaryKeys = ["subjectId","studentId"],
    tableName = "subject_and_student",
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
        parentColumn = "id",          // The PK in the Student entity
        entityColumn = "id",          // The PK in the Subject entity
        associateBy = Junction(
            value = SubjectsAndStudents::class,
            parentColumn = "studentId", // The column in the Junction table pointing to Student
            entityColumn = "subjectId"  // The column in the Junction table pointing to Subject
        )
    )
    val subjects: List<Subject>
)

data class SubjectsWithStudents(
    @Embedded val subject: Subject,
    @Relation(
        parentColumn = "id",          // The PK in the Subject entity
        entityColumn = "id",          // The PK in the Student entity
        associateBy = Junction(
            value = SubjectsAndStudents::class,
            parentColumn = "subjectId", // The column in the Junction table pointing to Subject
            entityColumn = "studentId"  // The column in the Junction table pointing to Student
        )
    )
    val students: List<Student>
)