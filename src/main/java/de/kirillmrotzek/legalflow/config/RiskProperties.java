package de.kirillmrotzek.legalflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "risk.thresholds")
public class RiskProperties {

    private int medium;
    private int high;
}
