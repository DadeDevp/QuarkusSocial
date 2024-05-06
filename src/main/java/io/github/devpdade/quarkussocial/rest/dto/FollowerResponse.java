package io.github.devpdade.quarkussocial.rest.dto;

import io.github.devpdade.quarkussocial.domain.model.Follower;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowerResponse {
    private Long id;
    private String name;

    public FollowerResponse(Follower follower){
        //chama o construtor com todos os argumentos
        this(follower.getId(),follower.getFollower().getName());
    }
}
