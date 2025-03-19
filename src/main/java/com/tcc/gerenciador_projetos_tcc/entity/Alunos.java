package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos")
public class Alunos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private Integer ra;

    @Column(nullable = false)
    private String faculdade;

    // Construtores
    public Alunos() {}

    public Alunos(Integer ra, String faculdade) {
        this.ra = ra;
        this.faculdade = faculdade;
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

    public String getFaculdade() {
        return faculdade;
    }

    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }
}
