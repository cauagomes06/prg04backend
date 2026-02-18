        package com.fithub.fithub_api.treino.entity;

        import com.fithub.fithub_api.avaliacao.entity.Avaliacao;
        import com.fithub.fithub_api.infraestructure.entity.Auditable;
        import com.fithub.fithub_api.infraestructure.entity.PersistenceEntity;
        import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
        import com.fithub.fithub_api.usuario.entity.Usuario;
        import jakarta.persistence.*;
        import lombok.*;
        import lombok.experimental.SuperBuilder;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.HashSet;
        import java.util.List;
        import java.util.Set;

        @Getter
        @Setter
        @SuperBuilder
        @AllArgsConstructor
        @NoArgsConstructor
        @Table(name = "treinos")
        @Entity
        public class Treino extends PersistenceEntity implements Serializable {


            @Column(nullable = false)
            private String nome;

            @Column
            private String descricao;

            @Enumerated(EnumType.STRING)
            @Column(nullable = false)
            private StatusTreino status;

            @ManyToOne(fetch = FetchType.LAZY)
            @JoinColumn(name = "criador_id" , nullable = false)
            private Usuario criador;

            // Relacionamento com os itens do treino (Um treino tem Muitos Itens)
            @OneToMany(
                    mappedBy = "treino", // "treino" é o nome do campo na classe ItemTreino
                    cascade = CascadeType.ALL,
                    orphanRemoval = true
            )
            private List<ItemTreino> itensTreino = new ArrayList<>();

            // Lista de alunos que seguem este treino
            @ManyToMany(mappedBy = "treinosAssinados")
            private Set<Usuario> alunosSeguidores = new HashSet<>();



            @Column(name = "media_nota", columnDefinition = "float8 default 0.0")
            private Double mediaNota = 0.0;

            @Column(name = "total_avaliacoes", columnDefinition = "integer default 0")
            private Integer totalAvaliacoes = 0;

            // Relacionamento inverso
            @OneToMany(mappedBy = "treino", cascade = CascadeType.ALL, orphanRemoval = true)
            private List<Avaliacao> avaliacoes = new ArrayList<>();
        }
