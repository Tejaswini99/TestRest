package com.rest.test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

public class MissingFieldInPostBooksTest {

    @Test
    public void verifyMissingFieldInPostBooks() {
        // Base URL of the API
        RestAssured.baseURI = "http://localhost:8085/books";

        // Missing price field in request body
        String requestBody = "{\"name\": \"New Book\", \"author\": \"Author\"}"; // Missing price

        // Create the request specification
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic("admin", "password")
                .contentType("application/json")
                .body(requestBody); // Body with missing required field

        // Send POST request and get the response
        Response response = requestSpecification.post();

        // Print the response details for debugging
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.prettyPrint());

        // Validate response status code
        response.then().statusCode(400); // Expect 400 Bad Request
    }
}
