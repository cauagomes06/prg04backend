package com.fithub.fithub_api.conquista.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.usuario.entity.Usuario; // Verifique se este import bate com seu projeto
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "usuario_conquistas", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"usuario_id", "conquista_id"})
})
public class UsuarioConquista extends PersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conquista_id", nullable = false)
    private Conquista conquista;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime dataDesbloqueio = LocalDateTime.now();
}