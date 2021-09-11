package com.twinkle.banking.controller;

import com.twinkle.banking.model.response.Statistics;
import com.twinkle.banking.service.impl.TransactionServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static reactor.core.publisher.Mono.when;

@RunWith(SpringRunner.class)
@WebFluxTest(TransactionController.class)
class TransactionController {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    private TransactionServiceImpl transactionService;


    @Test
    void testGetEmployeeById() {
        Statistics statistics = new Statistics(1.1d, 1.1d, 1.1d, 1.1d, 1L);
        Mono<Statistics> responseMono = Mono.just(statistics);

        when(transactionService.getTransaction()).thenReturn(statistics);

        WebTestClient.BodySpec<Statistics, ?> statisticsBodySpec = webTestClient.get()
                .uri("/employees/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Statistics.class);

        Assertions.assertEquals(1, Objects.requireNonNull(statisticsBodySpec.returnResult().getResponseBody()).getCount());
    }
}