package com.fithub.fithub_api.infraestructure.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@MappedSuperclass
public abstract class PersistenceEntity extends Auditable{

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(nullable = false)
    private Long id;

}
