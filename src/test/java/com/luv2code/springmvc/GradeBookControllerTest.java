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
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;


@SpringBootTest
@TestPropertySource("/application.properties")
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
	
	
	@BeforeAll
	public static void setup()
	{
		request = new MockHttpServletRequest();
		request.setParameter("firstname", "Chad");
		request.setParameter("lastname", "Darby");
		request.setParameter("emailAddress", "chad.darby@luv2code_school.com");
	}
	
	
	@BeforeEach
	public void setupDatabase()
	{
		jdbcTemplate.execute("INSERT INTO student(id,firstname,lastname,email_address)"
				+ " values(1,'Eric','Roby','eric_roby@luv2code_school.com')");
	}
	
	@Test
	@DisplayName("Student Http Request")
	public void getSTudentHtttpRequest() throws Exception
	{
		CollegeStudent studentOne = new GradebookCollegeStudent("Eric", 
				"Petters", "eric@citalin.com" );
		
		CollegeStudent studentTwo = new GradebookCollegeStudent("Arthur", 
				"Kings", "artur.kings@citalin.com" );
		
		List<CollegeStudent> collegeStudentsList = new ArrayList<>(Arrays.asList(studentOne,studentTwo));
		
		when(studentAndGradeServiceMock.getGradebook())
			.thenReturn(collegeStudentsList);
		
		assertIterableEquals(collegeStudentsList, studentAndGradeServiceMock.getGradebook());
		
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/"))
				.andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "index");
	}
	
	
	@Test
	@DisplayName("Create Student via http request")
	public void createStudentHttpRequest() throws Exception
	{
		CollegeStudent studentOne = new GradebookCollegeStudent("Eric", 
				"Petters", "eric@citalin.com" );
		
		List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne));
		
		when(studentAndGradeServiceMock.getGradebook()).thenReturn(collegeStudentList);
		
		assertIterableEquals(collegeStudentList, studentAndGradeServiceMock.getGradebook());
		
		MvcResult mvcResult = (MvcResult) this.mockMvc.perform(MockMvcRequestBuilders.post("/")
				.contentType(MediaType.APPLICATION_JSON)
				.param("firstname", request.getParameterValues("firstname"))
				.param("lastname", request.getParameterValues("lastname"))
				.param("emailAddress", request.getParameterValues("emailAddress"))
				).andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "index");
		
		CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");
		
		assertNotNull(verifyStudent,"Student should be found");
		
		assertEquals("chad.darby@luv2code_school.com", verifyStudent.getEmailAddress(),"Exact email is expected");
	}
	
	@Test
	@DisplayName("Delete an student")
	public void deleteStudentHttpRequest() throws Exception
	{
		int id = 1;
		//Get student first 
		Optional<CollegeStudent> verifyStudent = studentDao.findById(id);
		assertTrue(verifyStudent.isPresent(),"Student should exist before removing it");
		
		//Delete from controller
		MvcResult mvcResult = this.mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",id))
				.andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "index");
		
		verifyStudent = studentDao.findById(id);
		assertFalse(verifyStudent.isPresent(),"Student should NOT exist before removing it");
		
		
	}
	
	@Test
	@DisplayName("Error page for student that does not exist")
	public void errorPageOnDelete() throws Exception
	{
		int id = 2;
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/delete/student/{id}",id))
				.andExpect(status().isOk()).andReturn();
		
		ModelAndView mav = mvcResult.getModelAndView();
		
		ModelAndViewAssert.assertViewName(mav, "error");		
		
	}
	
	
	@AfterEach
	public void setupAfterTransaction()
	{
		jdbcTemplate.execute("DELETE FROM student");
	}
	
	
	

}
