package com.parcial.tareas_ad.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable()); // Temporalmente deshabilitado para pruebas
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Sin encriptación para pruebas
    }

    @Bean
    public org.springframework.security.authentication.AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        
        // Configuración LDAP - Versión compatible con Spring Boot 4.0.5
        auth
            .ldapAuthentication()
            .contextSource()
                .url("ldap://192.168.1.41:389/dc=parcial,dc=tareas")
                .managerDn("admin@parcial.tareas")
                .managerPassword("@Pass123")
            .and()
            .userDnPatterns("cn={0}")
            .groupSearchBase("ou=groups");
        
        // Configuración local como fallback - solo para usuarios locales
        auth
            .inMemoryAuthentication()
            .withUser("admin")
            .password("admin")
            .roles("USER", "ADMIN")
            .and()
            .withUser("test")
            .password("test")
            .roles("USER");
        
        return auth.build();
    }
}