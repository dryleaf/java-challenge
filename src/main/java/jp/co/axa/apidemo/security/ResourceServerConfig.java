package jp.co.axa.apidemo.security;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import static org.springframework.security.config.Customizer.withDefaults;


/**
 * Resource Server configuration.
 *
 * @author Josimar Lopes
 */
@EnableWebSecurity
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {
  private JWK jwk;

  public ResourceServerConfig(JWK jwk) {
    this.jwk = jwk;
  }

  @Override
  public void configure(HttpSecurity http) throws Exception {
    // @formatter:off
    http
      .sessionManagement(management -> management
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
      .requestMatchers(matchers -> matchers
        .antMatchers("/api/**")
        .antMatchers("/oauth/**"))
      .authorizeRequests(requests -> requests
        .mvcMatchers("/api/health", "/api/docs/**", "/api/swagger-ui/**")
        .permitAll()
        .mvcMatchers(HttpMethod.GET, "/api/employees/**")
        .access("hasAnyAuthority('SCOPE_employee:read', 'SCOPE_employee:write') or hasAnyRole('ROLE_MANAGER', 'ROLE_CLIENT')")
        .mvcMatchers(HttpMethod.POST, "/api/employees/**")
        .access("hasAuthority('SCOPE_employee:write')  or hasRole('ROLE_MANAGER')")
        .mvcMatchers(HttpMethod.PUT, "/api/employees/**")
        .access("hasAuthority('SCOPE_employee:write')  or hasRole('ROLE_MANAGER')")
        .mvcMatchers(HttpMethod.DELETE, "/api/employees/**")
        .access("hasAuthority('SCOPE_employee:write')  or hasRole('ROLE_MANAGER')")
        .anyRequest()
        .authenticated())
      .httpBasic(withDefaults())
      .oauth2ResourceServer(server -> server
        .jwt());
    // @formatter:on
  }

  @Bean
  JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder
        .withSecretKey(((OctetSequenceKey) this.jwk).toSecretKey("HMACSHA512"))
        .macAlgorithm(MacAlgorithm.HS512)
        .build();
  }

}
