package com.luv2code.springmvc;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.servlet.ModelAndView;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.service.StudentAndGradeService;


@SpringBootTest
@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
public class GradeBookControllerTest {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private MockMvc mockMvc;
	
	@Mock
	private StudentAndGradeService studentAndGradeServiceMock;
	
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
	
	@AfterEach
	public void setupAfterTransaction()
	{
		jdbcTemplate.execute("DELETE FROM student");
	}
	
	
	

}
