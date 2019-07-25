package net.punter.accounting.controller.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.punter.accounting.controller.AccountCommandController;
import net.punter.accounting.controller.AccountQueryController;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.AccountTransaction;
import net.punter.accounting.repository.AccountRepository;
import net.punter.accounting.service.AccountService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {AccountQueryController.class, AccountCommandController.class})
public class AccountsApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private AccountRepository accountRepository;

    @Before
    public void beforeSetup() {

    }


    @Test
    public void contextLoadTest() throws Exception {
    }

    @Test
    public void testGetAllAccounts() throws Exception {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        List<Account> accountList = Arrays.asList(account);
        when(accountRepository.findAll()).thenReturn(Arrays.asList(account));
        MvcResult mvcResult = mockMvc.perform(get(AccountQueryController.PATH)
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk()).andReturn();


        String actualResponseBody =
                mvcResult.getResponse().getContentAsString();
        String expectedResponseBody =
                new ObjectMapper().writeValueAsString(accountList);
        assertThat(expectedResponseBody)
                .isEqualToIgnoringWhitespace(actualResponseBody);

    }

    @Test
    public void testGetAccount() throws Exception {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        account.setName("max-steel");
        when(accountRepository.findById(Long.valueOf(787L))).thenReturn(Optional.of(account));

        MvcResult mvcResult = mockMvc.perform(get(AccountQueryController.PATH + "/787")
                .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().
                        isOk()).
                        andReturn();

        assertThat(mvcResult.getResponse().getContentAsString().contains("max-steel")).isTrue();
    }


    @Test
    public void testCreateNew() throws Exception {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        account.setName("max-steel");
        account.setId(787L);

        when(accountService.createAccount(any(Account.class))).thenReturn(account);

        final ObjectMapper mapper = new ObjectMapper();
        final String accountString = mapper.writeValueAsString(account);

        MvcResult mvcResult = mockMvc.
                perform(post(AccountCommandController.PATH).content(accountString)
                        .contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().
                        isCreated()).
                        andReturn();

        assertThat(mvcResult.getResponse().getContentAsString().contains("787")).isTrue();
        assertThat(mvcResult.getResponse().getContentAsString().contains("max-steel")).isTrue();
    }

    @Test
    public void testTransact() throws Exception {
        String uuid = UUID.randomUUID().toString();
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAmount(BigDecimal.TEN);
        accountTransaction.setCurrency(Currency.getInstance("EUR"));
        accountTransaction.setType(AccountTransaction.TYPE.CREDIT);

        when(accountService.deposit(any(Long.class), any(AccountTransaction.class))).thenReturn(uuid);

        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(accountTransaction);
        String transactionReference = mockMvc.perform(post(AccountCommandController.PATH + "/1/transactions").content(body).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();

        assertThat(transactionReference).isEqualTo(uuid);
    }

    @Test
    public void testGetTransactionsForAccountId() throws Exception {
        String uuid = UUID.randomUUID().toString();
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setId(uuid);
        accountTransaction.setAmount(BigDecimal.TEN);
        accountTransaction.setCurrency(Currency.getInstance("EUR"));
        accountTransaction.setType(AccountTransaction.TYPE.CREDIT);
        account.getAccountTransactions().add(accountTransaction);

        when(accountRepository.findById(any(Long.class))).thenReturn(Optional.of(account));

        ObjectMapper objectMapper = new ObjectMapper();
        String expected = objectMapper.writeValueAsString(Arrays.asList(accountTransaction));

        String responseBody = mockMvc.perform(get(AccountQueryController.PATH + "/1/transactions").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();

        assertThat(responseBody).isEqualToIgnoringWhitespace(expected);
    }
}
