package net.punter.accountapp.controllers;


import lombok.extern.slf4j.Slf4j;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1/accounts")
@Slf4j
public class AccountController {

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
            account = accountService.get(Long.valueOf(accountId));
            return new ResponseEntity<Account>(account, HttpStatus.OK);
        } catch (Exception exception) {
            log.warn("Requested resource does not exist", exception);
        }
        return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
    }

    @PostMapping(value = "", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Account> createNew(@RequestBody @Valid Account account) {
        Account persisted = accountService.createNew(account);
        return new ResponseEntity<Account>(persisted, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<Void> deleteAccount(@PathVariable("id") long accountId) {
        accountService.delete(Long.valueOf(accountId));
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }


    @PostMapping(value = "/{id}/transaction", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<String> transact(@PathVariable("id") long accountId,
                                           @RequestBody AccountTransaction accountTransaction) {
        String transactionReference = "";
        Long account = Long.valueOf(accountId);
        HttpHeaders headers = new HttpHeaders();
        if (account.equals(Long.valueOf(0l)) || AccountTransaction.TYPE.INVALID.equals(accountTransaction.getType())) {
            headers.add("request", accountTransaction.toString());
            return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
        }
        if (AccountTransaction.TYPE.CREDIT == accountTransaction.getType()) {
            transactionReference = accountService.deposit(account, accountTransaction);
        }

        if (AccountTransaction.TYPE.DEBIT == accountTransaction.getType()) {
            transactionReference = accountService.withDraw(account, accountTransaction);
        }
        return new ResponseEntity<String>(transactionReference, HttpStatus.OK);
    }
}
