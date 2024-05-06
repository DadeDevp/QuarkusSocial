package io.github.devpdade.github.quarkussocial.rest;

import io.github.devpdade.quarkussocial.domain.model.Follower;
import io.github.devpdade.quarkussocial.domain.model.Post;
import io.github.devpdade.quarkussocial.domain.model.User;
import io.github.devpdade.quarkussocial.domain.respository.FollowerRepository;
import io.github.devpdade.quarkussocial.domain.respository.PostRepository;
import io.github.devpdade.quarkussocial.domain.respository.UserRepository;
import io.github.devpdade.quarkussocial.rest.dto.CreatePostRequest;
import io.github.devpdade.quarkussocial.rest.PostResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
@TestHTTPEndpoint(PostResource.class) //já identifica a url usada na classe PostResource
class PostResourceTest {

    @Inject
    UserRepository userRepository;
    @Inject
    PostRepository postRepository;
    @Inject
    FollowerRepository followerRepository;

    Long userId;
    Long userNotFollowerId;
    Long userFollowerId;

    @BeforeEach //executa antes  de cada um dos testes
    @Transactional
    public void setUP(){
        //User padrao de testes
        var user = new User();
        user.setAge(30);
        user.setName("Fulano");
        userRepository.persist(user);
        userId = user.getId();

        //Criar uma postagem
        Post post = new Post();
        post.setText("Hello");
        post.setUser(user);
        postRepository.persist(post);

        //User que nao segue o User Padrao
        var userNotFollower = new User();
        user.setAge(33);
        user.setName("Cicrano");
        userRepository.persist(userNotFollower);
        userNotFollowerId = userNotFollower.getId();

        //User seguidor do User Padrao
        var userFollower = new User();
        user.setAge(34);
        user.setName("Beltrano");
        userRepository.persist(userFollower);
        userFollowerId = userFollower.getId();

        Follower follower = new Follower();
        follower.setUser(user);
        follower.setFollower(userFollower);
        followerRepository.persist(follower);
    }

    @Test
    @DisplayName("should create a post for a user")
    void createPostTeste() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId",userId)
        .when()
                .post()
        .then()
                .statusCode(201);
    }

    @Test
    @DisplayName("should return 404 when trying to make a post for an inexistent user")
    void postForInexistentUserTeste() {
        var postRequest = new CreatePostRequest();
        postRequest.setText("Some text");

        var inexistentUserId = 99;

        given()
                .contentType(ContentType.JSON)
                .body(postRequest)
                .pathParam("userId",inexistentUserId)
        .when()
                .post()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 404 when user doesn't exist")
    public void listPostUserNotFoundTest(){
        var inexistentUserId = 999;

        given()
                .pathParam("userId",inexistentUserId)
        .when()
                .get()
        .then()
                .statusCode(404);
    }

    @Test
    @DisplayName("should return 400 when followerId header is not present")
    public void listPostFollowerHeaderNotSendTest(){

        given()
                .pathParam("userId", userId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("You forgot the header followerId!"));
    }

    @Test
    @DisplayName("should return 400 when follower doesn't exist")
    public void listPostFollowerNotFoundTest(){

        var inexistentFollowerId = 99;

        given()
                .pathParam("userId", userId)
                .header("followerId",inexistentFollowerId)
        .when()
                .get()
        .then()
                .statusCode(400)
                .body(Matchers.is("Inexistent followerId"));
    }

    @Test
    @DisplayName("should return 403 when follower isn't a follower") //403 forbidden
    public void listPostNotAFollowerTest(){

        given()
                .pathParam("userId", userId)
                .header("followerId",userNotFollowerId)
        .when()
                .get()
        .then()
                .statusCode(403)
                .body(Matchers.is("You can't see these posts"));

    }

    @Test
    @DisplayName("should list posts")
    public void listPostTest(){
        given()
            .pathParam("userId", userId)
            .header("followerId",userFollowerId)
        .when()
            .get()
        .then()
            .statusCode(200)
            .body("size()", Matchers.is(1));
    }
}