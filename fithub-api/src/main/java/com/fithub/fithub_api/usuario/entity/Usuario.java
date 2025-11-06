package com.fithub.fithub_api.usuario.entity;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.treino.entity.Treino;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@Entity
@Table(name ="usuarios")
public class Usuario extends Auditable implements Serializable  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(name = "username", unique = true,nullable = false,length = 50)
    private String username;

    @Column(name = "senha", nullable = false, length = 255)
    private String password;
    // relacionamento com pessoa(guarda os dados pessoais)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", referencedColumnName = "id", unique = true)
    private Pessoa pessoa;

    //relacionamentos de perfil e plano
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id",nullable = false)
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "plano_id",nullable = false)
    private Plano plano;

    //Relacionamentos de Assinatura
    @Enumerated(EnumType.STRING) // Diz ao JPA para salvar o nome do enum,  ex: "ATIVO"
    @Column(name = "status_plano")
    private StatusPlano statusPlano;

    @Column(name = "data_vencimento_plano")
    private LocalDate dataVencimentoPlano;


    // --- Relacionamentos de Funcionalidades ---

    // Lista de treinos que este usuário criou
    @OneToMany(mappedBy = "criador")
    private Set<Treino> treinosCriados = new HashSet<>();

    // Lista de aulas que este usuário (como instrutor) ministra
    @OneToMany(mappedBy = "instrutor")
    private Set<Aula> aulasMinistradas = new HashSet<>();

    // Lista de reservas que este usuário (como cliente) fez
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reserva> reservas = new HashSet<>();

    // Um Usuário pode se increver em varias competicoes
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscricao> inscricoes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return Objects.equals(id, usuario.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
