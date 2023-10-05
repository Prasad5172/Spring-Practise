package com.example.demo.configure;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authorization.AuthorityAuthorizationManager;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

import com.example.demo.model.ERole;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
        @Autowired
        private AuthenticationProvider authenticationProvider;

        @Autowired
        @Qualifier("handlerExceptionResolver")
        private HandlerExceptionResolver exceptionResolver;

        @Bean
        public JwtAthFilter jwtAthFilter() {
                return new JwtAthFilter(exceptionResolver);
        }

        @Autowired
        private LogoutHandler logoutHandler;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                System.out.println("securityfilterchain");
                AuthorityAuthorizationManager<RequestAuthorizationContext> hasRoleUser = AuthorityAuthorizationManager
                                .hasAuthority(ERole.USER.name());
                hasRoleUser.setRoleHierarchy(roleHierarchy());
                http
                                .csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                                                .requestMatchers("/api/v1/auth/**").permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(sessionManagement -> sessionManagement
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .authenticationProvider(authenticationProvider)
                                .addFilterBefore(jwtAthFilter(), UsernamePasswordAuthenticationFilter.class)
                                .logout(logout -> logout
                                                .logoutUrl("/api/v1/auth/logout")
                                                .addLogoutHandler(logoutHandler)
                                                .logoutSuccessHandler((request, responce,
                                                                authentication) -> SecurityContextHolder
                                                                                .clearContext()));
                return http.build();
        }

        @Bean
  RoleHierarchy roleHierarchy() {
    Map<String, List<String>> roleHierarchyMap = new HashMap<>();
    roleHierarchyMap.put(ERole.ADMIN.name() , List.of(ERole.USER.name()));
    RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
    roleHierarchy.setHierarchy(RoleHierarchyUtils.roleHierarchyFromMap(roleHierarchyMap));
    return roleHierarchy;
  }

}
