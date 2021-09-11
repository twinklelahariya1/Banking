package com.twinkle.banking.service;

import com.twinkle.banking.exception.DataNotFoundException;
import com.twinkle.banking.exception.InvalidRequestException;
import com.twinkle.banking.exception.TransactionTimedOut;
import com.twinkle.banking.model.request.Transaction;
import com.twinkle.banking.model.response.Statistics;
import com.twinkle.banking.model.response.TransactionResponse;
import com.twinkle.banking.repository.TransactionRepository;
import com.twinkle.banking.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest()
class TransactionServiceTest {

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Mock
    private TransactionRepository transactionRepository;

    @Test
    void getTransactions() {
        TransactionResponse transactionResponse = new TransactionResponse(11.1d, ZonedDateTime.now());
        when(transactionRepository.getMinuteAgoTransactions()).thenReturn(Flux.just(transactionResponse));
        Mono<Statistics> transaction = transactionService.getTransaction();

        StepVerifier
                .create(transaction)
                .consumeNextWith(project -> {
                    assertThat(project.getCount()).isEqualTo(1);
                })
                .verifyComplete();
    }

    @Test
    void getTransactionsDataNotFoundException() {
        when(transactionRepository.getMinuteAgoTransactions()).thenReturn(Flux.empty());
        Mono<Statistics> transaction = transactionService.getTransaction();

        StepVerifier
                .create(transaction)
                .expectErrorMatches(throwable -> throwable instanceof DataNotFoundException)
                .verify();
    }

    @Test
    void saveTransaction() {
        TransactionResponse transactionResponse = new TransactionResponse(1.1d, ZonedDateTime.now());
        Transaction transaction = new Transaction(1.1d, ZonedDateTime.now());
        Mono<TransactionResponse> transactionResponseMono = Mono.just(transactionResponse);
        when(transactionRepository.saveTransaction(Mockito.any())).thenReturn(transactionResponseMono);

        Mono<TransactionResponse> responseMono = transactionService.saveTransaction(transaction);

        StepVerifier
                .create(responseMono)
                .consumeNextWith(transactionResponse1 -> {
                    assertThat(transactionResponse1.getAmount().equals(1.1d));
                }).verifyComplete();
    }

    @Test
    void saveTransactionInvalidRequestException() {
        Transaction transaction = new Transaction(1.1d, ZonedDateTime.now().toInstant().plusSeconds(1000000000).atZone(ZoneId.of("UTC")));

        Mono<TransactionResponse> responseMono = transactionService.saveTransaction(transaction);

        StepVerifier
                .create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof InvalidRequestException)
                .verify();
    }

    @Test
    void saveTransactionTransactionExpired() {
        Transaction transaction = new Transaction(1.1d, ZonedDateTime.now().toInstant().minusSeconds(1000000000).atZone(ZoneId.of("UTC")));

        Mono<TransactionResponse> responseMono = transactionService.saveTransaction(transaction);

        StepVerifier
                .create(responseMono)
                .expectErrorMatches(throwable -> throwable instanceof TransactionTimedOut)
                .verify();
    }

    @Test
    void deleteTransactions(){
        when(transactionRepository.deleteAll()).thenReturn(Mono.empty());

        Mono<Object> mono = transactionService.deleteTransaction();

        StepVerifier
                .create(mono)
                .expectNextCount(0)
                .verifyComplete();
    }
}
