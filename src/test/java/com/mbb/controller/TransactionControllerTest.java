package com.mbb.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbb.model.TransactionDto;
import com.mbb.service.TransactionService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private TransactionService transactionService;

  @Autowired
  private ObjectMapper objectMapper;

  private TransactionDto sampleDto() {
    return new TransactionDto(
        1L,
        "123456789",
        new BigDecimal("100.00"),
        "Test transaction",
        LocalDate.of(2024, 1, 1),
        LocalTime.of(10, 30),
        "cust-123"
    );
  }

  @Test
  void getAll_ShouldReturnPagedTransactions() throws Exception {
    Page<TransactionDto> page = new PageImpl<>(List.of(sampleDto()));
    Mockito.when(transactionService.listAll(any())).thenReturn(page);

    mockMvc.perform(get("/transactions/all"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].accountNumber").value("123456789"));
  }

  @Test
  void getById_ShouldReturnTransaction() throws Exception {
    Mockito.when(transactionService.getById(1L)).thenReturn(sampleDto());

    mockMvc.perform(get("/transactions/{id}", 1L))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("123456789"));
  }

  @Test
  void searchByCustomerId_ShouldReturnPagedResults() throws Exception {
    Page<TransactionDto> page = new PageImpl<>(List.of(sampleDto()));
    Mockito.when(transactionService.searchByCustomerId(eq("cust-123"), any())).thenReturn(page);

    mockMvc.perform(get("/transactions/search/customer")
        .param("customerId", "cust-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].customerId").value("cust-123"));
  }

  @Test
  void searchByAccount_ShouldReturnPagedResults() throws Exception {
    Page<TransactionDto> page = new PageImpl<>(List.of(sampleDto()));
    Mockito.when(transactionService.searchByAccountNumber(eq("123456789"), any())).thenReturn(page);

    mockMvc.perform(get("/transactions/search/account")
        .param("accountNumber", "123456789"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].accountNumber").value("123456789"));
  }

  @Test
  void searchByDescription_ShouldReturnPagedResults() throws Exception {
    Page<TransactionDto> page = new PageImpl<>(List.of(sampleDto()));
    Mockito.when(transactionService.searchByDescription(eq("Test"), any())).thenReturn(page);

    mockMvc.perform(get("/transactions/search/description")
        .param("description", "Test"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].description").value("Test transaction"));
  }

  @Test
  void update_ShouldUpdateAndReturnTransaction() throws Exception {
    TransactionDto dto = sampleDto();
    Mockito.when(transactionService.update(any())).thenReturn(dto);

    mockMvc.perform(put("/transactions")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accountNumber").value("123456789"));
  }
}
