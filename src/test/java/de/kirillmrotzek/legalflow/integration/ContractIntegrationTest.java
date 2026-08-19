package de.kirillmrotzek.legalflow.integration;

import de.kirillmrotzek.legalflow.enums.ContractStatus;
import de.kirillmrotzek.legalflow.enums.ContractType;
import de.kirillmrotzek.legalflow.enums.RiskLevel;
import de.kirillmrotzek.legalflow.model.Contract;
import de.kirillmrotzek.legalflow.repository.ContractRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.annotation.DirtiesContext;
import static org.hamcrest.Matchers.hasItem;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ContractIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContractRepository contractRepository;

    @Test
    void createContract_shouldPersistAndReturnContract() throws Exception {

        String requestJson = """
                {
                    "title": "Integration NDA",
                    "contractNumber": "INT-NDA-001",
                    "counterparty": "Microsoft",
                    "contractType": "NDA",
                    "contractStatus": "DRAFT",
                    "riskLevel": "LOW",
                    "startDate": "2026-08-20",
                    "endDate": "2027-08-20",
                    "governingLaw": "German Law",
                    "contractValue": 15000,
                    "autoRenewal": true
                }
                """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.title")
                        .value("Integration NDA"))
                .andExpect(jsonPath("$.contractNumber")
                        .value("INT-NDA-001"))
                .andExpect(jsonPath("$.counterparty")
                        .value("Microsoft"));

        Contract savedContract =
                contractRepository.findAll()
                        .stream()
                        .filter(contract ->
                                "INT-NDA-001".equals(
                                        contract.getContractNumber()
                                ))
                        .findFirst()
                        .orElseThrow();

        assertNotNull(savedContract.getId());

        assertEquals(
                "Integration NDA",
                savedContract.getTitle()
        );

        assertEquals(
                "Microsoft",
                savedContract.getCounterparty()
        );
    }

    @Test
    void createContract_thenGetById_shouldReturnPersistedContract() throws Exception {

        String requestJson = """
            {
                "title": "Integration Service Agreement",
                "contractNumber": "INT-SERVICE-001",
                "counterparty": "Siemens",
                "contractType": "SERVICE",
                "contractStatus": "ACTIVE",
                "riskLevel": "MEDIUM",
                "startDate": "2026-08-20",
                "endDate": "2027-08-20",
                "governingLaw": "German Law",
                "contractValue": 50000,
                "autoRenewal": false
            }
            """;

        String location =
                mockMvc.perform(
                                post("/contracts")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(requestJson)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        assertNotNull(location);

        mockMvc.perform(
                        get(location)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Integration Service Agreement"))
                .andExpect(jsonPath("$.contractNumber")
                        .value("INT-SERVICE-001"))
                .andExpect(jsonPath("$.counterparty")
                        .value("Siemens"))
                .andExpect(jsonPath("$.contractType")
                        .value("SERVICE"))
                .andExpect(jsonPath("$.contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.riskLevel")
                        .value("MEDIUM"));
    }

    @Test
    void getContracts_shouldFilterByStatusTypeAndMinValue() throws Exception {

        Contract contract1 = new Contract();
        contract1.setContractNumber("SRV-001");
        contract1.setTitle("Important Service Agreement");
        contract1.setCounterparty("Acme GmbH");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setContractType(ContractType.SERVICE);
        contract1.setRiskLevel(RiskLevel.MEDIUM);
        contract1.setContractValue(new BigDecimal("25000"));
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2027, 1, 1));

        Contract contract2 = new Contract();
        contract2.setContractNumber("SRV-002");
        contract2.setTitle("Small Service Agreement");
        contract2.setCounterparty("Beta GmbH");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setContractType(ContractType.SERVICE);
        contract2.setRiskLevel(RiskLevel.LOW);
        contract2.setContractValue(new BigDecimal("5000"));
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2027, 2, 1));

        Contract contract3 = new Contract();
        contract3.setContractNumber("NDA-001");
        contract3.setTitle("Confidentiality Agreement");
        contract3.setCounterparty("Gamma GmbH");
        contract3.setContractStatus(ContractStatus.ACTIVE);
        contract3.setContractType(ContractType.NDA);
        contract3.setRiskLevel(RiskLevel.HIGH);
        contract3.setContractValue(new BigDecimal("30000"));
        contract3.setStartDate(LocalDate.of(2026, 3, 1));
        contract3.setEndDate(LocalDate.of(2027, 3, 1));

        contractRepository.saveAll(
                List.of(contract1, contract2, contract3)
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "ACTIVE")
                                .param("type", "SERVICE")
                                .param("minValue", "10000")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].contractNumber")
                        .value("SRV-001"))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Important Service Agreement"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_shouldFilterByStartDateRange() throws Exception {

        Contract contract1 = new Contract();
        contract1.setContractNumber("DATE-001");
        contract1.setTitle("Contract Before Range");
        contract1.setCounterparty("Company A");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setContractType(ContractType.SERVICE);
        contract1.setRiskLevel(RiskLevel.LOW);
        contract1.setContractValue(new BigDecimal("10000"));
        contract1.setStartDate(LocalDate.of(2026, 1, 15));
        contract1.setEndDate(LocalDate.of(2027, 1, 15));

        Contract contract2 = new Contract();
        contract2.setContractNumber("DATE-002");
        contract2.setTitle("Contract In Range");
        contract2.setCounterparty("Company B");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setContractType(ContractType.SERVICE);
        contract2.setRiskLevel(RiskLevel.MEDIUM);
        contract2.setContractValue(new BigDecimal("20000"));
        contract2.setStartDate(LocalDate.of(2026, 6, 15));
        contract2.setEndDate(LocalDate.of(2027, 6, 15));

        Contract contract3 = new Contract();
        contract3.setContractNumber("DATE-003");
        contract3.setTitle("Contract After Range");
        contract3.setCounterparty("Company C");
        contract3.setContractStatus(ContractStatus.ACTIVE);
        contract3.setContractType(ContractType.SERVICE);
        contract3.setRiskLevel(RiskLevel.HIGH);
        contract3.setContractValue(new BigDecimal("30000"));
        contract3.setStartDate(LocalDate.of(2026, 11, 15));
        contract3.setEndDate(LocalDate.of(2027, 11, 15));

        contractRepository.saveAll(
                List.of(contract1, contract2, contract3)
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-05-01")
                                .param("startDateTo", "2026-08-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].contractNumber")
                        .value("DATE-002"))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Contract In Range"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_shouldFilterByEndDateRange() throws Exception {

        Contract contract1 = new Contract();
        contract1.setContractNumber("END-001");
        contract1.setTitle("Contract Before End Date Range");
        contract1.setCounterparty("Company A");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setContractType(ContractType.SERVICE);
        contract1.setRiskLevel(RiskLevel.LOW);
        contract1.setContractValue(new BigDecimal("10000"));
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2027, 1, 15));

        Contract contract2 = new Contract();
        contract2.setContractNumber("END-002");
        contract2.setTitle("Contract In End Date Range");
        contract2.setCounterparty("Company B");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setContractType(ContractType.SERVICE);
        contract2.setRiskLevel(RiskLevel.MEDIUM);
        contract2.setContractValue(new BigDecimal("20000"));
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2027, 6, 15));

        Contract contract3 = new Contract();
        contract3.setContractNumber("END-003");
        contract3.setTitle("Contract After End Date Range");
        contract3.setCounterparty("Company C");
        contract3.setContractStatus(ContractStatus.ACTIVE);
        contract3.setContractType(ContractType.SERVICE);
        contract3.setRiskLevel(RiskLevel.HIGH);
        contract3.setContractValue(new BigDecimal("30000"));
        contract3.setStartDate(LocalDate.of(2026, 3, 1));
        contract3.setEndDate(LocalDate.of(2027, 11, 15));

        contractRepository.saveAll(
                List.of(contract1, contract2, contract3)
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2027-05-01")
                                .param("endDateTo", "2027-08-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].contractNumber")
                        .value("END-002"))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Contract In End Date Range"))
                .andExpect(jsonPath("$.content[0].endDate")
                        .value("2027-06-15"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_withInvalidStartDateRange_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("startDateFrom", "2026-08-20")
                                .param("startDateTo", "2026-08-19")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("startDateFrom must be before or equal to startDateTo"));
    }

    @Test
    void getContracts_withInvalidEndDateRange_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("endDateFrom", "2027-08-20")
                                .param("endDateTo", "2027-08-19")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("endDateFrom must be before or equal to endDateTo"));
    }

    @Test
    void getContracts_withInvalidValueRange_shouldReturnBadRequest()
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
    void getContracts_shouldFilterByRiskLevel() throws Exception {

        Contract lowRisk = new Contract();
        lowRisk.setContractNumber("RISK-001");
        lowRisk.setTitle("Low Risk Contract");
        lowRisk.setCounterparty("Company A");
        lowRisk.setContractStatus(ContractStatus.ACTIVE);
        lowRisk.setContractType(ContractType.SERVICE);
        lowRisk.setRiskLevel(RiskLevel.LOW);
        lowRisk.setContractValue(new BigDecimal("10000"));
        lowRisk.setStartDate(LocalDate.of(2026, 1, 1));
        lowRisk.setEndDate(LocalDate.of(2027, 1, 1));

        Contract mediumRisk = new Contract();
        mediumRisk.setContractNumber("RISK-002");
        mediumRisk.setTitle("Medium Risk Contract");
        mediumRisk.setCounterparty("Company B");
        mediumRisk.setContractStatus(ContractStatus.ACTIVE);
        mediumRisk.setContractType(ContractType.SERVICE);
        mediumRisk.setRiskLevel(RiskLevel.MEDIUM);
        mediumRisk.setContractValue(new BigDecimal("20000"));
        mediumRisk.setStartDate(LocalDate.of(2026, 2, 1));
        mediumRisk.setEndDate(LocalDate.of(2027, 2, 1));

        Contract highRisk = new Contract();
        highRisk.setContractNumber("RISK-003");
        highRisk.setTitle("High Risk Contract");
        highRisk.setCounterparty("Company C");
        highRisk.setContractStatus(ContractStatus.ACTIVE);
        highRisk.setContractType(ContractType.SERVICE);
        highRisk.setRiskLevel(RiskLevel.HIGH);
        highRisk.setContractValue(new BigDecimal("30000"));
        highRisk.setStartDate(LocalDate.of(2026, 3, 1));
        highRisk.setEndDate(LocalDate.of(2027, 3, 1));

        contractRepository.saveAll(
                List.of(lowRisk, mediumRisk, highRisk)
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("riskLevel", "HIGH")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].contractNumber")
                        .value("RISK-003"))
                .andExpect(jsonPath("$.content[0].title")
                        .value("High Risk Contract"))
                .andExpect(jsonPath("$.content[0].riskLevel")
                        .value("HIGH"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_shouldFilterByCounterparty() throws Exception {

        Contract contract1 = new Contract();
        contract1.setContractNumber("CP-001");
        contract1.setTitle("Microsoft Service Agreement");
        contract1.setCounterparty("Microsoft");
        contract1.setContractStatus(ContractStatus.ACTIVE);
        contract1.setContractType(ContractType.SERVICE);
        contract1.setRiskLevel(RiskLevel.MEDIUM);
        contract1.setContractValue(new BigDecimal("25000"));
        contract1.setStartDate(LocalDate.of(2026, 1, 1));
        contract1.setEndDate(LocalDate.of(2027, 1, 1));

        Contract contract2 = new Contract();
        contract2.setContractNumber("CP-002");
        contract2.setTitle("Siemens Supply Agreement");
        contract2.setCounterparty("Siemens");
        contract2.setContractStatus(ContractStatus.ACTIVE);
        contract2.setContractType(ContractType.SUPPLIER);
        contract2.setRiskLevel(RiskLevel.LOW);
        contract2.setContractValue(new BigDecimal("15000"));
        contract2.setStartDate(LocalDate.of(2026, 2, 1));
        contract2.setEndDate(LocalDate.of(2027, 2, 1));

        Contract contract3 = new Contract();
        contract3.setContractNumber("CP-003");
        contract3.setTitle("Microsoft NDA");
        contract3.setCounterparty("Microsoft");
        contract3.setContractStatus(ContractStatus.DRAFT);
        contract3.setContractType(ContractType.NDA);
        contract3.setRiskLevel(RiskLevel.HIGH);
        contract3.setContractValue(new BigDecimal("5000"));
        contract3.setStartDate(LocalDate.of(2026, 3, 1));
        contract3.setEndDate(LocalDate.of(2027, 3, 1));

        contractRepository.saveAll(
                List.of(contract1, contract2, contract3)
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("counterparty", "Microsoft")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].counterparty")
                        .value("Microsoft"))
                .andExpect(jsonPath("$.content[1].counterparty")
                        .value("Microsoft"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_shouldApplyMultipleFiltersTogether() throws Exception {

        Contract matchingContract = new Contract();
        matchingContract.setContractNumber("COMBO-001");
        matchingContract.setTitle("Matching Contract");
        matchingContract.setCounterparty("Company A");
        matchingContract.setContractStatus(ContractStatus.ACTIVE);
        matchingContract.setContractType(ContractType.SERVICE);
        matchingContract.setRiskLevel(RiskLevel.HIGH);
        matchingContract.setContractValue(new BigDecimal("50000"));
        matchingContract.setStartDate(LocalDate.of(2026, 6, 1));
        matchingContract.setEndDate(LocalDate.of(2027, 6, 30));

        Contract wrongStatus = new Contract();
        wrongStatus.setContractNumber("COMBO-002");
        wrongStatus.setTitle("Wrong Status");
        wrongStatus.setCounterparty("Company B");
        wrongStatus.setContractStatus(ContractStatus.DRAFT);
        wrongStatus.setContractType(ContractType.SERVICE);
        wrongStatus.setRiskLevel(RiskLevel.HIGH);
        wrongStatus.setContractValue(new BigDecimal("50000"));
        wrongStatus.setStartDate(LocalDate.of(2026, 6, 1));
        wrongStatus.setEndDate(LocalDate.of(2027, 6, 30));

        Contract wrongRisk = new Contract();
        wrongRisk.setContractNumber("COMBO-003");
        wrongRisk.setTitle("Wrong Risk");
        wrongRisk.setCounterparty("Company C");
        wrongRisk.setContractStatus(ContractStatus.ACTIVE);
        wrongRisk.setContractType(ContractType.SERVICE);
        wrongRisk.setRiskLevel(RiskLevel.MEDIUM);
        wrongRisk.setContractValue(new BigDecimal("50000"));
        wrongRisk.setStartDate(LocalDate.of(2026, 6, 1));
        wrongRisk.setEndDate(LocalDate.of(2027, 6, 30));

        Contract wrongValue = new Contract();
        wrongValue.setContractNumber("COMBO-004");
        wrongValue.setTitle("Wrong Value");
        wrongValue.setCounterparty("Company D");
        wrongValue.setContractStatus(ContractStatus.ACTIVE);
        wrongValue.setContractType(ContractType.SERVICE);
        wrongValue.setRiskLevel(RiskLevel.HIGH);
        wrongValue.setContractValue(new BigDecimal("5000"));
        wrongValue.setStartDate(LocalDate.of(2026, 6, 1));
        wrongValue.setEndDate(LocalDate.of(2027, 6, 30));

        Contract wrongStartDate = new Contract();
        wrongStartDate.setContractNumber("COMBO-005");
        wrongStartDate.setTitle("Wrong Start Date");
        wrongStartDate.setCounterparty("Company E");
        wrongStartDate.setContractStatus(ContractStatus.ACTIVE);
        wrongStartDate.setContractType(ContractType.SERVICE);
        wrongStartDate.setRiskLevel(RiskLevel.HIGH);
        wrongStartDate.setContractValue(new BigDecimal("50000"));
        wrongStartDate.setStartDate(LocalDate.of(2025, 6, 1));
        wrongStartDate.setEndDate(LocalDate.of(2027, 6, 30));

        Contract wrongEndDate = new Contract();
        wrongEndDate.setContractNumber("COMBO-006");
        wrongEndDate.setTitle("Wrong End Date");
        wrongEndDate.setCounterparty("Company F");
        wrongEndDate.setContractStatus(ContractStatus.ACTIVE);
        wrongEndDate.setContractType(ContractType.SERVICE);
        wrongEndDate.setRiskLevel(RiskLevel.HIGH);
        wrongEndDate.setContractValue(new BigDecimal("50000"));
        wrongEndDate.setStartDate(LocalDate.of(2026, 6, 1));
        wrongEndDate.setEndDate(LocalDate.of(2028, 6, 30));

        contractRepository.saveAll(
                List.of(
                        matchingContract,
                        wrongStatus,
                        wrongRisk,
                        wrongValue,
                        wrongStartDate,
                        wrongEndDate
                )
        );

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "ACTIVE")
                                .param("type", "SERVICE")
                                .param("riskLevel", "HIGH")
                                .param("minValue", "10000")
                                .param("startDateFrom", "2026-01-01")
                                .param("startDateTo", "2026-12-31")
                                .param("endDateFrom", "2027-01-01")
                                .param("endDateTo", "2027-12-31")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].contractNumber")
                        .value("COMBO-001"))
                .andExpect(jsonPath("$.content[0].title")
                        .value("Matching Contract"))
                .andExpect(jsonPath("$.content[0].contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.content[0].contractType")
                        .value("SERVICE"))
                .andExpect(jsonPath("$.content[0].riskLevel")
                        .value("HIGH"))
                .andExpect(jsonPath("$.content[0].contractValue")
                        .value(50000.00))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getContracts_withInvalidRiskLevel_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("riskLevel", "VERY_HIGH")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'VERY_HIGH' for parameter 'riskLevel'"));
    }

    @Test
    void getContracts_withInvalidStatus_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("status", "UNKNOWN")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'UNKNOWN' for parameter 'status'"));
    }

    @Test
    void getContracts_withInvalidMinValue_shouldReturnBadRequest()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("minValue", "abc")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("Invalid value 'abc' for parameter 'minValue'"));
    }

    @Test
    void getContractById_whenContractDoesNotExist_shouldReturnNotFound()
            throws Exception {

        mockMvc.perform(
                        get("/contracts/999999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Contract with id 999999 not found"));
    }

    @Test
    void updateContract_shouldUpdateAndReturnContract() throws Exception {

        String createJson = """
            {
                "title": "Original Contract",
                "contractNumber": "UPDATE-001",
                "counterparty": "Original Company",
                "contractType": "SERVICE",
                "contractStatus": "DRAFT",
                "riskLevel": "LOW",
                "startDate": "2026-08-20",
                "endDate": "2027-08-20",
                "governingLaw": "German Law",
                "contractValue": 10000,
                "autoRenewal": false
            }
            """;

        String location =
                mockMvc.perform(
                                post("/contracts")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        assertNotNull(location);

        String updateJson = """
            {
                "title": "Updated Contract",
                "contractNumber": "UPDATE-001",
                "counterparty": "Updated Company",
                "contractType": "SERVICE",
                "contractStatus": "ACTIVE",
                "riskLevel": "HIGH",
                "startDate": "2026-09-01",
                "endDate": "2027-09-01",
                "governingLaw": "German Law",
                "contractValue": 25000,
                "autoRenewal": true
            }
            """;

        mockMvc.perform(
                                put(location)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title")
                        .value("Updated Contract"))
                .andExpect(jsonPath("$.contractNumber")
                        .value("UPDATE-001"))
                .andExpect(jsonPath("$.counterparty")
                        .value("Updated Company"))
                .andExpect(jsonPath("$.contractStatus")
                        .value("ACTIVE"))
                .andExpect(jsonPath("$.riskLevel")
                        .value("HIGH"))
                .andExpect(jsonPath("$.contractValue")
                        .value(25000))
                .andExpect(jsonPath("$.autoRenewal")
                        .value(true));
    }

    @Test
    void updateContract_whenContractDoesNotExist_shouldReturnNotFound()
            throws Exception {

        String updateJson = """
            {
                "title": "Updated Contract",
                "contractNumber": "UPDATE-404",
                "counterparty": "Company",
                "contractType": "SERVICE",
                "contractStatus": "ACTIVE",
                "riskLevel": "MEDIUM",
                "startDate": "2026-08-20",
                "endDate": "2027-08-20",
                "governingLaw": "German Law",
                "contractValue": 10000,
                "autoRenewal": false
            }
            """;

        mockMvc.perform(
                                put("/contracts/999999")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updateJson)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Contract with id 999999 not found"));
    }

    @Test
    void deleteContract_shouldDeleteContract() throws Exception {

        String createJson = """
            {
                "title": "Contract To Delete",
                "contractNumber": "DELETE-001",
                "counterparty": "Delete Company",
                "contractType": "NDA",
                "contractStatus": "DRAFT",
                "riskLevel": "LOW",
                "startDate": "2026-08-20",
                "endDate": "2027-08-20",
                "governingLaw": "German Law",
                "contractValue": 5000,
                "autoRenewal": false
            }
            """;

        String location =
                mockMvc.perform(
                                post("/contracts")
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .content(createJson)
                        )
                        .andExpect(status().isCreated())
                        .andExpect(header().exists("Location"))
                        .andReturn()
                        .getResponse()
                        .getHeader("Location");

        assertNotNull(location);

        mockMvc.perform(
                        delete(location)
                )
                .andExpect(status().isNoContent());

        mockMvc.perform(
                        get(location)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Contract with id " +
                                location.substring(location.lastIndexOf("/") + 1) +
                                " not found"));
    }

    @Test
    void deleteContract_whenContractDoesNotExist_shouldReturnNotFound()
            throws Exception {

        mockMvc.perform(
                        delete("/contracts/999999")
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message")
                        .value("Contract with id 999999 not found"));
    }

    @Test
    void createContract_withBlankTitle_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
        {
            "title": "",
            "contractNumber": "VALID-001",
            "counterparty": "Microsoft",
            "contractType": "NDA",
            "contractStatus": "DRAFT",
            "riskLevel": "LOW",
            "startDate": "2026-08-20",
            "endDate": "2027-08-20",
            "governingLaw": "German Law",
            "contractValue": 15000,
            "autoRenewal": true
        }
        """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors")
                        .value(hasItem("title: must not be blank")));
    }

    @Test
    void createContract_withMissingContractType_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
        {
            "title": "Validation Test",
            "contractNumber": "VALID-002",
            "counterparty": "Microsoft",
            "contractStatus": "DRAFT",
            "riskLevel": "LOW",
            "startDate": "2026-08-20",
            "endDate": "2027-08-20",
            "governingLaw": "German Law",
            "contractValue": 15000,
            "autoRenewal": true
        }
        """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors")
                        .value(hasItem(
                                "contractType: must not be null"
                        )));
    }

    @Test
    void createContract_withNegativeContractValue_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
        {
            "title": "Validation Test",
            "contractNumber": "VALID-003",
            "counterparty": "Microsoft",
            "contractType": "NDA",
            "contractStatus": "DRAFT",
            "riskLevel": "LOW",
            "startDate": "2026-08-20",
            "endDate": "2027-08-20",
            "governingLaw": "German Law",
            "contractValue": -100,
            "autoRenewal": true
        }
        """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors")
                        .value(hasItem(
                                "contractValue: must be greater than 0"
                        )));
    }

    @Test
    void createContract_withMissingAutoRenewal_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
        {
            "title": "Validation Test",
            "contractNumber": "VALID-004",
            "counterparty": "Microsoft",
            "contractType": "NDA",
            "contractStatus": "DRAFT",
            "riskLevel": "LOW",
            "startDate": "2026-08-20",
            "endDate": "2027-08-20",
            "governingLaw": "German Law",
            "contractValue": 15000
        }
        """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors")
                        .value(hasItem(
                                "autoRenewal: must not be null"
                        )));
    }

    @Test
    void createContract_withBlankCounterparty_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
        {
            "title": "Validation Test",
            "contractNumber": "VALID-005",
            "counterparty": "   ",
            "contractType": "NDA",
            "contractStatus": "DRAFT",
            "riskLevel": "LOW",
            "startDate": "2026-08-20",
            "endDate": "2027-08-20",
            "governingLaw": "German Law",
            "contractValue": 15000,
            "autoRenewal": true
        }
        """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors")
                        .value(hasItem(
                                "counterparty: must not be blank"
                        )));
    }

    @Test
    void createContract_withBlankContractNumber_shouldReturnBadRequest()
            throws Exception {

        String requestJson = """
    {
        "title": "Validation Test",
        "contractNumber": "",
        "counterparty": "Microsoft",
        "contractType": "NDA",
        "contractStatus": "DRAFT",
        "riskLevel": "LOW",
        "startDate": "2026-08-20",
        "endDate": "2027-08-20",
        "governingLaw": "German Law",
        "contractValue": 15000,
        "autoRenewal": true
    }
    """;

        mockMvc.perform(
                        post("/contracts")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors")
                        .value(hasItem(
                                "contractNumber: must not be blank"
                        )));
    }

    @Test
    void getContracts_whenNoContractsMatch_shouldReturnEmptyPage()
            throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .param("counterparty", "NonExistingCompany")
                                .param("page", "0")
                                .param("size", "10")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10));
    }


}
