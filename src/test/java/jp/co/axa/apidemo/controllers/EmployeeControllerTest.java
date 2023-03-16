package jp.co.axa.apidemo.controllers;

import static jp.co.axa.apidemo.utils.MockTestCaseUtils.NEW_EMPLOYEE_JSON;
import static jp.co.axa.apidemo.utils.MockTestCaseUtils.bearerToken;
import static jp.co.axa.apidemo.utils.MockTestCaseUtils.obtainAccessToken;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import jp.co.axa.apidemo.config.BeanConfig;
import jp.co.axa.apidemo.entities.Employee;
import jp.co.axa.apidemo.security.AuthorizationServerConfig;
import jp.co.axa.apidemo.security.ResourceServerConfig;
import jp.co.axa.apidemo.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;

/**
 * Basic employee controller test cases.
 *
 * @author Josimar Lopes
 */
@Slf4j
@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@WebMvcTest(EmployeeController.class)
@Import({AuthorizationServerConfig.class, ResourceServerConfig.class, BeanConfig.class})
public class EmployeeControllerTest {
  @Autowired
  private MockMvc mvc;
  @Autowired
  private ObjectMapper mapper;
  @MockBean
  private EmployeeService employeeService;

  private String accessToken;
  private Employee employee;

  @Before
  public void setup() throws Exception {
    employee = mapper.readValue(NEW_EMPLOYEE_JSON, Employee.class);
    accessToken = obtainAccessToken("axa-manager", "manager-secret", "employee:write", this.mvc);
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldPostEmployee_andCheckIfCreated_withSuccess() throws Exception {

    given(this.employeeService.saveEmployee(any(Employee.class))).willReturn(Optional.of(employee));

    this.mvc
      .perform(post("/api/employees").with(bearerToken(accessToken))
        .contentType("application/vnd.axa.v1+json").content(NEW_EMPLOYEE_JSON)
        .accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isCreated())
      .andExpect(header().string("Location", containsString("/api/employees")))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(jsonPath("$.id").isNumber())
      .andExpect(jsonPath("$.name", is(employee.getName())))
      .andExpect(jsonPath("$.salary", is(employee.getSalary())))
      .andExpect(jsonPath("$.department", is(employee.getDepartment())))
      .andExpect(jsonPath("$._links").exists()).andDo(print());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldGetAllEmployees_andCheckIfFetched_withSuccess() throws Exception {
    // Test on an empty collection
    given(this.employeeService.retrieveEmployees()).willReturn(Optional.of(new ArrayList<>()));

    this.mvc
      .perform(get("/api/employees").with(bearerToken(accessToken))
          .contentType("application/vnd.axa.v1+json").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$._embedded.employees").doesNotHaveJsonPath())
      .andExpect(jsonPath("$._links.self.href").hasJsonPath()).andDo(print());

    // Test on a collection of employees
    given(this.employeeService.retrieveEmployees())
      .willReturn(Optional.of(Collections.singletonList(employee)));

    this.mvc
      .perform(get("/api/employees").with(bearerToken(accessToken))
        .contentType("application/vnd.axa.v1+json").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk()).andExpect(jsonPath("$._embedded.employees").exists())
      .andExpect(jsonPath("$._embedded.employees[0].name", is(employee.getName())))
      .andDo(print());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldPutEmployee_andCheckIfUpdated_withSuccess() throws Exception {

    final String updatedEmployeeJson =
        "" + "{\n" + "  \"salary\": 999999,\n" + "  \"department\": \"Special Forces\"\n" + "}";

    employee.setSalary(999999);
    employee.setDepartment("Special Forces");

    given(this.employeeService.updateEmployee(any(Employee.class), anyLong()))
      .willReturn(Optional.of(employee));

    final Long id = new Long(1);

    this.mvc
      .perform(put("/api/employees/".concat(String.valueOf(id))).with(bearerToken(accessToken))
        .contentType("application/vnd.axa.v1+json").content(updatedEmployeeJson)
        .accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isNoContent()).andExpect(header().string("Location",
        containsString("/api/employees/".concat(String.valueOf(id)))))
      .andDo(print());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldGetOneEmployeeByTextId_andCheckIfFetched_withSuccess() throws Exception {
    // Test on a unique employee
    given(this.employeeService.getEmployee(any())).willReturn(Optional.of(employee));

    final Long id = new Long(1);

    this.mvc
      .perform(get("/api/employees/".concat(String.valueOf(id))).with(bearerToken(accessToken))
        .contentType("application/vnd.axa.v1+json").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isOk()).andExpect(jsonPath("$.id").isNumber())
      .andExpect(jsonPath("$.name").exists()).andExpect(jsonPath("$.salary").exists())
      .andExpect(jsonPath("$.department").exists())
      .andExpect(jsonPath("$._links.self.href").isNotEmpty()).andDo(print());
  }

  @Test
  @SuppressWarnings("deprecation")
  public void shouldDeleteEmployee_andCheckIfDeleted_withSuccess() throws Exception {

    final Long id = new Long(1);

    this.mvc
      .perform(delete("/api/employees/".concat(String.valueOf(id))).with(bearerToken(accessToken))
        .contentType("application/vnd.axa.v1+json").accept(MediaType.APPLICATION_JSON_VALUE))
      .andExpect(status().isNoContent()).andExpect(header().doesNotExist("Location"))
      .andDo(print());
  }
}
