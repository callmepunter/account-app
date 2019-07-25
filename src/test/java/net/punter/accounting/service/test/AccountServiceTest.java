package net.punter.accounting.service.test;


import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.domain.Balance;
import net.punter.accounting.repository.AccountRepository;
import net.punter.accounting.repository.AccountTransactionRepository;
import net.punter.accounting.service.AccountService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.*;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Went for mockito because I am bit by dependency-woodoo or spring boot
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServicesTest {


    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;

    @Mock
    AccountTransactionRepository accountTransactionRepository;


    Supplier<String> uuidSupplier = () ->
            UUID.randomUUID().toString();

    @Test
    public void contextLoadTest() throws Exception {

    }

    @Test
    public void testDeleteAccount() {
        doNothing().when(accountRepository).deleteById(any(Long.class));
        accountService.deleteAccount(1L);
    }

    @Test
    public void testGetAllAccounts() {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        account.setName("test-case");
        when(accountRepository.findAll()).thenReturn(Arrays.asList(account));
        Collection<Account> allAccounts = accountService.getAllAccounts();
        assertTrue(allAccounts.size() == 1);
    }

    @Test
    public void testGetAllAccountTransactions_whenNoneExist() {
        when(accountRepository.findById(any(Long.class))).thenReturn(Optional.empty());
        List<AccountTransaction> transactions = accountService.getAllAccountTreansactions(999L);
        assertNotNull(transactions);
        assertTrue(transactions.size() == 0);
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

        String reference = accountService.withdraw(accountNumber, accountTransaction);

        Mockito.verify(accountRepository).saveAndFlush(toBeSaved);
        Mockito.verify(accountTransactionRepository).saveAndFlush(accountTransaction);
        Assert.assertEquals(reference, transactionReference);

    }

    @Test
    public void saveAccountTest() {
        Account toBeSaved = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        toBeSaved.setName("test-case");
        when(accountRepository.saveAndFlush(any(Account.class))).then(returnsFirstArg());
        Account saved = accountService.createAccount(toBeSaved);
        assertThat(saved.getName(), equalTo(toBeSaved.getName()));
    }

}
