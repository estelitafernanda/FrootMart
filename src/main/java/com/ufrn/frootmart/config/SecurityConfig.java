package com.ufrn.frootmart.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/").authenticated();
                    auth.requestMatchers("/index").hasRole("USER");
                    auth.requestMatchers("/verCarrinho").hasRole("USER");
                    auth.requestMatchers("/adicionarCarrinho/{id}").hasRole("USER");
                    //auth.requestMatchers("/removerCarrinho/{id}").hasRole("USER");
                    auth.requestMatchers("/finalizarCompra").hasRole("USER");
                    auth.requestMatchers("/admin").hasRole("ADMIN");
                    auth.requestMatchers("/cadastro").hasRole("ADMIN");
                    auth.requestMatchers("/editar/{id}").hasRole("ADMIN");
                    auth.requestMatchers("/deletar/{id}").hasRole("ADMIN");
                    auth.requestMatchers("/salvar/{editar_ou_cadastrar}").hasRole("ADMIN");
                    auth.requestMatchers("/cadastroUsuario").permitAll();
                    auth.requestMatchers("/processCadastroUsuario").permitAll();
                    auth.anyRequest().authenticated();
                })
                .formLogin(login -> login
                        .loginPage("/login")
                        .permitAll()
                        .successHandler(authenticationSuccessHandler())
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(logoutSuccessHandler())
                        .permitAll()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, auth) -> {
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ROLE_USER");

            System.out.println("entrou no handle");
            if(role.equals("ROLE_ADMIN")) {
                System.out.println("admin");
                response.sendRedirect("/admin");
            } else if(role.equals("ROLE_USER")) {
                System.out.println("user");
                response.sendRedirect("/index");
            } else {
                System.out.println("sem role");
                response.sendRedirect("/login");
            }
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            // Redirect to custom logout page
            response.sendRedirect("/logout");
        };
    }
}
