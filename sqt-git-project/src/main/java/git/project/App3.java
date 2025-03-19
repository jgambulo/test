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

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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

    //Part 3: Test the GET /books/{id} endpoint
    @Test
    @Order(3)
    public void testGetBookById() {
    	int bookId = 4;

        Response response = given().auth().basic("user", "password")
                            .contentType("application/json")
                            .when().get("http://localhost:8085/books/" + bookId)
                            .then().statusCode(200)
                            .extract().response();

        // Validate the book details
        response.then().body("id", equalTo(bookId))
                .body("name", equalTo("A to the Bodhisattva Way of Life"))
                .body("author", equalTo("Santideva"))
                .body("price", equalTo(15.41F));
    }

    //Part 4: Test the PUT /books/{id} endpoint
    @Test
   	@Order(4)
    public void testUpdateBook() {
    	int bookId = 1;

        String updatedRequestBody = "{\n" +
                "    \"id\": 1,\n" +
                "    \"name\": \"A to the Bodhisattva Way of Life\",\n" +
                "    \"author\": \"Santideva\",\n" +
                "    \"price\": 20.00\n" +
                "}";

        Response response = given().auth().basic("admin", "password")
                           	.contentType("application/json")
                            .body(updatedRequestBody)
                            .when().put("http://localhost:8085/books/" + bookId)
                            .then().statusCode(200)
                            .extract().response();

        // Validate the updated book details
        response.then().body("price", equalTo(20.00f));
    }
/*
    //Part 5: Test the DELETE /books/{id} endpoint
    @Test
    @Order(5)
    public void testDeleteBook() {
    	int bookId = 6;

        given().auth().basic("admin", "password")
               .contentType("application/json")
               .when().delete("http://localhost:8085/books/" + bookId)
               .then().statusCode(200)
               .extract().response();

        // Optionally, check if the book is deleted by trying to get the same book
        given().auth().basic("admin", "password")
               .when().get("http://localhost:8085/books/" + bookId)
               .then().statusCode(404); // Expecting 404 Not Found
    }
*/
    
   	//Test Get Book with Invalid ID
    @Test
   	@Order(6)
    public void testGetBookWithInvalidId() {
    	given()
    	.auth().basic("admin", "password")
        .contentType("application/json")
        .when().get("/books/999999")
        .then()
        .statusCode(404)
        .body("error", equalTo("Not Found"));

    }
    
    //Test Create Book with Missing Fields
    @Test
    @Order(7)
    public void testCreateBookWithMissingFields() {
        String requestBody = "{\"title\": \"\", \"author\": \"\", \"isbn\": \"\"}";
        
     
        given()
            .auth().basic("admin", "password") 
            .contentType("application/json")     
            .body(requestBody)                  
            .when().post("http://localhost:8085/books")        
            .then()
            .statusCode(400);       // Expect 400 Bad Request for missing fields
         
    }


    //Test Create Book with Invalid Data
    @Test
    @Order(8)
    public void testCreateBookWithInvalidData() {
        String requestBody = "{\"title\": \"@BadTitle\", \"author\": \"John123\", \"isbn\": \"invalidISBN\"}";

        given()
	        .auth().basic("admin", "password") 
	        .contentType("application/json")
            .body(requestBody)
            .when().post("http://localhost:8085/books")
            .then()
            .statusCode(400); // Expect 400 Bad Request for Invalid data format
    }
  
    //Test Unauthorized Access
    @Test
    @Order(9)
    public void testUnauthorizedAccess() {
        given()
            .when().get("/books")
            .then()
            .statusCode(401)
            .body("error", equalTo("Unauthorized"));
    }

    
    //Test Update Book with Invalid ID
    @Test
    @Order(10)
    public void testUpdateBookWithInvalidId() {
        String updatedRequestBody = "{\"title\": \"Updated Book\", \"author\": \"Jane Doe\", \"isbn\": \"654321\"}";

        given()
	        .auth().basic("admin", "password") 
	        .contentType("application/json")
            .body(updatedRequestBody)
            .when().put("/books/999999") // Non-existent book ID
            .then()
            .statusCode(404)
            .body("error", equalTo("Not Found"));
    }
    
    //Test Delete Book with Invalid ID
    @Test
    @Order(11)
    public void testDeleteBookWithInvalidId() {
        given()
	        .auth().basic("admin", "password") 
	        .contentType("application/json")
            .when().delete("/books/999999") // Assuming this ID does not exist
            .then()
            .statusCode(404)
            .body("error", equalTo("Not Found"));
    }
  
    //Test Get Books Pagination
    @Test
    @Order(12)
    public void testGetBooksWithPagination() {
        given()
	        .auth().basic("admin", "password") 
	        .contentType("application/json")
            .queryParam("page", 1)
            .queryParam("limit", 5)
            .when().get("http://localhost:8085/books")
            .then()
            .statusCode(200);
            //.body("size()", equalTo(24));
    }

}
