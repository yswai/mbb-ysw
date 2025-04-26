package com.mbb.batchprocessing;

import com.mbb.entity.transaction.Transaction;
import com.mbb.model.TransactionDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.core.convert.ConversionService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class TransactionItemProcessor implements ItemProcessor<TransactionDto, Transaction> {

	private static final Logger log = LoggerFactory.getLogger(TransactionItemProcessor.class);

	private final ConversionService conversionService;

	public TransactionItemProcessor(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	public Transaction process(final TransactionDto transactionDto) {

		final Transaction entity = conversionService.convert(transactionDto, Transaction.class);

		log.info("Converting ({}) into entity ({})", transactionDto, entity);

		return entity;
	}

}
