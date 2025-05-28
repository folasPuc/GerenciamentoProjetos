package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "task_files")
public class TaskFiles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // TaskId como campo primitivo, sem relacionamento JPA direto
    @Column(name = "task_id")
    private Long taskId;

    @Column(name = "filename", nullable = false)
    private String filename;

    @Lob
    @Column(name = "file_data", columnDefinition = "LONGBLOB")
    private byte[] file;

    public TaskFiles() {}

    public TaskFiles(Long taskId, String filename, byte[] file) {
        this.taskId = taskId;
        this.filename = filename;
        this.file = file;
    }

    // Getters e Setters

    public Long getId() {
        return id;
    }

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getFile() {
        return file;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }
}
