package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.SecurityAuthority;
import de.kirillmrotzek.legalflow.enums.SecurityRole;

import java.util.Set;

public final class SecurityRoleAuthorityMapper {

    private SecurityRoleAuthorityMapper() {
    }

    public static Set<String> authoritiesFor(SecurityRole role) {
        return switch (role) {
            case LEGAL -> Set.of(
                    SecurityAuthority.CONTRACT_READ.name(),
                    SecurityAuthority.CONTRACT_CREATE.name(),
                    SecurityAuthority.CONTRACT_UPDATE.name(),
                    SecurityAuthority.REVIEW_START.name(),
                    SecurityAuthority.REVIEW_APPROVE.name(),
                    SecurityAuthority.REVIEW_REJECT.name()
            );

            case FINANCE -> Set.of(
                    SecurityAuthority.CONTRACT_READ.name(),
                    SecurityAuthority.FINANCIAL_APPROVE.name()
            );

            case MANAGEMENT -> Set.of(
                    SecurityAuthority.CONTRACT_READ.name(),
                    SecurityAuthority.MANAGEMENT_APPROVE.name()
            );

            case ADMIN -> Set.of(
                    SecurityAuthority.CONTRACT_READ.name(),
                    SecurityAuthority.CONTRACT_CREATE.name(),
                    SecurityAuthority.CONTRACT_UPDATE.name(),
                    SecurityAuthority.REVIEW_START.name(),
                    SecurityAuthority.REVIEW_APPROVE.name(),
                    SecurityAuthority.REVIEW_REJECT.name(),
                    SecurityAuthority.FINANCIAL_APPROVE.name(),
                    SecurityAuthority.MANAGEMENT_APPROVE.name(),
                    SecurityAuthority.COMPLIANCE_REVIEW.name()
            );
        };
    }
}
