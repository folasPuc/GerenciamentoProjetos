package com.tcc.gerenciador_projetos_tcc.component;

import com.tcc.gerenciador_projetos_tcc.entity.Message;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.MessageService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.shared.Registration;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class GroupChatComponent extends VerticalLayout {

    private final Div messagesContainer;
    private final TextField messageInput;
    private final List<ChatMessage> messages = new ArrayList<>();
    private final String currentUser;
    private final Long groupId;
    private final String groupName;
    private final MessageService messageService;
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final GrupoService grupoService;

    /**
     * Construtor para o componente de chat de grupo
     *
     * @param currentUser Nome do usuário atual
     * @param groupId ID do grupo
     * @param groupName Nome do grupo para exibição
     */
    public GroupChatComponent(String currentUser, Long groupId, String groupName, MessageService messageService, GrupoService grupoService) {
        this.currentUser = currentUser;
        this.groupId = groupId;
        this.groupName = groupName;
        this.messageService = messageService;
        this.grupoService = grupoService;

        setSizeFull();
        setPadding(false);
        setSpacing(false);

        // Cabeçalho do chat
        HorizontalLayout headerLayout = new HorizontalLayout();
        headerLayout.setWidthFull();
        headerLayout.setPadding(true);
        headerLayout.setSpacing(true);
        headerLayout.setAlignItems(Alignment.CENTER);

        H3 groupTitle = new H3(groupName);
        groupTitle.getStyle().set("margin", "0");

        Button membersButton = new Button(VaadinIcon.USERS.create());
        membersButton.getElement().setAttribute("title", "Ver membros");
        membersButton.addClickListener(e -> showGroupMembers());

        headerLayout.add(groupTitle, membersButton);
        headerLayout.setFlexGrow(1, groupTitle);

        // Container para as mensagens
        messagesContainer = new Div();
        messagesContainer.addClassName("chat-messages");
        messagesContainer.setWidthFull();
        messagesContainer.getStyle()
                .set("width", "100%")                 // Ocupa toda a largura disponível
                .set("box-sizing", "border-box")      // Inclui padding na largura total
                .set("word-wrap", "break-word")       // Quebra palavras longas
                .set("overflow-wrap", "break-word")  // Suporte adicional para quebra de texto
                .set("background-color", "var(--lumo-contrast-5pct)");

        // Layout para o campo de texto e botão de enviar
        HorizontalLayout inputLayout = new HorizontalLayout();
        inputLayout.setWidthFull();
        inputLayout.setPadding(true);
        inputLayout.setSpacing(true);

        messageInput = new TextField();
        messageInput.setPlaceholder("Digite sua mensagem...");
        messageInput.setClearButtonVisible(true);
        messageInput.setWidthFull();

        Button sendButton = new Button(VaadinIcon.ENTER.create());
        sendButton.addClickListener(e -> sendMessage());
        sendButton.addClickShortcut(Key.ENTER);

        inputLayout.add(messageInput, sendButton);
        inputLayout.expand(messageInput);

        add(headerLayout, messagesContainer, inputLayout);
        expand(messagesContainer);
        
        loadMessages();

        // Adicionar estilos CSS
        applyStyles();
    }

    private void loadMessages() {
        List<Message> messageList = messageService.getMessagesByGroupId(groupId);

        messagesContainer.removeAll(); // Limpa mensagens anteriores, se houver

        for (Message message : messageList) {
            // Cria um container individual para cada mensagem
            Div messageContainer = new Div();
            messageContainer.addClassName("chat-message");

            if (message.getSender().equals(currentUser)) {
                messageContainer.addClassName("sent");
            } else {
                messageContainer.addClassName("received");
            }

            VerticalLayout messageContent = new VerticalLayout();
            messageContent.setPadding(false);
            messageContent.setSpacing(false);

            Span senderSpan = new Span(message.getSender());
            senderSpan.addClassName("message-sender");

            Span messageText = new Span(message.getContent());

            Span timeSpan = new Span(message.getTimestamp().format(timeFormatter));
            timeSpan.addClassName("message-time");

            messageContent.add(senderSpan, messageText, timeSpan);
            messageContainer.add(messageContent);

            messagesContainer.add(messageContainer);
        }

        scrollToBottom();
    }


    private void applyStyles() {
        getStyle().set("height", "100%");

        String styles = ".chat-messages {"
                + "padding: 0.5rem 1rem;"
                + "margin-bottom: 0.5rem;"
                + "border-radius: 1rem;"
                + "max-width: 100%;"
                + "word-break: break-word;"
                + "max-height: 700px;" // define a altura máxima
                + "overflow-y: auto;"   // ativa scroll vertical
                + "}"
                + ".chat-message.sent {"
                + "align-self: flex-end;"
                + "background-color: var(--lumo-primary-color);"
                + "color: var(--lumo-primary-contrast-color);"
                + "border-bottom-right-radius: 0.25rem;"
                + "}"
                + ".chat-message.received {"
                + "align-self: flex-start;"
                + "background-color: var(--lumo-contrast-10pct);"
                + "border-bottom-left-radius: 0.25rem;"
                + "}"
                + ".message-time {"
                + "font-size: var(--lumo-font-size-xs);"
                + "margin-top: 0.25rem;"
                + "opacity: 0.8;"
                + "}"
                + ".message-sender {"
                + "font-weight: bold;"
                + "margin-bottom: 0.25rem;"
                + "}"
                + ".system-message {"
                + "align-self: center;"
                + "font-style: italic;"
                + "color: var(--lumo-tertiary-text-color);"
                + "margin: 0.5rem 0;"
                + "padding: 0.25rem 0.5rem;"
                + "border-radius: 0.5rem;"
                + "background-color: var(--lumo-contrast-5pct);"
                + "}";

        getElement().executeJs("const style = document.createElement('style');" +
                "style.textContent = $0;" +
                "document.head.appendChild(style);", styles);
    }

    /**
     * Exibe uma notificação de membro entrando no grupo
     *
     * @param username Nome do usuário que entrou
     */
    public void addUserJoinedNotification(String username) {
        Div notification = new Div();
        notification.setText(username + " entrou no grupo");
        notification.addClassName("system-message");
        messagesContainer.add(notification);
        scrollToBottom();
    }

    /**
     * Exibe uma notificação de membro saindo do grupo
     *
     * @param username Nome do usuário que saiu
     */
    public void addUserLeftNotification(String username) {
        Div notification = new Div();
        notification.setText(username + " saiu do grupo");
        notification.addClassName("system-message");
        messagesContainer.add(notification);
        scrollToBottom();
    }

    /**
     * Método para exibir a lista de membros do grupo
     * (Este método será chamado quando o botão de membros for clicado)
     */
    private void showGroupMembers() {
        // Disparar evento para solicitar a lista de membros do grupo
        fireEvent(new GroupMembersRequestEvent(this, groupId));
    }

    /**
     * Envia uma mensagem para o grupo
     */
    public void sendMessage() {
        String text = messageInput.getValue().trim();
        if (!text.isEmpty()) {

            if (!grupoService.existsById(groupId)) {
                Notification.show("Este grupo foi excluído.");
                UI.getCurrent().navigate("/homeview");
                return;
            }

            // Adicionar a mensagem localmente
            addMessage(new ChatMessage(text, currentUser, LocalDateTime.now(), true));

            messageService.saveMessage(new ChatMessage(text, currentUser, LocalDateTime.now(), true), groupId);

            messageInput.clear();
            messageInput.focus();

            // Disparar evento de nova mensagem
            fireEvent(new GroupChatMessageEvent(this,
                    new ChatMessage(text, currentUser, LocalDateTime.now(), true), groupId));
        }
    }

    /**
     * Recebe uma mensagem de outro usuário no grupo
     *
     * @param text Texto da mensagem
     * @param sender Nome do remetente
     */
    public void receiveMessage(String text, String sender) {
        addMessage(new ChatMessage(text, sender, LocalDateTime.now(), false));
    }

    /**
     * Adiciona uma mensagem ao container de chat
     *
     * @param message Mensagem a ser adicionada
     */
    private void addMessage(ChatMessage message) {
        messages.add(message);

        Div messageContainer = new Div();
        messageContainer.addClassName("chat-message");
        messageContainer.addClassName(message.isSent() ? "sent" : "received");

        VerticalLayout messageContent = new VerticalLayout();
        messageContent.setPadding(false);
        messageContent.setSpacing(false);

        Span senderSpan = new Span(message.getSender());
        senderSpan.addClassName("message-sender");
        messageContent.add(senderSpan);


        Span messageText = new Span(message.getText());
        Span timeSpan = new Span(message.getTimestamp().format(timeFormatter));
        timeSpan.addClassName("message-time");

        messageContent.add(messageText, timeSpan);
        messageContainer.add(messageContent);
        messagesContainer.add(messageContainer);

        // Scroll para a última mensagem
        scrollToBottom();
    }

    /**
     * Rola para a última mensagem
     */
    private void scrollToBottom() {
        getElement().executeJs("setTimeout(() => {" +
                "const container = this.querySelector('.chat-messages');" +
                "if (container) {" +
                "  container.scrollTop = container.scrollHeight;" +
                "}" +
                "}, 0);");
    }

    /**
     * Registra um listener para eventos de mensagem
     *
     * @param listener Listener a ser registrado
     * @return Registration que pode ser usada para remover o listener
     */
    public Registration addGroupChatMessageListener(ComponentEventListener<GroupChatMessageEvent> listener) {
        return addListener(GroupChatMessageEvent.class, listener);
    }

    /**
     * Registra um listener para eventos de solicitação de membros
     *
     * @param listener Listener a ser registrado
     * @return Registration que pode ser usada para remover o listener
     */
    public Registration addGroupMembersRequestListener(ComponentEventListener<GroupMembersRequestEvent> listener) {
        return addListener(GroupMembersRequestEvent.class, listener);
    }

    /**
     * Classe para representar uma mensagem do chat
     */
    public static class ChatMessage {
        private final String text;
        private final String sender;
        private final LocalDateTime timestamp;
        private final boolean sent;

        public ChatMessage(String text, String sender, LocalDateTime timestamp, boolean sent) {
            this.text = text;
            this.sender = sender;
            this.timestamp = timestamp;
            this.sent = sent;
        }

        public String getText() {
            return text;
        }

        public String getSender() {
            return sender;
        }

        public LocalDateTime getTimestamp() {
            return timestamp;
        }

        public boolean isSent() {
            return sent;
        }
    }

    /**
     * Evento para notificar novas mensagens no chat de grupo
     */
    public static class GroupChatMessageEvent extends ComponentEvent<GroupChatComponent> {
        private final ChatMessage message;
        private final Long groupId;

        public GroupChatMessageEvent(GroupChatComponent source, ChatMessage message, Long groupId) {
            super(source, false);
            this.message = message;
            this.groupId = groupId;
        }

        public ChatMessage getMessage() {
            return message;
        }

        public Long getGroupId() {
            return groupId;
        }
    }

    /**
     * Evento para solicitar a lista de membros do grupo
     */
    public static class GroupMembersRequestEvent extends ComponentEvent<GroupChatComponent> {
        private final Long groupId;

        public GroupMembersRequestEvent(GroupChatComponent source, Long groupId) {
            super(source, false);
            this.groupId = groupId;
        }

        public Long getGroupId() {
            return groupId;
        }
    }
}