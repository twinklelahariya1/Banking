package com.twinkle.banking.service;

import com.twinkle.banking.model.request.Transaction;
import com.twinkle.banking.model.response.Statistics;
import com.twinkle.banking.model.response.TransactionResponse;
import reactor.core.publisher.Mono;

public interface TransactionService {
    Mono<Statistics> getTransaction();

    Mono<TransactionResponse> saveTransaction(Transaction transaction);

    <T> Mono<T> deleteTransaction();
}
