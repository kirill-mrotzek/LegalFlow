package de.kirillmrotzek.legalflow.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticatedUser_shouldBeAvailableInSecurityContext() throws Exception {
        mockMvc.perform(
                        get("/security/me")
                                .with(httpBasic("kirill", "password"))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(
                        containsString("kirill")
                ))
                .andExpect(content().string(
                        containsString("authenticated=true")
                ));
    }
}
