package jp.co.axa.apidemo.services;

import java.util.List;
import java.util.Optional;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.repositories.EmployeeRepository;

/**
 * {@inheritDoc}
 */
@Service
public class EmployeeServiceImpl implements EmployeeService {

  private EmployeeRepository employeeRepository;

  @Autowired
  public void setEmployeeRepository(EmployeeRepository employeeRepository) {
    this.employeeRepository = employeeRepository;
  }

  @Override
  public Optional<List<Employee>> retrieveEmployees() {
    return Optional.ofNullable(employeeRepository.findAll());
  }

  @Cacheable(value = "employees", key = "#employeeId")
  @Override
  public Optional<Employee> getEmployee(Long employeeId) {
    return employeeRepository.findById(employeeId);
  }

  @Override
  public Optional<Employee> saveEmployee(Employee employee) {
    Optional<Employee> newEmployee = Optional.ofNullable(employeeRepository.save(employee));

    return newEmployee;
  }

  @CacheEvict(value = "employees", key = "#employeeId")
  @Override
  public void deleteEmployee(Long employeeId) {
    employeeRepository.deleteById(employeeId);
  }

  @CacheEvict(value = "employees", key = "#employeeId")
  @Override
  public Optional<Employee> updateEmployee(Employee newEmployee, @NotNull Long employeeId) {
    return Optional.ofNullable(employeeRepository.findById(employeeId)
      .map(employee -> {
        if (newEmployee.getName() != null) {
          employee.setName(newEmployee.getName());
        }
        if (newEmployee.getDepartment() != null) {
          employee.setDepartment(newEmployee.getDepartment());
        }
        
        if (newEmployee.getSalary() != null) {
          employee.setSalary(newEmployee.getSalary());
        }
        
        Employee updatedEmployee = employeeRepository.update(employee);
        return updatedEmployee;
      }).orElseGet(() -> {
        newEmployee.setId(employeeId);
        return employeeRepository.save(newEmployee);
      }));
  }
}