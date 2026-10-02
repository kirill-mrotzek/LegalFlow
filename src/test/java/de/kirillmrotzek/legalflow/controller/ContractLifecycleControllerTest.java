package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.exception.InvalidContractStatusTransitionException;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.service.ContractLifecycleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ContractLifecycleController.class)
class ContractLifecycleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractLifecycleService contractLifecycleService;

    @MockitoBean
    private ContractMapper contractMapper;

    @Test
    void changeContractStatus_shouldChangeStatusSuccessfully() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setContractStatus(ContractStatus.SIGNED);

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setContractStatus(ContractStatus.SIGNED);

        when(contractLifecycleService.changeStatus(
                eq(1L),
                eq(ContractStatus.SIGNED)
        )).thenReturn(contract);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        patch("/contracts/1/status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": "SIGNED"
                            }
                            """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.contractStatus")
                        .value("SIGNED"));

        verify(contractLifecycleService)
                .changeStatus(1L, ContractStatus.SIGNED);

        verify(contractMapper)
                .toResponse(contract);
    }

    @Test
    void changeContractStatus_shouldReturnBadRequestForInvalidTransition()
            throws Exception {

        when(contractLifecycleService.changeStatus(
                eq(1L),
                eq(ContractStatus.ACTIVE)
        )).thenThrow(
                new InvalidContractStatusTransitionException(
                        ContractStatus.DRAFT,
                        ContractStatus.ACTIVE
                )
        );

        mockMvc.perform(
                        patch("/contracts/1/status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": "ACTIVE"
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Invalid contract status transition: DRAFT -> ACTIVE"
                                )
                );

        verify(contractLifecycleService)
                .changeStatus(1L, ContractStatus.ACTIVE);
    }

    @Test
    void changeContractStatus_shouldReturnNotFoundWhenContractDoesNotExist()
            throws Exception {

        when(contractLifecycleService.changeStatus(
                eq(999L),
                eq(ContractStatus.SIGNED)
        )).thenThrow(
                new ContractNotFoundException(999L)
        );

        mockMvc.perform(
                        patch("/contracts/999/status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": "SIGNED"
                            }
                            """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(
                        jsonPath("$.message")
                                .value("Contract with id 999 not found")
                );

        verify(contractLifecycleService)
                .changeStatus(999L, ContractStatus.SIGNED);
    }

    @Test
    void changeContractStatus_shouldReturnBadRequestWhenStatusIsNull()
            throws Exception {

        mockMvc.perform(
                        patch("/contracts/1/status")
                                .contentType(APPLICATION_JSON)
                                .content("""
                            {
                                "status": null
                            }
                            """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]")
                        .value("status: must not be null"));

        verifyNoInteractions(contractLifecycleService);
    }
}
