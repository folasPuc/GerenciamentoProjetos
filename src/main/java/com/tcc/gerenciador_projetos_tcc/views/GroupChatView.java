package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.component.GroupChatComponent;
import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.service.GroupChatService;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.MessageService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.shared.Registration;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;

import java.util.List;
import java.util.Optional;

@Route("group-chat")
public class GroupChatView extends VerticalLayout implements HasUrlParameter<Long> {

    private GroupChatComponent chatComponent;
    private final GroupChatService chatService;
    private final GrupoService grupoService;
    private final MessageService messageService;
    private String currentUser;
    private Long groupId;
    private String groupName;
    private Registration messageListenerRegistration;
    private Registration membersRequestRegistration;

    public GroupChatView(GroupChatService chatService, GrupoService grupoService, MessageService messageService) {
        this.chatService = chatService;
        this.grupoService = grupoService;
        this.messageService = messageService;
        setSizeFull();
        setPadding(true);

        // O usuário atual seria normalmente obtido da sessão ou autenticação
        this.currentUser = getCurrentUserName();
    }

    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        // O parâmetro é o ID do grupo
        this.groupId = parameter;

        // Em um caso real, você obteria o grupo do banco de dados
        Optional<Grupo> grupo = grupoService.findById(groupId);
        this.groupName = grupo.get().getNome(); // Substituir por nome real do grupo

        initUI();
    }

    private void initUI() {
        removeAll();

        H2 title = new H2("Chat do Grupo: " + groupName);

        chatComponent = new GroupChatComponent(currentUser, groupId, groupName, messageService, grupoService);

        add(title, chatComponent);
        expand(chatComponent);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        // Registrar no serviço de chat para receber mensagens
        chatService.joinGroupChat(groupId, currentUser, message -> {
            getUI().ifPresent(ui -> {
                ui.access(() -> {
                    chatComponent.receiveMessage(message.getText(), message.getSender(), message.getFileData(), message.getFileName(), message.getFileMimeType());
                });
            });
        });

        // Registrar listener de mensagens no componente de chat
        messageListenerRegistration = chatComponent.addGroupChatMessageListener(event -> {
            GroupChatComponent.ChatMessage message = event.getMessage();
            chatService.sendMessageToGroup(
                    message.getText(),
                    message.getSender(),
                    event.getGroupId(),
                    message.getFileData(),      // Supondo que sua ChatMessage tenha esses getters
                    message.getFileName(),
                    message.getFileType()
            );
        });

        // Registrar listener para solicitações de membros
        membersRequestRegistration = chatComponent.addGroupMembersRequestListener(event -> {
            showGroupMembers(event.getGroupId());
        });
    }

    @Override
    protected void onDetach(DetachEvent detachEvent) {
        // Limpar recursos ao sair da página
        chatService.leaveGroupChat(groupId, currentUser);

        if (messageListenerRegistration != null) {
            messageListenerRegistration.remove();
        }

        if (membersRequestRegistration != null) {
            membersRequestRegistration.remove();
        }

        super.onDetach(detachEvent);
    }

    /**
     * Exibe um diálogo com a lista de membros do grupo
     *
     * @param groupId ID do grupo
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

    private String getCurrentUserName() {
        // Em um aplicativo real, você obteria isso da sessão do usuário ou autenticação
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        return user.getNome() + " " + user.getSobrenome();
    }
}