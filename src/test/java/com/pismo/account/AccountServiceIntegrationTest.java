package com.pismo.account;

import com.pismo.account.dto.request.AccountRequest;
import com.pismo.account.dto.request.TransactionRequest;
import com.pismo.account.dto.response.AccountResponse;
import com.pismo.account.dto.response.TransactionResponse;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AccountServiceIntegrationTest {

    @LocalServerPort
    private int port;

    private static Long createdAccountId;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    @Test
    @Order(1)
    @DisplayName("Integration Test - Create Account")
    void testCreateAccount() {
        AccountRequest request = new AccountRequest("12345678900");

        AccountResponse response = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/accounts")
                .then()
                .statusCode(201)
                .body("document_number", equalTo("12345678900"))
                .body("account_id", notNullValue())
                .extract()
                .as(AccountResponse.class);

        createdAccountId = response.getAccountId();
    }

    @Test
    @Order(2)
    @DisplayName("Integration Test - Get Account")
    void testGetAccount() {
        given()
                .when()
                .get("/accounts/" + createdAccountId)
                .then()
                .statusCode(200)
                .body("account_id", equalTo(createdAccountId.intValue()))
                .body("document_number", equalTo("12345678900"));
    }

    @Test
    @Order(3)
    @DisplayName("Integration Test - Create Purchase Transaction (Negative Amount)")
    void testCreatePurchaseTransaction() {
        TransactionRequest request = new TransactionRequest(createdAccountId, 1L, new BigDecimal("50.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/transactions")
                .then()
                .statusCode(201)
                .body("account_id", equalTo(createdAccountId.intValue()))
                .body("operation_type_id", equalTo(1))
                .body("amount", equalTo(-50.0f));
    }

    @Test
    @Order(4)
    @DisplayName("Integration Test - Create Payment Transaction (Positive Amount)")
    void testCreatePaymentTransaction() {
        TransactionRequest request = new TransactionRequest(createdAccountId, 4L, new BigDecimal("60.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/transactions")
                .then()
                .statusCode(201)
                .body("account_id", equalTo(createdAccountId.intValue()))
                .body("operation_type_id", equalTo(4))
                .body("amount", equalTo(60.0f));
    }

    @Test
    @Order(5)
    @DisplayName("Integration Test - Create Duplicate Account")
    void testCreateDuplicateAccount() {
        AccountRequest request = new AccountRequest("12345678900");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/accounts")
                .then()
                .statusCode(409);
    }

    @Test
    @Order(6)
    @DisplayName("Integration Test - Get Non-Existent Account")
    void testGetNonExistentAccount() {
        given()
                .when()
                .get("/accounts/999999")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(7)
    @DisplayName("Integration Test - Create Transaction with Invalid Account")
    void testCreateTransactionWithInvalidAccount() {
        TransactionRequest request = new TransactionRequest(999999L, 1L, new BigDecimal("50.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/transactions")
                .then()
                .statusCode(404);
    }

    @Test
    @Order(8)
    @DisplayName("Integration Test - Create Transaction with Invalid Operation Type")
    void testCreateTransactionWithInvalidOperationType() {
        TransactionRequest request = new TransactionRequest(createdAccountId, 999L, new BigDecimal("50.00"));

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/transactions")
                .then()
                .statusCode(404);
    }
}
