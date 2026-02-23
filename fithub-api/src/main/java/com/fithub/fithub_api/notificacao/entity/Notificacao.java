// Notificacao.java
package com.fithub.fithub_api.notificacao.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notificacoes")
public class Notificacao extends PersistenceEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_usuario_id", nullable = false)
    private Usuario destinatario;

    @Column(nullable = false)
    private String titulo; // Ex: "Nova Conquista!"

    @Column(nullable = false, length = 500)
    private String mensagem;

    @Column(nullable = false)
    private String tipo; // Ex: "CONQUISTA", "SISTEMA", "COMPETICAO"

    @Column(nullable = false)
    private boolean lida = false;

    @Column
    private String link;
}