package com.tcc.gerenciador_projetos_tcc.service;

import com.tcc.gerenciador_projetos_tcc.component.GroupChatComponent;
import com.tcc.gerenciador_projetos_tcc.entity.Message;
import com.tcc.gerenciador_projetos_tcc.repository.MessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    private final MessageRepository repository;

    public MessageService(MessageRepository repository) {
        this.repository = repository;
    }

    public List<Message> getMessagesByGroupId(Long groupId) {
        return repository.findByGroupIdOrderByTimestampAsc(groupId);
    }

    public Message saveMessage(GroupChatComponent.ChatMessage chatMessage, Long groupId) {
        Message entity = new Message(
                chatMessage.getSender(),
                chatMessage.getText(),
                chatMessage.getTimestamp(),
                groupId
        );
        return repository.save(entity);
    }

    public void deleteMessagesByGroupId(Long groupId) {
        repository.deleteByGroupId(groupId);
    }
}
