package jp.co.axa.apidemo.config;


import com.nimbusds.jose.jwk.JWK;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.HateoasPageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * Common bean configuration.
 *
 * @author Josimar Lopes
 */
@Slf4j
@Configuration
@EnableSpringDataWebSupport
public class BeanConfig {

  @Bean
  public HateoasPageableHandlerMethodArgumentResolver resolver() {
    return new HateoasPageableHandlerMethodArgumentResolver();
  }

  @Bean
  public JWK jwk() {
    try {
      /*
       * No key rotation strategy. So a new HS512 secure secret keys can be generated manually.
       * ↓
       * log.debug("Generate New HS512 key: {}", new OctetSequenceKeyGenerator(512)
       *     .keyID(UUID.randomUUID().toString())
       *     .algorithm(JWSAlgorithm.HS512)
       *     .generate());
       */
      return JWK.parse(
        "{\n"
        + "\"kty\": \"oct\",\n"
        + "\"kid\": \"dccdd98e-4ca4-40c8-9e16-ea54381c765f\",\n"
        + "\"k\": \"1NRHXuvVE_DiWtOQtnZ-R3y2NmbjkBmBgQ5pOrJSvKfxPp2r-WLjGehwer0A4qh2_Uz5k0p9rjlIMaHOdgfp1w\",\n"
        + "\"alg\": \"HS512\"\n"
        + "}");
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
