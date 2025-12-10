package com.example.studentmate.Data.Dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.studentmate.Data.Models.Assessment

@Dao
interface AssessmentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: Assessment): Long

    @Query("SELECT * FROM assessments")
    suspend fun getAllAssessments(): List<Assessment>

    @Query("SELECT * FROM assessments WHERE id = :assessmentId")
    suspend fun getAssessmentById(assessmentId: Int): Assessment?

    @Query("SELECT * FROM assessments WHERE studentId = :studentId")
    suspend fun getAssessmentsForStudent(studentId: Int): List<Assessment>

    @Query("SELECT * FROM assessments WHERE subjectId = :subjectId")
    suspend fun getAssessmentsForSubject(subjectId: Int): List<Assessment>

    @Query("SELECT * FROM assessments WHERE subjectId=:subjectId AND studentId=:studentId")
    suspend fun GetAssessmentByStudentIdAndSubjectId(subjectId: Int, studentId: Int): Assessment

    @Update
    suspend fun updateAssessment(assessment: Assessment)

    @Delete
    suspend fun deleteAssessment(assessment: Assessment)
}
