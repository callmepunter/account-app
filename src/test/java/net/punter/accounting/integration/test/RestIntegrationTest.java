package net.punter.accounting.integration.test;


import io.restassured.RestAssured;
import net.punter.accounting.domain.Account;
import net.punter.accounting.domain.Balance;
import net.punter.accounting.repository.AccountRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Currency;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("embedded")
public class RestIntegrationTest {

    @Autowired
    AccountRepository accountRepository;

    @LocalServerPort
    int randomServerPort;

    @Value("${spring.application.name}")
    private String applicationRootName;


    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = randomServerPort;
    }


    @Test
    public void contextLoaded() {

    }

    //@Test
    public void createNewAccount() {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        account.setName("integration-test");
        account.credit(new Balance(BigDecimal.valueOf(1000L), Currency.getInstance("AUD")));
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(account)
                .post(applicationRootName + "/api/v1/accounts").prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }

    @Test
    public void getAccount_IfAbsent() {
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get(applicationRootName + "/api/v1/accounts/100").prettyPeek()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAccount_IfPresent() {
        Account targetAccount = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        targetAccount.setName("first");
        targetAccount.credit(new Balance(BigDecimal.valueOf(1000L), Currency.getInstance("AUD")));

        Account persisted = accountRepository.saveAndFlush(targetAccount);
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get(applicationRootName + "/api/v1/accounts/" + persisted.getId()).prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void getAllTransactionForAccountId() {
        Account targetAccount = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        targetAccount.setName("first");
        targetAccount.credit(new Balance(BigDecimal.valueOf(1000L), Currency.getInstance("AUD")));

        Account persisted = accountRepository.saveAndFlush(targetAccount);
        persisted.debit(new Balance(BigDecimal.TEN, Currency.getInstance("AUD")));
        persisted = accountRepository.saveAndFlush(persisted);

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(targetAccount)
                .get(applicationRootName + "/api/v1/accounts/" + persisted.getId() + "/transactions").prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());

        assertThat(persisted.findBalance(Currency.getInstance("AUD")).getAmount()).isEqualTo("990");

    }
}
