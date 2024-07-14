package com.excitel.config;import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testEndpoints() throws Exception {
        mockMvc.perform(get("/api/v2/auth/users"))
                .andExpect(status().isNonAuthoritativeInformation());

        mockMvc.perform(post("/api/v2/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")) // You need to replace this with an actual user registration data
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/api/v2/auth/health"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v2/auth/password/*"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/actuator/*"))
                .andExpect(status().is4xxClientError());

        mockMvc.perform(get("/actuator"))
                .andExpect(status().is2xxSuccessful());
    }
}