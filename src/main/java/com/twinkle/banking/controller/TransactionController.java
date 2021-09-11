package com.twinkle.banking.controller;

import com.twinkle.banking.model.request.Transaction;
import com.twinkle.banking.model.response.APIResponse;
import com.twinkle.banking.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/statistics")
    @ResponseStatus(HttpStatus.OK)
    public Mono<APIResponse> getStatistic() {
        return transactionService.getTransaction().map(statistics -> new APIResponse(200, statistics, null));
    }

    @PostMapping(value = "/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<APIResponse> saveTransaction(@RequestBody @Validated Transaction transaction) {
        return transactionService.saveTransaction(transaction).map(transaction1 -> new APIResponse(201, transaction1, null));
    }

    @DeleteMapping(value = "/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<APIResponse> deleteTransaction() {
        return transactionService.deleteTransaction();
    }
}

