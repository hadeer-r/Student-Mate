package com.example.studentmate.Data
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.studentmate.Data.Dao.AssessmentDao
import com.example.studentmate.Data.Dao.StudentDao
import com.example.studentmate.Data.Dao.SubjectDao
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import com.example.studentmate.Data.Relations.SubjectsAndStudents
import com.example.studentmate.converters.Converters

@Database(
    entities = [
        Assessment::class,
        Student::class,
        Subject::class,
        SubjectsAndStudents::class],
    version = 1,
    exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun assessmentDao(): AssessmentDao
    abstract fun subjectDao(): SubjectDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase?=null

        fun getDatabase(context: Context): AppDatabase{
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}