package com.fithub.fithub_api.pessoa.entity;

import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
import com.fithub.fithub_api.usuario.entity.Usuario;
import com.fithub.fithub_api.infraestructure.entity.Auditable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "pessoas")
public  class Pessoa extends PersistenceEntity implements Serializable {

    @Column(nullable = false,length = 100)
    private String nomeCompleto;

    @Column(nullable = false,unique = true,length = 11)
    private String cpf;

    @Column(nullable = false)
    private String telefone;

    // Relacionamento Inverso: A classe Pessoa não tem a chave estrangeira.
    // 'mappedBy = "pessoa"' diz: "A configuração desta relação está na classe Usuario, no campo chamado 'pessoa'".
    @OneToOne(mappedBy = "pessoa")
    private Usuario usuario;


}
