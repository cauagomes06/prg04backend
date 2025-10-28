package com.fithub.fithub_api.usuario.entity;

import com.fithub.fithub_api.infraestructure.entity.Auditable;
import com.fithub.fithub_api.perfil.entity.Perfil;
import com.fithub.fithub_api.pessoa.entity.Pessoa;
import com.fithub.fithub_api.plano.entity.Plano;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;


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

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "pessoa_id", referencedColumnName = "id", unique = true)
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "perfil_id",nullable = false)
    private Perfil perfil;

    @ManyToOne
    @JoinColumn(name = "plano_id",nullable = false)
    private Plano plano;

    @Enumerated(EnumType.STRING) // Diz ao JPA para salvar o nome do enum,  ex: "ATIVO"
    @Column(name = "status_plano")
    private StatusPlano statusPlano;

    @Column(name = "data_vencimento_plano")
    private LocalDate dataVencimentoPlano;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id == usuario.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
