package io.github.devpdade.quarkussocial.rest;

import io.github.devpdade.quarkussocial.domain.model.Post;
import io.github.devpdade.quarkussocial.domain.model.User;
import io.github.devpdade.quarkussocial.domain.respository.FollowerRepository;
import io.github.devpdade.quarkussocial.domain.respository.PostRepository;
import io.github.devpdade.quarkussocial.domain.respository.UserRepository;
import io.github.devpdade.quarkussocial.rest.dto.CreatePostRequest;
import io.github.devpdade.quarkussocial.rest.dto.PostResponse;
import io.github.devpdade.quarkussocial.rest.dto.ResponseError;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.panache.common.Sort;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Set;

@Path("users/{userId}/posts")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class PostResource {

    @Inject
    UserRepository userRepository;
    @Inject
    PostRepository postRepository;
    @Inject
    FollowerRepository followerRepository;
    @Inject
    Validator validator;

    @GET
    public Response listPosts(@PathParam("userId") Long userId, 
                              @HeaderParam("followerId") Long followerId) {

        //Verifica se o id retorna algum User
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //Verifica se ele passou header na requisicao
        if (followerId == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("You forgot the header followerId!").build();
        }

        //Pesquisa o follower
        User follower = userRepository.findById(followerId);

        //Verifica se o follower existe
        if (follower == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Inexistent followerId").build();
        }

        //Verifica se o follower segue o User
        boolean follows = followerRepository.follows(follower,user);
        //Se nao segue nao pode ver os posts
        if (!follows) {
            return Response.status(Response.Status.FORBIDDEN).entity("You can't see these posts").build();
        }

        //Ou eu posso fazer um select dentro do metodo find
        //Dentro do find estou filtrando a lista de posts pela propriedade user e ordenando pelo dateTime decrescente
        PanacheQuery<Post> query = postRepository.find("user", Sort.by("dateTime", Sort.Direction.Descending) , user);
        List<PostResponse> list = query.stream()
                                        //.map(post -> PostResponse.fromEntity(post))
                                        .map(PostResponse::fromEntity)
                                        .toList();

        return Response.ok(list).build();
    }

    @POST
    @Transactional
    public Response savePost(@PathParam("userId") Long userId, CreatePostRequest createPostRequest) {

        //verifica as validacoes
        Set<ConstraintViolation<CreatePostRequest>> violations = validator.validate(createPostRequest);

        if (!violations.isEmpty()) {
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        //Verifica se o id retorna algum User
        User user = userRepository.findById(userId);
        if (user == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        //Criar um post novo e persiste no banco
        Post newPost = new Post();
        newPost.setText(createPostRequest.getText());
        newPost.setUser(user);
        postRepository.persist(newPost);

        return Response.status(Response.Status.CREATED).build();
    }
}
