package com.parcial.tareas_ad.security;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/error", "/css/**", "/js/**", "/images/**", "/h2-console/**").permitAll()

                // TAREAS
                .requestMatchers("/tasks").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/tasks/new").hasRole("ADMIN")
                .requestMatchers("/tasks/edit/**").hasAnyRole("ADMIN", "USER")

                .anyRequest().authenticated()
            )
            .authenticationManager(authenticationManager)
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(
        ActiveDirectoryAuthenticationProvider activeDirectoryAuthenticationProvider
    ) {
        return new ProviderManager(List.of(activeDirectoryAuthenticationProvider, localAuthenticationProvider()));
    }

    private DaoAuthenticationProvider localAuthenticationProvider() {
        InMemoryUserDetailsManager userDetailsService = new InMemoryUserDetailsManager(
            User.withUsername("admin")
                .password("admin")
                .roles("USER", "ADMIN")
                .build(),
            User.withUsername("test")
                .password("test")
                .roles("USER")
                .build()
        );

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }
}