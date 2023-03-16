package jp.co.axa.apidemo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.hateoas.server.core.Relation;

import lombok.Getter;
import lombok.Setter;

/**
 * Employee entity class.
 * 
 * @author Josimar Lopes
 */
@Entity
@Table(name="EMPLOYEE")
@Relation(collectionRelation = "employees")
public class Employee {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(name = "EMPLOYEE_NAME", nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "EMPLOYEE_SALARY", nullable = false)
    private Integer salary;

    @Getter
    @Setter
    @Column(name = "DEPARTMENT", nullable = false)
    private String department;

}
