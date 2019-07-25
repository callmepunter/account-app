package net.punter.accounting.service.test;


import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.domain.Balance;
import net.punter.accounting.repository.AccountRepository;
import net.punter.accounting.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

/**
 * Went for mockito because I am bit by dependency-woodoo or spring boot
 */
@RunWith(MockitoJUnitRunner.class)
public class AccountServiceTest {


    @InjectMocks
    AccountService accountService;

    @Mock
    AccountRepository accountRepository;


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

    /**
     * Saving account has 0 balance
     * Initiate a transaction of 10 EURO
     * Account should end with 10 EURO balance
     * A credit transaction logged for 10 euros.
     *
     * @throws Exception
     */
    @Test
    public void testCredit() throws Exception {
        long accountNumber = 787L;
        String transactionReference = uuidSupplier.get();

        Account spyOfRequestedAccount = Mockito.spy(new Account());
        spyOfRequestedAccount.setType(Account.ACCOUNT_TYPE.SAVINGS);
        spyOfRequestedAccount.setName("test-case");
        spyOfRequestedAccount.setId(accountNumber);

        Balance spyOfRequestedAccountBalance = Mockito.spy(new Balance(Currency.getInstance("EUR")));
        spyOfRequestedAccountBalance.setAmount(BigDecimal.ZERO);
        spyOfRequestedAccount.credit(spyOfRequestedAccountBalance);

        AccountTransaction spyOfInputTransaction = Mockito.spy(new AccountTransaction());
        spyOfInputTransaction.setType(AccountTransaction.TYPE.CREDIT);
        spyOfInputTransaction.setCurrency(Currency.getInstance("EUR"));
        spyOfInputTransaction.setAmount(BigDecimal.TEN);


        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(spyOfRequestedAccount));

        String resultReference = accountService.deposit(accountNumber, spyOfInputTransaction);
        //make sure amount and currency were read.
        Mockito.verify(spyOfInputTransaction).getAmount();
        Mockito.verify(spyOfInputTransaction).getCurrency();
        Mockito.verify(spyOfRequestedAccount).credit(any(Balance.class));
        Mockito.verify(accountRepository).saveAndFlush(any(Account.class));
        //started with 0 balance should be 10 EURO now.
        assertThat(spyOfRequestedAccount.findBalance(Currency.getInstance("EUR")).getAmount(), equalTo(BigDecimal.TEN));
        System.out.println(resultReference);
    }

    /**
     * Starting with a 10 EURO account
     * Transaction is ordered for 10 EURO
     * Balance should become 0 EURO
     * Transaction logged should be of 10 EURO, as it was a debit
     */
    @Test
    public void testDebit() throws Exception {
        long accountNumber = 787L;
        String transactionReference = uuidSupplier.get();

        Account spyOfRequestedAccount = Mockito.spy(new Account());
        spyOfRequestedAccount.setType(Account.ACCOUNT_TYPE.SAVINGS);
        spyOfRequestedAccount.setName("test-case");
        spyOfRequestedAccount.setId(accountNumber);

        Balance spyOfRequestedAccountBalance = Mockito.spy(new Balance(Currency.getInstance("EUR")));
        spyOfRequestedAccountBalance.setAmount(BigDecimal.TEN);
        spyOfRequestedAccount.credit(spyOfRequestedAccountBalance);

        AccountTransaction spyOfInputTransaction = Mockito.spy(new AccountTransaction());
        spyOfInputTransaction.setType(AccountTransaction.TYPE.DEBIT);
        spyOfInputTransaction.setCurrency(Currency.getInstance("EUR"));
        spyOfInputTransaction.setAmount(BigDecimal.TEN);


        when(accountRepository.findById(accountNumber)).thenReturn(Optional.of(spyOfRequestedAccount));

        String resultReference = accountService.withdraw(accountNumber, spyOfInputTransaction);
        //make sure amount and currency were read.
        Mockito.verify(spyOfInputTransaction).getAmount();
        Mockito.verify(spyOfInputTransaction).getCurrency();
        Mockito.verify(spyOfRequestedAccount).debit(any(Balance.class));
        Mockito.verify(accountRepository).saveAndFlush(any(Account.class));
        //started with 10 EURO should be 0 now
        assertThat(spyOfRequestedAccount.findBalance(Currency.getInstance("EUR")).getAmount(), equalTo(BigDecimal.ZERO));
        System.out.println(resultReference);
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
