package com.mbb.service;

import com.mbb.entity.transaction.Transaction;
import com.mbb.entity.transaction.TransactionRepository;
import com.mbb.model.TransactionDto;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  @Autowired
  private TransactionRepository repository;
  @Autowired
  private ConversionService conversionService;

  public TransactionDto getById(Long id) {
    return repository.findById(id).map(tx -> conversionService.convert(tx, TransactionDto.class)).orElseThrow();
  }

  public Page<TransactionDto> listAll(Pageable pageable) {
    return repository.findAll(pageable)
        .map(t -> conversionService.convert(t, TransactionDto.class));
  }

  public Page<TransactionDto> searchByCustomerId(String customerId, Pageable pageable) {
    return repository.findByCustomerId(customerId, pageable)
        .map(t -> conversionService.convert(t, TransactionDto.class));
  }

  public Page<TransactionDto> searchByAccountNumber(String accountNumber, Pageable pageable) {
    return repository.findByAccountNumberContainingIgnoreCase(accountNumber, pageable)
        .map(t -> conversionService.convert(t, TransactionDto.class));
  }

  public Page<TransactionDto> searchByDescription(String description, Pageable pageable) {
    return repository.searchByDescription(description, pageable)
        .map(t -> conversionService.convert(t, TransactionDto.class));
  }

  @Transactional
  @SneakyThrows
  public TransactionDto update(TransactionDto dto) {
    Transaction transaction = repository.findById(dto.getId()).orElseThrow();
    transaction.setAccountNumber(dto.getAccountNumber());
    transaction.setTrxAmount(dto.getTrxAmount());
    transaction.setDescription(dto.getDescription());
    transaction.setTrxDate(dto.getTrxDate());
    transaction.setTrxTime(dto.getTrxTime());
    transaction.setCustomerId(dto.getCustomerId());
    repository.save(transaction);
    return dto;
  }
}
