package com.mbb.batchprocessing;

import com.mbb.entity.transaction.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class JobCompletionNotificationListener implements JobExecutionListener {

	private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

	private final TransactionRepository transactionRepository;

	public JobCompletionNotificationListener(TransactionRepository transactionRepository) {
		this.transactionRepository = transactionRepository;
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
			log.info("!!! JOB FINISHED! Time to verify the results");
			transactionRepository.findAll().forEach(transaction -> log.info("Found <{}> in the database.", transaction));
		}
	}
}
