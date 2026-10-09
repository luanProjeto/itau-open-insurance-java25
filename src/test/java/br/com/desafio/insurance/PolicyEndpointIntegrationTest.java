package br.com.desafio.insurance;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PolicyEndpointIntegrationTest {
    @Autowired
    MockMvc mvc;

    @Test
    void createAndReadPolicy() throws Exception {
        String json = """ 
                {"policyId":"IT-2026-01","proposalId":"PROP-01","documentType":"POLICY","issuanceType":"NEW","issuanceDate":"2026-01-01","termStartDate":"2026-01-01","termEndDate":"2027-01-01","maxLMG":150000.00,"insuredName":"Segurado Fictício"}
                """;
        mvc.perform(post("/api/v1/policies").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isCreated()).andExpect(jsonPath("$.policyId").value("IT-2026-01"));
        mvc.perform(get("/insurance-patrimonial/IT-2026-01/policy-info")).andExpect(status().isOk()).andExpect(jsonPath("$.insuredName").value("Segurado Fictício"));
        mvc.perform(post("/api/v1/policies").contentType(MediaType.APPLICATION_JSON).content(json)).andExpect(status().isConflict());
        mvc.perform(delete("/api/v1/policies/IT-2026-01")).andExpect(status().isNoContent());
        mvc.perform(get("/api/v1/policies/IT-2026-01")).andExpect(jsonPath("$.status").value("CANCELLED"));
    }
}
