package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import com.tcc.gerenciador_projetos_tcc.entity.Task;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.service.*;
import com.tcc.gerenciador_projetos_tcc.views.UIManager.UIManager;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;


import java.util.Collection;
import java.util.List;
import java.util.Set;

@PageTitle("Home")
@Route("homeview")
public class HomeView extends HorizontalLayout {

    private final UserService userService;
    private final AlunoService alunoService;
    private  final GrupoService grupoService;
    private final TaskService taskService;
    private VerticalLayout sidebar;
    private Users user;
    Grid<Grupo> grupoGrid;
    KanbanView kanbanView;
    private HorizontalLayout mainContent;
    private final MessageService messageService;
    private final GroupChatService groupChatService;
    private final TaskFilesService taskFilesService;
    private final CalendarEventService calendarEventService;

    // Content layouts
    private VerticalLayout empyContent;
    private VerticalLayout kanbanContent;


    public HomeView(UserService userService, AlunoService alunoService, GrupoService grupoService, TaskService taskService, MessageService messageService, GroupChatService groupChatService, TaskFilesService taskFilesService, CalendarEventService calendarEventService) {
        this.userService = userService;
        this.alunoService = alunoService;
        this.grupoService = grupoService;
        this.taskService = taskService;
        this.messageService = messageService;
        this.groupChatService = groupChatService;
        this.taskFilesService = taskFilesService;
        this.calendarEventService = calendarEventService;
        // Recupera o usuário da sessão

        user = VaadinSession.getCurrent().getAttribute(Users.class);

        UIManager.getInstance().addUI(UI.getCurrent());
        UI.getCurrent().getSession().setAttribute(HomeView.class, this);
        UI.getCurrent().getSession().setAttribute(Users.class, user);

        setSizeFull(); // Ocupa toda a tela
        setPadding(false);
        setSpacing(false);
        getStyle().set("overflow-y", "hidden");

        if (user != null) {
            setupLayout();
        } else {
            add(new Text("User not authenticated"));
        }
    }

    private void setupLayout() {
        // Create main layout
        mainContent = new HorizontalLayout();
        mainContent.setSizeFull();
        mainContent.setPadding(false);
        mainContent.setSpacing(false);

        // Initialize sidebar
        initSidebar();

        // Initialize content areas
        initContentAreas();

        // Add components to the main layout
        mainContent.add(sidebar, empyContent);

        // Add the main layout to this component
        add(mainContent);
    }

    private void initContentAreas() {
        // Grupos content (default view)
        empyContent = new VerticalLayout();

        // Initialize Kanban content
        kanbanContent = new VerticalLayout();
        kanbanContent.setSizeFull();
        kanbanContent.setPadding(true);

        // Add grid to grupos content
        addGroupSection();
    }

    private void initSidebar() {
        sidebar = new VerticalLayout();
        sidebar.setWidth("470px");
        sidebar.setPadding(true);
        sidebar.setSpacing(true);

        addUserInfo(user); // Exibe as informações do usuário

    }

    private void addUserInfo(Users user) {
        // Cria o avatar e nome do usuário
        Avatar avatar = new Avatar(user.getNome());
        avatar.setImage("https://www.gravatar.com/avatar?d=mp"); // Placeholder (mudar se necessário)
        avatar.setHeight("50px");
        avatar.setWidth("50px");

        Span nomeText = new Span(user.getNome());
        nomeText.getStyle()
                .set("font-weight", "bold")
                .set("font-size", "16px")
                .set("color", "#333");

        HorizontalLayout avatarNomeLayout = new HorizontalLayout(avatar, nomeText);
        avatarNomeLayout.setAlignItems(Alignment.CENTER);
        avatarNomeLayout.setSpacing(true);

        Span faculdadeText = new Span(user.getFaculdade());
        faculdadeText.getStyle()
                .set("font-size", "14px")
                .set("color", "#666");

        // Layout de informações do usuário
        VerticalLayout userLayout = new VerticalLayout(avatarNomeLayout, faculdadeText);
        userLayout.setSpacing(false);
        userLayout.setPadding(false);

        sidebar.add(userLayout);
    }

    private void addGroupSection() {
        // Botão para criar grupos
        Button criarGrupoButton = new Button("Criar Grupo", event -> abrirCriacaoGrupo());
        Button abrirCalendarioAluno = new Button(new Icon(VaadinIcon.CALENDAR), event -> abrirCalendarioAluno());

        // Grid com os grupos do usuário
        grupoGrid = new Grid<>();
        grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId()));

        grupoGrid.asSingleSelect().addValueChangeListener(event -> {
            Grupo grupoSelecionado = event.getValue();
            if (grupoSelecionado != null) {
                // Aqui você pode fazer o que quiser com o grupo selecionado
                UI.getCurrent().getSession().setAttribute("kanbanSelected", grupoSelecionado);

                // 🧼 Limpeza antes de trocar conteúdo
                if (!kanbanContent.getChildren().toList().isEmpty()) {
                    Component existing = kanbanContent.getComponentAt(0);
                    if (existing instanceof InlineGroupChatView) {
                        ((InlineGroupChatView) existing).cleanup();
                    }
                }



                if (grupoSelecionado.getTipo().equals("Task")) {
                    // Create KanbanView
                    kanbanView = new KanbanView(grupoSelecionado.getId(), grupoSelecionado.getNome(), taskService, grupoService, taskFilesService);
                    kanbanContent.removeAll();
                    kanbanContent.add(kanbanView);
                    mainContent.replace(mainContent.getComponentAt(1), kanbanContent);

                } else {
                    //Significa que o grupo eh do tipo Chat, entao devo construir o componenete de chat e substituir no main content
                    //Mas o jeito que crio o meu chat eh
                    // Button abrirChatButton = new Button(new Icon(VaadinIcon.COMMENT), e -> {
                    //                getUI().ifPresent(ui -> ui.navigate("group-chat/" + grupo.getId()));
                    //            });

                    InlineGroupChatView inlineChatView = new InlineGroupChatView(
                            grupoSelecionado.getId(),
                            grupoSelecionado.getNome(),
                            getCurrentUserName(),
                            groupChatService,
                            messageService,
                            grupoService
                    );

                    // Limpar e inserir novo conteúdo
                    kanbanContent.removeAll();
                    kanbanContent.add(inlineChatView);
                    mainContent.replace(mainContent.getComponentAt(1), kanbanContent);


                }

            } else {

                // 🧼 Limpeza antes de trocar conteúdo
                if (!kanbanContent.getChildren().toList().isEmpty()) {
                    Component existing = kanbanContent.getComponentAt(0);
                    if (existing instanceof InlineGroupChatView) {
                        ((InlineGroupChatView) existing).cleanup();
                    }
                }

                mainContent.replace(mainContent.getComponentAt(1), empyContent);
                UI.getCurrent().getSession().setAttribute("kanbanSelected", null);
            }
        });


        // Coluna com o nome do grupo e os botões alinhados
        grupoGrid.addComponentColumn(grupo -> {
            Span nomeGrupo = new Span(grupo.getNome());
            nomeGrupo.getStyle()
                    .set("max-width", "150px") // Limita o tamanho do texto
                    .set("overflow", "hidden") // Esconde o excesso
                    .set("white-space", "nowrap") // Mantém o texto em uma linha
                    .set("text-overflow", "ellipsis"); // Adiciona "..." se o texto for muito longo

            Button adicionarUsuarioButton = new Button(new Icon(VaadinIcon.PLUS_CIRCLE), e -> abrirDialogAdicionarUsuario(grupo));
            Button removerUsuarioButton = new Button(new Icon(VaadinIcon.MINUS_CIRCLE), e -> abrirDialogRemoverUsuario(grupo));
            Button deletarGrupoButton = new Button(new Icon(VaadinIcon.TRASH), e -> deletarGrupo(grupo));
            Button abrirCalendarioGrupo = new Button(new Icon(VaadinIcon.CALENDAR), e -> abrirCalendarioGrupo(grupo.getId()));

            Button abrirChatButton = new Button(new Icon(VaadinIcon.COMMENT), e -> {
                getUI().ifPresent(ui -> ui.navigate("group-chat/" + grupo.getId()));
            });
            abrirChatButton.getElement().setProperty("title", "Abrir chat do grupo");

            // Criando o layout horizontal e alinhando corretamente
            HorizontalLayout layout = new HorizontalLayout(nomeGrupo, adicionarUsuarioButton, removerUsuarioButton, deletarGrupoButton, abrirChatButton, abrirCalendarioGrupo);
            layout.setSpacing(true);
            layout.setPadding(false);
            layout.setMargin(false);
            layout.setAlignItems(FlexComponent.Alignment.CENTER);

            // Define a largura correta para manter a estrutura alinhada
            layout.setFlexGrow(1, nomeGrupo); // Nome ocupa espaço flexível
            layout.setFlexGrow(0, adicionarUsuarioButton, removerUsuarioButton, deletarGrupoButton, abrirChatButton, abrirCalendarioGrupo); // Botões não crescem
            layout.setFlexShrink(0, adicionarUsuarioButton, removerUsuarioButton, deletarGrupoButton, abrirChatButton, abrirCalendarioGrupo); // Botões não encolhem

            return layout;
        }).setHeader("Grupos").setAutoWidth(true);

        grupoGrid.setHeight("600px");
        grupoGrid.setWidth("450px");

        // Adiciona o botão e o grid à sidebar
        sidebar.add(criarGrupoButton, abrirCalendarioAluno, grupoGrid);
    }

    private void abrirCalendarioGrupo(Long id) {

        if (id != null) {
            UI.getCurrent().navigate("calendar-grupo/" + id + "-" + "GROUP");
        } else {

            Notification notification = new Notification("Grupo nao encontrado", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();

        }
    }

    private void abrirCalendarioAluno() {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);

        if (user != null) {
            // Navega para a rota: /calendario/{id}-{type}
            UI.getCurrent().navigate("calendar-aluno/" + user.getId() + "-" + "USER");
        } else {

            Notification notification = new Notification("Usuário não encontrado na sessão!", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
        }
    }

    private void deletarGrupo(Grupo grupo) {

        // Excluir o grupo
        grupoService.deletar(grupo.getId());
        List<Task> deleteTaskFileList = taskService.getTasksByGroup(grupo.getId().intValue());
        taskFilesService.deleteAllTaskFiles(deleteTaskFileList);
        calendarEventService.deleteAllByGroupId(grupo.getId().intValue());
        taskService.deleteAllTasksByGroupId(grupo.getId().intValue());
        messageService.deleteMessagesByGroupId(grupo.getId());

        // Obtém todas as sessões da UI conectadas
        Set<UI> uis = UIManager.getInstance().getAllUIs();

        for (UI ui : uis) {

            ui.access(() -> {

                HomeView homeView = ui.getSession().getAttribute(HomeView.class);
                Users users = ui.getSession().getAttribute(Users.class);


                homeView.grupoGrid.setItems(grupoService.buscarPorUsuario(users.getId())); // Atualiza o grid de grupos
                homeView.grupoGrid.getDataProvider().refreshAll();
            });

        }

        // Exibe uma notificação confirmando a exclusão

        Notification notification = new Notification("Grupo " + grupo.getNome() + " deletado com sucesso.", 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        notification.open();




    }


    // Método para abrir o diálogo de remoção de usuários
    private void abrirDialogRemoverUsuario(Grupo grupo) {
        Dialog dialog = new Dialog();

        // Grid para exibir os usuários do grupo
        Grid<Users> usuariosGrid = new Grid<>();
        usuariosGrid.addColumn(Users::getNome).setHeader("Nome");
        usuariosGrid.addColumn(Users::getSobrenome).setHeader("Sobrenome");
        usuariosGrid.addColumn(Users::getCurso).setHeader("Curso");
        usuariosGrid.addColumn(Users::getFaculdade).setHeader("Faculdade");

        // Adiciona uma coluna com botão de remoção
        usuariosGrid.addComponentColumn(usuario -> {
            Button removerButton = new Button("Remover", event -> {
                grupo.removerUsuario(usuario);
                grupoService.salvar(grupo);
                usuariosGrid.setItems(grupo.getUsuarios()); // Atualiza o grid


                // Obtém todas as sessões da UI conectadas
                Set<UI> uis = UIManager.getInstance().getAllUIs();

                for (UI ui : uis) {

                    ui.access(() -> {
                        HomeView homeView = ui.getSession().getAttribute(HomeView.class);
                        Users users = ui.getSession().getAttribute(Users.class);


                        homeView.grupoGrid.setItems(grupoService.buscarPorUsuario(users.getId())); // Atualiza o grid de grupos
                        homeView.grupoGrid.getDataProvider().refreshAll();
                    });

                }



            });
            return removerButton;
        }).setHeader("Ações");

        usuariosGrid.setItems(grupo.getUsuarios()); // Preenche a grid com os usuários do grupo
        usuariosGrid.setWidth("100%");
        usuariosGrid.setHeight("300px");

        // Botão para fechar o diálogo
        Button fecharButton = new Button("Fechar", event -> dialog.close());

        VerticalLayout layout = new VerticalLayout(usuariosGrid, fecharButton);
        layout.setPadding(true);
        layout.setSpacing(true);

        dialog.add(layout);
        dialog.setWidth("800px");
        dialog.setHeight("800px");
        dialog.open();
    }

    public void abrirDialogAdicionarUsuario(Grupo grupo) {
        Dialog dialog = new Dialog();

        // Campo para digitar o nome do usuário
        TextField nomeUsuarioField = new TextField("Nome do Usuário");

        // Grid para exibir os resultados da busca
        Grid<Users> usuariosGrid = new Grid<>();
        usuariosGrid.addColumn(Users::getNome).setHeader("Nome");
        usuariosGrid.addColumn(Users::getSobrenome).setHeader("Sobrenome");
        usuariosGrid.addColumn(Users::getCurso).setHeader("Curso");
        usuariosGrid.addColumn(Users::getFaculdade).setHeader("Faculdade");
        usuariosGrid.setSelectionMode(Grid.SelectionMode.SINGLE); // Habilita seleção de um único usuário

        // Ajusta a largura e altura do Grid
        usuariosGrid.setWidth("100%");  // Largura 100% do contêiner
        usuariosGrid.setHeight("300px");  // Defina a altura desejada, por exemplo, 300px

        // Botão de busca para filtrar os usuários
        Button buscarButton = new Button("Buscar", event -> {
            String nomeUsuario = nomeUsuarioField.getValue();
            List<Users> usuarios = userService.buscarPorNomeFaculdade(nomeUsuario, user.getFaculdade()); // Busca usuários
            usuariosGrid.setItems(usuarios); // Exibe a lista de usuários na grid
        });

        // Botão para adicionar o usuário selecionado ao grupo
        Button adicionarButton = new Button("Adicionar", event -> {
            // Obtém o usuário selecionado
            Users usuarioSelecionado = usuariosGrid.getSelectedItems().stream().findFirst().orElse(null);

            if (usuarioSelecionado != null) {
                grupo.addUsuario(usuarioSelecionado); // Adiciona o usuário ao grupo
                grupoService.salvar(grupo); // Salva o grupo atualizado

                Notification notification = new Notification("Adicionado : " + usuarioSelecionado.getNome() + " " + usuarioSelecionado.getSobrenome(), 3000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                notification.open();



                // Obtém todas as sessões da UI conectadas
                Set<UI> uis = UIManager.getInstance().getAllUIs();

                for (UI ui : uis) {

                    ui.access(() -> {

                        HomeView homeView = ui.getSession().getAttribute(HomeView.class);
                        Users users = ui.getSession().getAttribute(Users.class);


                        homeView.grupoGrid.setItems(grupoService.buscarPorUsuario(users.getId())); // Atualiza o grid de grupos
                        homeView.grupoGrid.getDataProvider().refreshAll();
                    });

                }

                dialog.close(); // Fecha o diálogo
            } else {

                Notification notification = new Notification("Por favor, selecione um usuário para adicionar ao grupo.", 3000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();

            }
        });

        // Layout do modal com o campo de busca, a grid e o botão de adicionar
        VerticalLayout layout = new VerticalLayout(nomeUsuarioField, buscarButton, usuariosGrid, adicionarButton);
        layout.setSpacing(true);
        layout.setPadding(true);

        // Ajusta o tamanho do layout do Dialog
        dialog.setWidth("800px"); // Define a largura do Dialog (ajuste conforme necessário)
        dialog.setHeight("800px"); // Define a altura do Dialog (ajuste conforme necessário)

        // Adiciona o layout ao diálogo
        dialog.add(layout);
        dialog.open(); // Abre o modal
    }


    private void abrirCriacaoGrupo() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Criar Novo Grupo");

        TextField nomeGrupoField = new TextField("Nome do Grupo");

        Select<String> tipoGrupoSelect = new Select<>();
        tipoGrupoSelect.setLabel("Tipo do Grupo");
        tipoGrupoSelect.setItems("Chat", "Task");
        tipoGrupoSelect.setValue("Task"); // Valor padrão

        Button salvarButton = new Button("Criar", event -> {
            String nomeGrupo = nomeGrupoField.getValue();
            String tipoGrupo = tipoGrupoSelect.getValue();
            if (!nomeGrupo.isEmpty()) {
                Grupo novoGrupo = new Grupo(nomeGrupo);
                novoGrupo.addUsuario(user); // Associa o criador ao grupo
                novoGrupo.setTipo(tipoGrupo);
                grupoService.salvar(novoGrupo);
                grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId())); // Atualiza o grid

                Notification.show("Grupo: " + nomeGrupo + " criado", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                dialog.close();
            }
        });

        Button cancelarButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(salvarButton, cancelarButton);
        //dialog.add(nomeGrupoField, tipoGrupoSelect, botoes);

        VerticalLayout layout = new VerticalLayout(nomeGrupoField, tipoGrupoSelect, botoes);
        layout.setSpacing(true);
        layout.setPadding(true);
        dialog.add(layout);

        dialog.open(); // Abre o modal
    }


    // Quando o Layout for destruído, removemos a UI do UIManager
    @Override
    protected void onDetach(DetachEvent event) {
        // Remove a UI da lista de UIs conectadas
        UIManager.getInstance().removeUI(UI.getCurrent());
    }

    private String getCurrentUserName() {
        // Em um aplicativo real, você obteria isso da sessão do usuário ou autenticação
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);
        return user.getNome() + " " + user.getSobrenome();
    }
}
