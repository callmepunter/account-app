package net.punter.accountapp.services.test;


import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import net.punter.accountapp.repositories.AccountRepository;
import net.punter.accountapp.repositories.AccountTransactionRepository;
import net.punter.accountapp.services.impl.AccountServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

/**
 * Went for mockito because I am bit by dependency-woodoo or spring boot
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServicesTest {


    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountTransactionRepository accountTransactionRepository;


    Supplier<String> uuidSupplier = () ->
            UUID.randomUUID().toString();

    @Test
    public void contextLoadTest() throws Exception {

    }

    /**
     * Saving account has 0 balance
     * Initiate a transaction of 10 EURO
     * Account should end with 10 EURO balance
     * A credit transaction logged for 10 euros.
     *
     * @throws Exception
     */
    @Test
    public void deposit() throws Exception {
        long accountNumber = 787L;
        String transactionReference = uuidSupplier.get();

        Account requested = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        requested.setName("test-case");
        requested.setId(accountNumber);

        Balance euroZero = new Balance(Currency.getInstance("EUR"));
        euroZero.setAmount(BigDecimal.ZERO);
        euroZero.setAccount(requested);
        requested.addBalance(euroZero);


        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setType(AccountTransaction.TYPE.CREDIT);
        accountTransaction.setCurrency(Currency.getInstance("EUR"));
        accountTransaction.setAmount(BigDecimal.TEN);

        //after service layer logic runs, following objects should be the result
        Account toBeSaved = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        toBeSaved.setName("test-case");
        toBeSaved.setId(accountNumber);

        AccountTransaction savedTransaction = new AccountTransaction();
        savedTransaction.setId(transactionReference);
        savedTransaction.setType(AccountTransaction.TYPE.CREDIT);
        savedTransaction.setCurrency(Currency.getInstance("EUR"));
        savedTransaction.setAmount(BigDecimal.TEN);

        Balance euroTen = new Balance(Currency.getInstance("EUR"));
        euroTen.setAmount(BigDecimal.TEN);
        euroTen.setAccount(requested);

        toBeSaved.addBalance(euroTen);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(requested));
        when(accountTransactionRepository.saveAndFlush(accountTransaction)).thenReturn(savedTransaction);

        String resultReference = accountService.deposit(accountNumber, accountTransaction);

        Mockito.verify(accountRepository).saveAndFlush(toBeSaved);
        Mockito.verify(accountTransactionRepository).saveAndFlush(accountTransaction);
        Assert.assertEquals(transactionReference, resultReference);

    }

    /**
     * Starting with a 10 EURO account
     * Transaction is ordered for 10 EURO
     * Balance should become 0 EURO
     * Transaction logged should be of 10 EURO, as it was a debit
     */
    @Test
    public void withdraw() throws Exception {
        long accountNumber = 787L;
        String transactionReference = uuidSupplier.get();

        Account requested = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        requested.setName("test-case");
        requested.setId(accountNumber);

        Balance tenEuro = new Balance(Currency.getInstance("EUR"));
        tenEuro.setAmount(BigDecimal.TEN);
        tenEuro.setAccount(requested);
        requested.addBalance(tenEuro);


        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setType(AccountTransaction.TYPE.DEBIT);// it is debit
        accountTransaction.setCurrency(Currency.getInstance("EUR"));
        accountTransaction.setAmount(BigDecimal.TEN);

        Account toBeSaved = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        toBeSaved.setName("test-case");
        toBeSaved.setId(accountNumber);

        Balance zeroEuro = new Balance(Currency.getInstance("EUR"));
        zeroEuro.setAmount(BigDecimal.ZERO);
        zeroEuro.setAccount(requested);
        toBeSaved.addBalance(zeroEuro);// balance should become zero

        AccountTransaction savedTransaction = new AccountTransaction();
        savedTransaction.setId(transactionReference);
        savedTransaction.setType(AccountTransaction.TYPE.CREDIT);
        savedTransaction.setCurrency(Currency.getInstance("EUR"));
        savedTransaction.setAmount(BigDecimal.TEN);

        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(requested));
        when(accountTransactionRepository.saveAndFlush(accountTransaction)).thenReturn(savedTransaction);

        String reference = accountService.withDraw(accountNumber, accountTransaction);

        Mockito.verify(accountRepository).saveAndFlush(toBeSaved);
        Mockito.verify(accountTransactionRepository).saveAndFlush(accountTransaction);
        Assert.assertEquals(reference, transactionReference);

    }

}
