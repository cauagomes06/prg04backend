package com.fithub.fithub_api.inscricao.entity;

import com.fithub.fithub_api.competicao.entity.Competicao;
import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "inscricoes_competicoes",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id","competicao_id"})) //usuario so pode se increver uma vez na mesma competicao
public class Inscricao extends PersistenceEntity implements Serializable {


    // Muitos-para-Um: Muitas inscrições para UM Usuário
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Muitos-para-Um: Muitas inscrições para UMA Competição
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competicao_id", nullable = false)
    private Competicao competicao;

    // O resultado/pontuacao do utilizador
    @Column(length = 50)
    private String resultado;

    private LocalDateTime dataSubmissao;

}
