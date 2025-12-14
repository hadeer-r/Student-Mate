package com.example.studentmate.Data.Dao
import androidx.room.*
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.studentmate.Data.Models.Subject
import com.example.studentmate.Data.Relations.StudentWithSubjects
import com.example.studentmate.Data.Relations.SubjectsAndStudents
import kotlinx.coroutines.flow.Flow
@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(subject: Subject): Long

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)

    @Query("SELECT * FROM subjects")
    suspend fun getAll(): List<Subject>

    @Query("SELECT * FROM subjects WHERE id=:id")
    suspend fun getById(id: Int): Subject

    @Insert
    suspend fun insertStudentSubjectCrossRef(crossRef: SubjectsAndStudents)
    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): Flow<List<Subject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)
    // In SubjectDao.kt
    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    suspend fun getStudentSubjects(studentId: Int): List<StudentWithSubjects>
}
