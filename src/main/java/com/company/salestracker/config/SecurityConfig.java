package com.company.salestracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.company.salestracker.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
 DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

 provider.setPasswordEncoder(passwordEncoder());
 return provider;
    }

    
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    	
        http.csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/auth/login").permitAll()
//         	  .requestMatchers("/api/users").hasAuthority("ROLE_SUPER_ADMIN")
//         	 .requestMatchers("/api/roles").hasAuthority("ROLE_SUPER_ADMIN")
//         	 .requestMatchers("/api/roles").hasAuthority("ADMIN")
//         	 .requestMatchers("/api/users").hasAuthority("ADMIN")
//         	  .requestMatchers("/api/complaints/{id}").hasAuthority("ADMIN")
         //   .requestMatchers(HttpMethod.GET,"/api/complaints").hasAuthority("ADMIN")
         //   .requestMatchers("/custmer").hasAuthority("CUSTMER")
         //   .requestMatchers("/manager").hasAuthority("MANAGER")
                
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .sessionManagement(sess ->
                sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            ).exceptionHandling(exception -> exception

            	      .authenticationEntryPoint((request, response, ex) -> {
            	       response.setContentType("application/json");
            	       response.setStatus(401);
            	       response.getWriter().write("""
            	         {
            	           "success": false,
            	           "message": "Unauthorized - Invalid or Missing Token",
            	           "errorCode": "AUTH_401"
            	         }
            	         """);
            	      })

            	      .accessDeniedHandler((request, response, ex) -> {
            	       response.setContentType("application/json");
            	       response.setStatus(403);
            	       response.getWriter().write("""
            	         {
            	           "success": false,
            	           "message": "Forbidden - You don't have permission",
            	           "errorCode": "AUTH_403"
            	         }
            	         """);
            	      }));

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}


