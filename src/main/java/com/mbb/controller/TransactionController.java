package com.mbb.controller;

import com.mbb.model.TransactionDto;
import com.mbb.service.TransactionService;
import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

  @Autowired
  private TransactionService service;

  @GetMapping("/all")
  public Page<TransactionDto> getAll(Pageable pageable) {
    return service.listAll(pageable);
  }

  @GetMapping("/{id}")
  public TransactionDto getById(@PathParam("id") Long id) {
    return service.getById(id);
  }

  @GetMapping("/search/customer")
  public Page<TransactionDto> searchByCustomerId(@RequestParam String customerId, Pageable pageable) {
    return service.searchByCustomerId(customerId, pageable);
  }

  @GetMapping("/search/account")
  public Page<TransactionDto> searchByAccount(@RequestParam String accountNumber, Pageable pageable) {
    return service.searchByAccountNumber(accountNumber, pageable);
  }

  @GetMapping("/search/description")
  public Page<TransactionDto> searchByDescription(@RequestParam String description, Pageable pageable) {
    return service.searchByDescription(description, pageable);
  }

  @PutMapping
  public TransactionDto update(@RequestBody TransactionDto dto) {
    return service.update(dto);
  }
}
