package com.example.bedrockagent.travel.api;

import com.example.bedrockagent.travel.service.TravelAgentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TravelPlanController.class)
class TravelPlanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TravelAgentService service;

    @Test
    void returns400OnInvalidRequest() throws Exception {
        mockMvc.perform(post("/api/v1/travel/plan")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
