package com.tcc.gerenciador_projetos_tcc.model;

public class User {
    private Integer ra;
    private String nome;
    private String sobrenome;
    private String email;
    private String faculdade;
    private String curso;

    // Construtores
    public User() {}

    public User(Integer ra, String nome, String sobrenome, String email, String faculdade, String curso) {
        this.ra = ra;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.faculdade = faculdade;
        this.curso = curso;
    }

    // Getters e Setters
    public Integer getRa() {
        return ra;
    }

    public void setRa(Integer ra) {
        this.ra = ra;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFaculdade() {
        return faculdade;
    }

    public void setFaculdade(String faculdade) {
        this.faculdade = faculdade;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    @Override
    public String toString() {
        return "User{" +
                "ra=" + ra +
                ", nome='" + nome + '\'' +
                ", sobrenome='" + sobrenome + '\'' +
                ", email='" + email + '\'' +
                ", faculdade='" + faculdade + '\'' +
                ", curso='" + curso + '\'' +
                '}';
    }
}
