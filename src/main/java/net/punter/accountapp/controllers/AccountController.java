package net.punter.accountapp.controllers;


import lombok.extern.slf4j.Slf4j;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import net.punter.accountapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

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
        Account Account = accountService.get(Long.valueOf(accountId));
        if (Account == null) {
            return new ResponseEntity<Account>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Account>(Account, HttpStatus.OK);
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
    public ResponseEntity<Set<Balance>> transact(@PathVariable("id") long accountId,
                                                 @RequestBody AccountTransaction accountTransaction) {
        Set<Balance> availableBalances  = new HashSet<>();
        Long account = Long.valueOf(accountId);
        HttpHeaders headers = new HttpHeaders();
        if (account.equals(Long.valueOf(0l)) || AccountTransaction.TYPE.INVALID.equals(accountTransaction.getType())) {
            headers.add("request", accountTransaction.toString());
            return new ResponseEntity<Set<Balance>>(headers, HttpStatus.BAD_REQUEST);
        }
        if (AccountTransaction.TYPE.CREDIT == accountTransaction.getType()) {
            availableBalances.addAll(accountService.deposite(account, accountTransaction));
        }

        if (AccountTransaction.TYPE.DEBIT == accountTransaction.getType()) {
            availableBalances.addAll(accountService.deposite(account, accountTransaction));
        }
        return new ResponseEntity<Set<Balance>>(new HashSet<>(availableBalances), HttpStatus.OK);
    }
}
