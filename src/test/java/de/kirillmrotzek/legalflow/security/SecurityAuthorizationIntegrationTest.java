package de.kirillmrotzek.legalflow.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
class SecurityAuthorizationIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private long createContractAsLegal() throws Exception {

        String contractNumber = "SEC-REV-" + System.nanoTime();

        String response = mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                                {
                                  "title": "Security Review Contract",
                                  "contractNumber": "%s",
                                  "counterparty": "Microsoft",
                                  "contractType": "NDA",
                                  "startDate": "2026-08-20",
                                  "endDate": "2027-08-20",
                                  "governingLaw": "German Law",
                                  "contractValue": 15000,
                                  "autoRenewal": true
                                }
                                """.formatted(contractNumber))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return new ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();
    }

    private long createContractInReviewAsLegal() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/start")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().is2xxSuccessful());

        return contractId;
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    void unauthenticatedUser_shouldReceive401() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void legalUser_shouldReadContracts() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void financeUser_shouldReadContracts() throws Exception {

        mockMvc.perform(
                        get("/contracts")
                                .with(httpBasic("finance", "password"))
                )
                .andExpect(status().isOk());
    }

    @Test
    void financeUser_shouldNotCreateContract() throws Exception {

        mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("finance", "password"))
                                .contentType("application/json")
                                .content("""
                                {
                                    "title": "Security Test Contract",
                                    "contractNumber": "SEC-001",
                                    "counterparty": "Test Company",
                                    "contractType": "NDA",
                                    "startDate": "2026-08-20",
                                    "endDate": "2027-08-20",
                                    "governingLaw": "German Law",
                                    "contractValue": 10000,
                                    "autoRenewal": false
                                }
                                """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void legalUser_shouldCreateContract() throws Exception {
        mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Security Test Contract",
                                "contractNumber": "SEC-LEGAL-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 10000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void managementUser_shouldNotCreateContract() throws Exception {
        mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("management", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Security Test Contract",
                                "contractNumber": "SEC-MGMT-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 10000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void legalUser_shouldUpdateContract() throws Exception {
        String response = mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Original Contract",
                                "contractNumber": "SEC-UPDATE-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 10000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long contractId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();

        mockMvc.perform(
                        put("/contracts/" + contractId)
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Updated Contract",
                                "contractNumber": "SEC-UPDATE-001",
                                "counterparty": "Updated Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 20000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isOk());
    }

    @Test
    void financeUser_shouldNotUpdateContract() throws Exception {
        String response = mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Security Test Contract",
                                "contractNumber": "SEC-FIN-UPDATE-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 10000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long contractId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();

        mockMvc.perform(
                        put("/contracts/" + contractId)
                                .with(httpBasic("finance", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Unauthorized Update",
                                "contractNumber": "SEC-FIN-UPDATE-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 20000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void managementUser_shouldNotUpdateContract() throws Exception {
        String response = mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Security Test Contract",
                                "contractNumber": "SEC-MGMT-UPDATE-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 10000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long contractId = new com.fasterxml.jackson.databind.ObjectMapper()
                .readTree(response)
                .get("id")
                .asLong();

        mockMvc.perform(
                        put("/contracts/" + contractId)
                                .with(httpBasic("management", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                                "title": "Unauthorized Update",
                                "contractNumber": "SEC-MGMT-UPDATE-001",
                                "counterparty": "Test Company",
                                "contractType": "NDA",
                                "startDate": "2026-08-20",
                                "endDate": "2027-08-20",
                                "governingLaw": "German Law",
                                "contractValue": 20000,
                                "autoRenewal": false
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void legalUser_shouldStartReview() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/start")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void financeUser_shouldNotStartReview() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/start")
                                .with(httpBasic("finance", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void legalUser_shouldApproveReview() throws Exception {

        long contractId = createContractInReviewAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/approve")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void financeUser_shouldNotApproveReview() throws Exception {

        long contractId = createContractInReviewAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/approve")
                                .with(httpBasic("finance", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void legalUser_shouldRejectReview() throws Exception {

        long contractId = createContractInReviewAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/reject")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    void financeUser_shouldNotRejectReview() throws Exception {

        long contractId = createContractInReviewAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/reject")
                                .with(httpBasic("finance", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void financeUser_shouldNotDeleteContract() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        delete("/contracts/" + contractId)
                                .with(httpBasic("finance", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUser_shouldDeleteContract() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        delete("/contracts/" + contractId)
                                .with(httpBasic("admin", "password"))
                )
                .andExpect(status().isNoContent());
    }

    @Test
    void financeUser_shouldNotChangeReviewStatusDirectly() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        patch("/contracts/" + contractId + "/review-status")
                                .with(httpBasic("finance", "password"))
                                .contentType("application/json")
                                .content("""
                                {
                                  "status": "IN_REVIEW"
                                }
                                """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUser_shouldChangeReviewStatusDirectly() throws Exception {
        long contractId = createContractAsLegal();

        mockMvc.perform(
                patch("/contracts/" + contractId + "/review-status")
                        .with(httpBasic("admin", "password"))
                        .contentType("application/json")
                        .content("""
                        {
                          "status": "IN_REVIEW"
                        }
                        """)
        ).andExpect(status().isOk());
    }

    @Test
    void legalUser_shouldNotDeleteContract() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        delete("/contracts/" + contractId)
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void managementUser_shouldNotApproveReview() throws Exception {

        long contractId = createContractInReviewAsLegal();

        mockMvc.perform(
                        post("/contracts/" + contractId + "/review/approve")
                                .with(httpBasic("management", "password"))
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUser_shouldCreateContract() throws Exception {
        mockMvc.perform(
                        post("/contracts")
                                .with(httpBasic("admin", "password"))
                                .contentType("application/json")
                                .content("""
                    {
                        "title": "Admin Test Contract",
                        "contractNumber": "SEC-ADMIN-001",
                        "counterparty": "Test Company",
                        "contractType": "NDA",
                        "startDate": "2026-08-20",
                        "endDate": "2027-08-20",
                        "governingLaw": "German Law",
                        "contractValue": 10000,
                        "autoRenewal": false
                    }
                    """)
                )
                .andExpect(status().isCreated());
    }

    @Test
    void legalUser_shouldNotChangeContractStatus() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        patch("/contracts/" + contractId + "/status")
                                .with(httpBasic("kirill", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                              "status": "SIGNED"
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void financeUser_shouldNotChangeContractStatus() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        patch("/contracts/" + contractId + "/status")
                                .with(httpBasic("finance", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                              "status": "SIGNED"
                            }
                            """)
                )
                .andExpect(status().isForbidden());
    }

    @Test
    void adminUser_shouldChangeContractStatus() throws Exception {

        long contractId = createContractAsLegal();

        mockMvc.perform(
                        patch("/contracts/" + contractId + "/status")
                                .with(httpBasic("admin", "password"))
                                .contentType("application/json")
                                .content("""
                            {
                              "status": "SIGNED"
                            }
                            """)
                )
                .andExpect(status().isOk());
    }
}
