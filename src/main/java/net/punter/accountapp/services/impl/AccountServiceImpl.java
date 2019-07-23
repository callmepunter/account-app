package net.punter.accountapp.services.impl;


import lombok.extern.slf4j.Slf4j;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import net.punter.accountapp.repositories.AccountRepository;
import net.punter.accountapp.repositories.AccountTransactionRepository;
import net.punter.accountapp.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.orm.ObjectRetrievalFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    AccountTransactionRepository accountTransactionRepository;

    @Override
    public Account createNew(Account account) {
        return accountRepository.saveAndFlush(account);
    }

    @Transactional(readOnly = true)
    public Collection<Account> getAllAccounts() throws DataAccessException {
        List<Account> accountList = accountRepository.findAll();
        return Collections.unmodifiableList(accountList);

    }


    @Override
    public Account get(Long id) {
        Optional<Account> accountHolder = Optional.empty();
        try {
            accountHolder = accountRepository.findById(id);
        } catch (ObjectRetrievalFailureException | EmptyResultDataAccessException e) {
            log.warn("Account with id:" + id + "does not exist! ", e);
        }
        return accountHolder.get();
    }

    @Override
    public void delete(Long accountNumber) {

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
        return null;
    }

    @Override
    @Transactional
    public String withDraw(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Balance availableBalance = account.findBalance(accountTransaction.getCurrency());
            if (availableBalance != null && availableBalance.isGreaterThan(accountTransaction.getAmount())) {
                BigDecimal availableAmount = availableBalance.getAmount();
                availableBalance.setAmount(availableAmount.min(accountTransaction.getAmount()));
                availableBalance.setAccount(account);
                accountRepository.saveAndFlush(account);

                accountTransaction.setAccount(account);
                return accountTransactionRepository.saveAndFlush(accountTransaction).getId();
            }

        }
        return null;
    }
}