package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "alunos_unicamp")
public class AlunoUnicamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false, unique = true)
    private Integer ra;

    // Construtores
    public AlunoUnicamp() {}

    public AlunoUnicamp(Integer ra) {
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
