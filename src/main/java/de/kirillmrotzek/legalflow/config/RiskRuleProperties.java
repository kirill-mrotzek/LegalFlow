package de.kirillmrotzek.legalflow.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "risk.rules")
public class RiskRuleProperties {

    private HighContractValue highContractValue = new HighContractValue();
    private AutoRenewal autoRenewal = new AutoRenewal();
    private LongTermContract longTermContract = new LongTermContract();
    private ForeignGoverningLaw foreignGoverningLaw = new ForeignGoverningLaw();
    private UnlimitedLiability unlimitedLiability = new UnlimitedLiability();

    @Getter
    @Setter
    public static class HighContractValue {
        private BigDecimal threshold;
        private int points;
    }

    @Getter
    @Setter
    public static class AutoRenewal {
        private int points;
    }

    @Getter
    @Setter
    public static class LongTermContract {
        private int years;
        private int points;
    }

    @Getter
    @Setter
    public static class ForeignGoverningLaw {
        private int euPoints;
        private int nonEuPoints;
    }

    @Getter
    @Setter
    public static class UnlimitedLiability {
        private int points;
    }
}
