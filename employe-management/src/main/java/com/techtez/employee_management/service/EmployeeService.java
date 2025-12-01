package com.techtez.employee_management.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.techtez.employee_management.entity.Employee;

public interface EmployeeService {

	List<Employee> getAllEmployees();

	Optional<Employee> getEmployeeById(Long id);

	Employee createEmployee(Employee employee);

	Employee updateEmployee(Long id, Employee employee);

	boolean deleteEmployee(Long id);

	List<Employee> getEmployeesByDepartment(String department);

	List<Employee> getEmployeesByJoiningDate(LocalDate date);

	List<Employee> getEmployeesByJoiningDateRangeAsc(LocalDate start, LocalDate end);

	List<Employee> getEmployeesByJoiningDateRangeDesc(LocalDate start, LocalDate end);
}
