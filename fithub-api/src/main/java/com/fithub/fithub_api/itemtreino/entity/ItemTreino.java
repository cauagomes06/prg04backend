package com.fithub.fithub_api.itemtreino.entity;

import com.fithub.fithub_api.exercicio.entity.Exercicio;
import com.fithub.fithub_api.treino.entity.Treino;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name ="itens_treino")
public class ItemTreino  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String series;

    @Column(length = 50)
    private String repeticoes;

    @Column(length = 50)
    private String descanso;

    private int ordem; // Para ordenar os exercícios (ex: 1º, 2º, 3º...)

    // --- RELACIONAMENTOS ---

    // Muitos itens pertencem a UM treino
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "treino_id", nullable = false)
    private Treino treino;

    // Muitos itens podem apontar para UM exercício do catálogo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exercicio_id", nullable = false)
    private Exercicio exercicio;
}
