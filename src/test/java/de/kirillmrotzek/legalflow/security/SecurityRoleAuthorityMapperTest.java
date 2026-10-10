package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.SecurityAuthority;
import de.kirillmrotzek.legalflow.enums.SecurityRole;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityRoleAuthorityMapperTest {

    @Test
    void legalRole_shouldHaveLegalAuthorities() {

        Set<String> authorities =
                SecurityRoleAuthorityMapper.authoritiesFor(SecurityRole.LEGAL);

        assertThat(authorities)
                .containsExactlyInAnyOrder(
                        SecurityAuthority.CONTRACT_READ.name(),
                        SecurityAuthority.CONTRACT_CREATE.name(),
                        SecurityAuthority.CONTRACT_UPDATE.name(),
                        SecurityAuthority.REVIEW_START.name(),
                        SecurityAuthority.REVIEW_APPROVE.name(),
                        SecurityAuthority.REVIEW_REJECT.name()
                );
    }

    @Test
    void financeRole_shouldHaveFinanceAuthorities() {

        Set<String> authorities =
                SecurityRoleAuthorityMapper.authoritiesFor(SecurityRole.FINANCE);

        assertThat(authorities)
                .containsExactlyInAnyOrder(
                        SecurityAuthority.CONTRACT_READ.name(),
                        SecurityAuthority.FINANCIAL_APPROVE.name()
                );
    }

    @Test
    void managementRole_shouldHaveManagementAuthorities() {

        Set<String> authorities =
                SecurityRoleAuthorityMapper.authoritiesFor(SecurityRole.MANAGEMENT);

        assertThat(authorities)
                .containsExactlyInAnyOrder(
                        SecurityAuthority.CONTRACT_READ.name(),
                        SecurityAuthority.MANAGEMENT_APPROVE.name()
                );
    }

    @Test
    void adminRole_shouldHaveAllAuthorities() {

        Set<String> authorities =
                SecurityRoleAuthorityMapper.authoritiesFor(SecurityRole.ADMIN);

        assertThat(authorities)
                .containsExactlyInAnyOrder(
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
    }
}
