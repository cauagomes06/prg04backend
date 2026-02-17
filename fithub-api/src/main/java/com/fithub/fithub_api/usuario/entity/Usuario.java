package com.fithub.fithub_api.usuario.entity;

import com.fithub.fithub_api.aula.entity.Aula;
import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.inscricao.entity.Inscricao;
import com.fithub.fithub_api.notificacao.entity.Notificacao;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.plano.entity.Plano;
import com.fithub.fithub_api.reserva.entity.Reserva;
import com.fithub.fithub_api.treino.entity.Treino;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name ="usuarios")
public class Usuario extends PersistenceEntity implements Serializable, UserDetails {


    @Column(name = "username", unique = true,nullable = false,length = 50)
    private String username;

    @Column(name = "senha", nullable = false, length = 255)
    private String password;
    // relacionamento com pessoa(guarda os dados pessoais)
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", referencedColumnName = "id", unique = true)
    private Pessoa pessoa;

    @Column(name = "foto_url")
    private String fotoUrl;

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

    // Treinos que o usuário decidiu seguir
    @ManyToMany
    @JoinTable(
            name = "usuarios_treinos_assinados",
            joinColumns = @JoinColumn(name = "usuario_id"),
            inverseJoinColumns = @JoinColumn(name = "treino_id")
    )
    private Set<Treino> treinosAssinados = new HashSet<>();

    // Lista de aulas que este usuário (como instrutor) ministra
    @OneToMany(mappedBy = "instrutor")
    private Set<Aula> aulasMinistradas = new HashSet<>();

    // Lista de reservas que este usuário (como cliente) fez
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Reserva> reservas = new HashSet<>();

    // Um Usuário pode se increver em varias competicoes
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Inscricao> inscricoes = new HashSet<>();

    // Um Usuário pode ter muitas Notificações
    @OneToMany(mappedBy = "destinatario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Notificacao> notificacoes = new HashSet<>();

    @Column(name = "score_total", nullable = false, columnDefinition = "int default 0")
    private int scoreTotal = 0;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (this.perfil != null) {
            // Assume que perfil.getNome() retorna algo como "ROLE_ADMIN" ou "ADMIN"
            return List.of(new SimpleGrantedAuthority(this.perfil.getNome()));
        }
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
