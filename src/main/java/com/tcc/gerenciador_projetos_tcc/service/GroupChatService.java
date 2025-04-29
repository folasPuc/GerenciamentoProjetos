package com.tcc.gerenciador_projetos_tcc.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Service
public class GroupChatService {

    // Mapa que armazena usuários por grupo
    // Chave externa: ID do grupo
    // Chave interna: Nome do usuário
    // Valor: Manipulador de mensagens para esse usuário
    private final Map<Long, Map<String, Consumer<ChatMessage>>> groupListeners = new ConcurrentHashMap<>();

    public static class ChatMessage {
        private final String text;
        private final String sender;
        private final Long groupId;
        private final LocalDateTime timestamp;

        public ChatMessage(String text, String sender, Long groupId, LocalDateTime timestamp) {
            this.text = text;
            this.sender = sender;
            this.groupId = groupId;
            this.timestamp = timestamp;
        }

        public String getText() {
            return text;
        }

        public String getSender() {
            return sender;
        }

        public Long getGroupId() {
            return groupId;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }
    }

    /**
     * Adiciona um usuário a um grupo específico
     *
     * @param groupId ID do grupo
     * @param username Nome do usuário
     * @param messageHandler Manipulador de mensagens para o usuário
     */
    public void joinGroupChat(Long groupId, String username, Consumer<ChatMessage> messageHandler) {
        groupListeners.computeIfAbsent(groupId, k -> new ConcurrentHashMap<>())
                .put(username, messageHandler);
    }

    /**
     * Remove um usuário de um grupo específico
     *
     * @param groupId ID do grupo
     * @param username Nome do usuário
     */
    public void leaveGroupChat(Long groupId, String username) {
        if (groupListeners.containsKey(groupId)) {
            groupListeners.get(groupId).remove(username);

            // Se o grupo ficar vazio, remova-o
            if (groupListeners.get(groupId).isEmpty()) {
                groupListeners.remove(groupId);
            }
        }
    }

    /**
     * Envia uma mensagem para todos os membros de um grupo específico (exceto o remetente)
     *
     * @param text Texto da mensagem
     * @param sender Nome do remetente
     * @param groupId ID do grupo
     */
    public void sendMessageToGroup(String text, String sender, Long groupId) {
        if (!groupListeners.containsKey(groupId)) {
            return;
        }

        ChatMessage message = new ChatMessage(text, sender, groupId, LocalDateTime.now());

        // Enviar para todos os outros usuários do grupo
        groupListeners.get(groupId).forEach((username, handler) -> {
            if (!username.equals(sender)) {
                handler.accept(message);
            }
        });
    }

    /**
     * Verifica se um usuário é membro de um grupo
     *
     * @param groupId ID do grupo
     * @param username Nome do usuário
     * @return true se o usuário for membro do grupo, false caso contrário
     */
    public boolean isUserInGroup(String groupId, String username) {
        return groupListeners.containsKey(groupId) &&
                groupListeners.get(groupId).containsKey(username);
    }

    /**
     * Obtém a lista de usuários em um grupo específico
     *
     * @param groupId ID do grupo
     * @return Lista com os nomes dos usuários no grupo
     */
    public List<String> getUsersInGroup(Long groupId) {
        if (!groupListeners.containsKey(groupId)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(groupListeners.get(groupId).keySet());
    }
}