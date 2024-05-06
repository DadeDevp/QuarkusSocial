package io.github.devpdade.github.quarkussocial.rest;

import io.github.devpdade.quarkussocial.rest.dto.ResponseError;
import io.github.devpdade.quarkussocial.rest.dto.CreateUserRequest;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)//Vc define a ordem da execucao dos testes
class UserResourceTest {
    @TestHTTPResource("/users")
    URL apiUrl;

    @Test
    @DisplayName("Should create an user successfully")
    @Order(1) //Esse será o primeiro teste a ser implementado
    public void createUserTest() {
        var user = new CreateUserRequest();
        user.setName("Fulano");
        user.setAge(22);

        Response response = given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiUrl)//endpoint
                .then()
                    .extract()
                    .response(); //pega a resposta

        //Testar se o status code é 201
        assertEquals(201, response.getStatusCode());

        //Testar se gera um id nao nulo
        assertNotNull(response.jsonPath().getString("id"));

    }

    @Test
    @DisplayName("Should return when json is not valid")
    @Order(2)
    public void createUserValidationErrorTest() {
        var user = new CreateUserRequest();
        user.setAge(null);
        user.setName(null);

        var response = given()
                    .contentType(ContentType.JSON)
                    .body(user)
                .when()
                    .post(apiUrl)
                .then()
                    .extract()
                    .response();

        //testar se o status code é 422
        Assertions.assertEquals(ResponseError.UNPROCESSABLE_ENTITY_STATUS, response.getStatusCode());

        //testar se a mensagem de error é Validation Error
        //pega a propriedade message da entidade ResponseError
        assertEquals("Validation Error", response.jsonPath().getString("message"));

        List<Map<String, String>> errors = response.jsonPath().getList("errors");
        assertNotNull(errors.get(0).get("message"));
        assertNotNull(errors.get(1).get("message"));
//        assertEquals("Name is required",errors.get(0).get("message"));
//        assertEquals("Age is required",errors.get(0).get("message"));

    }

    @Test
    @DisplayName("Should list all users")
    @Order(3)
    public void listAllUsersTest() {
        given()
                    .contentType(ContentType.JSON)
                .when()
                .   get(apiUrl)
                .then()
                    .statusCode(200)
                    .body("size()", Matchers.is(1)); // o tamannho da lista vai ser de um elemento pq apenas um foi inserido no createUserTest
    }



}