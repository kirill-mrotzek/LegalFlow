package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.SecurityAuthority;
import de.kirillmrotzek.legalflow.enums.SecurityRole;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .httpBasic(Customizer.withDefaults())
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.POST, "/contracts/*/review/start")
                        .hasAuthority(SecurityAuthority.REVIEW_START.name())

                        .requestMatchers(HttpMethod.POST, "/contracts/*/review/approve")
                        .hasAuthority(SecurityAuthority.REVIEW_APPROVE.name())

                        .requestMatchers(HttpMethod.POST, "/contracts/*/review/reject")
                        .hasAuthority(SecurityAuthority.REVIEW_REJECT.name())

                        .anyRequest().authenticated()
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails user = userWithRole(
                "kirill",
                "password",
                SecurityRole.LEGAL,
                passwordEncoder
        );

        UserDetails reviewer = User.builder()
                .username("reviewer")
                .password(passwordEncoder.encode("password"))
                .authorities(
                        SecurityAuthority.CONTRACT_READ.name(),
                        SecurityAuthority.REVIEW_START.name(),
                        SecurityAuthority.REVIEW_REJECT.name()
                )
                .build();

        UserDetails finance = userWithRole(
                "finance",
                "password",
                SecurityRole.FINANCE,
                passwordEncoder
        );

        UserDetails management = userWithRole(
                "management",
                "password",
                SecurityRole.MANAGEMENT,
                passwordEncoder
        );

        UserDetails admin = userWithRole(
                "admin",
                "password",
                SecurityRole.ADMIN,
                passwordEncoder
        );

        return new InMemoryUserDetailsManager(
                user,
                reviewer,
                finance,
                management,
                admin
        );
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    private UserDetails userWithRole(
            String username,
            String password,
            SecurityRole role,
            PasswordEncoder passwordEncoder) {

        return User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .authorities(
                        SecurityRoleAuthorityMapper.authoritiesFor(role)
                                .toArray(String[]::new)
                )
                .build();
    }
}
