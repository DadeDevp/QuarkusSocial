package io.github.devpdade.quarkussocial.rest;

import io.github.devpdade.quarkussocial.domain.model.Follower;
import io.github.devpdade.quarkussocial.domain.model.User;
import io.github.devpdade.quarkussocial.domain.respository.FollowerRepository;
import io.github.devpdade.quarkussocial.domain.respository.UserRepository;
import io.github.devpdade.quarkussocial.rest.dto.FollowerRequest;
import io.github.devpdade.quarkussocial.rest.dto.FollowerResponse;
import io.github.devpdade.quarkussocial.rest.dto.FollowersPerUserResponse;
import io.github.devpdade.quarkussocial.rest.dto.ResponseError;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import jakarta.validation.Validator;

import java.util.Set;

@Path("/users/{userId}/followers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FollowerResource {

    @Inject
    FollowerRepository followerRepository;
    @Inject
    UserRepository userRepository;
    @Inject
    Validator validator;

    //Pode usar PUT para criar um recurso
    //O PUT tende a ser imdepotente

    @PUT
    @Transactional
    public Response followUser(@PathParam("userId") Long userId, FollowerRequest followerRequest) {

        //Verifica se o usuario está tentando seguir ele mesmo
        if (userId.equals(followerRequest.getFollowerId())) {
            return Response.status(Response.Status.CONFLICT).entity("You can't follow yourself").build();
        }

        //Verifica as validacoes
        Set<ConstraintViolation<FollowerRequest>> violations = validator.validate(followerRequest);
        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = userRepository.findById(userId);
        User follower = userRepository.findById(followerRequest.getFollowerId());

        //Verificar se o user e o seguidor existem
        if (user == null || follower == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //Verifica se o follower já está seguindo o user, se nao estiver crio e persisto no banco
        boolean follows = followerRepository.follows(follower, user);
        if (!follows) {
            Follower entity = new Follower(user, follower);
            followerRepository.persist(entity);
        }
        return Response.noContent().build();
    }

    @GET
    public Response listFollowers(@PathParam("userId") Long userId) {
        var list = followerRepository.findByUser(userId);

        //Verificar se o user existe
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //Cria e seta o FollowersPerUserResponse
        FollowersPerUserResponse responseObject = new FollowersPerUserResponse();
        responseObject.setFollowersCount(list.size());

        var followerList = list.stream()
                .map(FollowerResponse::new)
                .toList();
        responseObject.setContent(followerList);

        return Response.ok(responseObject).build();
    }

    @DELETE
    @Transactional
    public Response unfollowUser(@PathParam("userId") Long userId, @QueryParam("followerId") Long followerId) {
        //Verificar se o user existe
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        followerRepository.deleteByFollowerAndUser(followerId,userId);
        return Response.noContent().build();
    }


}
