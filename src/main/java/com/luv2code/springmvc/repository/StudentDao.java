package com.luv2code.springmvc.repository;

import org.springframework.data.repository.CrudRepository;

import com.luv2code.springmvc.models.CollegeStudent;

public interface StudentDao extends CrudRepository<CollegeStudent, Integer> {

	public CollegeStudent findByEmailAddress(String emailAddress);
	

}
