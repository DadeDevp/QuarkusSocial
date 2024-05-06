package io.github.devpdade.github.quarkussocial.rest;

import io.github.devpdade.quarkussocial.domain.model.Follower;
import io.github.devpdade.quarkussocial.domain.model.User;
import io.github.devpdade.quarkussocial.domain.respository.FollowerRepository;
import io.github.devpdade.quarkussocial.domain.respository.UserRepository;
import io.github.devpdade.quarkussocial.rest.dto.FollowerRequest;
import io.github.devpdade.quarkussocial.rest.FollowerResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestHTTPEndpoint(FollowerResource.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class FollowerResourceTest {

    @Inject
    UserRepository userRepository;

    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long followerId;

    @BeforeEach
    @Transactional
    void setUp() {
        //User padrao de testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //Criar um user seguidor
        var follower = new User();
        follower.setAge(31);
        follower.setName("Cicrano");
        userRepository.persist(follower);
        followerId = follower.getId();
        
        //cria um follower
        var followerEntity = new Follower();
        followerEntity.setFollower(follower);
        followerEntity.setUser(user);
        followerRepository.persist(followerEntity);

    }

    @Test
    @DisplayName("should return 409 when followerId is equal to userId") //um user tentando seguir ele mesmo
    @Order(1)
    public void sameUserAsFollowerTest(){
        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId) //vem do path do teste
        .when()
                .put()
        .then()
                .statusCode(Response.Status.CONFLICT.getStatusCode())
                .body(Matchers.is("You can't follow yourself"));
    }

    @Test
    @DisplayName("should return 404 on follow a user when userId does not exist") //um user tentando seguir ele mesmo
    @Order(2)
    public void userNotFoundWhenTryingToFollowTest(){
        var inexistentUserId = 99999;

        var body = new FollowerRequest();
        body.setFollowerId(userId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",inexistentUserId) //vem do path do teste
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should follower a user") //um user tentando seguir ele mesmo
    @Order(3)
    public void followerUserTest(){
        var body = new FollowerRequest();
        body.setFollowerId(followerId);

        given()
                .contentType(ContentType.JSON)
                .body(body)
                .pathParam("userId",userId) //vem do path do teste
        .when()
                .put()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }

    @Test
    @DisplayName("should return 404 on list user followers and userId does not exist") //um user tentando seguir ele mesmo
    @Order(4)
    public void userNotFoundWhenListFollowersTest(){
        var inexistentUserId = 99999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",inexistentUserId) //vem do path do teste
        .when()
                .get()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should list users followers") //um user tentando seguir ele mesmo
    @Order(5)
    public void listFollowersTest(){
        var response = given()
                .contentType(ContentType.JSON)
                .pathParam("userId", userId) //vem do path do teste
                .when()
                .get()
                .then()
                .extract().response();

        var followersCount = response.jsonPath().get("followersCount");
        var followerContent = response.jsonPath().getList("content");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatusCode());
        assertEquals(1,followersCount);
        assertEquals(1,followerContent.size());
    }

    @Test
    @DisplayName("should return 404 on unfollow user and user id doesnt exist") //um user tentando seguir ele mesmo
    @Order(4)
    public void userNotFoundWhenUnfollowingAUserTest(){
        var inexistentUserId = 99999;

        given()
                .contentType(ContentType.JSON)
                .pathParam("userId",inexistentUserId) //vem do path do teste
                .queryParam("followerId",followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NOT_FOUND.getStatusCode());
    }

    @Test
    @DisplayName("should Unfollow an user") //um user tentando seguir ele mesmo
    @Order(4)
    public void unfollowUserTest(){

        given()
                .pathParam("userId",userId) //vem do path do teste
                .queryParam("followerId",followerId)
        .when()
                .delete()
        .then()
                .statusCode(Response.Status.NO_CONTENT.getStatusCode());
    }
}