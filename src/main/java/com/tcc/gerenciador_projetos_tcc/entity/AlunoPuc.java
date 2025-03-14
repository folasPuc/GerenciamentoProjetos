package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos_puc")
public class AlunoPuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private Integer ra;

    // Construtores
    public AlunoPuc() {}

    public AlunoPuc(Integer ra) {
        this.ra = ra;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getRa() {
        return ra;
    }

    public void setRa(Integer ra) {
        this.ra = ra;
    }
}
