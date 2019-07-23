package net.punter.accountapp.integration.test;


import net.punter.accountapp.controllers.AccountController;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.boot.actuate.metrics.web.client.RestTemplateExchangeTags.uri;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("dev")
public class AccountAppIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;
    @Autowired
    AccountController controller;

    @LocalServerPort
    int randomServerPort;


    private URI getApiAddress(String location){
        return testRestTemplate.getRestTemplate().getUriTemplateHandler().expand(location);
    }

    private ResponseEntity<Account> createNewAccount(String accountName){

        Account requested = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        requested.setName(accountName);
        RequestEntity requestEntity = RequestEntity
                .post(getApiAddress("/account-app/api/v1/accounts"))
                .accept(APPLICATION_JSON)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(requested);
        ResponseEntity<Account> responseEntity = testRestTemplate.exchange(requestEntity, Account.class);
        return responseEntity;
    }

    @Test
    public void ping() {
        RequestEntity requestEntity = RequestEntity
                .get(getApiAddress("/account-app/ping"))
                .accept(APPLICATION_JSON)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .build();
        ResponseEntity<String> responseEntity = testRestTemplate.exchange(requestEntity, String.class);
        assertEquals(OK, responseEntity.getStatusCode());

    }

    @Test
    public void test_create_new_account() {
        String holderName = "john doe";
        ResponseEntity<Account> responseEntity = createNewAccount(holderName);
        assertEquals(OK, responseEntity.getStatusCode());
        assertTrue(responseEntity.getBody().getId() > Long.valueOf(0l));
        assertTrue(holderName.equals(responseEntity.getBody().getName()));
        assertTrue(holderName.equals(responseEntity.getBody().getName()));
    }

    @Test
    public void transact_on_existing_account() {
        String holderName = "john doe";
        ResponseEntity<Account> responseEntity = createNewAccount(holderName);
        AccountTransaction accountTransaction = new AccountTransaction();
        accountTransaction.setAmount(BigDecimal.TEN);
        accountTransaction.setCurrency(Currency.getInstance("AUD"));
        accountTransaction.setType(AccountTransaction.TYPE.CREDIT);

        RequestEntity requestEntity = RequestEntity
                .post(getApiAddress("/account-app/api/v1/accounts/"+responseEntity.getBody().getId()+"/transaction"))
                .accept(APPLICATION_JSON)
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(accountTransaction);
        ResponseEntity<Set<Balance>> balances = testRestTemplate.exchange(requestEntity, new ParameterizedTypeReference<Set<Balance>>() {});
        System.out.println(balances);

    }
}
