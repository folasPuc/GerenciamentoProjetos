package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "grupo_usuarios",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<Users> usuarios = new HashSet<>();

//    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<Task> tasks;
//
//    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ChatMessage> chatMessages;

    public Grupo() {}

    public Grupo(String nome) {
        this.nome = nome;
    }

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Users> getUsuarios() {
        return usuarios;
    }

    public void setUsuarios(Set<Users> usuarios) {
        this.usuarios = usuarios;
    }

    public void addUsuario(Users user) {
        this.usuarios.add(user);  // 🔹 Método seguro para adicionar usuários
    }

    public void removerUsuario(Users user) {
        this.usuarios.remove(user);  // 🔹 Remove o usuário do grupo
    }

//    public Set<Task> getTasks() {
//        return tasks;
//    }
//
//    public void setTasks(Set<Task> tasks) {
//        this.tasks = tasks;
//    }
//
//    public Set<ChatMessage> getChatMessages() {
//        return chatMessages;
//    }
//
//    public void setChatMessages(Set<ChatMessage> chatMessages) {
//        this.chatMessages = chatMessages;
//    }
}
