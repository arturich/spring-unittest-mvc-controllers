package com.luv2code.springmvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;

@SpringBootTest
@TestPropertySource("/application-test.properties")
@AutoConfigureMockMvc
public class GradeBookControllerTest {

	private static MockHttpServletRequest request;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private MockMvc mockMvc;

	@Mock
	private StudentAndGradeService studentAndGradeServiceMock;

	@Autowired
	private StudentDao studentDao;
	
	@Autowired
	private StudentAndGradeService studentAndGradeService;
	
	@Autowired
	private MathGradesDao mathGradesDao;

	@Value("${sql.scripts.create.student}")
	String sqlCreateStudent;

	@Value("${sql.scripts.create.math.grade}")
	String sqlMathGrade;

	@Value("${sql.scripts.create.science.grade}")
	String sqlScienceGrade;

	@Value("${sql.scripts.create.history.grade}")
	String sqlHistoryGrade;

	@Value("${sql.script.delete.student}")
	String sqlDeleteStudent;

	@Value("${sql.script.delete.math.grade}")
	String sqlDeleteMath;

	@Value("${sql.script.delete.science.grade}")
	String sqlDeleteScience;

	@Value("${sql.script.delete.history.grade}")
	String sqlDeleteHistory;

	@BeforeAll
	public static void setup() {
		request = new MockHttpServletRequest();
		request.setParameter("firstname", "Chad");
		request.setParameter("lastname", "Darby");
		request.setParameter("emailAddress", "chad.darby@luv2code_school.com");
	}

	@BeforeEach
	public void setupDatabase() {
		jdbcTemplate.execute(sqlCreateStudent);
		jdbcTemplate.execute(sqlMathGrade);
		jdbcTemplate.execute(sqlScienceGrade);
		jdbcTemplate.execute(sqlHistoryGrade);
	}

	@Test
	@DisplayName("Student Http Request")
	public void getSTudentHtttpRequest() throws Exception {
		CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Petters", "eric@citalin.com");

		CollegeStudent studentTwo = new GradebookCollegeStudent("Arthur", "Kings", "artur.kings@citalin.com");

		List<CollegeStudent> collegeStudentsList = new ArrayList<>(Arrays.asList(studentOne, studentTwo));

		when(studentAndGradeServiceMock.getGradebook()).thenReturn(collegeStudentsList);

		assertIterableEquals(collegeStudentsList, studentAndGradeServiceMock.getGradebook());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");
	}

	@Test
	@DisplayName("Create Student via http request")
	public void createStudentHttpRequest() throws Exception {
		CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Petters", "eric@citalin.com");

		List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));

		when(studentAndGradeServiceMock.getGradebook()).thenReturn(collegeStudentList);

		assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradebook());

		MvcResult mvcResult = (MvcResult) this.mockMvc
				.perform(MockMvcRequestBuilders.post("/").contentType(MediaType.APPLICATION_JSON)
						.param("firstname", request.getParameterValues("firstname"))
						.param("lastname", request.getParameterValues("lastname"))
						.param("emailAddress", request.getParameterValues("emailAddress")))
				.andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");

		CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");

		assertNotNull(verifyStudent, "Student should be found");

		assertEquals("chad.darby@luv2code_school.com", verifyStudent.getEmailAddress(), "Exact email is expected");
	}

	@Test
	@DisplayName("Delete an student")
	public void deleteStudentHttpRequest() throws Exception {
		int id = 1;
		// Get student first
		Optional<CollegeStudent> verifyStudent = studentDao.findById(id);
		assertTrue(verifyStudent.isPresent(), "Student should exist before removing it");

		// Delete from controller
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", id))
				.andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "index");

		verifyStudent = studentDao.findById(id);
		assertFalse(verifyStudent.isPresent(), "Student should NOT exist before removing it");

	}

	@Test
	@DisplayName("Error page for student that does not exist")
	public void errorPageOnDelete() throws Exception {
		int id = 2;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}", id))
				.andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "error");

	}

	@Test
	@DisplayName("Get student information http resquest")
	public void getStudentInformationHttpRequest() throws Exception {
		int id = 1;

		assertTrue(studentDao.findById(id).isPresent(), "Student must exist");

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", id))
				.andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "studentInformation");

	}

	@Test
	@DisplayName("Error page at wrong Student Information id")
	public void errorPageAtStudenInformation() throws Exception {
		int id = 0;

		assertFalse(studentDao.findById(id).isPresent());

		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/studentInformation/{id}", id))
				.andExpect(status().isOk()).andReturn();

		ModelAndView mav = mvcResult.getModelAndView();

		ModelAndViewAssert.assertViewName(mav, "error");
	}

	@Test
	@DisplayName("Add/Create grades")
	public void addGrades() throws Exception
	{
		
		assertTrue(studentDao.findById(1).isPresent());		
				
		GradebookCollegeStudent student = studentAndGradeService.studentInformation(1);
		
		assertEquals(1,student.getStudentGrades().getMathGradeResults().size());
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/grades")
				.contentType(MediaType.APPLICATION_JSON)
				.param("gradeType", "math")
				.param("studentId", "1")
				.param("grade", "100")
				).andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "studentInformation");
		
		student = studentAndGradeService.studentInformation(1);
		assertEquals(2,student.getStudentGrades().getMathGradeResults().size());
		
		
		
	}
	
	@Test
	@DisplayName("Try to create grade if student does not exist")
	public void createGradeWhenStudentDoesNotExist() throws Exception
	{
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/grades")
				.contentType(MediaType.APPLICATION_JSON)
				.param("gradeType","math")
				.param("studentId", "0")
				.param("grade", "98")
				).andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "error");
		
	}
	
	@Test
	@DisplayName("Create a grade when subject is invalid")
	public void createAGradeWhenSubjectIsInvalid() throws Exception
	{
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/grades")
				.contentType(MediaType.APPLICATION_JSON)
				.param("gradeType","literature")
				.param("studentId", "1")
				.param("grade", "98")				
				).andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav,"error");
	}
	
	@Test
	@DisplayName("Delete grades")
	public void deleteGradesHttpRequest() throws Exception
	{
		//assert that the grade exist
		Optional<MathGrade> mathGrade = mathGradesDao.findById(1);
		assertTrue(mathGrade.isPresent(),"Subject must exist");
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get("/grades/{id}/{gradeType}",1,"math"))
				.andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "studentInformation");
		
		//Confirm grade is not present anymore
		mathGrade = mathGradesDao.findById(1);
		
		assertFalse(mathGrade.isPresent(),"Math grade shouln't be present");
				
	}
	
	@Test
	@DisplayName("Delete a Grade when id is invalid")
	public void deleteGradeWhenIdIsInvalid() throws Exception
	{
		Optional<MathGrade> mathGrade = mathGradesDao.findById(2);
		
		assertFalse(mathGrade.isPresent(),"Grade should not exist");
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/grades/{id}/{gradeType}",2,"math"))
				.andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "error");
	}
	
	@Test
	@DisplayName("Delete a grade when subject is invalid")
	public void deleteGradeWhenSubjectIdIsInvalid() throws Exception
	{
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
				.get("/grades/{id}/{gradeType}",1,"literature"))
				.andExpect(status().isOk())
				.andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "error");
	}
	

	@AfterEach
	public void setupAfterTransaction() {
		jdbcTemplate.execute(sqlDeleteStudent);
		jdbcTemplate.execute(sqlDeleteMath);
		jdbcTemplate.execute(sqlDeleteScience);
		jdbcTemplate.execute(sqlDeleteHistory);
	}

}
