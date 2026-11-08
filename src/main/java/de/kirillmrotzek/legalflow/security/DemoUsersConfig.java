package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.SecurityRole;
import de.kirillmrotzek.legalflow.enums.SecurityAuthority;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@Profile({"dev", "test"})
public class DemoUsersConfig {

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {

        UserDetails kirill = userWithRole(
                "kirill",
                "password",
                SecurityRole.LEGAL,
                passwordEncoder
        );

        UserDetails reviewer = User.builder()
                .username("reviewer")
                .password(passwordEncoder.encode("password"))
                .authorities(
                        SecurityAuthority.REVIEW_START.name(),
                        SecurityAuthority.REVIEW_REJECT.name(),
                        SecurityAuthority.CONTRACT_READ.name()
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
                kirill,
                reviewer,
                finance,
                management,
                admin
        );
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
