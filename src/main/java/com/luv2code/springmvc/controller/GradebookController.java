package com.luv2code.springmvc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Gradebook;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.service.StudentAndGradeService;

@Controller
public class GradebookController {

	@Autowired
	private Gradebook gradebook;

	@Autowired
	private StudentAndGradeService studentService;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStudents(Model m) {
		Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
		m.addAttribute("students", collegeStudents);
		return "index";
	}

	@GetMapping("/studentInformation/{id}")
	public String studentInformation(@PathVariable int id, Model m) {

		if (!studentService.checkIfStudentIsNull(id)) {
			return "error";
		}

		GradebookCollegeStudent studentEntity = studentService.studentInformation(id);

		if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
			double mathAvg = studentEntity.getStudentGrades()
					.findGradePointAverage(studentEntity.getStudentGrades().getMathGradeResults());
			m.addAttribute("mathAverage", mathAvg);
		} else {
			m.addAttribute("mathAverage", "N/A");
		}

		if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
			double scienceAvg = studentEntity.getStudentGrades()
					.findGradePointAverage(studentEntity.getStudentGrades().getScienceGradeResults());
			m.addAttribute("scienceAverage", scienceAvg);
		} else {
			m.addAttribute("scienceAverage", "N/A");
		}
		if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
			double historyAvg = studentEntity.getStudentGrades()
					.findGradePointAverage(studentEntity.getStudentGrades().getHistoryGradeResults());
			m.addAttribute("historyAverage", historyAvg);
		} else {
			m.addAttribute("historyAverage", "N/A");
		}

		m.addAttribute("student", studentEntity);

		return "studentInformation";
	}

	@PostMapping("/")
	public String createStudent(@ModelAttribute("student") CollegeStudent student, Model m) {
		studentService.createStudent(student);

		Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();
		m.addAttribute("students", collegeStudents);

		return "index";
	}

	@GetMapping("/delete/student/{id}")
	public String deleteStudent(@PathVariable int id, Model m) {

		if (!studentService.checkIfStudentIsNull(id)) {

			return "error";
		}

		studentService.deleteStudent(id);

		Iterable<CollegeStudent> collegeStudents = studentService.getGradebook();

		m.addAttribute("students", collegeStudents);

		return "index";
	}
	
	@PostMapping("/grades")
	public String createGrade(@RequestParam("grade") double grade,
							  @RequestParam("gradeType") String gradeType,
							  @RequestParam("studentId") int studentId,
							  Model m)
	{			
		
		
		if(studentService.createGrade(grade, studentId, gradeType))
		{
			GradebookCollegeStudent studentEntity = studentService.studentInformation(studentId);
			
			m.addAttribute("student",studentEntity);
			
			if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
				double mathAvg = studentEntity.getStudentGrades()
						.findGradePointAverage(studentEntity.getStudentGrades().getMathGradeResults());
				m.addAttribute("mathAverage", mathAvg);
			} else {
				m.addAttribute("mathAverage", "N/A");
			}

			if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
				double scienceAvg = studentEntity.getStudentGrades()
						.findGradePointAverage(studentEntity.getStudentGrades().getScienceGradeResults());
				m.addAttribute("scienceAverage", scienceAvg);
			} else {
				m.addAttribute("scienceAverage", "N/A");
			}
			if (studentEntity.getStudentGrades().getMathGradeResults().size() > 0) {
				double historyAvg = studentEntity.getStudentGrades()
						.findGradePointAverage(studentEntity.getStudentGrades().getHistoryGradeResults());
				m.addAttribute("historyAverage", historyAvg);
			} else {
				m.addAttribute("historyAverage", "N/A");
			}
			
			return "studentInformation";
		}
		else
			return "error";
	}

}
