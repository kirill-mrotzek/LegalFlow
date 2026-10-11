package de.kirillmrotzek.legalflow.security;

import de.kirillmrotzek.legalflow.service.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class ContractServiceMethodSecurityTest {

    @Autowired
    private ContractService contractService;

    @Test
    @WithMockUser(authorities = "CONTRACT_READ")
    void userWithoutContractCreateAuthority_shouldNotCreateContract() {

        assertThatThrownBy(() ->
                contractService.save(null)
        ).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "CONTRACT_READ")
    void userWithoutContractUpdateAuthority_shouldNotUpdateContract() {

        assertThatThrownBy(() ->
                contractService.update(999L, null)
        ).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @WithMockUser(authorities = "CONTRACT_CREATE")
    void userWithoutContractReadAuthority_shouldNotSearchContracts() {

        assertThatThrownBy(() ->
                contractService.search(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        PageRequest.of(0, 10)
                )
        ).isInstanceOf(AccessDeniedException.class);
    }
}
