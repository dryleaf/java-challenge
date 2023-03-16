package jp.co.axa.apidemo.repositories;

/**
 * Customizes employee entity repository
 * 
 * @author Josimar Lopes
 * 
 * @param <T> Employee entity
 * @param <ID> Employee ID
 */
public interface EmployeeRepositoryCustom<T, ID> {
  /**
   * Update the given employee.
   * @param employee current employee
   * @return employee
   */
  T update(T employee);
}
