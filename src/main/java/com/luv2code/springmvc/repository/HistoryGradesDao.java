package com.luv2code.springmvc.repository;

import org.springframework.data.repository.CrudRepository;

import com.luv2code.springmvc.models.HistoryGrade;

public interface HistoryGradesDao extends CrudRepository<HistoryGrade, Integer>{
	
	public Iterable<HistoryGrade> findGradesByStudentId(int id);
	
	public void deleteByStudentId(int id);

}
