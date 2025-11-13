        package com.fithub.fithub_api.treino.entity;

        import com.fithub.fithub_api.infraestructure.entity.Auditable;
        import com.fithub.fithub_api.itemtreino.entity.ItemTreino;
        import com.fithub.fithub_api.usuario.entity.Usuario;
        import jakarta.persistence.*;
        import lombok.Getter;
        import lombok.NoArgsConstructor;
        import lombok.Setter;

        import java.io.Serializable;
        import java.util.ArrayList;
        import java.util.List;

        @Getter
        @Setter
        @NoArgsConstructor
        @Table(name = "treinos")
        @Entity
        public class Treino extends Auditable implements Serializable {

            @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
            @Column(name = "treino_id")
            private Long id;

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
        }
