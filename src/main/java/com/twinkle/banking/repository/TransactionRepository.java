package com.twinkle.banking.repository;

import com.twinkle.banking.model.response.TransactionResponse;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TransactionRepository {

    private final List<TransactionResponse> transactionResponses = new ArrayList<>();

    public Mono<TransactionResponse> saveTransaction(TransactionResponse transactionResponse) {
        transactionResponses.add(transactionResponse);
        return Mono.just(transactionResponse);
    }

    public Flux<TransactionResponse> getMinuteAgoTransactions() {
        Instant i = ZonedDateTime.now().toInstant();
        Instant instant = i.minusSeconds(60);
        return Flux.fromStream(transactionResponses.stream()
                .filter(transactionResponse -> transactionResponse.getTimestamp().toInstant().isAfter(instant))
                .filter(transactionResponse -> transactionResponse.getTimestamp().toInstant().isBefore(i)));
    }

    public <T> Mono<T> deleteAll() {
        transactionResponses.clear();
        return Mono.empty();
    }
}
