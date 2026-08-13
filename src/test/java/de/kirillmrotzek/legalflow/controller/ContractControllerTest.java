package de.kirillmrotzek.legalflow.controller;

import de.kirillmrotzek.legalflow.mapper.ContractMapper;
import de.kirillmrotzek.legalflow.service.ContractService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import de.kirillmrotzek.legalflow.dto.ContractResponse;
import de.kirillmrotzek.legalflow.model.Contract;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import de.kirillmrotzek.legalflow.exception.ContractNotFoundException;
import de.kirillmrotzek.legalflow.dto.ContractRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import java.util.List;

@WebMvcTest(ContractController.class)
class ContractControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ContractService contractService;

    @MockitoBean
    private ContractMapper contractMapper;

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
                .andExpect(status().isNotFound());
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
                .andExpect(status().isNotFound());
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

        when(contractService.findAll())
                .thenReturn(List.of(contract1, contract2));

        when(contractMapper.toResponse(contract1))
                .thenReturn(response1);

        when(contractMapper.toResponse(contract2))
                .thenReturn(response2);

        mockMvc.perform(get("/contracts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("NDA"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].title").value("Service Agreement"));
    }
}
