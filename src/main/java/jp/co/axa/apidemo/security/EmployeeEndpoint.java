package jp.co.axa.apidemo.security;


import java.util.Collections;
import java.util.Map;

import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * Basic employee api endpoint health check.
 *
 * @author Josimar Lopes
 */
@FrameworkEndpoint
public class EmployeeEndpoint {

  @GetMapping("/api/health")
  @ResponseBody
  public Map<String, String> getHealthStatus() {
    return Collections.singletonMap("status", "UP");
  }
}
