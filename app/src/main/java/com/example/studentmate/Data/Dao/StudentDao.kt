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
    fun GetAll(): LiveData<List<Student>>

    @Query("SELECT * FROM students WHERE id=:id ")
    fun GetById(id: Int): LiveData<Student>

    @Query("SELECT * FROM students WHERE password=:password AND email=:email")
    fun GetByEmailAndPassword(password: String, email: String): LiveData<Student>
}
