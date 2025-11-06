package com.fithub.fithub_api.reserva.entity;

import com.fithub.fithub_api.aula.entity.Aula;
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
@Table(name = "reservas",
        // Restrição para garantir que um usuario não reserve a mesma aula duas vezes
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "aula_id"})
)
public class Reserva extends Auditable implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Muitas Reservas podem ser de UM Utilizador
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    // Muitas Reservas podem ser para UMA Aula
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aula_id", nullable = false)
    private Aula aula;
}
