package com.techtez.employee_management.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.techtez.employee_management.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>{
	
	    Optional<Employee> findByEmailIgnoreCase(String email);
	    
	    List<Employee> findByDepartment(String department);
	    
	    List<Employee> findByDateOfJoining(LocalDate dateOfJoining);
	    
	    List<Employee> findByDateOfJoiningBetweenOrderByDateOfJoiningAsc(LocalDate startDate, LocalDate endDate);
	    
	    List<Employee> findByDateOfJoiningBetweenOrderByDateOfJoiningDesc(LocalDate startDate, LocalDate endDate);

}
