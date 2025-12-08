package com.example.studentmate.Data.Dao

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

@Dao
interface SubjectDao {
    @Insert
    suspend fun insert(subject: Subject)

    @Update
    suspend fun update(subject: Subject)

    @Delete
    suspend fun delete(subject: Subject)

    @Query("SELECT * FROM subjects")
    fun getAll(): LiveData<List<Subject>>

    @Query("SELECT * FROM subjects WHERE id=:id")
    fun getById(id: Int): LiveData<Subject>

    @Insert
    suspend fun insertStudentSubjectCrossRef(crossRef: SubjectsAndStudents)

    // In SubjectDao.kt
    @Transaction
    @Query("SELECT * FROM students WHERE id = :studentId")
    fun getStudentSubjects(studentId: Int): LiveData<List<StudentWithSubjects>>}
