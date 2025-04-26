package com.mbb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDto {
  private Long id;
  private String accountNumber;
  private BigDecimal trxAmount;
  private String description;
  @DateTimeFormat(pattern = "yyyy-MM-dd")
  private LocalDate trxDate;
  @DateTimeFormat(pattern = "HH:mm:ss")
  private LocalTime trxTime;
  private String customerId;
}
