package com.Tienda.Tienda_de_Barrio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new LegacyAwarePasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider authenticationProvider(TiendaUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsPasswordService(userDetailsService);
        return provider;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, DaoAuthenticationProvider authenticationProvider)
            throws Exception {
        http
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/login", "/cambiar-contrasena", "/css/**", "/js/**", "/images/**", "/error")
                        .permitAll()
                        .requestMatchers("/web/usuarios/**", "/api/usuarios/**")
                        .hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/web/productos/**", "/web/proveedores/**",
                                "/web/clientes/*/editar", "/web/ventas/*/eliminar", "/web/compras/*/eliminar")
                        .hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/productos/**", "/api/clientes/**", "/api/proveedores/**")
                        .hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.PUT, "/api/productos/**", "/api/clientes/**", "/api/proveedores/**")
                        .hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.DELETE, "/api/productos/**", "/api/proveedores/**",
                                "/api/ventas/**", "/api/compras/**")
                        .hasAnyRole("ADMIN", "ADMINISTRADOR")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll())
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll());
        return http.build();
    }
}
