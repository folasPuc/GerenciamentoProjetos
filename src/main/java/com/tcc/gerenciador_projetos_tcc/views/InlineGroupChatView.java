package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.component.GroupChatComponent;
import com.tcc.gerenciador_projetos_tcc.service.GroupChatService;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.MessageService;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.shared.Registration;

import java.util.List;

public class InlineGroupChatView extends VerticalLayout {

    private final GroupChatService chatService;
    private final GroupChatComponent chatComponent;
    private Registration messageListenerRegistration;
    private Registration membersRequestRegistration;

    private final Long groupId;
    private final String groupName;
    private final String currentUser;
    private final MessageService messageService;
    private final GrupoService grupoService;

    public InlineGroupChatView(
            Long groupId,
            String groupName,
            String currentUser,
            GroupChatService chatService,
            MessageService messageService,
            GrupoService grupoService
    ) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.currentUser = currentUser;
        this.chatService = chatService;
        this.messageService = messageService;
        this.grupoService = grupoService;

        this.chatComponent = new GroupChatComponent(currentUser, groupId, groupName, messageService, grupoService);

        configureLayout();
        setupChatListeners();
    }

    private void configureLayout() {
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        H2 title = new H2("Chat do Grupo: " + groupName);
        add(title, chatComponent);
        expand(chatComponent);
    }

    private void setupChatListeners() {
        // Entrar no grupo
        chatService.joinGroupChat(groupId, currentUser, message -> {
            getUI().ifPresent(ui -> ui.access(() ->
                    chatComponent.receiveMessage(
                            message.getText(),
                            message.getSender(),
                            message.getFileData(),
                            message.getFileName(),
                            message.getFileMimeType()
                    )
            ));
        });


        // Listener de envio de mensagens
        messageListenerRegistration = chatComponent.addGroupChatMessageListener(event -> {
            chatService.sendMessageToGroup(
                    event.getMessage().getText(),
                    event.getMessage().getSender(),
                    event.getGroupId(),
                    event.getMessage().getFileData(),
                    event.getMessage().getFileName(),
                    event.getMessage().getFileType()
            );
        });


        // Listener de requisição de membros
        membersRequestRegistration = chatComponent.addGroupMembersRequestListener(event -> {
            showGroupMembers(event.getGroupId());
        });
    }

    /**
     * Método para limpar recursos (por exemplo, chamar ao remover do layout)
     */
    public void cleanup() {
        chatService.leaveGroupChat(groupId, currentUser);
        if (messageListenerRegistration != null) messageListenerRegistration.remove();
        if (membersRequestRegistration != null) membersRequestRegistration.remove();
    }

    /**
     * Exibe um diálogo com a lista de membros do grupo
     */
    private void showGroupMembers(Long groupId) {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);

        H3 title = new H3("Membros do Grupo");

        List<String> members = chatService.getUsersInGroup(groupId);

        Grid<String> memberGrid = new Grid<>();
        memberGrid.setItems(members);
        memberGrid.addColumn(member -> member).setHeader("Nome");
        memberGrid.setHeight("300px");

        content.add(title, memberGrid);
        dialog.add(content);

        dialog.open();
    }
}

