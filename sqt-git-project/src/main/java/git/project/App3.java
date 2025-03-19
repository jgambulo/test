package git.project;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;



import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;


public class App3 {

	//private static int bookId;
	 RequestSpecification requestSpecification;
	 Response response;
	 ValidatableResponse validatableResponse;

    @BeforeAll
    public static void setup() {
    	 // Base URL of the API
        RestAssured.baseURI = "http://localhost:8085/books";
        
        String username = "user"; 
        String password = "password";
        
     // Create the request specification
        RequestSpecification requestSpecification = given()
                .auth().preemptive().basic(username, password) // Use preemptive basic auth
                .log().all(); // Log all request details (headers, body, etc.)
        
        
     // Send GET request and get the response
        Response response = requestSpecification.get();

        // Print the response details for debugging
        System.out.println("Response Status Code: " + response.getStatusCode());
        System.out.println("Response Body: " + response.prettyPrint());
        System.out.println("Response Headers: " + response.getHeaders());

        // Perform validation on the response
        ValidatableResponse validatableResponse = response.then();

        /* Validate status code */
        validatableResponse.statusCode(200);

        // Validate status line
        validatableResponse.statusLine("HTTP/1.1 200 ");
    }
    
    
    //Part 1: Test the GET /books endpoint
    @Test
   	@Order(1)
    public void testGetBooks() {
        Response response = given()
                .auth().basic("user", "password")
                .contentType("application/json")
	            .when().get("http://localhost:8085/books")
	            .then().statusCode(200)
	            .extract().response();

		// Validate pagination and books data
		response.then()
		.body("", hasSize(greaterThanOrEqualTo(2))); // Ensures at least 2 books exist
	    
//		.body("[1].id", equalTo(1))
//	    .body("[1].name", equalTo("A Guide to the Bodhisattva Way of Life"))
//	    .body("[1].author", equalTo("Santideva"))
//	    .body("[1].price", equalTo(15.41F))
//		
//		.body("[2].id", equalTo(2))
//	    .body("[2].name", equalTo("The Life-Changing Magic of Tidying Up"))
//	    .body("[2].author", equalTo("Marie Kondo"))
//	    .body("[2].price", equalTo(9.69F));
    }
   
    //Part 2: Test the POST /books endpoint
    @Test
    @Order(2)
    public void testCreateBook() {
    	String requestBody = "{\n" +
                "    \"name\": \"A to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 15.41\n" +
                "}";

        Response response = given().auth().basic("admin", "password")
                            .contentType("application/json")
                            .body(requestBody)
                            .when().post("http://localhost:8085/books")
                            .then().statusCode(201)
                            .extract().response();

        // Validate the response body
        response.then().body("name", equalTo("A to the Bodhisattva Way of Life"))
                .body("author", equalTo("Santideva"))
                .body("price", equalTo(15.41f));
    }


}
