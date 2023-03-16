package jp.co.axa.apidemo.repositories;

import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jp.co.axa.apidemo.entities.Employee;
import lombok.extern.slf4j.Slf4j;

/**
 * {@inheritDoc}
 */
@Slf4j
@Transactional
@Repository
public class EmployeeRepositoryCustomImpl implements EmployeeRepositoryCustom<Employee, Long> {
  private EntityManager entityManager;

  @Autowired
  public void setEntityManager(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public Employee update(@NotNull Employee employee) {
    Session session = entityManager.unwrap(Session.class);
    session.update(employee);

    return employee;
  }
}
