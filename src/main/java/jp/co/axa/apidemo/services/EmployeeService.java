package jp.co.axa.apidemo.services;

import jp.co.axa.apidemo.entities.Employee;

import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

/**
 * The employee service provides an interface with the database layer in order to manage the Employee entity.
 * 
 * @author Josimar Lopes
 */
public interface EmployeeService {
  /**
   * Gets a list of employees.
   * @return The employee list.
   */
  public Optional<List<Employee>> retrieveEmployees();
  /**
   * Gets an employee by its ID.
   * @param employeeId The ID of the employee.
   * @return The retrieved employee.
   */
  public Optional<Employee> getEmployee(Long employeeId);
  /**
   * Stores a new employee.
   * @param employee The employee to store.
   */
  public Optional<Employee> saveEmployee(Employee employee);
  /**
   * The employee to delete from the database by its ID.
   * @param employeeId The ID of the employee.
   */
  public void deleteEmployee(Long employeeId);
  /**
   * Updates an employee if it exists, otherwise stores new employee.
   * @param employee The employee to update.
   * @param employeeId The employee's ID.'
   * @return The updated employee.
   */
  public Optional<Employee> updateEmployee(Employee employee, Long employeeId);
}