package com.fithub.fithub_api.aula.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.usuario.entity.Usuario;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name ="aulas")
public class Aula extends Auditable implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 500)
    private String descricao;

    @Column(nullable = false)
    private LocalDateTime dataHoraInicio;

    @Column(nullable = false)
    private Integer duracaoMinutos;

    @Column(nullable = false)
    private Integer vagasTotais;

    // Relacionamento com o Instrutor
    // Muitas Aulas podem ser ministradas por UM Usuário (Instrutor)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instrutor_usuario_id", nullable = false)
    private Usuario instrutor;

    // Relacionamento com as reservas
    // Uma Aula pode ter muitas Reservas
    @OneToMany(mappedBy = "aula", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reserva> reservas = new HashSet<>();

}
