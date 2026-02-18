package com.fithub.fithub_api.avaliacao.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.treino.entity.Treino;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "avaliacoes", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "treino_id"}) // Um usuário só pode dar 1 nota por treino
})
public class Avaliacao extends PersistenceEntity {


    @Column(nullable = false)
    private Integer nota; // 1 a 5

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;
}
