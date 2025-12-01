package com.techtez.employee_management.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.techtez.employee_management.entity.Employee;
import com.techtez.employee_management.service.EmployeeServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController {
	
	private static final Logger logger = LogManager.getLogger(EmployeeController.class);
	
	@Autowired
	private EmployeeServiceImpl service;
	
	// helper to build standard message responses
    private static Map<String, Object> msgBody(String message, int status) {
        Map<String, Object> m = new HashMap<>();
        m.put("message", message);
        m.put("status", status);
        return m;
    }
        
	@GetMapping
	public ResponseEntity<?> getAllEmployees()
	{
		try {
            List<Employee> list = service.getAllEmployees();
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            logger.error("Error fetching all employees", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(msgBody("Unable to fetch employees", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
	}
	
    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable Long id) {
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(msgBody("Invalid id", HttpStatus.BAD_REQUEST.value()));
        }
        try {
            Optional<Employee> optional = service.getEmployeeById(id);
            if (optional.isPresent()) {
                return ResponseEntity.ok(optional.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(msgBody("Employee not found", HttpStatus.NOT_FOUND.value()));
            }
        } catch (Exception ex) {
            logger.error("Error fetching employee by id {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(msgBody("Unable to fetch employee", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
	
    @PostMapping
    public ResponseEntity<?> createEmployee(@RequestBody Employee employee) {
    	
        // optional: fail fast if body missing
        if (employee == null) {
            return ResponseEntity.badRequest().body(msgBody("Request body required", 400));
        }

        try {
            Employee created = service.createEmployee(employee); // service does validations
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException iae) {
            // service uses messages like "Duplicate_Email", "Email_required", etc.
            if ("Duplicate_Email".equalsIgnoreCase(iae.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msgBody("Duplicate email", 409));
            }
            return ResponseEntity.badRequest().body(msgBody(iae.getMessage(), 400));
        } catch (Exception ex) {
            logger.error("Unexpected error creating employee", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(msgBody("Unable to create employee", 500));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
    	
        if (id == null || id <= 0) {
            return ResponseEntity.badRequest().body(msgBody("Invalid id", 400));
        }
        if (employee == null) {
            return ResponseEntity.badRequest().body(msgBody("Request body required", 400));
        }

        try {
            Employee updated = service.updateEmployee(id, employee); // service handles checks & duplicate logic
            return ResponseEntity.ok(updated);
        } catch (NoSuchElementException nse) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msgBody("Employee not found", 404));
        } catch (IllegalArgumentException iae) {
            if ("Duplicate_Email".equalsIgnoreCase(iae.getMessage())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(msgBody("Duplicate email", 409));
            }
            return ResponseEntity.badRequest().body(msgBody(iae.getMessage(), 400));
        } catch (Exception ex) {
            logger.error("Unexpected error updating employee id {}", id, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(msgBody("Unable to update employee", 500));
        }
    }

	
	@DeleteMapping("/{id}")
	public ResponseEntity<?> deleteEmployee(@PathVariable Long id)
	{
		logger.info("DELETE /api/employees/{}", id);
		
		 if (id == null || id <= 0) {
	            return ResponseEntity.badRequest().body(msgBody("Invalid id", HttpStatus.BAD_REQUEST.value()));
	        }
		 
		try {
			Boolean deleted = service.deleteEmployee(id);
			
			if(deleted)
			{
				return ResponseEntity.ok(msgBody("Employee Deleted Successfully", HttpStatus.OK.value()));
			}
			else
			{
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(msgBody("Employee not found", HttpStatus.NOT_FOUND.value()));
			}
		}
		catch(Exception e)
		{
			logger.error("error deleting employee {}", id, e);
			
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msgBody("Unable to delete employee", HttpStatus.INTERNAL_SERVER_ERROR.value()));
		}
	}
	
	@GetMapping("/department/{department}")
    public ResponseEntity<?> getEmployeesByDepartment(@PathVariable String department) {
        if (department == null || department.isBlank()) {
            return ResponseEntity.badRequest().body(msgBody("Department param required", HttpStatus.BAD_REQUEST.value()));
        }
        try {
            List<Employee> list = service.getEmployeesByDepartment(department);
            return ResponseEntity.ok(list);
        } catch (Exception ex) {
            logger.error("Error fetching employees by department {}", department, ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(msgBody("Unable to fetch employees", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }
	
	 @GetMapping("/joining-date")
	    public ResponseEntity<?> getEmployeesByDate(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
	        if (date == null) {
	            return ResponseEntity.badRequest().body(msgBody("date param required (yyyy-MM-dd)", HttpStatus.BAD_REQUEST.value()));
	        }
	        try {
	            List<Employee> list = service.getEmployeesByJoiningDate(date);
	            return ResponseEntity.ok(list);
	        } catch (Exception ex) {
	            logger.error("Error fetching by joining date {}", date, ex);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(msgBody("Unable to fetch employees", HttpStatus.INTERNAL_SERVER_ERROR.value()));
	        }
	    }
	 
	 @GetMapping("/joining-date-range")
	    public ResponseEntity<?> getEmployeesByJoiningDateRange(
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end,
	            @RequestParam(defaultValue = "asc") String order) {
	        if (start == null || end == null) {
	            return ResponseEntity.badRequest().body(msgBody("start and end required (yyyy-MM-dd)", HttpStatus.BAD_REQUEST.value()));
	        }
	        if (end.isBefore(start)) {
	            return ResponseEntity.badRequest().body(msgBody("end must be >= start", HttpStatus.BAD_REQUEST.value()));
	        }
	        try {
	            List<Employee> list;
	            if ("desc".equalsIgnoreCase(order)) {
	                list = service.getEmployeesByJoiningDateRangeDesc(start, end);
	            } else {
	                list = service.getEmployeesByJoiningDateRangeAsc(start, end);
	            }
	            return ResponseEntity.ok(list);
	        } catch (Exception ex) {
	            logger.error("Error fetching joining range {} - {}", start, end, ex);
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                    .body(msgBody("Unable to fetch employees", HttpStatus.INTERNAL_SERVER_ERROR.value()));
	        }
	    }
}
