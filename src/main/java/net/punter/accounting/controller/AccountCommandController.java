package net.punter.accounting.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = AccountCommandController.PATH)
@Slf4j
@RequiredArgsConstructor
public class AccountCommandController {

    public static final String PATH = "/api/v1/accounts";

    @Autowired
    private final AccountService accountService;


    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> createNew(@RequestBody @Valid Account account) {
        Account persisted = accountService.createAccount(account);
        return new ResponseEntity<Account>(persisted, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") long accountId) {
        accountService.deleteAccount(Long.valueOf(accountId));
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/{id}/transactions",
            produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> transact(@PathVariable("id") long accountId,
                                           @RequestBody AccountTransaction accountTransaction) {
        String transactionId = "";
        Long accountNumber = accountId;
        HttpHeaders headers = new HttpHeaders();
        if (accountNumber.equals(0L) || AccountTransaction.TYPE.INVALID.equals(accountTransaction.getType())) {
            headers.add("request", accountTransaction.toString());
            return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
        }
        if (AccountTransaction.TYPE.CREDIT == accountTransaction.getType()) {
            transactionId = accountService.deposit(accountNumber, accountTransaction);
        }

        if (AccountTransaction.TYPE.DEBIT == accountTransaction.getType()) {
            transactionId = accountService.withdraw(accountNumber, accountTransaction);

        }
        if ("INVALID".equals(transactionId)) {
            return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<String>(transactionId, HttpStatus.CREATED);
    }
}
