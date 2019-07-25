package net.punter.accounting.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.domain.Balance;
import net.punter.accounting.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountService {

    @Autowired
    private final AccountRepository accountRepository;

    @Transactional
    public Account createAccount(Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Transactional
    public void deleteAccount(Long accountNumber) {
        accountRepository.deleteById(accountNumber);
    }

    @Transactional
    public String deposit(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Balance availableBalance = account.findBalance(accountTransaction.getCurrency());
            BigDecimal availableAmount = availableBalance.getAmount();
            availableBalance.setAmount(availableAmount.add(accountTransaction.getAmount()));
            accountTransaction.setAccount(account);
            account.getAccountTransactions().add(accountTransaction);
            accountRepository.saveAndFlush(account);
            return "OK";
        }
        return "INVALID'";
    }

    @Transactional
    public String withdraw(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            account.debit(new Balance(accountTransaction.getAmount(), accountTransaction.getCurrency()));
            accountTransaction.setAccount(account);
            account.getAccountTransactions().add(accountTransaction);
            accountRepository.saveAndFlush(account);
            return "OK";
        }
        return "INVALID";
    }

}