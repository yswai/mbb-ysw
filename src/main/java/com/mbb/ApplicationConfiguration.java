package com.mbb;

import com.mbb.batchprocessing.JobCompletionNotificationListener;
import com.mbb.batchprocessing.TransactionItemProcessor;
import com.mbb.entity.transaction.Transaction;
import com.mbb.model.TransactionDto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.orm.jpa.JpaTransactionManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class ApplicationConfiguration {

	@Bean
	public ConversionService conversionService() {
		DefaultConversionService conversionService = new DefaultConversionService();
		DefaultConversionService.addDefaultConverters(conversionService);
		conversionService.addConverter(new Converter<String, LocalDate>() {
			@Override
			public LocalDate convert(String text) {
				return LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE);
			}
		});
		conversionService.addConverter(new Converter<String, LocalTime>() {
			@Override
			public LocalTime convert(String text) {
				return LocalTime.parse(text, DateTimeFormatter.ISO_LOCAL_TIME);
			}
		});
		conversionService.addConverter(new Converter<Transaction, TransactionDto>() {
			@Override
			public TransactionDto convert(Transaction transaction) {
				final Long id = transaction.getId();
				final String accountNumber = transaction.getAccountNumber();
				final BigDecimal trxAmount = transaction.getTrxAmount();
				final String description = transaction.getDescription();
				final LocalDate trxDate = transaction.getTrxDate();
				final LocalTime trxTime = transaction.getTrxTime();
				final String customerId = transaction.getCustomerId();
				return new TransactionDto(
						id, accountNumber, trxAmount, description, trxDate, trxTime, customerId
				);
			}
		});
		conversionService.addConverter(new Converter<TransactionDto, Transaction>() {
			@Override
			public Transaction convert(TransactionDto dto) {
				final Long id = dto.getId();
				final String accountNumber = dto.getAccountNumber();
				final BigDecimal trxAmount = dto.getTrxAmount();
				final String description = dto.getDescription();
				final LocalDate trxDate = dto.getTrxDate();
				final LocalTime trxTime = dto.getTrxTime();
				final String customerId = dto.getCustomerId();
				return new Transaction(
						id, accountNumber, trxAmount, description, trxDate, trxTime, customerId, null
				);
			}
		});
		return conversionService;
	}

	@Bean
	public FieldSetMapper<TransactionDto> transactionFieldSetMapper(ConversionService conversionService) {
		BeanWrapperFieldSetMapper<TransactionDto> mapper = new BeanWrapperFieldSetMapper<>();
		mapper.setConversionService(conversionService);
		mapper.setTargetType(TransactionDto.class);
		return mapper;
	}

	@Bean
	public FlatFileItemReader<TransactionDto> reader(FieldSetMapper<TransactionDto> transactionFieldSetMapper) {
		return new FlatFileItemReaderBuilder<TransactionDto>()
				.fieldSetMapper(transactionFieldSetMapper)
				.linesToSkip(1)
				.name("transactionItemReader")
				.resource(new ClassPathResource("dataSource.txt"))
				.delimited().delimiter("|")
				.names("accountNumber", "trxAmount", "description", "trxDate", "trxTime", "customerId")
				.build();
	}

	@Bean
	public TransactionItemProcessor processor(ConversionService conversionService) {
		return new TransactionItemProcessor(conversionService);
	}

	@Bean
	public JpaItemWriter<Transaction> writer(EntityManagerFactory emf) {
		JpaItemWriter writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}

	@Bean
	public Job importTransactionJob(JobRepository jobRepository, Step step1, JobCompletionNotificationListener listener) {
		return new JobBuilder("importTransactionJob", jobRepository)
			.listener(listener)
			.start(step1)
			.build();
	}

	@Bean
	public Step step1(JobRepository jobRepository, JpaTransactionManager transactionManager,
										FlatFileItemReader<TransactionDto> reader, TransactionItemProcessor processor,
										JpaItemWriter<Transaction> writer) {
		return new StepBuilder("step1", jobRepository)
			.<TransactionDto, Transaction>chunk(3, transactionManager)
			.reader(reader)
			.processor(processor)
			.writer(writer)
			.build();
	}
}
