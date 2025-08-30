package com.example.carins.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CarControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void insuranceValid_ok_whenValidInputs() throws Exception {
        mockMvc.perform(get("/api/cars/{carId}/insurance-valid", 1L)
                        .param("date", "2024-06-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.carId").value(1))
                .andExpect(jsonPath("$.date").value("2024-06-01"))
                .andExpect(jsonPath("$.valid").isBoolean());
    }

    @Test
    void insuranceValid_404_whenCarNotFound() throws Exception {
        mockMvc.perform(get("/api/cars/{carId}/insurance-valid", 999L)
                        .param("date", "2024-06-01"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Car 999 not found"));
    }

    @Test
    void insuranceValid_400_whenInvalidDateFormat() throws Exception {
        mockMvc.perform(get("/api/cars/{carId}/insurance-valid", 1L)
                        .param("date", "2025/01/01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format. Use ISO format: YYYY-MM-DD"));
    }

    @Test
    void insuranceValid_400_whenDateOutOfRange() throws Exception {
        mockMvc.perform(get("/api/cars/{carId}/insurance-valid", 1L)
                        .param("date", "1800-01-01"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}


