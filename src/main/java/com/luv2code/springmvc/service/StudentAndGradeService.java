package com.luv2code.springmvc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.Grade;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.models.StudentGrades;
import com.luv2code.springmvc.repository.HistoryGradesDao;
import com.luv2code.springmvc.repository.MathGradesDao;
import com.luv2code.springmvc.repository.ScienceGradesDao;
import com.luv2code.springmvc.repository.StudentDao;

@Service
@Transactional
public class StudentAndGradeService {
	
	@Autowired
	StudentDao studentDao;
	
	@Autowired
	@Qualifier("mathGrades")
	private MathGrade mathGrade;
	
	@Autowired
	@Qualifier("scienceGrades")
	private ScienceGrade scienceGrade;
	
	@Autowired 
	@Qualifier("historyGrades")
	private HistoryGrade historyGrade;
	
	@Autowired
	private MathGradesDao mathGradesDao;
	
	@Autowired
	private ScienceGradesDao scienceGradesDao;
	
	@Autowired
	private HistoryGradesDao historyGradesDao;
	
	@Autowired
	StudentGrades studentGrades;
	
	
	public void createStudent(String firstName, String lastName, String emailAddress)
	{
		CollegeStudent student = new CollegeStudent(firstName, lastName, emailAddress);
		student.setId(0);
		
		studentDao.save(student);
	}
	
	public boolean checkIfStudentIsNull(int id)
	{
		Optional<CollegeStudent> student = studentDao.findById(id);
		
		if(student.isPresent())
			return true;
		
		return false;
	}

	public void deleteStudent(int id) {
		if(checkIfStudentIsNull(id))
		{
			studentDao.deleteById(id);
			mathGradesDao.deleteByStudentId(id);
			scienceGradesDao.deleteByStudentId(id);
			historyGradesDao.deleteByStudentId(id);
			
		}
		
	}

	public void deleteStudent(CollegeStudent student) {
		int id = student.getId();
		
		if(checkIfStudentIsNull(id))
		{
			studentDao.delete(student);
			mathGradesDao.deleteByStudentId(id);
			scienceGradesDao.deleteByStudentId(id);
			historyGradesDao.deleteByStudentId(id);
			
		}	
		
	}

	public Iterable<CollegeStudent> getGradebook() {
		
		Iterable<CollegeStudent> collegeStudents = studentDao.findAll();
		
		return collegeStudents;
	}
	
	public void createStudent(CollegeStudent student)
	{		
		student.setId(0);
		
		studentDao.save(student);
	}

	public boolean createGrade(double grade, int studentId, String gradeType) {
		
		if(!checkIfStudentIsNull(studentId))
		{
			return false;
		}
		
		if( grade >= 0 && grade <= 100)
		{
			if(gradeType.equals("math"))
			{
				mathGrade.setId(0);
				mathGrade.setGrade(grade);
				mathGrade.setStudentId(studentId);
				mathGradesDao.save(mathGrade);
				return true;
			}
			
			if(gradeType.equals("science"))
			{
				scienceGrade.setId(0);
				scienceGrade.setGrade(grade);
				scienceGrade.setStudentId(studentId);
				scienceGradesDao.save(scienceGrade);
				return true;
			}
			
			if(gradeType.equals("history"))
			{
				historyGrade.setId(0);
				historyGrade.setGrade(grade);
				historyGrade.setStudentId(studentId);
				historyGradesDao.save(historyGrade);
				return true;
			}
		}
		
		return false;
	}

	public Integer deleteGrade(int gradeId, String gradeType) {
		int studentId = 0;
		
		if(gradeType.equals("math"))
		{
			Optional<MathGrade> mathGrade = mathGradesDao.findById(gradeId);
			
			if(mathGrade.isPresent())
			{
				studentId = mathGrade.get().getStudentId();
				mathGradesDao.deleteById(gradeId);				
			}
			
		}
		
		if(gradeType.equals("science"))
		{
			Optional<ScienceGrade> scienceGrade = scienceGradesDao.findById(gradeId);
			
			if(scienceGrade.isPresent())
			{
				studentId = scienceGrade.get().getStudentId();
				scienceGradesDao.deleteById(gradeId);				
			}
			
		}
		
		if(gradeType.equals("history"))
		{
			Optional<HistoryGrade> historyGrade = historyGradesDao.findById(gradeId);
			
			if(historyGrade.isPresent())
			{
				studentId = historyGrade.get().getStudentId();
				historyGradesDao.deleteById(gradeId);				
			}
			
		}
		
		return studentId;
	}

	public GradebookCollegeStudent studentInformation(int id) {
		
		Optional<CollegeStudent> student = studentDao.findById(id);
		
		if(student.isPresent())
		{
			Iterable<MathGrade> mathGrades = mathGradesDao.findGradesByStudentId(id);
			Iterable<HistoryGrade> historyGrades = historyGradesDao.findGradesByStudentId(id);
			Iterable<ScienceGrade> scienceGrades = scienceGradesDao.findGradesByStudentId(id);
			
			List<Grade> mathGradeList = new ArrayList<>();
			mathGrades.forEach(mathGradeList::add);
			
			List<Grade> historyGradeList = new ArrayList<>();
			historyGrades.forEach(historyGradeList::add);
			
			List<Grade> scienceGradeList = new ArrayList<>();
			scienceGrades.forEach(scienceGradeList::add);
			
			studentGrades.setHistoryGradeResults(historyGradeList);
			studentGrades.setMathGradeResults(mathGradeList);
			studentGrades.setScienceGradeResults(scienceGradeList);
			
			GradebookCollegeStudent gradebookCollegeStudent = 
					new GradebookCollegeStudent(
							student.get().getId(),
							student.get().getFirstname(),
							student.get().getLastname(),
							student.get().getEmailAddress(),
							studentGrades);
			return gradebookCollegeStudent;
		}
		
		
		return null;
	}
	
	public void configureStudentInformationModel(int id, Model m)
	{
		GradebookCollegeStudent studentEntity = studentInformation(id);
		
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
	}

}
