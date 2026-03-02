package com.fithub.fithub_api.conquista.entity;

import com.fithub.fithub_api.conquista.enums.TipoMedalha;
import com.fithub.fithub_api.conquista.enums.TipoMetrica;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "conquistas")
public class Conquista extends PersistenceEntity {

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private String icone; // Ex: "fa-dumbbell"

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoMedalha tipo; // BRONZE, PRATA, OURO

    @Column(unique = true, nullable = false)
    private String chaveTecnica; // Ex: "PRIMEIRO_TREINO"

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_metrica", nullable = false)
    private TipoMetrica tipoMetrica;

    @Column(name = "valor_necessario", nullable = false)
    private Double valorNecessario; // Ex: 1.0 (para 1 treino), 1000.0 (para carga)
}