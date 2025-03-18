package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class MainTest {

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = "http://localhost:8085/books";
    }

    // 1. Invalid Credentials
    @Test
    public void testInvalidCredentials() {
        given().auth().preemptive().basic("wrongUser", "wrongPass")
                .when().get()
                .then().statusCode(401);
    }

    // 2. Missing Authentication Header
    @Test
    public void testMissingAuthHeader() {
        given().when().get()
                .then().statusCode(401);
    }

    // 3. Invalid URL
    @Test
    public void testInvalidURL() {
        given().auth().preemptive().basic("admin", "password")
                .when().get("/invalidEndpoint")
                .then().statusCode(400);
    }

    // 4. Missing Required Field in POST
    @Test
    public void testMissingFieldInPost() {
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Book Name\", \"author\":\"Author Name\"}") // Missing price field
                .when().post()
                .then().statusCode(400);
    }

    // 5. Invalid Data Type for Price
    @Test
    public void testInvalidPriceType() {
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Book Name\", \"author\":\"Author\", \"price\":\"invalidPrice\"}")
                .when().post()
                .then().statusCode(400);
    }

    // 6. Malformed JSON in POST
    @Test
    public void testMalformedJson() {
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"Book Name\", \"author\":\"Author\", \"price\":15.41") // Missing closing brace
                .when().post()
                .then().statusCode(400);
    }

    // 7. Requesting Non-Existent Book ID
    @Test
    public void testNonExistentBookId() {
        given().auth().preemptive().basic("admin", "password")
                .when().get("/9999") // Assuming ID 9999 does not exist
                .then().statusCode(404);
    }

    // 8. Sending Negative ID
    @Test
    public void testNegativeId() {
        given().auth().preemptive().basic("admin", "password")
                .when().get("/-1")
                .then().statusCode(400);
    }

    // 9. Sending Non-Numeric ID
    @Test
    public void testNonNumericId() {
        given().auth().preemptive().basic("admin", "password")
                .when().get("/abc")
                .then().statusCode(400);
    }

    // 10. Updating Non-Existent Book
    @Test
    public void testUpdateNonExistentBook() {
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"id\":9999, \"name\":\"Updated Book\", \"author\":\"Author\", \"price\":10.99}")
                .when().put("/9999")
                .then().statusCode(500);
    }

    // 11. Updating a Book with Missing Fields
    @Test
    public void testUpdateBookMissingFields() {
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"id\":1, \"name\":\"Updated Book\"}") // Missing author and price
                .when().put("/1")
                .then().statusCode(500);
    }

    // 12. Invalid Authorization Role for POST
    @Test
    public void testInvalidRoleForPost() {
        given().auth().preemptive().basic("user", "password") // 'user' role should not be allowed to create books
                .header("Content-Type", "application/json")
                .body("{\"name\":\"New Book\", \"author\":\"Author\", \"price\":9.99}")
                .when().post()
                .then().statusCode(403);
    }

    // 13. Deleting Non-Existent Book
    @Test
    public void testDeleteNonExistentBook() {
        given().auth().preemptive().basic("admin", "password")
                .when().delete("/9999")
                .then().statusCode(500);
    }

    // 14. Using GET Instead of POST
    @Test
    public void testGetInsteadOfPost() {
        given().auth().preemptive().basic("admin", "password")
                .when().post("/1") // Using POST instead of GET
                .then().statusCode(405);
    }

    // 15. Exceeding Character Limit for Book Name
    @Test
    public void testBookNameTooLong() {
        String longName = "A".repeat(501); // Assuming 500 is the max limit
        given().auth().preemptive().basic("admin", "password")
                .header("Content-Type", "application/json")
                .body("{\"name\":\"" + longName + "\", \"author\":\"Author\", \"price\":10.99}")
                .when().post()
                .then().statusCode(400);
    }
}
