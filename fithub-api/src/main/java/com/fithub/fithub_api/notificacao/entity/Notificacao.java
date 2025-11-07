package com.fithub.fithub_api.notificacao.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "notificacoes")
public class Notificacao extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento: Muitas notificações para UM destinatário (Usuário)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destinatario_usuario_id", nullable = false)
    private Usuario destinatario;

    @Column(nullable = false, length = 500)
    private String mensagem; // Ex: "Parabéns! Você venceu o Desafio de Supino!"

    @Column(nullable = false)
    private boolean lida = false; // Começa como "não lida" por defeito

    @Column
    private String link; // (Opcional) URL para onde o utilizador é redirecionado (ex: "/competicoes/1")
}
