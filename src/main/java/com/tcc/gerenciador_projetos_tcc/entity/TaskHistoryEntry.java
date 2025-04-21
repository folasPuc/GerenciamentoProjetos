package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TaskHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime timestamp;

    private String action;
    private String user;

    @Column(columnDefinition = "TEXT")
    private String details;

    @ManyToOne(fetch = FetchType.EAGER)
    private Task task;

    public TaskHistoryEntry() {}

    public TaskHistoryEntry(String action, String user, String details, Task task) {
        this.timestamp = LocalDateTime.now();
        this.action = action;
        this.user = user;
        this.details = details;
        this.task = task;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
