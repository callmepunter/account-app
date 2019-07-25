package net.punter.accounting.controllers;


import lombok.extern.slf4j.Slf4j;
import net.punter.accounting.domains.Account;
import net.punter.accounting.domains.AccountTransaction;
import net.punter.accounting.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = AccountsApiController.PATH)
@Slf4j
public class AccountsApiController {

    public static final String PATH = "/api/v2/accounts";

    @Autowired
    private AccountService accountService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Account>> getAllAccounts() {
        Collection<Account> accounts = new ArrayList<Account>();
        accounts.addAll(accountService.getAllAccounts());
        if (accounts.isEmpty()) {
            return new ResponseEntity<Collection<Account>>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Collection<Account>>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> getAccount(@PathVariable("id") long accountId) {
        Account account = null;
        try {
            account = accountService.getAccount(Long.valueOf(accountId));
            return new ResponseEntity<Account>(account, HttpStatus.OK);
        } catch (Exception exception) {
            log.warn("Requested resource does not exist", exception);
        }
        return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
    }

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

    @GetMapping(value = "/{id}/transactions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AccountTransaction>> getTransactions(@PathVariable("id") long accountId) {
        try {
            List<AccountTransaction> allTransactions = accountService.getAllAccounTreansactions(accountId);
            return new ResponseEntity<List<AccountTransaction>>(allTransactions, HttpStatus.OK);

        } catch (Exception exception) {
            log.warn("Requested resource does not exist", exception);
        }
        return new ResponseEntity<List<AccountTransaction>>(Collections.EMPTY_LIST, HttpStatus.NOT_FOUND);
    }
}
