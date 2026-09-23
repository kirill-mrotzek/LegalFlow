package de.kirillmrotzek.legalflow.lifecycle;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContractStatusTransitionValidatorTest {

    private ContractStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ContractStatusTransitionValidator();
    }

    @Test
    void shouldAllowDraftToSigned() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.DRAFT,
                        ContractStatus.SIGNED
                )
        );
    }

    @Test
    void shouldAllowSignedToActive() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.SIGNED,
                        ContractStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldAllowActiveToExpired() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.ACTIVE,
                        ContractStatus.EXPIRED
                )
        );
    }

    @Test
    void shouldAllowActiveToTerminated() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.ACTIVE,
                        ContractStatus.TERMINATED
                )
        );
    }

    @Test
    void shouldAllowExpiredToArchived() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.EXPIRED,
                        ContractStatus.ARCHIVED
                )
        );
    }

    @Test
    void shouldAllowTerminatedToArchived() {
        assertTrue(
                validator.isAllowed(
                        ContractStatus.TERMINATED,
                        ContractStatus.ARCHIVED
                )
        );
    }

    @Test
    void shouldRejectDraftToActive() {
        assertFalse(
                validator.isAllowed(
                        ContractStatus.DRAFT,
                        ContractStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectSignedToDraft() {
        assertFalse(
                validator.isAllowed(
                        ContractStatus.SIGNED,
                        ContractStatus.DRAFT
                )
        );
    }

    @Test
    void shouldRejectExpiredToActive() {
        assertFalse(
                validator.isAllowed(
                        ContractStatus.EXPIRED,
                        ContractStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectTerminatedToActive() {
        assertFalse(
                validator.isAllowed(
                        ContractStatus.TERMINATED,
                        ContractStatus.ACTIVE
                )
        );
    }

    @Test
    void shouldRejectAnyTransitionFromArchived() {
        assertFalse(
                validator.isAllowed(
                        ContractStatus.ARCHIVED,
                        ContractStatus.ACTIVE
                )
        );
    }
}
