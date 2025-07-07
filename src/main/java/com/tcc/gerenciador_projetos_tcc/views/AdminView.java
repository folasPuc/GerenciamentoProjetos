package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.Alunos;
import com.tcc.gerenciador_projetos_tcc.entity.Task;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import com.tcc.gerenciador_projetos_tcc.service.*;
import com.tcc.gerenciador_projetos_tcc.repository.UsersRepository;
import com.tcc.gerenciador_projetos_tcc.views.UIManager.UIManager;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.Set;

@Route("admin")
public class AdminView extends VerticalLayout {

    private final AlunoService alunoService;
    private final GrupoService grupoService;
    private final TaskFilesService taskFilesService;
    private final CalendarEventService calendarEventService;
    private final TaskService taskService;
    private final MessageService messageService;
    private final UserService userService;
    private Select<String> collegeSelect;
    private Grid<Grupo> grid;
    private Users user;

    private final VerticalLayout content = new VerticalLayout();

    public AdminView(AlunoService alunoService, GrupoService grupoService, TaskFilesService taskFilesService, CalendarEventService calendarEventService, TaskService taskService, MessageService messageService, UserService userService) {
        this.alunoService = alunoService;
        this.grupoService = grupoService;
        this.taskFilesService = taskFilesService;
        this.calendarEventService = calendarEventService;
        this.taskService = taskService;
        this. messageService = messageService;
        this.userService = userService;

        // Tabs
        Tab alunosTab = new Tab("Cadastrar Aluno");
        Tab gruposTab = new Tab("Gerenciar Grupos");
        Tabs tabs = new Tabs(alunosTab, gruposTab);

        tabs.addSelectedChangeListener(e -> setContent(e.getSelectedTab()));

        add(tabs, content);

        configSelect();
        // Inicializa com aba Aluno
        setContent(alunosTab);

        user = VaadinSession.getCurrent().getAttribute(Users.class);
    }

    private void configSelect() {
        collegeSelect = new Select<>();
        collegeSelect.setLabel("Faculdade");
        collegeSelect.setItems("PUCCAMPINAS", "UNICAMP");
        collegeSelect.setPlaceholder("Selecione a faculdade");
        collegeSelect.getStyle().set("width", "300px");
    }

    private void setContent(Tab tab) {
        content.removeAll();

        if ("Cadastrar Aluno".equals(tab.getLabel())) {
            content.add(createAlunoForm());
        } else if ("Gerenciar Grupos".equals(tab.getLabel())) {
            content.add(createGrupoGrid());
        }
    }

    private VerticalLayout createAlunoForm() {
        IntegerField raField = new IntegerField("RA");
        Button salvar = new Button("Salvar", e -> {
            Alunos aluno = new Alunos();
            aluno.setRa(raField.getValue());
            aluno.setFaculdade(collegeSelect.getValue());
            alunoService.save(aluno);
            raField.clear();
        });

        return new VerticalLayout(raField, collegeSelect, salvar);
    }

    private VerticalLayout createGrupoGrid() {
        grid = new Grid<>(Grupo.class, false);
        grid.addColumn(Grupo::getId).setHeader("ID");
        grid.addColumn(Grupo::getNome).setHeader("Nome");

        // Coluna com botão Adicionar Membro
        grid.addComponentColumn(grupo -> {
            Button adicionarMembro = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
            adicionarMembro.addClickListener(e -> abrirDialogAdicionarUsuario(grupo));
            return adicionarMembro;
        }).setHeader("Ações");

// Coluna com botão Remover Membro
        grid.addComponentColumn(grupo -> {
            Button removerMembro = new Button(new Icon(VaadinIcon.MINUS_CIRCLE));
            removerMembro.addClickListener(e -> abrirDialogRemoverUsuario(grupo));
            return removerMembro;
        }).setHeader("");

        grid.setItems(grupoService.listarTodos());

        TextField nomeGrupo = new TextField("Nome do Grupo");

        Button buscarGrupoButton = new Button("Buscar Grupo", event -> {
            String nomeGrupoSearch = nomeGrupo.getValue();
            List<Grupo> grupos = grupoService.buscarPorNome(nomeGrupoSearch);
            grid.setItems(grupos);
        });





        Button criar = new Button("Criar Grupo", e -> {
            abrirCriacaoGrupo();
        });

        Button deletar = new Button("Deletar Grupo Selecionado", e -> {
            Grupo selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                grupoService.deletar(selected.getId());
                List<Task> deleteTaskFileList = taskService.getTasksByGroup(selected.getId().intValue());
                taskFilesService.deleteAllTaskFiles(deleteTaskFileList);
                calendarEventService.deleteAllByGroupId(selected.getId().intValue());
                taskService.deleteAllTasksByGroupId(selected.getId().intValue());
                messageService.deleteMessagesByGroupId(selected.getId());
                grid.setItems(grupoService.listarTodos());
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(nomeGrupo, buscarGrupoButton, criar, deletar);

        return new VerticalLayout(buttons, grid);
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
                grid.setItems(grupoService.listarTodos()); // Atualiza o grid
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


    private void abrirDialogRemoverUsuario(Grupo grupo) {
        Dialog dialog = new Dialog();

        // Grid para exibir os usuários do grupo
        Grid<Users> usuariosGrid = new Grid<>();
        usuariosGrid.addColumn(Users::getNome).setHeader("Nome");
        usuariosGrid.addColumn(Users::getSobrenome).setHeader("Sobrenome");
        usuariosGrid.addColumn(Users::getCurso).setHeader("Curso");
        usuariosGrid.addColumn(Users::getFaculdade).setHeader("Faculdade");

        // Coluna com botão de remoção por usuário
        usuariosGrid.addComponentColumn(usuario -> {
            Button removerButton = new Button("Remover", event -> {
                grupo.removerUsuario(usuario);
                grupoService.salvar(grupo);

                Notification.show("Removido: " + usuario.getNome() + " " + usuario.getSobrenome());

                // Atualiza o grid local
                usuariosGrid.setItems(grupo.getUsuarios());

                // Atualiza todos os grids de grupos na HomeView
                Set<UI> uis = UIManager.getInstance().getAllUIs();
                for (UI ui : uis) {
                    ui.access(() -> {
                        HomeView homeView = ui.getSession().getAttribute(HomeView.class);
                        Users user = ui.getSession().getAttribute(Users.class);
                        if (homeView != null && user != null) {
                            homeView.grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId()));
                            homeView.grupoGrid.getDataProvider().refreshAll();
                        }
                    });
                }
            });
            return removerButton;
        }).setHeader("Ações");

        usuariosGrid.setItems(grupo.getUsuarios());
        usuariosGrid.setWidth("100%");
        usuariosGrid.setHeight("300px");

        // Botão de fechar
        Button fecharButton = new Button("Fechar", event -> dialog.close());

        // Layout final igual ao adicionar
        VerticalLayout layout = new VerticalLayout(usuariosGrid, fecharButton);
        layout.setSpacing(true);
        layout.setPadding(true);

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
            List<Users> usuarios = userService.listarTodos();
            usuariosGrid.setItems(usuarios); // Exibe a lista de usuários na grid
        });

        // Botão para adicionar o usuário selecionado ao grupo
        Button adicionarButton = new Button("Adicionar", event -> {
            // Obtém o usuário selecionado
            Users usuarioSelecionado = usuariosGrid.getSelectedItems().stream().findFirst().orElse(null);

            if (usuarioSelecionado != null) {
                grupo.addUsuario(usuarioSelecionado); // Adiciona o usuário ao grupo
                grupoService.salvar(grupo); // Salva o grupo atualizado
                Notification.show("Adicionado : " + usuarioSelecionado.getNome() + usuarioSelecionado.getSobrenome());


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
                Notification.show("Por favor, selecione um usuário para adicionar ao grupo.");
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
}
