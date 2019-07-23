package net.punter.accountapp.integration.test;


import io.restassured.RestAssured;
import net.punter.accountapp.controllers.AccountController;
import net.punter.accountapp.domains.Account;
import net.punter.accountapp.domains.AccountTransaction;
import net.punter.accountapp.domains.Balance;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Currency;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
public class IntegrationTest {

    @Autowired
    AccountController controller;

    @LocalServerPort
    int randomServerPort;



    @Before
    public void setup() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = randomServerPort;
    }


    @Test
    public void ping() {
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("account-app/ping").prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());

    }

    @Test
    public void createNewAccount() {
        Account account = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        account.setName("integration-test");
        account.addBalance(new Balance(BigDecimal.valueOf(1000L),Currency.getInstance("AUD")));
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(account)
                .post("account-app/api/v1/accounts").prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value());

    }

    @Test
    public void getAccount_IfAbsent() {
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("account-app/api/v1/accounts/1").prettyPeek()
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value());
    }

    @Test
    public void getAccount_IfPresent() {
        Account first = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        first.setName("first");
        first.addBalance(new Balance(BigDecimal.valueOf(1000L),Currency.getInstance("AUD")));
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(first)
                .post("account-app/api/v1/accounts").prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("account-app/api/v1/accounts/1").prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());
    }

    @Test
    public void performTransaction_OnAccount() {
        Account first = new Account(Account.ACCOUNT_TYPE.SAVINGS);
        first.setName("first");
        first.addBalance(new Balance(BigDecimal.valueOf(1000L),Currency.getInstance("AUD")));
        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(first)
                .post("account-app/api/v1/accounts").prettyPeek()
                .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .when()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .get("account-app/api/v1/accounts/1").prettyPeek()
                .then()
                .statusCode(HttpStatus.OK.value());
    }
}
