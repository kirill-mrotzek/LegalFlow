package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.decision.ApprovalRole;
import de.kirillmrotzek.legalflow.decision.DecisionPriority;
import de.kirillmrotzek.legalflow.decision.DecisionSupport;
import de.kirillmrotzek.legalflow.dto.*;
import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.exception.InvalidContractStatusTransitionException;
import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.mapper.DecisionSupportMapper;
import de.kirillmrotzek.legalflow.risk.RiskAssessment;
import de.kirillmrotzek.legalflow.risk.RiskFactor;
import de.kirillmrotzek.legalflow.service.ContractLifecycleService;
import de.kirillmrotzek.legalflow.service.ContractService;
import de.kirillmrotzek.legalflow.service.DecisionSupportService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import de.kirillmrotzek.legalflow.model.Contract;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import org.springframework.data.domain.PageRequest;

import de.kirillmrotzek.legalflow.mapper.RiskAssessmentMapper;
import de.kirillmrotzek.legalflow.service.RiskAssessmentService;
import static org.hamcrest.Matchers.hasItem;

@WebMvcTest(ContractController.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private ContractMapper contractMapper;

    @MockitoBean
    private RiskAssessmentService riskAssessmentService;

    @MockitoBean
    private RiskAssessmentMapper riskAssessmentMapper;

    @MockitoBean
    private DecisionSupportService decisionSupportService;

    @MockitoBean
    private DecisionSupportMapper decisionSupportMapper;

    @MockitoBean
    private ContractLifecycleService contractLifecycleService;

    @Test
    void getContractById_shouldReturn200() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");

        when(contractService.findById(1L))
                .thenReturn(contract);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(get("/contracts/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("NDA"));
    }

    @Test
    void getContractById_shouldReturn404() throws Exception {

        when(contractService.findById(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(get("/contracts/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Contract with id 999 not found"));
    }

    @Test
    void getContractById_shouldReturn400WhenIdIsInvalid() throws Exception {

        mockMvc.perform(get("/contracts/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'abc' for parameter 'id'"));
    }

    @Test
    void createContract_shouldReturn201() throws Exception {

        Contract contract = new Contract();
        contract.setTitle("NDA");
        contract.setContractNumber("NDA-002");

        when(contractMapper.toEntity(any(ContractRequest.class)))
                .thenReturn(contract);

        Contract savedContract = new Contract();
        savedContract.setId(2L);
        savedContract.setTitle("NDA");
        savedContract.setContractNumber("NDA-002");

        when(contractService.save(contract))
                .thenReturn(savedContract);

        ContractResponse response = new ContractResponse();
        response.setId(2L);
        response.setTitle("NDA");
        response.setContractNumber("NDA-002");

        when(contractMapper.toResponse(savedContract))
                .thenReturn(response);

        mockMvc.perform(
                        post("/contracts")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "NDA",
                                            "contractNumber": "NDA-002",
                                            "counterparty": "Microsoft",
                                            "contractType": "NDA",
                                            "contractStatus": "DRAFT",
                                            "riskLevel": "LOW",
                                            "startDate": "2026-08-10",
                                            "endDate": "2027-08-10",
                                            "governingLaw": "German Law",
                                            "contractValue": 15000,
                                            "autoRenewal": true
                                        }
                                        """)
                )
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/contracts/2"))
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.title").value("NDA"))
                .andExpect(jsonPath("$.contractNumber").value("NDA-002"));
    }

    @Test
    void createContract_shouldReturn400WhenTitleIsBlank() throws Exception {

        mockMvc.perform(
                        post("/contracts")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "contractNumber": "NDA-002",
                                            "counterparty": "Microsoft",
                                            "contractType": "NDA",
                                            "contractStatus": "DRAFT",
                                            "riskLevel": "LOW",
                                            "startDate": "2026-08-10",
                                            "endDate": "2027-08-10",
                                            "governingLaw": "German Law",
                                            "contractValue": 15000,
                                            "autoRenewal": true
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").value("title: must not be blank"));
    }

    @Test
    void createContract_shouldReturn400WhenMultipleFieldsAreInvalid() throws Exception {

        mockMvc.perform(
                        post("/contracts")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                    {
                                        "title": "",
                                        "contractNumber": "",
                                        "counterparty": "Microsoft",
                                        "contractType": "NDA",
                                        "contractStatus": "DRAFT",
                                        "startDate": "2026-08-10",
                                        "endDate": "2027-08-10",
                                        "governingLaw": "German Law",
                                        "contractValue": -100,
                                        "autoRenewal": true
                                    }
                                    """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors.length()").value(3))
                .andExpect(jsonPath("$.errors", hasItem("title: must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("contractNumber: must not be blank")))
                .andExpect(jsonPath("$.errors", hasItem("contractValue: must be greater than 0")));
    }

    @Test
    void updateContract_shouldReturn200() throws Exception {

        Contract contract = new Contract();

        when(contractMapper.toEntity(any(ContractRequest.class)))
                .thenReturn(contract);

        Contract updatedContract = new Contract();
        updatedContract.setId(1L);
        updatedContract.setTitle("Updated NDA");

        when(contractService.update(1L, contract))
                .thenReturn(updatedContract);

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("Updated NDA");

        when(contractMapper.toResponse(updatedContract))
                .thenReturn(response);

        mockMvc.perform(
                        put("/contracts/1")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "Updated NDA",
                                            "contractNumber": "NDA-002",
                                            "counterparty": "Microsoft",
                                            "contractType": "NDA",
                                            "contractStatus": "DRAFT",
                                            "riskLevel": "LOW",
                                            "startDate": "2026-08-10",
                                            "endDate": "2027-08-10",
                                            "governingLaw": "German Law",
                                            "contractValue": 15000,
                                            "autoRenewal": true
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Updated NDA"));
    }

    @Test
    void updateContract_shouldReturn404() throws Exception {

        Contract contract = new Contract();

        when(contractMapper.toEntity(any(ContractRequest.class)))
                .thenReturn(contract);

        when(contractService.update(999L, contract))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(
                        put("/contracts/999")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "Updated NDA",
                                            "contractNumber": "NDA-002",
                                            "counterparty": "Microsoft",
                                            "contractType": "NDA",
                                            "contractStatus": "DRAFT",
                                            "riskLevel": "LOW",
                                            "startDate": "2026-08-10",
                                            "endDate": "2027-08-10",
                                            "governingLaw": "German Law",
                                            "contractValue": 15000,
                                            "autoRenewal": true
                                        }
                                        """)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Contract with id 999 not found"));
    }

    @Test
    void updateContract_shouldReturn400WhenTitleIsBlank() throws Exception {

        mockMvc.perform(
                        put("/contracts/1")
                                .contentType(APPLICATION_JSON)
                                .content("""
                                        {
                                            "title": "",
                                            "contractNumber": "NDA-002",
                                            "counterparty": "Microsoft",
                                            "contractType": "NDA",
                                            "contractStatus": "DRAFT",
                                            "riskLevel": "LOW",
                                            "startDate": "2026-08-10",
                                            "endDate": "2027-08-10",
                                            "governingLaw": "German Law",
                                            "contractValue": 15000,
                                            "autoRenewal": true
                                        }
                                        """)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0]").value("title: must not be blank"));
    }

    @Test
    void deleteContract_shouldReturn204() throws Exception {

        mockMvc.perform(
                        delete("/contracts/1")
                )
                .andExpect(status().isNoContent());

        verify(contractService).delete(1L);
    }

    @Test
    void deleteContract_shouldReturn404() throws Exception {

        doThrow(new ContractNotFoundException(999L))
                .when(contractService)
                .delete(999L);

        mockMvc.perform(
                        delete("/contracts/999")
                )
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllContracts_shouldReturn200() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("NDA");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Service Agreement");

        ContractResponse response1 = new ContractResponse();
        response1.setId(1L);
        response1.setTitle("NDA");

        ContractResponse response2 = new ContractResponse();
        response2.setId(2L);
        response2.setTitle("Service Agreement");

        Page<Contract> contractPage =
                new PageImpl<>(
                        List.of(contract1, contract2),
                        PageRequest.of(0, 10),
                        25
                );

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        mockMvc.perform(
                        get("/contracts")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("NDA"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Service Agreement"));
    }

    @Test
    void getAllContracts_shouldReturnPaginationMetadata() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(11L);
        contract1.setTitle("NDA");

        Contract contract2 = new Contract();
        contract2.setId(12L);
        contract2.setTitle("Service Agreement");

        ContractResponse response1 = new ContractResponse();
        response1.setId(11L);
        response1.setTitle("NDA");

        ContractResponse response2 = new ContractResponse();
        response2.setId(12L);
        response2.setTitle("Service Agreement");

        Page<Contract> contractPage =
                new PageImpl<>(
                        List.of(contract1, contract2),
                        org.springframework.data.domain.PageRequest.of(1, 10),
                        25
                );

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        mockMvc.perform(
                        get("/contracts")
                                .param("page", "1")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(3));
    }

    @Test
    void getAllContracts_shouldFilterByStatus() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Active NDA");
        contract1.setContractStatus(ContractStatus.ACTIVE);

        ContractResponse response1 = new ContractResponse();
        response1.setId(1L);
        response1.setTitle("Active NDA");
        response1.setContractStatus(ContractStatus.ACTIVE);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1));

        when(contractService.search(
                eq(ContractStatus.ACTIVE),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "ACTIVE")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Active NDA"))
                .andExpect(jsonPath("$.content[0].contractStatus")
                        .value("ACTIVE"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenStatusIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "INVALID")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'INVALID' for parameter 'status'"));
    }

    @Test
    void getAllContracts_shouldFilterByCounterparty() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setCounterparty("Google");

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setCounterparty("Google");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                eq("google"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("counterparty", "google")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("NDA"))
                .andExpect(jsonPath("$.content[0].counterparty")
                        .value("Google"));
    }

    @Test
    void getAllContracts_shouldFilterByContractType() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Google NDA");
        contract.setContractType(ContractType.NDA);

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("Google NDA");
        response.setContractType(ContractType.NDA);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                eq(ContractType.NDA),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("type", "NDA")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Google NDA"))
                .andExpect(jsonPath("$.content[0].contractType")
                        .value("NDA"));
    }

    @Test
    void getAllContracts_shouldFilterByRiskLevel() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("High Risk NDA");
        contract.setRiskLevel(RiskLevel.HIGH);

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("High Risk NDA");
        response.setRiskLevel(RiskLevel.HIGH);

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                eq(RiskLevel.HIGH),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("riskLevel", "HIGH")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id")
                        .value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("High Risk NDA"))
                .andExpect(jsonPath("$.content[0].riskLevel")
                        .value("HIGH"));
    }

    @Test
    void getAllContracts_shouldFilterByStatusAndCounterparty() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Google NDA");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setCounterparty("Google");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Google Service Agreement");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setCounterparty("Google Cloud");

        ContractResponse response1 = new ContractResponse();
        response1.setId(1L);
        response1.setTitle("Google NDA");
        response1.setContractStatus(ContractStatus.ACTIVE);
        response1.setCounterparty("Google");

        ContractResponse response2 = new ContractResponse();
        response2.setId(2L);
        response2.setTitle("Google Service Agreement");
        response2.setContractStatus(ContractStatus.ACTIVE);
        response2.setCounterparty("Google Cloud");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract1, contract2));

        when(contractService.search(
                eq(ContractStatus.ACTIVE),
                isNull(),
                isNull(),
                eq("google"),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "ACTIVE")
                                .param("counterparty", "google")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Google NDA"))
                .andExpect(jsonPath("$.content[0].contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].counterparty")
                        .value("Google"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Google Service Agreement"))
                .andExpect(jsonPath("$.content[1].contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.content[1].counterparty")
                        .value("Google Cloud"));
    }

    @Test
    void getAllContracts_shouldSortByTitleAscending() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Alpha Service Agreement");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Microsoft License");

        Contract contract3 = new Contract();
        contract3.setId(3L);
        contract3.setTitle("Zeta NDA");

        ContractResponse response1 = new ContractResponse();
        response1.setId(1L);
        response1.setTitle("Alpha Service Agreement");

        ContractResponse response2 = new ContractResponse();
        response2.setId(2L);
        response2.setTitle("Microsoft License");

        ContractResponse response3 = new ContractResponse();
        response3.setId(3L);
        response3.setTitle("Zeta NDA");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(
                        contract1,
                        contract2,
                        contract3
                ));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        when(contractMapper.toResponse(contract3))
                .thenReturn(response3);

        mockMvc.perform(
                        get("/contracts")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "title,asc")

                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Alpha Service Agreement"))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Microsoft License"))
                .andExpect(jsonPath("$.content[2].title")
                        .value("Zeta NDA"));
    }

    @Test
    void getAllContracts_shouldSortByTitleDescending() throws Exception {

        Contract contract1 = new Contract();
        contract1.setId(1L);
        contract1.setTitle("Zeta NDA");

        Contract contract2 = new Contract();
        contract2.setId(2L);
        contract2.setTitle("Microsoft License");

        Contract contract3 = new Contract();
        contract3.setId(3L);
        contract3.setTitle("Alpha Service Agreement");

        ContractResponse response1 = new ContractResponse();
        response1.setId(1L);
        response1.setTitle("Zeta NDA");

        ContractResponse response2 = new ContractResponse();
        response2.setId(2L);
        response2.setTitle("Microsoft License");

        ContractResponse response3 = new ContractResponse();
        response3.setId(3L);
        response3.setTitle("Alpha Service Agreement");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(
                        contract1,
                        contract2,
                        contract3
                ));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        when(contractMapper.toResponse(contract3))
                .thenReturn(response3);

        mockMvc.perform(
                        get("/contracts")
                                .param("page", "0")
                                .param("size", "10")
                                .param("sort", "title,desc")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title")
                        .value("Zeta NDA"))
                .andExpect(jsonPath("$.content[1].title")
                        .value("Microsoft License"))
                .andExpect(jsonPath("$.content[2].title")
                        .value("Alpha Service Agreement"));
    }

    @Test
    void getAllContracts_shouldFilterByMinValue() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("High Value NDA");
        contract.setContractValue(new BigDecimal("50000"));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("High Value NDA");
        response.setContractValue(new BigDecimal("50000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(new BigDecimal("10000")),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("minValue", "10000")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("High Value NDA"))
                .andExpect(jsonPath("$.content[0].contractValue")
                        .value(50000));
    }

    @Test
    void getAllContracts_shouldFilterByMaxValue() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Small NDA");
        contract.setContractValue(new BigDecimal("5000"));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("Small NDA");
        response.setContractValue(new BigDecimal("5000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(new BigDecimal("10000")),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("maxValue", "10000")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Small NDA"))
                .andExpect(jsonPath("$.content[0].contractValue")
                        .value(5000));
    }

    @Test
    void getAllContracts_shouldFilterByMinAndMaxValue() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Medium Value NDA");
        contract.setContractValue(new BigDecimal("25000"));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("Medium Value NDA");
        response.setContractValue(new BigDecimal("25000"));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(new BigDecimal("10000")),
                eq(new BigDecimal("50000")),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("minValue", "10000")
                                .param("maxValue", "50000")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Medium Value NDA"))
                .andExpect(jsonPath("$.content[0].contractValue")
                        .value(25000));
    }

    @Test
    void getAllContracts_shouldFilterByStartDateFrom() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 3, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setStartDate(LocalDate.of(2026, 3, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2026, 1, 1)),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-01-01")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].startDate")
                        .value("2026-03-01"));
    }

    @Test
    void getAllContracts_shouldFilterByStartDateTo() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 3, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setStartDate(LocalDate.of(2026, 3, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2026, 12, 31)),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateTo", "2026-12-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].startDate")
                        .value("2026-03-01"));
    }

    @Test
    void getAllContracts_shouldFilterByEndDateFrom() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2027, 1, 1)),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2027-01-01")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].endDate")
                        .value("2027-06-01"));
    }

    @Test
    void getAllContracts_shouldFilterByEndDateTo() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2027, 12, 31)),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateTo", "2027-12-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].endDate")
                        .value("2027-06-01"));
    }

    @Test
    void getAllContracts_shouldFilterByStartDateRange() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setStartDate(LocalDate.of(2026, 3, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setStartDate(LocalDate.of(2026, 3, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2026, 1, 1)),
                eq(LocalDate.of(2026, 3, 31)),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-01-01")
                                .param("startDateTo", "2026-03-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].startDate")
                        .value("2026-03-01"));
    }

    @Test
    void getAllContracts_shouldFilterByEndDateFromAndTo() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");
        response.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2027, 1, 1)),
                eq(LocalDate.of(2027, 12, 31)),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2027-01-01")
                                .param("endDateTo", "2027-12-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("NDA"))
                .andExpect(jsonPath("$.content[0].endDate")
                        .value("2027-06-01"));
    }

    @Test
    void getAllContracts_shouldFilterByStatusAndEndDateRange() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("Active NDA");
        contract.setContractStatus(ContractStatus.ACTIVE);
        contract.setEndDate(LocalDate.of(2027, 6, 1));

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("Active NDA");
        response.setContractStatus(ContractStatus.ACTIVE);
        response.setEndDate(LocalDate.of(2027, 6, 1));

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                eq(ContractStatus.ACTIVE),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                eq(LocalDate.of(2027, 1, 1)),
                eq(LocalDate.of(2027, 12, 31)),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "ACTIVE")
                                .param("endDateFrom", "2027-01-01")
                                .param("endDateTo", "2027-12-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Active NDA"))
                .andExpect(jsonPath("$.content[0].contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].endDate")
                        .value("2027-06-01"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenStartDateFromIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "invalid-date")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllContracts_shouldReturn400WhenStartDateToIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateTo", "invalid-date")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllContracts_shouldReturn400WhenEndDateFromIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "invalid-date")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllContracts_shouldReturn400WhenEndDateToIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateTo", "invalid-date")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllContracts_shouldReturn400WhenStartDateFromIsAfterStartDateTo() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-12-31")
                                .param("startDateTo", "2026-01-01")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("startDateFrom must be before or equal to startDateTo"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenEndDateFromIsAfterEndDateTo() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2026-12-31")
                                .param("endDateTo", "2026-01-01")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("endDateFrom must be before or equal to endDateTo"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenMinValueIsGreaterThanMaxValue()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("minValue", "50000")
                                .param("maxValue", "10000")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("minValue must be less than or equal to maxValue"));
    }

    @Test
    void getAllContracts_shouldPassPageableWithPageSizeAndSorting() throws Exception {

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("NDA");

        ContractResponse response = new ContractResponse();
        response.setId(1L);
        response.setTitle("NDA");

        Page<Contract> contractPage =
                new PageImpl<>(List.of(contract));

        when(contractService.search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)))
                .thenReturn(contractPage);

        when(contractMapper.toResponse(contract))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts")
                                .param("page", "2")
                                .param("size", "5")
                                .param("sort", "title,desc")
                )
                .andExpect(status().isOk());

        verify(contractService).search(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                argThat(pageable ->
                        pageable.getPageNumber() == 2
                                && pageable.getPageSize() == 5
                                && pageable.getSort().getOrderFor("title") != null
                                && pageable.getSort()
                                .getOrderFor("title")
                                .getDirection()
                                .isDescending()
                )
        );
    }

    @Test
    void getAllContracts_shouldReturn400WhenStartDateRangeIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-10-01")
                                .param("startDateTo", "2026-09-01")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("startDateFrom must be before or equal to startDateTo"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenValueRangeIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("minValue", "100000")
                                .param("maxValue", "50000")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("minValue must be less than or equal to maxValue"));
    }

    @Test
    void getAllContracts_shouldReturn400WhenEndDateRangeIsInvalid() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2026-10-01")
                                .param("endDateTo", "2026-09-01")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("endDateFrom must be before or equal to endDateTo"));
    }

    @Test
    void getRiskAssessment_shouldReturnRiskAssessment() throws Exception {

        RiskFactor factor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds € 100.000",
                "High contract value increases potential financial exposure"
        );

        List<RiskFactor> factors = List.of(factor);

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.HIGH,
                factors
        );

        when(riskAssessmentService.assess(any(Contract.class)))
                .thenReturn(assessment);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("High Value Contract");

        when(contractService.findById(1L))
                .thenReturn(contract);

        RiskFactorResponse factorResponse = new RiskFactorResponse();
        factorResponse.setCode("HIGH_CONTRACT_VALUE");
        factorResponse.setPoints(30);
        factorResponse.setReason("Contract value exceeds € 100.000");
        factorResponse.setRiskExplanation(
                "High contract value increases potential financial exposure"
        );

        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setScore(30);
        response.setRiskLevel(RiskLevel.HIGH);
        response.setFactors(List.of(factorResponse));

        when(riskAssessmentMapper.toResponse(assessment))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts/1/risk-assessment")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.score").value(30))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.factors[0].code").value("HIGH_CONTRACT_VALUE"))
                .andExpect(jsonPath("$.factors[0].points").value(30))
                .andExpect(jsonPath("$.factors[0].reason")
                        .value("Contract value exceeds € 100.000"))
                .andExpect(jsonPath("$.factors[0].riskExplanation")
                .value("High contract value increases potential financial exposure"));

        verify(contractService).findById(1L);
        verify(riskAssessmentService).assess(contract);
        verify(riskAssessmentMapper).toResponse(assessment);
    }

    @Test
    void getRiskAssessment_shouldReturn404() throws Exception {

        when(contractService.findById(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(get("/contracts/999/risk-assessment"))
                .andExpect(status().isNotFound());

        verify(contractService).findById(999L);
        verify(riskAssessmentService, never()).assess(any(Contract.class));
    }

    @Test
    void getDecisionSupport_shouldReturnDecisionSupport() throws Exception {

        RiskFactor factor = new RiskFactor(
                "HIGH_CONTRACT_VALUE",
                30,
                "Contract value exceeds € 100.000",
                "High contract value increases potential financial exposure"
        );

        List<RiskFactor> factors = List.of(factor);

        RiskAssessment assessment = new RiskAssessment(
                30,
                RiskLevel.HIGH,
                factors
        );

        when(riskAssessmentService.assess(any(Contract.class)))
                .thenReturn(assessment);

        Contract contract = new Contract();
        contract.setId(1L);
        contract.setTitle("High Value Contract");

        when(contractService.findById(1L))
                .thenReturn(contract);

        DecisionSupport decisionSupport = new DecisionSupport(
                "Finance review required",
                "Contract value exceeds € 100.000",
                List.of(
                        ApprovalRole.LEGAL,
                        ApprovalRole.FINANCE,
                        ApprovalRole.MANAGEMENT
                ),
                DecisionPriority.MEDIUM,
                "Route contract for Legal, Finance, Management approval"
        );

        when(decisionSupportService.generate(assessment))
                .thenReturn(decisionSupport);

        DecisionSupportResponse response = new DecisionSupportResponse();
        response.setRecommendation("Finance review required");
        response.setRationale("Contract value exceeds € 100.000");
        response.setRequiredApprovals(List.of(
                ApprovalRole.LEGAL,
                ApprovalRole.FINANCE,
                ApprovalRole.MANAGEMENT
        ));
        response.setPriority(DecisionPriority.MEDIUM);
        response.setNextAction("Route contract for Legal, Finance, Management approval");

        when(decisionSupportMapper.toDecisionSupportResponse(decisionSupport))
                .thenReturn(response);

        mockMvc.perform(
                        get("/contracts/1/decision-support")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendation").value("Finance review required"))
                .andExpect(jsonPath("$.rationale").value("Contract value exceeds € 100.000"))
                .andExpect(jsonPath("$.requiredApprovals[0]").value("LEGAL"))
                .andExpect(jsonPath("$.requiredApprovals[1]").value("FINANCE"))
                .andExpect(jsonPath("$.requiredApprovals[2]").value("MANAGEMENT"))
                .andExpect(jsonPath("$.priority").value("MEDIUM"))
                .andExpect(jsonPath("$.nextAction").value("Route contract for Legal, Finance, Management approval"));

        verify(contractService).findById(1L);
        verify(riskAssessmentService).assess(contract);
        verify(decisionSupportService).generate(assessment);
        verify(decisionSupportMapper).toDecisionSupportResponse(decisionSupport);
    }

    @Test
    void getDecisionSupport_shouldReturn404() throws Exception {

        when(contractService.findById(999L))
                .thenThrow(new ContractNotFoundException(999L));

        mockMvc.perform(get("/contracts/999/decision-support"))
                .andExpect(status().isNotFound());

        verify(contractService).findById(999L);
        verify(riskAssessmentService, never())
                .assess(any(Contract.class));
        verify(decisionSupportService, never())
                .generate(any(RiskAssessment.class));
    }

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