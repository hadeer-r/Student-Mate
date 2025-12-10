package com.example.studentmate.Data.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.studentmate.Data.Models.Student

@Dao
interface StudentDao {
    @Insert
    suspend fun insert(student: Student): Long


    @Update
    suspend fun update(student: Student)

    @Delete
    suspend fun delete(student: Student)

    @Query("SELECT * FROM students")
    suspend fun GetAll(): List<Student>

    @Query("SELECT * FROM students WHERE id=:id ")
    suspend fun GetById(id: Int): Student

    @Query("SELECT * FROM students WHERE password=:password AND email=:email")
    suspend fun GetByEmailAndPassword(password: String, email: String): Student?
}
