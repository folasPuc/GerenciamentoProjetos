package com.tcc.gerenciador_projetos_tcc.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;
    private String content;
    private LocalDateTime timestamp;

    private Long groupId; // apenas o ID do grupo

    // Novos campos para arquivo
    private String fileName;           // nome do arquivo
    private String fileType;           // tipo MIME
    @Lob
    private byte[] fileData;           // conteúdo do arquivo

    public Message() {}

    public Message(String sender, String content, LocalDateTime timestamp, Long groupId, String fileName, String fileType, byte[] fileData) {
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.groupId = groupId;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileData = fileData;
    }

    // Getters e setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }
}
