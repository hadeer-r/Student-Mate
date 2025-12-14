package com.example.studentmate.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase // Import this
import com.example.studentmate.Data.Dao.AssessmentDao
import com.example.studentmate.Data.Dao.StudentDao
import com.example.studentmate.Data.Dao.SubjectDao
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import com.example.studentmate.Data.Relations.SubjectsAndStudents
import com.example.studentmate.converters.Converters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date


@Database(
    entities = [
        Assessment::class,
        Student::class,
        Subject::class,
        SubjectsAndStudents::class],
    version = 1,
    exportSchema = false
)

@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun subjectDao(): SubjectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                )
                    .addCallback(StudentDatabaseCallback())
                    .build()
                INSTANCE = instance
                instance
            }
        }

        // Define the Callback
        private class StudentDatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // 3. This runs only ONCE when the DB is first built
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateDatabase(database)
                    }
                }
            }

            suspend fun populateDatabase(db: AppDatabase) {
                val studentDao = db.studentDao()
                val subjectDao = db.subjectDao()
                val assessmentDao = db.assessmentDao()

                // Remember to capture the IDs!
                val student1 = Student(
                    name = "John Doe",
                    email = "john.doe@example.com",
                    password = "password",
                    )

                // CRITICAL: Capture the generated ID
                val studentId = db.studentDao().insert(student1)

                val subject1 = Subject(name = "Mathematics",
                    credits = 3,
                    color = "#FF5733")
                val subject2 = Subject(name = "History"
                    , credits = 3,
                    color = "#FF5733")

                // CRITICAL: Capture generated IDs
                val subject1Id = db.subjectDao().insert(subject1)
                val subject2Id = db.subjectDao().insert(subject2)

                // Associate subjects using the CAPTURED IDs (not subject1.id)
                db.subjectDao().insertStudentSubjectCrossRef(
                    SubjectsAndStudents(
                        subjectId = subject1Id.toInt(),
                        studentId = studentId.toInt()
                    )
                )
                db.subjectDao().insertStudentSubjectCrossRef(
                    SubjectsAndStudents(
                        subjectId = subject2Id.toInt(),
                        studentId = studentId.toInt()
                    )
                )

                // Create assessments using CAPTURED IDs
                db.assessmentDao().insertAssessment(
                    Assessment(
                        subjectId = subject1Id.toInt(),
                        studentId = studentId.toInt(),
                        title = "Math Exam",
                        description = "Final exam for Mathematics",
                        deadline = Date(),
                        score = 100,
                        isExam = true,
                        isDone = false,
                        actualScore = 0,
                    )
                )

                db.assessmentDao().insertAssessment(
                    Assessment(
                        subjectId = subject2Id.toInt(),
                        studentId = studentId.toInt(),
                        title = "History Paper",
                        description = "Term paper on World War II",
                        deadline = Date(),
                        score = 100,
                        isExam = false,
                        actualScore = 0,
                        isDone = true,
                    )
                )
            }
        }
    }
}