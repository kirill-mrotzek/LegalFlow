package de.kirillmrotzek.legalflow.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityController {

    @GetMapping("/security/me")
    public String currentUser() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        return authentication.getName()
                + " | "
                + authentication.getAuthorities()
                + " | authenticated="
                + authentication.isAuthenticated();
    }
}
