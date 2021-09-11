package com.twinkle.banking.controller;

import com.twinkle.banking.model.request.Transaction;
import com.twinkle.banking.model.response.APIResponse;
import com.twinkle.banking.model.response.Statistics;
import com.twinkle.banking.model.response.TransactionResponse;
import com.twinkle.banking.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;

import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@WebFluxTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private TransactionServiceImpl transactionService;

    @Test
    void testGetStatistic() {
        Statistics statistics = new Statistics(1.1d, 1.1d, 1.1d, 1.1d, 1L);
        Mono<Statistics> responseMono = Mono.just(statistics);

        when(transactionService.getTransaction()).thenReturn(Mono.just(statistics));

        webTestClient.get()
                .uri("/statistics")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(APIResponse.class)
                .value(apiResponse -> apiResponse.getBody().equals(statistics));
    }

    @Test
    void testSaveTransaction() {
        Transaction transaction = new Transaction(1.1d, ZonedDateTime.now());
        TransactionResponse transactionResponse = new TransactionResponse(1.1d, ZonedDateTime.now());
        when(transactionService.saveTransaction(Mockito.any())).thenReturn(Mono.just(transactionResponse));

        webTestClient.post()
                .uri("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(transaction), Transaction.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(APIResponse.class)
                .value(apiResponse -> apiResponse.getBody().equals(transactionResponse));
    }

    @Test
    void testDeleteTransaction() {
        when(transactionService.deleteTransaction()).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/transactions")
                .exchange()
                .expectStatus().isNoContent();
    }
}