package jp.co.axa.apidemo.utils;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Common mock test cases utils settings.
 *
 * @author Josimar Lopes
 */
public final class MockTestCaseUtils {
  public static String NEW_EMPLOYEE_JSON = "" 
    + "{\n"
    +  "  \"id\": 1,\n"
    +  "  \"name\": \"Jack Bower\",\n"
    +  "  \"salary\": 10000000,\n"
    +  "  \"department\": \"CTU\"\n"
    +  "}";

  private MockTestCaseUtils() {}

  private static class BearerTokenRequestPostProcessor implements RequestPostProcessor {
    private String token;

    BearerTokenRequestPostProcessor(String token) {
      this.token = token;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
      request.addHeader("Authorization", "Bearer " + this.token);
      return request;
    }
  }

  public static RequestPostProcessor bearerToken(String token) {
    return new MockTestCaseUtils.BearerTokenRequestPostProcessor(token);
  }

  @SuppressWarnings("deprecation")
  public static String obtainAccessToken(final String clientId, final String clientSecret,
      final String scope, final MockMvc mvc) throws Exception {
    MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
    params.add("grant_type", "client_credentials");
    params.add("scope", scope);

    ResultActions result = mvc
        .perform(post("/oauth/token").with(httpBasic(clientId, clientSecret)).params(params)
            .accept(MediaType.APPLICATION_JSON_UTF8))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));

    String resultString = result.andReturn().getResponse().getContentAsString();
    JacksonJsonParser parser = new JacksonJsonParser();

    return parser.parseMap(resultString).get("access_token").toString();
  }
}
