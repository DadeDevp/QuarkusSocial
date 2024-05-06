package io.github.devpdade.quarkussocial.rest;

import io.github.devpdade.quarkussocial.domain.model.User;
import io.github.devpdade.quarkussocial.domain.respository.UserRepository;
import io.github.devpdade.quarkussocial.rest.dto.CreateUserRequest;
import io.github.devpdade.quarkussocial.rest.dto.ResponseError;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;

import java.net.URI;
import java.util.Set;

@Path("/users")
@Consumes(MediaType.APPLICATION_JSON) //nessa api eu vou receber dados no formato json
@Produces(MediaType.APPLICATION_JSON) //nessa api eu vou retornar dados no formato json
public class UserResource {

    @Inject
    UserRepository userRepository;
    @Inject
    Validator validator;

    @GET
    public Response listAllUsers(){
        PanacheQuery<User> users = userRepository.findAll();
        return Response.ok(users.list()).build();
    }
    @POST
    @Transactional //abrir uma transacao com o banco de dados para atualizacao dos dados
    public Response createUser(CreateUserRequest userRequest){

        //Validar o objeto enviado no body
        //A variavel violations recebe os campos que estao inválidos
        Set<ConstraintViolation<CreateUserRequest>> violations = validator.validate(userRequest);

        //Verifica se a lista de violations nao está vazia, se nao estiver é pq existe algum campo inválido
        if (!violations.isEmpty()) {
            //Usa o método estatico da classe response error para criar um responseerror passando como parametro
            //as violations encontradas na requisicao
//            ResponseError responseError = ResponseError.createFromValidation(violations);
//            return Response.status(400).entity(responseError).build();
            return ResponseError.createFromValidation(violations).withStatusCode(ResponseError.UNPROCESSABLE_ENTITY_STATUS);
        }

        User user = new User();
        user.setName(userRequest.getName());
        user.setAge(userRequest.getAge());

        //Salvar no banco
        userRepository.persist(user);

        URI uri = UriBuilder.fromResource(UserResource.class).path("{id}").resolveTemplate("id",user.getId()).build();

        return Response.created(uri).entity(user).build();
    }


    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateUser(@PathParam("id") Long id, CreateUserRequest userRequest){
        User user = userRepository.findById(id);

        if (user != null) {
            user.setName(userRequest.getName());
            user.setAge(userRequest.getAge());
            userRepository.persist(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @Path("/{id}")
    @DELETE
    @Transactional
    public Response deleteUser(@PathParam("id") Long id){
        User user = userRepository.findById(id);

        if (user != null) {
            userRepository.delete(user);
            return Response.noContent().build();
        }
        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
