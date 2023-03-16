package jp.co.axa.apidemo.security;


import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.OctetSequenceKey;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;


/**
 * Authorization Server Configuration.
 *
 * @author Josimar Lopes
 */
@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

  private AuthenticationManager authenticationManager;
  private JWK jwk;

  public AuthorizationServerConfig(AuthenticationConfiguration authenticationConfiguration, JWK jwk) throws Exception {
    this.authenticationManager = authenticationConfiguration.getAuthenticationManager();
    this.jwk = jwk;
  }

  @Override
  public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
    super.configure(security);
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
    // @formatter:off
    endpoints
        .authenticationManager(this.authenticationManager)
        .accessTokenConverter(accessTokenConverter())
        .tokenStore(tokenStore());
    // @formatter:on
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    // @formatter:off
    clients.inMemory()
      .withClient("axa-client")
        .secret(passwordEncoder().encode("client-secret"))
        .authorizedGrantTypes("client_credentials")
        .authorities("ROLE_CLIENT")
        .scopes("employee:read")
        .accessTokenValiditySeconds(600_000_000)
        .and()
      .withClient("axa-manager")
        .secret(passwordEncoder().encode("manager-secret"))
        .authorizedGrantTypes("client_credentials")
        .authorities("ROLE_MANAGER")
        .scopes("employee:read", "employee:write")
        .accessTokenValiditySeconds(600_000_000);
    // @formatter:on
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }

  @Bean
  public TokenStore tokenStore() {
    return new JwtTokenStore(accessTokenConverter());
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    MacSigner verifier = new MacSigner("HMACSHA512", ((OctetSequenceKey) this.jwk).toSecretKey("HMACSHA512"));

    JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
    converter.setSigner(verifier);
    converter.setVerifier(verifier);

    return converter;
  }
}
