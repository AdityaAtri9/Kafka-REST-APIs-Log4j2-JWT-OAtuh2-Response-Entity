package com.techtez.employee_management.service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.techtez.employee_management.entity.Employee;
import com.techtez.employee_management.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

	private static final Logger logger = LogManager.getLogger(EmployeeServiceImpl.class);

	@Autowired
	private EmployeeRepository repository;

	@Override
	public List<Employee> getAllEmployees() {
		logger.debug("getAllEmployees()");
		return repository.findAll();
	}

	@Override
	public Optional<Employee> getEmployeeById(Long id) {
		logger.debug("getEmployeeById({})", id);
		return Optional.of(repository.findById(id).orElse(null));
	}

	@Override
	public Employee createEmployee(Employee employee) {
		logger.debug("creatingEmployee = {}", employee != null ? employee.getEmail() : null);

		// Validation checks
		if (employee == null) {
			throw new IllegalArgumentException("Employee_null");
		}

		if (employee.getEmail() == null || employee.getEmail().isEmpty())
		{
			throw new IllegalArgumentException("Email_required");
		}
		
		if(employee.getName() == null || employee.getName().isEmpty())
		{
			throw new IllegalArgumentException("Name_required");
		}
		
		if(employee.getDepartment() == null || employee.getDepartment().isEmpty())
		{
			throw new IllegalArgumentException("Department_required");
		}

		// Normalizing email
		String email = employee.getEmail().toLowerCase().trim();
		
		// Duplicate check
		Optional<Employee> duplicateEmail = repository.findByEmailIgnoreCase(email);
		if (duplicateEmail.isPresent()) {
			throw new IllegalArgumentException("Duplicate_Email : Employee already exists.");
		}

		Employee newEmployee = new Employee();
		newEmployee.setName(employee.getName());
		newEmployee.setEmail(email);
		newEmployee.setDepartment(employee.getDepartment());
		newEmployee.setDateOfJoining(employee.getDateOfJoining());
		
		Employee saved = repository.save(newEmployee);
		logger.info("Employee saved successfully : {}", saved);
		return saved;
	}

	@Override
	public Employee updateEmployee(Long id, Employee employee) {
		logger.debug("Updating Employee = {} with id = {}", employee != null ? employee.getEmail() : null,id);
		if(employee == null)
		{
			throw new IllegalArgumentException("Employee_null");
		}
		
		// Fetching Existing Employee
		Employee exist = repository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Employee_not_found"));
		
//		// Duplicate email check
//		if(exist.getEmail().equalsIgnoreCase(employee.getEmail()))
//		{
//			logger.error("Duplicate_email {}", employee);
//			throw new IllegalArgumentException("Duplicate_email");
//		}
		
		exist.setName(employee.getName());
		exist.setEmail(employee.getEmail());
		exist.setDepartment(employee.getDepartment());
		exist.setDateOfJoining(employee.getDateOfJoining());
		
		logger.info("Employee updated successfully= {}", exist);
		return repository.save(exist);
	}

	@Override
	public boolean deleteEmployee(Long id) {
		logger.debug("Deleting employee = {}", id);
		
		// Check if the employee with the ID is present in the database
		if(!repository.existsById(id))
		{
			logger.warn("Deletion failed, not found id = {}", id);
			return false;
		}
		
		repository.deleteById(id);
		logger.info("Employee deleted successfully id = {}", id);
		return true;
	}

	@Override
	public List<Employee> getEmployeesByDepartment(String department) {
		logger.debug("getEmployeesByDepartment {}", department);
		return repository.findByDepartment(department);
	}

	@Override
	public List<Employee> getEmployeesByJoiningDate(LocalDate date) {
		logger.debug("getEmployeeByJoiningDate {}", date);
		return repository.findByDateOfJoining(date);
	}

	@Override
	public List<Employee> getEmployeesByJoiningDateRangeAsc(LocalDate start, LocalDate end) {
		logger.debug("getEmployeesByJoiningDateRandeAsc {} - {}", start, end);
		return repository.findByDateOfJoiningBetweenOrderByDateOfJoiningAsc(start, end);
	}

	@Override
	public List<Employee> getEmployeesByJoiningDateRangeDesc(LocalDate start, LocalDate end) {
		logger.debug("getEmployeesByJoiningDateDesc {} - {}", start, end);
		return repository.findByDateOfJoiningBetweenOrderByDateOfJoiningDesc(start, end);
	}

}
