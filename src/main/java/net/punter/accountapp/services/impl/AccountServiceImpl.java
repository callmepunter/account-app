package net.punter.accountapp.services.impl;


import lombok.extern.slf4j.Slf4j;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import net.punter.accountapp.repositories.AccountRepository;
import net.punter.accountapp.services.AccountService;
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
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;


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
    public Set<Balance> deposite(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Balance> availableBalanceHolder = Optional.empty();
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Set<Balance> availableBalances = accountHolder.get().getBalances();
            availableBalanceHolder = availableBalances.stream().filter(balance ->
                 balance.getCurrency().equals(accountTransaction.getCurrency())
            ).findFirst();
            Balance availableBalance = availableBalanceHolder.get();
            BigDecimal availableAmount = availableBalance.getAmount();
            availableBalance.setAmount(availableAmount.add(accountTransaction.getAmount()));
            accountRepository.saveAndFlush(account);
            return account.getBalances();
        }
        return null;
    }

    @Override
    @Transactional
    public Set<Balance> withDraw(Long accountNumber, AccountTransaction accountTransaction) {
        Optional<Balance> availableBalanceHolder = Optional.empty();
        Optional<Account> accountHolder = accountRepository.findById(accountNumber);
        if (accountHolder.isPresent()) {
            Account account = accountHolder.get();
            Set<Balance> availableBalances = accountHolder.get().getBalances();
            availableBalanceHolder = availableBalances.stream().filter(balance ->
                    balance.getCurrency().equals(accountTransaction.getCurrency())
            ).findFirst();
            Balance availableBalance = availableBalanceHolder.get();
            BigDecimal availableAmount = availableBalance.getAmount();
            availableBalance.setAmount(availableAmount.add(accountTransaction.getAmount()));
            accountRepository.saveAndFlush(account);
            return account.getBalances();
        }
        return null;
    }
}
