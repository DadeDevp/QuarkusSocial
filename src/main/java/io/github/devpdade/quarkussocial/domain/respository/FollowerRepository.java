package io.github.devpdade.quarkussocial.domain.respository;

import io.github.devpdade.quarkussocial.domain.model.Follower;
import io.github.devpdade.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import io.quarkus.panache.common.Parameters;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class FollowerRepository implements PanacheRepository<Follower> {
    //Metodo para verificar se o follower já segue o user
    public boolean follows(User follower, User user) {
        //Uma outra opcao de fazer:
        //Map<String, Object> params = new HashMap<>();
        //params.put("follower", follower);
        //params.put("user", user);

        //Cria os parametros da query e executa a mesma no metodo find
        var params = Parameters.with("follower", follower).and("user", user).map();
        PanacheQuery<Follower> query = find("follower =:follower and user =:user", params);

        //Pega o primeiro resultado da query
        Optional<Follower> result = query.firstResultOptional();

        return result.isPresent();
    }

    public List<Follower> findByUser(Long userId) {
        //Lista todos os seguidores de determinado user
        PanacheQuery<Follower> query = find("user.id", userId);
        return query.list();
    }

    public void deleteByFollowerAndUser(Long followerId, Long userId) {
        var params = Parameters
                .with("userId", userId)
                .and("followerId", followerId)
                .map();
        delete("follower.id =:followerId and user.id=:userId",params);
    }
}
