package jp.co.axa.apidemo.controllers;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;

@SecurityScheme(
  type = SecuritySchemeType.OAUTH2,
  name = "oAuth2Auth",
  in = SecuritySchemeIn.HEADER,
  bearerFormat = "bearer",
  flows = @OAuthFlows(
    clientCredentials = @OAuthFlow(
      tokenUrl = "/oauth/token",
      scopes = {
        @OAuthScope(name = "employee:read"),
        @OAuthScope(name = "employee:write")})))
@Slf4j
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

  private EmployeeService employeeService;

  @Autowired
  public void setEmployeeService(EmployeeService employeeService) {
    this.employeeService = employeeService;
  }

  @Operation(
    summary = "Retrieve all employees",
    security = @SecurityRequirement(name = "oAuth2Auth"),
    responses = {
      @ApiResponse(responseCode = "200", description = "when employees are fetched successfully"),
      @ApiResponse(responseCode = "404", description = "when results in exception error")})
  @GetMapping(
    consumes = {"application/vnd.axa.v1+json"},
    produces = {"application/json"})
  public @ResponseBody ResponseEntity<CollectionModel<EntityModel<Employee>>> getEmployees() {
    List<EntityModel<Employee>> employees = 
      (employeeService
        .retrieveEmployees()
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No employee found!")))
      .stream()
      .map(employee -> EntityModel.of(employee,
        linkTo(methodOn(EmployeeController.class).getEmployee(employee.getId())).withSelfRel(),
        linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("employees")))
      .collect(Collectors.toList());

    return ResponseEntity
      .ok()
      .body(CollectionModel
        .of(employees, linkTo(methodOn(EmployeeController.class)
          .getEmployees())
          .withSelfRel()));
  }

  @Operation(
    summary = "Retrieve a single employee based on the uid",
    security = @SecurityRequirement(name = "oAuth2Auth"),
    responses = {
      @ApiResponse(responseCode = "200", description = "when employee was fetched successfully"),
      @ApiResponse(responseCode = "404", description = "when an exception error occurs")})
  @GetMapping(
    value = "/{employeeId}",
    consumes = {"application/vnd.axa.v1+json"},
    produces = {"application/json"})
  public @ResponseBody ResponseEntity<?> getEmployee(@PathVariable(name = "employeeId") Long employeeId) {
    Employee oneEmployee =
      employeeService
        .getEmployee(employeeId)
        .orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with id " + employeeId + " cannot be found!"));
    
    EntityModel<Employee> employeeModel = EntityModel.of(oneEmployee,
      linkTo(methodOn(EmployeeController.class).getEmployee(oneEmployee.getId())).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).getEmployees()).withRel("employees"));
    
    return ResponseEntity
      .ok()
      .body(employeeModel);
  }

  @Operation(
      summary = "Save an employee",
      security = @SecurityRequirement(name = "oAuth2Auth"),
      responses = {
          @ApiResponse(responseCode = "201", description = "when employee is created successfully"),
          @ApiResponse(responseCode = "400", description = "when an error is encountered"),
          @ApiResponse(responseCode = "404", description = "when results in exception error")})
  @PostMapping(
    consumes = {"application/vnd.axa.v1+json"},
    produces = {"application/json"})
  public @ResponseBody ResponseEntity<?> newEmployee(@RequestBody final Employee employee) {
    
    Employee newEmployee = 
      employeeService
        .saveEmployee(employee)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee could not be created."));
    
    try {
      EntityModel<Employee> employeeModel = 
        EntityModel.of(
          newEmployee,
          linkTo(methodOn(EmployeeController.class)
            .getEmployee(newEmployee.getId()))
            .withSelfRel());
      
      log.info("Employee with id: " + newEmployee.getId() + " created successfully.");
      
      return ResponseEntity
        .created(new URI(employeeModel.getRequiredLink(IanaLinkRelations.SELF).getHref()))
        .body(employeeModel);
    } catch (URISyntaxException e) {
      return ResponseEntity
        .badRequest()
        .body("Employee could not be created: " + employee);
    }
  }

  @Operation(
    summary = "Delete a employee",
    security = @SecurityRequirement(name = "oAuth2Auth"),
    responses = {
      @ApiResponse(responseCode = "204", description = "when employee is deleted successfully"),
      @ApiResponse(responseCode = "404", description = "when results in exception error")})
  @DeleteMapping(
    value = "/{employeeId}",
    consumes = {"application/vnd.axa.v1+json"},
    produces = {"application/json"})
  public ResponseEntity<?> deleteEmployee(@PathVariable(name = "employeeId") Long employeeId) {

    employeeService.deleteEmployee(employeeId);
    System.out.println("Employee Deleted Successfully");

    return ResponseEntity.noContent().build();
  }

  @Operation(
    summary = "Update a employee",
    security = @SecurityRequirement(name = "oAuth2Auth"),
    responses = {
      @ApiResponse(responseCode = "204", description = "when employee is updated successfully"),
      @ApiResponse(responseCode = "400", description = "when an error is encountered"),
      @ApiResponse(responseCode = "404", description = "when results in exception error")})
  @PutMapping(
    value = "/{employeeId}",
    consumes = {"application/vnd.axa.v1+json"},
    produces = {"application/json"})
  public ResponseEntity<?> updateEmployee(@RequestBody Employee employee, @PathVariable Long employeeId) {

    Employee oneEmployee =
      employeeService
        .updateEmployee(employee, employeeId)
        .orElseThrow(
          () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Employee with id " + employeeId + " cannot be updated!"));
    
    try {
      return ResponseEntity
        .noContent()
        .location(new URI(
          linkTo(methodOn(EmployeeController.class).getEmployee(employeeId))
          .withSelfRel()
          .getHref()))
        .build();
    } catch (URISyntaxException e) {
      return ResponseEntity
      .badRequest()
      .body("Employee |" + employeeId + "| could not be updated: " + oneEmployee);
    }
  }
}
