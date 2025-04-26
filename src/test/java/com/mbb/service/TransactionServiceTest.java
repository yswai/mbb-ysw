package com.mbb.service;

import com.mbb.entity.transaction.Transaction;
import com.mbb.entity.transaction.TransactionRepository;
import com.mbb.model.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {

  @InjectMocks
  private TransactionService transactionService;

  @Mock
  private TransactionRepository repository;

  @Mock
  private ConversionService conversionService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  private Transaction createTransaction(Long id) {
    Transaction t = new Transaction();
    t.setId(id);
    t.setAccountNumber("123456");
    t.setCustomerId("CUST123");
    t.setDescription("Test transaction");
    t.setTrxAmount(new BigDecimal("100.00"));
    t.setTrxDate(LocalDate.now());
    t.setTrxTime(LocalTime.now());
    return t;
  }

  private TransactionDto createDto(Long id) {
    TransactionDto dto = new TransactionDto();
    dto.setId(id);
    dto.setAccountNumber("123456");
    dto.setCustomerId("CUST123");
    dto.setDescription("Test transaction");
    dto.setTrxAmount(new BigDecimal("100.00"));
    dto.setTrxDate(LocalDate.now());
    dto.setTrxTime(LocalTime.now());
    return dto;
  }

  @Test
  void testGetById_Success() {
    Transaction tx = createTransaction(1L);
    TransactionDto dto = createDto(1L);

    when(repository.findById(1L)).thenReturn(Optional.of(tx));
    when(conversionService.convert(tx, TransactionDto.class)).thenReturn(dto);

    TransactionDto result = transactionService.getById(1L);

    assertNotNull(result);
    assertEquals(dto.getId(), result.getId());
  }

  @Test
  void testGetById_NotFound() {
    when(repository.findById(1L)).thenReturn(Optional.empty());
    assertThrows(NoSuchElementException.class, () -> transactionService.getById(1L));
  }

  @Test
  void testListAll() {
    Transaction tx = createTransaction(1L);
    TransactionDto dto = createDto(1L);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Transaction> page = new PageImpl<>(List.of(tx));

    when(repository.findAll(pageable)).thenReturn(page);
    when(conversionService.convert(tx, TransactionDto.class)).thenReturn(dto);

    Page<TransactionDto> result = transactionService.listAll(pageable);

    assertEquals(1, result.getTotalElements());
    assertEquals(dto.getId(), result.getContent().get(0).getId());
  }

  @Test
  void testSearchByCustomerId() {
    Transaction tx = createTransaction(1L);
    TransactionDto dto = createDto(1L);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Transaction> page = new PageImpl<>(List.of(tx));

    when(repository.findByCustomerId("CUST123", pageable)).thenReturn(page);
    when(conversionService.convert(tx, TransactionDto.class)).thenReturn(dto);

    Page<TransactionDto> result = transactionService.searchByCustomerId("CUST123", pageable);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void testSearchByAccountNumber() {
    Transaction tx = createTransaction(1L);
    TransactionDto dto = createDto(1L);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Transaction> page = new PageImpl<>(List.of(tx));

    when(repository.findByAccountNumberContainingIgnoreCase("123", pageable)).thenReturn(page);
    when(conversionService.convert(tx, TransactionDto.class)).thenReturn(dto);

    Page<TransactionDto> result = transactionService.searchByAccountNumber("123", pageable);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void testSearchByDescription() {
    Transaction tx = createTransaction(1L);
    TransactionDto dto = createDto(1L);
    Pageable pageable = PageRequest.of(0, 10);
    Page<Transaction> page = new PageImpl<>(List.of(tx));

    when(repository.searchByDescription("Test", pageable)).thenReturn(page);
    when(conversionService.convert(tx, TransactionDto.class)).thenReturn(dto);

    Page<TransactionDto> result = transactionService.searchByDescription("Test", pageable);

    assertEquals(1, result.getTotalElements());
  }

  @Test
  void testUpdate_Success() {
    TransactionDto dto = createDto(1L);
    Transaction tx = createTransaction(1L);

    when(repository.findById(dto.getId())).thenReturn(Optional.of(tx));
    when(repository.save(any(Transaction.class))).thenReturn(tx);

    TransactionDto result = transactionService.update(dto);

    assertNotNull(result);
    assertEquals(dto.getAccountNumber(), tx.getAccountNumber());
    verify(repository).save(tx);
  }

  @Test
  void testUpdate_NotFound() {
    TransactionDto dto = createDto(1L);
    when(repository.findById(dto.getId())).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> transactionService.update(dto));
  }
}
