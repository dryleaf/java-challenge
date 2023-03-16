package jp.co.axa.apidemo.repositories;

import jp.co.axa.apidemo.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;


/**
 * Employee repository.
 * 
 * @author Josimar Lopes
 */
public interface EmployeeRepository extends JpaRepository<Employee,Long>, EmployeeRepositoryCustom<Employee, Long> {
}
