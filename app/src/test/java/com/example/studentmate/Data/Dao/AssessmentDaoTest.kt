package com.example.studentmate.Data.Dao

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.studentmate.Data.AppDatabase
import com.example.studentmate.Data.Models.Assessment
import com.example.studentmate.Data.Models.Student
import com.example.studentmate.Data.Models.Subject
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.IOException
import java.util.Date

@RunWith(RobolectricTestRunner::class)
@Config(manifest = "--none")
class AssessmentDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var assessmentDao: AssessmentDao
    private lateinit var studentDao: StudentDao
    private lateinit var subjectDao: SubjectDao

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Use an in-memory database
        database = Room.inMemoryDatabaseBuilder(
            context,
            AppDatabase::class.java
        ).allowMainThreadQueries().build()

        assessmentDao = database.assessmentDao()
        studentDao = database.studentDao()
        subjectDao = database.subjectDao()
    }

    @After
    @Throws(IOException::class)
    fun down() {
        database.close()
    }

    // Helper
    private suspend fun createParentData(): Pair<Int, Int> {
        val student = Student(name = "Test Student", email = "test@test.com", password = "password")
        val subject = Subject(name = "Test Subject")

        val studentId = studentDao.insert(student).toInt()
        val subjectId = subjectDao.insert(subject).toInt()

        return Pair(studentId, subjectId)
    }

    private fun createAssessment(id: Int = 0, studentId: Int, subjectId: Int): Assessment {
        return Assessment(
            id = id,
            studentId = studentId,
            subjectId = subjectId,
            title = "Test Assessment",
            description = "Unit test description",
            deadline = Date(),
            score = 100,
            isExam = false,
            isDone = false,
            actualScore = 0
        )
    }

    // Tests for AssessmentDao

    @Test
    fun insertAssessment_success() = runBlocking {
        // Arrange
        val (stuId, subId) = createParentData()
        val assessment = createAssessment(id = 0, studentId = stuId, subjectId = subId)

        // Act
        val rowId = assessmentDao.insertAssessment(assessment)

        // Assert
        assertTrue(rowId > 0)
        val retrieved = assessmentDao.getAssessmentById(rowId.toInt())
        assertNotNull(retrieved)
        assertEquals(stuId, retrieved?.studentId)
    }

    @Test
    fun getAllAssessments_emptyTable() = runBlocking {
        // Act
        val list = assessmentDao.getAllAssessments()

        // Assert
        assertTrue( list.isEmpty())
    }


    @Test
    fun getAllAssessments_multiple_success() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val a1 = createAssessment(id = 1, studentId = stuId, subjectId = subId)
        val a2 = createAssessment(id = 2, studentId = stuId, subjectId = subId)
        assessmentDao.insertAssessment(a1)
        assessmentDao.insertAssessment(a2)

        // Act
        val list = assessmentDao.getAllAssessments()

        // Assert
        assertEquals(2, list.size)
    }

    @Test
    fun getAssessmentById_found() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val assessment = createAssessment(id = 55, studentId = stuId, subjectId = subId)
        assessmentDao.insertAssessment(assessment)

        // Act
        val result = assessmentDao.getAssessmentById(55)

        // Assert
        assertNotNull(result)
        assertEquals(55, result?.id)
        assertEquals("Test Assessment", result?.title)
    }

    @Test
    fun getAssessmentById_notFound() = runBlocking {
        // Act
        val result = assessmentDao.getAssessmentById(999)

        // Assert
        assertNull(result)
    }

    @Test
    fun getAssessmentsForStudent_found() = runBlocking {
        // Create 2 different students
        val (student1, subject1) = createParentData()
        val (student2, subject2) = createParentData() // Creates new unique IDs

        val a1 = createAssessment(id = 1, studentId = student1, subjectId = subject1)
        val a2 = createAssessment(id = 2, studentId = student1, subjectId = subject1)
        val a3 = createAssessment(id = 3, studentId = student2, subjectId = subject2) // Other student

        assessmentDao.insertAssessment(a1)
        assessmentDao.insertAssessment(a2)
        assessmentDao.insertAssessment(a3)

        // Act
        val result = assessmentDao.getAssessmentsForStudent(student1)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.studentId == student1 })
    }

    @Test
    fun getAssessmentsForStudent_notFound() = runBlocking {
        val (student1, subject1) = createParentData()
        val (student2, _) = createParentData()

        // Insert assessment for Student 1
        val a1 = createAssessment(id = 1, studentId = student1, subjectId = subject1)
        assessmentDao.insertAssessment(a1)

        // Student doesn't have any assessments
        val result = assessmentDao.getAssessmentsForStudent(student2)

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun getAssessmentsForSubject_found() = runBlocking {
        val (student1, subject1) = createParentData()
        val (_, subject2) = createParentData()

        val a1 = createAssessment(id = 1, studentId = student1, subjectId = subject1)
        val a2 = createAssessment(id = 2, studentId = student1, subjectId = subject1)
        val a3 = createAssessment(id = 3, studentId = student1, subjectId = subject2)

        assessmentDao.insertAssessment(a1)
        assessmentDao.insertAssessment(a2)
        assessmentDao.insertAssessment(a3)

        // Act
        val result = assessmentDao.getAssessmentsForSubject(subject1)

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.all { it.subjectId == subject1 })
    }

    @Test
    fun getAssessmentsForSubject_notFound() = runBlocking {
        val (_, subject1) = createParentData()

        // Act
        val result = assessmentDao.getAssessmentsForSubject(subject1)

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun GetAssessmentByStudentIdAndSubjectId_found() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val targetAssessment = createAssessment(id = 1, studentId = stuId, subjectId = subId)
        assessmentDao.insertAssessment(targetAssessment)
        val (_, otherSubId) = createParentData()
        assessmentDao.insertAssessment(createAssessment(id = 2, studentId = stuId, subjectId = otherSubId))

        // Act
        val result = assessmentDao.GetAssessmentByStudentIdAndSubjectId(subId, stuId)

        // Assert
        assertNotNull(result)
        assertEquals(stuId, result.studentId)
        assertEquals(subId, result.subjectId)
    }

    @Test
    fun updateAssessment_success() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val assessment = createAssessment(id = 1, studentId = stuId, subjectId = subId)
        assessmentDao.insertAssessment(assessment)

        // Act
        val updatedAssessment = assessment.copy(score = 95, isDone = true)
        assessmentDao.updateAssessment(updatedAssessment)

        // Assert
        val retrieved = assessmentDao.getAssessmentById(1)
        assertEquals(95, retrieved?.score)
        assertEquals(true, retrieved?.isDone)
    }

    @Test
    fun updateAssessment_nonExistent() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val assessment = createAssessment(id = 99, studentId = stuId, subjectId = subId)

        // Act
        assessmentDao.updateAssessment(assessment)

        // Assert
        val retrieved = assessmentDao.getAssessmentById(99)
        assertNull("Assessment should not exist", retrieved)
    }

    @Test
    fun deleteAssessment_success() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val assessment = createAssessment(id = 1, studentId = stuId, subjectId = subId)
        assessmentDao.insertAssessment(assessment)

        // Act
        assessmentDao.deleteAssessment(assessment)

        // Assert
        val retrieved = assessmentDao.getAssessmentById(1)
        assertNull(retrieved)
    }

    @Test
    fun deleteAssessment_nonExistent() = runBlocking {
        val (stuId, subId) = createParentData()

        // Arrange
        val assessment = createAssessment(id = 99, studentId = stuId, subjectId = subId)

        // Act
        assessmentDao.deleteAssessment(assessment)

        // Assert
        val retrieved = assessmentDao.getAssessmentById(99)
        assertNull(retrieved)
    }
}