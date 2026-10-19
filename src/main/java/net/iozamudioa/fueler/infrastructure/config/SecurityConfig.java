package net.iozamudioa.fueler.infrastructure.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import net.iozamudioa.fueler.infrastructure.adapters.in.security.ApiAuthenticationFilter;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain security(HttpSecurity http, ApiAuthenticationFilter filter) throws Exception {
    return http.csrf(c -> c.disable())
        .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(a -> a
            .requestMatchers("/actuator/health").permitAll()
            .requestMatchers(
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml")
            .permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/devices").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/devices/authorize").permitAll()
            .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/v1/**").hasRole("DEVICE")
            .anyRequest().denyAll())
        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class).build();
  }

  @Bean
  FilterRegistrationBean<ApiAuthenticationFilter> apiFilterRegistration(
      ApiAuthenticationFilter filter) {

    FilterRegistrationBean<ApiAuthenticationFilter> registration = new FilterRegistrationBean<>(filter);

    registration.setEnabled(false);

    return registration;
  }
}
