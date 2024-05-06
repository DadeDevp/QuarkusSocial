package io.github.devpdade.quarkussocial.domain.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "post_text")
    private String text;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;

    @ManyToOne // Muitos posts para um User
    @JoinColumn(name = "user_id")
    private User user;

    @PrePersist //Antes dele prepersistir ele seta o DateTime
    public void prePersist(){
        setDateTime(LocalDateTime.now());
    }

}