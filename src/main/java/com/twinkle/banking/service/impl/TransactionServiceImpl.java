package com.twinkle.banking.service.impl;

import com.twinkle.banking.exception.DataNotFoundException;
import com.twinkle.banking.exception.InvalidRequestException;
import com.twinkle.banking.exception.TransactionTimedOut;
import com.twinkle.banking.model.request.Transaction;
import com.twinkle.banking.model.response.Statistics;
import com.twinkle.banking.model.response.TransactionResponse;
import com.twinkle.banking.repository.TransactionRepository;
import com.twinkle.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public Mono<Statistics> getTransaction() {

        return transactionRepository.getMinuteAgoTransactions().collectList().flatMap(this::buildStatistics);
    }

    private Mono<Statistics> buildStatistics(List<TransactionResponse> transactionResponses) {

        if (transactionResponses.isEmpty()) {
            return Mono.error(new DataNotFoundException("Not found!"));
        }

        Long count = (long) transactionResponses.size();
        Double sum = transactionResponses.stream().mapToDouble(TransactionResponse::getAmount)
                .sum();
        Double max = transactionResponses.stream().mapToDouble(TransactionResponse::getAmount).max().orElse(0);
        Double min = transactionResponses.stream().mapToDouble(TransactionResponse::getAmount).min().orElse(0);

        return Mono.just(Statistics.builder().avg(sum / count)
                .count(count)
                .max(max)
                .min(min)
                .sum(sum).build());
    }


    @Override
    public Mono<TransactionResponse> saveTransaction(Transaction transaction) {

        return Mono.just(transaction)
                .flatMap(this::validateTransaction)
                .map(this::mapToTransactionResponse)
                .flatMap(transaction1 -> transactionRepository.saveTransaction(transaction1));
    }


    private Mono<Transaction> validateTransaction(Transaction transaction) {
        Instant instant = ZonedDateTime.now().toInstant();
        if (transaction.getTimestamp().toInstant().isAfter(instant)) {
            return Mono.error(new InvalidRequestException("Invalid Timestamp"));
        }
        if (transaction.getTimestamp().toInstant().isBefore(instant.minusSeconds(60)))
            return Mono.error(new TransactionTimedOut());
        return Mono.just(transaction);
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getAmount(), transaction.getTimestamp().withZoneSameInstant(ZoneId.of("UTC")));
    }

    @Override
    public <T> Mono<T> deleteTransaction() {
        return transactionRepository.deleteAll();
    }
}
