package net.punter.accounting.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping(value = AccountQueryController.PATH)
@Slf4j
@RequiredArgsConstructor
public class AccountQueryController {

    public static final String PATH = "/api/v1/accounts";

    @Autowired
    private final AccountRepository accountRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<Account>> getAllAccounts() {
        Collection<Account> accounts = accountRepository.findAll();
        if (accounts == null || accounts.isEmpty()) {
            return new ResponseEntity<Collection<Account>>(HttpStatus.NOT_FOUND);
        }
        accounts.stream().forEach(account -> account.trimTransactions());
        return new ResponseEntity<Collection<Account>>(accounts, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> getAccount(@PathVariable("id") @Valid Long accountId) {
        Optional<Account> holder = accountRepository.findById(accountId);
        if (holder.isPresent()) {
            Account responsePayload = holder.get();
            responsePayload.trimTransactions();
            return new ResponseEntity<Account>(responsePayload, HttpStatus.OK);
        }
        return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
    }


    @GetMapping(value = "/{id}/transactions", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Collection<AccountTransaction>> getTransactions(@PathVariable("id") @Valid Long accountId) {
        Optional<Account> holder = accountRepository.findById(accountId);
        if (holder.isPresent()) {
            return new ResponseEntity<Collection<AccountTransaction>>(holder.get().getAccountTransactions(), HttpStatus.OK);

        }
        return new ResponseEntity<Collection<AccountTransaction>>(Collections.EMPTY_LIST, HttpStatus.NOT_FOUND);
    }
}
