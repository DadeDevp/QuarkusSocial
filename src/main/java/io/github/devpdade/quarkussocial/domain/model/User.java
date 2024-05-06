package io.github.devpdade.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data

//User extends PanacheEntityBase, neste caso eu nao uso o repository
public class User{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //autoincrement e a estrategia é delegada ao banco
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "age")
    private Integer age;

}

