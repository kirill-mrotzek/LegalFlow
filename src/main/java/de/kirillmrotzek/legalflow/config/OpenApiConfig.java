package de.kirillmrotzek.legalflow.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "LegalFlow Contract Management API",
                version = "1.0.0"
        )
)
@Configuration
public class OpenApiConfig {
}
