package net.punter.accounting.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.domain.Balance;
import net.punter.accounting.repository.AccountRepository;
import net.punter.accounting.repository.AccountTransactionRepository;
import net.punter.accounting.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountTransactionRepository accountTransactionRepository;

    @Override
    public Account createAccount(Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Transactional(readOnly = true)
    public Collection<Account> getAllAccounts() throws DataAccessException {
        List<Account> accountList = accountRepository.findAll();
        return Collections.unmodifiableList(accountList);
    }


    @Override
    public Account getAccount(Long id) {
        Optional<Account> accountHolder = Optional.empty();
        try {
            accountHolder = accountRepository.findById(id);
        } catch (ObjectRetrievalFailureException | EmptyResultDataAccessException e) {
            log.warn("Account with id:" + id + "does not exist! ", e);
        }
        return accountHolder.get();
    }

    @Override
    public void deleteAccount(Long accountNumber) {
        accountRepository.deleteById(accountNumber);

    }


    @Override
    @Transactional
    public String deposit(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Balance availableBalance = account.findBalance(accountTransaction.getCurrency());
            BigDecimal availableAmount = availableBalance.getAmount();
            availableBalance.setAmount(availableAmount.add(accountTransaction.getAmount()));
            availableBalance.setAccount(account);
            accountRepository.saveAndFlush(account);

            accountTransaction.setAccount(account);
            return accountTransactionRepository.saveAndFlush(accountTransaction).getId();
        }
        return "INVALID'";
    }

    @Override
    @Transactional
    public String withdraw(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Balance availableBalance = account.findBalance(accountTransaction.getCurrency());
            if (availableBalance != null && availableBalance.isGreaterThanOrEquals(accountTransaction.getAmount())) {
                BigDecimal availableAmount = availableBalance.getAmount();
                availableBalance.setAmount(availableAmount.min(accountTransaction.getAmount()));
                availableBalance.setAccount(account);
                accountRepository.saveAndFlush(account);

                accountTransaction.setAccount(account);
                return accountTransactionRepository.saveAndFlush(accountTransaction).getId();
            }
        }
        return "INVALID";
    }

    @Override
    public List<AccountTransaction> getAllAccounTreansactions(Long accountId) {
        Optional<Account> accountHolder = accountRepository.findById(accountId);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            return new ArrayList<>(account.getAccountTransactions());
        }
        return Collections.EMPTY_LIST;
    }
}