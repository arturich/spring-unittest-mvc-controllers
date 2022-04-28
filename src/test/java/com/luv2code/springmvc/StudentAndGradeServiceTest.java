package com.luv2code.springmvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;

@SpringBootTest
@TestPropertySource("/application.properties")
class StudentAndGradeServiceTest {
	
	@Autowired
	private StudentAndGradeService studentService;
	
	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	MathGradesDao mathGradesDao;
	
	@Autowired
	ScienceGradesDao scienceGradesDao;
	
	@Autowired
	HistoryGradesDao historyGradesDao;
	
	
	@BeforeEach
	public void setupDatabase()
	{
		jdbcTemplate.execute("INSERT INTO student(id,firstname,lastname,email_address)"
				+ " values(1,'Eric','Roby','eric_roby@luv2code_school.com')");
	}

	@Test
	//@Disabled("Dont use it temporally")
	void createStudentService() {
		
		studentService.createStudent("Chad","Darby","chad.darby@luv2code_school.com");
		
		CollegeStudent student = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");
		
		assertEquals("chad.darby@luv2code_school.com",student.getEmailAddress(),"find by email");
		
	}
	
	
	@Test
	@DisplayName("Check if student is null")
	public void isStudentNullCheck()
	{
		assertTrue(studentService.checkIfStudentIsNull(1));
		assertFalse(studentService.checkIfStudentIsNull(0));
		
	}
	
	@Test
	@DisplayName("Delete the student by ID using service")
	public void deleteStudentById()
	{
		int id = 1;
		studentService.deleteStudent(id);
		
		assertFalse(studentService.checkIfStudentIsNull(id));
	}
	
	@Test
	@DisplayName("Delete the student by object using service")
	public void deleteStudentByObject()
	{
		int id = 1;
		Optional<CollegeStudent> student = studentDao.findById(id);
		if(student.isPresent())
		{
			studentService.deleteStudent(student.get());
		}
		
		assertFalse(studentService.checkIfStudentIsNull(id));
	}
	
	@Sql("/insertData.sql")
	@Test
	public void getGradebookService()
	{
		Iterable<CollegeStudent> iterableCollageStudents = studentService.getGradebook();
		
		List<CollegeStudent> collegeStudents = new ArrayList<>();
		
		for(CollegeStudent collegeStudent : iterableCollageStudents)
		{
			collegeStudents.add(collegeStudent);
		}
		
		assertEquals(5,collegeStudents.size());
		
	}
	
	@Test
	@DisplayName("Create Grade Service, insert grades and verify")
	public void createGradeService()
	{
		//Create the grade
		assertTrue(studentService.createGrade(80.5,1,"math"));
		assertTrue(studentService.createGrade(80.5,1,"science"));
		assertTrue(studentService.createGrade(80.5,1,"history"));
		
		
		//Get all grades with  studentID
		Iterable<MathGrade> mathGrades = mathGradesDao.findGradesByStudentId(1);
		Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradesByStudentId(1);
		Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradesByStudentId(1);
				
		//Verify there is grades
		assertTrue(mathGrades.iterator().hasNext(), "Student has math grades");
		assertTrue(scienceGrades.iterator().hasNext(), "Student has Science grades");
		assertTrue(historyGrades.iterator().hasNext(), "Student has History grades");
		
	}
	
	@Test
	@DisplayName("Error if Invalid Grade")
	public void createGradeServiceReturnFalse()
	{
		assertFalse(studentService.createGrade(105, 1, "math"));
		assertFalse(studentService.createGrade(-4, 1, "math"));
		assertFalse(studentService.createGrade(80.5, 2, "math"));
		assertFalse(studentService.createGrade(80.5, 1, "literature"));
	}
	
	
	
	@AfterEach
	public void setupAfterTransaction()
	{
		jdbcTemplate.execute("DELETE FROM student");
	}
	

}
