package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.service.ContractLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ContractLifecycleMethodSecurityTest {

    @Autowired
    private ContractLifecycleService contractLifecycleService;

    @Test
    @WithMockUser(authorities = "CONTRACT_READ")
    void userWithoutContractStatusChangeAuthority_shouldNotChangeStatus() {

        assertThatThrownBy(() ->
                contractLifecycleService.changeStatus(
                        999L,
                        ContractStatus.SIGNED
                )
        )
                .isInstanceOf(AccessDeniedException.class);
    }
}
