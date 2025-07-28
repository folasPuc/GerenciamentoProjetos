package com.tcc.gerenciador_projetos_tcc.component;

import com.tcc.gerenciador_projetos_tcc.entity.Message;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.MessageService;
import com.vaadin.flow.component.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MemoryBuffer;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.shared.Registration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

        // Componente de upload (ícone de clipe)
        MemoryBuffer buffer = new MemoryBuffer();
        Upload upload = new Upload(buffer);
        upload.setAcceptedFileTypes("image/*", ".pdf", ".docx", ".txt"); // personalize os tipos permitidos
        upload.setUploadButton(new Button(VaadinIcon.PAPERCLIP.create()));
        upload.setMaxFiles(1);
        upload.setDropAllowed(false);
        upload.addSucceededListener(event -> {
            InputStream fileData = buffer.getInputStream();
            String fileName = event.getFileName();
            String fileType = event.getMIMEType();
            handleFileUpload(fileData, fileName, fileType);

            upload.clearFileList();
        });

        inputLayout.add(upload, messageInput, sendButton);
        inputLayout.expand(messageInput);

        add(headerLayout, messagesContainer, inputLayout);
        expand(messagesContainer);
        
        loadMessages();

        // Adicionar estilos CSS
        applyStyles();
    }

    private void handleFileUpload(InputStream fileData, String fileName, String fileType) {

        // Aqui você pode salvar o arquivo, enviar pelo chat, etc.

        Notification.show("Arquivo '" + fileName + "' enviado.", 3000, Notification.Position.TOP_CENTER)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);


        // Exemplo: converter para array de bytes (se for enviar para o backend)
        try {
            byte[] bytes = fileData.readAllBytes();

            // Adicionar a mensagem localmente
            ChatMessage chatMessage = new ChatMessage(null, currentUser, LocalDateTime.now(), true, bytes, fileName, fileType);
            addMessage(chatMessage);
            messageService.saveMessage(chatMessage, groupId);

            // Disparar evento de nova mensagem
            fireEvent(new GroupChatMessageEvent(this,
                    chatMessage, groupId));

        } catch (IOException e) {

            Notification.show("Erro ao processar o arquivo.", 3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);

        }

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
            messageContent.add(senderSpan);

            if (message.getFileName() != null && message.getFileData() != null) {
                StreamResource resource = new StreamResource(
                        message.getFileName(),
                        () -> new ByteArrayInputStream(message.getFileData())
                );
                Anchor downloadLink = new Anchor(resource, message.getFileName());
                downloadLink.getElement().setAttribute("download", true);
                downloadLink.addClassName("download-link");

                if (message.getSender().equals(currentUser)) {
                    downloadLink.getStyle().set("color", "white");
                } else {
                    downloadLink.getStyle().set("color", "blue");
                    downloadLink.getStyle().set("text-decoration", "underline");
                }

                messageContent.add(downloadLink);
            }

            if (message.getContent() != null && !message.getContent().isBlank()) {
                Span messageText = new Span(message.getContent());
                messageContent.add(messageText);
            }


            Span timeSpan = new Span(message.getTimestamp().format(timeFormatter));
            timeSpan.addClassName("message-time");

            messageContent.add(timeSpan);
            messageContainer.add(messageContent);

            messagesContainer.add(messageContainer);
        }

        scrollToBottom();
    }


    private void applyStyles() {
        getStyle().set("height", "100%");

//        String styles = ".chat-messages {"
//                + "padding: 0.5rem 1rem;"
//                + "margin-bottom: 0.5rem;"
//                + "border-radius: 1rem;"
//                + "max-width: 100%;"
//                + "word-break: break-word;"
//                + "max-height: 700px;" // define a altura máxima
//                + "overflow-y: auto;"   // ativa scroll vertical
//                + "}"
//                + ".chat-message.sent {"
//                + "align-self: flex-end;"
//                + "background-color: var(--lumo-primary-color);"
//                + "color: var(--lumo-primary-contrast-color);"
//                + "border-bottom-right-radius: 0.25rem;"
//                + "}"
//                + ".chat-message.received {"
//                + "align-self: flex-start;"
//                + "background-color: var(--lumo-contrast-10pct);"
//                + "border-bottom-left-radius: 0.25rem;"
//                + "}"
//                + ".message-time {"
//                + "font-size: var(--lumo-font-size-xs);"
//                + "margin-top: 0.25rem;"
//                + "opacity: 0.8;"
//                + "}"
//                + ".message-sender {"
//                + "font-weight: bold;"
//                + "margin-bottom: 0.25rem;"
//                + "}"
//                + ".system-message {"
//                + "align-self: center;"
//                + "font-style: italic;"
//                + "color: var(--lumo-tertiary-text-color);"
//                + "margin: 0.5rem 0;"
//                + "padding: 0.25rem 0.5rem;"
//                + "border-radius: 0.5rem;"
//                + "background-color: var(--lumo-contrast-5pct);"
//                + "}";




        String styles = ".chat-messages {"
                + "padding: 0.5rem 1rem;"
                + "margin-bottom: 0.5rem;"
                + "border-radius: 1rem;"
                + "max-width: 100%;"
                + "word-break: break-word;"
                + "max-height: 700px;"
                + "overflow-y: auto;"
                + "display: flex;"
                + "flex-direction: column;"
                + "}"

                + ".chat-message {"
                + "padding: 0.75rem 1rem;"
                + "margin: 0.25rem 0;"
                + "border-radius: 0.75rem;"
                + "max-width: 70%;"
                + "word-break: break-word;"
                + "display: flex;"
                + "flex-direction: column;"
                + "}"

                + ".chat-message.sent {"
                + "align-self: flex-end;"
                + "background-color: var(--lumo-primary-color);"
                + "color: var(--lumo-primary-contrast-color);"
                + "border-top-left-radius: 1rem;"
                + "border-top-right-radius: 1rem;"
                + "border-bottom-left-radius: 1rem;"
                + "border-bottom-right-radius: 0.25rem;"
                + "margin-left: auto;"
                + "}"

                + ".chat-message.received {"
                + "align-self: flex-start;"
                + "background-color: var(--lumo-contrast-10pct);"
                + "color: var(--lumo-body-text-color);"
                + "border-top-left-radius: 1rem;"
                + "border-top-right-radius: 1rem;"
                + "border-bottom-right-radius: 1rem;"
                + "border-bottom-left-radius: 0.25rem;"
                + "margin-right: auto;"
                + "}"

                + ".chat-message.sent .message-sender {"
                + "color: white;"
                + "}"

                + ".chat-message.received .message-sender {"
                + "color: var(--lumo-secondary-text-color);"
                + "}"

                + ".message-time {"
                + "font-size: var(--lumo-font-size-xs);"
                + "margin-top: 0.25rem;"
                + "opacity: 0.8;"
                + "align-self: flex-end;"
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
                + "}"
                + ".chat-message a {"
                + "text-decoration: underline;"
                + "font-weight: 500;"
                + "display: inline-flex;"
                + "align-items: center;"
                + "gap: 0.25rem;"
                + "}"
                + ".chat-message a::after {"
                + "content: '🔗';"
                + "font-size: 0.9em;"
                + "}"
                + ".chat-message.sent a {"
                + "color: white;"
                + "}"
                + ".chat-message.received a {"
                + "color: var(--lumo-primary-color);"
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

                Notification.show("Este grupo foi excluído.", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);

                UI.getCurrent().navigate("/homeview");
                return;
            }

            // Adicionar a mensagem localmente
            addMessage(new ChatMessage(text, currentUser, LocalDateTime.now(), true, null, null, null));

            messageService.saveMessage(new ChatMessage(text, currentUser, LocalDateTime.now(), true, null, null, null), groupId);

            messageInput.clear();
            messageInput.focus();

            // Disparar evento de nova mensagem
            fireEvent(new GroupChatMessageEvent(this,
                    new ChatMessage(text, currentUser, LocalDateTime.now(), true, null,null,null), groupId));
        }
    }

    /**
     * Recebe uma mensagem de outro usuário no grupo
     *
     * @param text Texto da mensagem
     * @param sender Nome do remetente
     */
    public void receiveMessage(String text, String sender, byte[] fileData, String fileName, String fileMimeType) {
        addMessage(new ChatMessage(text, sender, LocalDateTime.now(), false, fileData, fileName, fileMimeType));
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

        if (message.getFileName() != null && message.getFileData() != null) {
            // É um arquivo - criar link de download
            StreamResource resource = new StreamResource(
                    message.getFileName(),
                    () -> new ByteArrayInputStream(message.getFileData())
            );
            Anchor downloadLink = new Anchor(resource, message.getFileName());
            downloadLink.getElement().setAttribute("download", true);
            downloadLink.addClassName("download-link");
            // Estilo condicional baseado em quem enviou
            if (message.isSent()) {
                downloadLink.getStyle().set("color", "white");
            } else {
                downloadLink.getStyle().set("color", "blue"); // ou remova esta linha para cor padrão
                downloadLink.getStyle().set("text-decoration", "underline");
            }
            messageContent.add(downloadLink);
        }

        // Mensagem de texto, se existir
        if (message.getText() != null && !message.getText().isBlank()) {
            Span messageText = new Span(message.getText());
            messageContent.add(messageText);
        }

        Span timeSpan = new Span(message.getTimestamp().format(timeFormatter));
        timeSpan.addClassName("message-time");
        messageContent.add(timeSpan);

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

        // NOVOS CAMPOS PARA SUPORTAR ARQUIVOS
        private final byte[] fileData;
        private final String fileName;
        private final String fileType;

        public ChatMessage(String text, String sender, LocalDateTime timestamp, boolean sent, byte[] fileData, String fileName, String fileType) {
            this.text = text;
            this.sender = sender;
            this.timestamp = timestamp;
            this.sent = sent;
            this.fileData = fileData;
            this.fileName = fileName;
            this.fileType = fileType;
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

        public byte[] getFileData() {
            return fileData;
        }

        public String getFileName() {
            return fileName;
        }

        public String getFileType() {
            return fileType;
        }

        // Métodos utilitários (opcional, mas útil)
        public boolean hasFile() {
            return fileData != null && fileName != null && fileType != null;
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