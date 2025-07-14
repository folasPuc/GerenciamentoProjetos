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
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Route("admin")
public class AdminView extends VerticalLayout implements BeforeEnterObserver {

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
        this.messageService = messageService;
        this.userService = userService;

        setWidthFull();
        setAlignItems(Alignment.CENTER);  // Centraliza tudo na view

        // Tabs
        Tab alunosTab = new Tab("Cadastrar RA");
        Tab gruposTab = new Tab("Gerenciar Grupos");
        Tab adminTab = new Tab("Cadastrar Admin"); // NOVO TAB
        Tab gerenciarAlunosTab = new Tab("Gerenciar Usuarios"); // NOVO

        Tabs tabs = new Tabs(alunosTab, gruposTab, adminTab, gerenciarAlunosTab);

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

        content.setAlignItems(Alignment.CENTER);
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.setWidthFull();
        content.setSpacing(true);

        if ("Cadastrar RA".equals(tab.getLabel())) {
            content.add(createAlunoForm());
        } else if ("Gerenciar Grupos".equals(tab.getLabel())) {
            content.add(createGrupoGrid());
        } else if ("Cadastrar Admin".equals(tab.getLabel())) {
            content.add(createAdminForm()); // NOVO FORM
        } else if ("Gerenciar Usuarios".equals(tab.getLabel())) {
            content.add(createAlunosGrid());
        }
    }

    private VerticalLayout createAlunosGrid() {
        H1 title = new H1("Gerenciar Usuarios");
        title.getStyle().set("color", "#002D72").set("font-size", "24px");

        Grid<Alunos> alunosGrid = new Grid<>(Alunos.class, false);
        alunosGrid.addColumn(Alunos::getId).setHeader("ID");
        alunosGrid.addColumn(Alunos::getFaculdade).setHeader("Faculdade");
        alunosGrid.addColumn(Alunos::getRa).setHeader("RA");

        alunosGrid.addComponentColumn(aluno -> {
            Button remover = new Button(new Icon(VaadinIcon.TRASH));
            remover.getElement().setAttribute("theme", "error"); // Vermelho
            remover.addClickListener(e -> {
                try {

                    Users user = userService.getUserByRa(aluno.getRa());

                    // 1️⃣ Remove de todos os grupos
                    List<Grupo> grupos = grupoService.buscarPorUsuario(user.getId());

                    System.out.println("Grupos: " + grupos);;


                    System.out.println("User: " + user);
                    for (Grupo grupo : grupos) {
                        grupo.removerUsuario(user);
                        grupoService.salvar(grupo);
                    }

                    alunoService.deletarAluno(aluno.getId());
                    userService.deleteByRa(aluno.getRa());
                    calendarEventService.deleteAllByUserId(user.getId());
                    alunosGrid.setItems(alunoService.listarTodos());
                    Notification.show("Aluno removido com sucesso!");
                } catch (Exception ex) {
                    Notification.show("Erro ao remover aluno: " + ex.getMessage());
                }
            });
            return remover;
        }).setHeader("Remover");

        alunosGrid.setItems(alunoService.listarTodos());
        alunosGrid.setWidth("600px");
        alunosGrid.setHeight("500px");
        alunosGrid.getStyle().set("margin", "0 auto");

        VerticalLayout layout = new VerticalLayout(title, alunosGrid);
        layout.setSizeFull();
        layout.setAlignItems(Alignment.CENTER);
        layout.setJustifyContentMode(JustifyContentMode.START);
        layout.setPadding(true);
        layout.setSpacing(true);

        return layout;
    }


    private VerticalLayout createAdminForm() {
        // Título do formulário
        H1 title = new H1("UniWorks - Cadastrar Admin");
        title.getStyle()
                .set("color", "#002D72")
                .set("font-family", "Segoe UI, Roboto, Arial, sans-serif")
                .set("font-size", "28px")
                .set("font-weight", "bold");

        // RA do novo Admin
        IntegerField raField = new IntegerField("RA");
        raField.getStyle().set("width", "300px");
        raField.setErrorMessage("Por favor, insira o RA.");
        raField.setRequired(true);
        raField.addBlurListener(event -> {
            if (raField.isEmpty()) {
                raField.setInvalid(true);
            } else {
                raField.setInvalid(false);
            }
        });

        // Senha do novo Admin
        PasswordField passwordField = new PasswordField("Senha");
        passwordField.getStyle().set("width", "300px");
        passwordField.setRequired(true);
        passwordField.setErrorMessage("A senha não pode estar vazia.");
        passwordField.addBlurListener(event -> {
            if (passwordField.isEmpty()) {
                passwordField.setInvalid(true);
            } else {
                passwordField.setInvalid(false);
            }
        });

        // Nome do novo Admin
        com.vaadin.flow.component.textfield.TextField nomeField = new com.vaadin.flow.component.textfield.TextField("Nome");
        nomeField.getStyle().set("width", "300px");
        nomeField.setRequired(true);
        nomeField.setErrorMessage("O nome não pode estar vazio.");
        nomeField.addBlurListener(event -> {
            if (nomeField.isEmpty()) {
                nomeField.setInvalid(true);
            } else {
                nomeField.setInvalid(false);
            }
        });

        // Sobrenome do novo Admin
        com.vaadin.flow.component.textfield.TextField sobrenomeField = new com.vaadin.flow.component.textfield.TextField("Sobrenome");
        sobrenomeField.getStyle().set("width", "300px");
        sobrenomeField.setRequired(true);
        sobrenomeField.setErrorMessage("O sobrenome não pode estar vazio.");
        sobrenomeField.addBlurListener(event -> {
            if (sobrenomeField.isEmpty()) {
                sobrenomeField.setInvalid(true);
            } else {
                sobrenomeField.setInvalid(false);
            }
        });

        // Email
        com.vaadin.flow.component.textfield.TextField emailField = new com.vaadin.flow.component.textfield.TextField("Email");
        emailField.getStyle().set("width", "300px");
        emailField.setRequired(true);
        emailField.setErrorMessage("O email não pode estar vazio.");
        emailField.addBlurListener(event -> {
            if (emailField.isEmpty()) {
                emailField.setInvalid(true);
            } else {
                emailField.setInvalid(false);
            }
        });

        // Faculdade
        Select<String> faculdadeSelect = new Select<>();
        faculdadeSelect.setLabel("Faculdade");
        faculdadeSelect.setItems("PUCCAMPINAS", "UNICAMP");
        faculdadeSelect.setPlaceholder("Selecione a faculdade");
        faculdadeSelect.getStyle().set("width", "300px");

        // Botão de salvar Admin
        Button salvarButton = new Button("Cadastrar Admin", e -> {
            Integer ra = raField.getValue();
            String senha = passwordField.getValue();
            String nome = nomeField.getValue();
            String sobrenome = sobrenomeField.getValue();
            String faculdade = faculdadeSelect.getValue();
            String email = emailField.getValue();

            if (verifyFieldsAdmin(ra, senha, nome, sobrenome, faculdade, email)) {
                try {
                    Users novoAdmin = new Users();
                    novoAdmin.setRa(ra);
                    novoAdmin.setEmail(email);
                    novoAdmin.setSenhaHash(senha);
                    novoAdmin.setNome(nome);
                    novoAdmin.setSobrenome(sobrenome);
                    novoAdmin.setFaculdade(faculdade);
                    novoAdmin.setRole("admin");

                    Alunos aluno = new Alunos();
                    aluno.setRa(ra);
                    aluno.setFaculdade(faculdade);
                    alunoService.save(aluno);


                    userService.salvarAdmin(novoAdmin.getRa(), novoAdmin.getNome(), novoAdmin.getSobrenome(), novoAdmin.getEmail(), novoAdmin.getSenhaHash(),
                            novoAdmin.getCurso(), novoAdmin.getFaculdade());

                    Notification.show("Admin cadastrado com sucesso!");

                    raField.clear();
                    passwordField.clear();
                    nomeField.clear();
                    sobrenomeField.clear();
                    faculdadeSelect.clear();

                } catch (Exception ex) {
                    Notification.show("Erro ao cadastrar admin: " + ex.getMessage());
                }
            }
        });

        salvarButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");

        VerticalLayout layout = new VerticalLayout(
                title,
                raField,
                passwordField,
                nomeField,
                sobrenomeField,
                emailField,
                faculdadeSelect,
                salvarButton
        );
//        layout.setAlignItems(Alignment.CENTER);
//        layout.setJustifyContentMode(JustifyContentMode.CENTER);

        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle().set("max-width", "400px");

        return layout;
    }

    private boolean verifyFieldsAdmin(Integer ra, String senha, String nome, String sobrenome, String faculdade, String email) {
        if (ra == null || ra <= 0) {
            Notification.show("O campo 'RA' é obrigatório e deve ser um valor válido.");
            return false;
        }
        if (senha == null || senha.trim().isEmpty()) {
            Notification.show("O campo 'Senha' é obrigatório.");
            return false;
        }
        if (nome == null || nome.trim().isEmpty()) {
            Notification.show("O campo 'Nome' é obrigatório.");
            return false;
        }
        if (sobrenome == null || sobrenome.trim().isEmpty()) {
            Notification.show("O campo 'Sobrenome' é obrigatório.");
            return false;
        }

        if (email == null || email.trim().isEmpty()) {
            Notification.show("O campo 'Email' é obrigatório.");
            return false;
        }

        if (faculdade == null || faculdade.trim().isEmpty()) {
            Notification.show("O campo 'Faculdade' é obrigatório.");
            return false;
        }
        return true;
    }

    private VerticalLayout createAlunoForm() {

        H1 title = new H1("Cadastro de RA");
        title.getStyle().set("color", "#002D72").set("font-size", "24px");


        IntegerField raField = new IntegerField("RA");
        raField.setWidth("300px");

        Button salvar = new Button("Salvar", e -> {
            try {
                if (raField.getValue() != null && collegeSelect.getValue() != null) {
                    Alunos aluno = new Alunos();
                    aluno.setRa(raField.getValue());
                    aluno.setFaculdade(collegeSelect.getValue());
                    alunoService.save(aluno);
                    raField.clear();
                    collegeSelect.clear();
                    Notification.show("Aluno salvo com sucesso!");
                } else {
                    Notification.show("Por favor, preencha todos os campos!");
                }
            } catch (Exception ex) {
                Notification.show("Erro ao salvar aluno: " + ex.getMessage());
            }
        });

        salvar.getStyle()
                .set("background", "#002D72")
                .set("color", "#FFFFFF")
                .set("border-radius", "8px");

        VerticalLayout layout = new VerticalLayout(title, raField, collegeSelect, salvar);
        layout.setAlignItems(Alignment.CENTER);
        layout.setSpacing(true);
        layout.setPadding(true);
        layout.getStyle().set("max-width", "400px");
        return layout;
    }

    private VerticalLayout createGrupoGrid() {

        H1 title = new H1("Gerenciar Grupos");
        title.getStyle().set("color", "#002D72").set("font-size", "24px");

        grid = new Grid<>(Grupo.class, false);
        grid.addColumn(Grupo::getId).setHeader("ID");
        grid.addColumn(Grupo::getNome).setHeader("Nome");
        grid.addColumn(grupo -> grupo.getUsuarios().size()).setHeader("Membros");

        // Coluna com botão Adicionar Membro
        grid.addComponentColumn(grupo -> {
            Button adicionarMembro = new Button(new Icon(VaadinIcon.PLUS_CIRCLE));
            adicionarMembro.addClickListener(e -> abrirDialogAdicionarUsuario(grupo));
            return adicionarMembro;
        }).setHeader("Adicionar");

        // Coluna com botão Remover Membro
        grid.addComponentColumn(grupo -> {
            Button removerMembro = new Button(new Icon(VaadinIcon.MINUS_CIRCLE));
            removerMembro.addClickListener(e -> abrirDialogRemoverUsuario(grupo));
            return removerMembro;
        }).setHeader("Remover");

        refreshGrid();

        TextField nomeGrupo = new TextField("Nome do Grupo");

        Button buscarGrupoButton = new Button("Buscar Grupo", event -> {
            String nomeGrupoSearch = nomeGrupo.getValue();
            if (nomeGrupoSearch != null && !nomeGrupoSearch.trim().isEmpty()) {
                List<Grupo> grupos = grupoService.buscarPorNome(nomeGrupoSearch);
                grid.setItems(grupos);
            } else {
                refreshGrid();
            }
        });

        Button criar = new Button("Criar Grupo", e -> {
            abrirCriacaoGrupo();
        });

        Button deletar = new Button("Deletar Grupo Selecionado", e -> {
            Grupo selected = grid.asSingleSelect().getValue();
            if (selected != null) {
                try {
                    grupoService.deletar(selected.getId());
                    List<Task> deleteTaskFileList = taskService.getTasksByGroup(selected.getId().intValue());
                    taskFilesService.deleteAllTaskFiles(deleteTaskFileList);
                    calendarEventService.deleteAllByGroupId(selected.getId().intValue());
                    taskService.deleteAllTasksByGroupId(selected.getId().intValue());
                    messageService.deleteMessagesByGroupId(selected.getId());
                    refreshGrid();
                    // Atualiza todas as sessões da UI conectadas
                    updateAllHomeViews();
                    Notification.show("Grupo deletado com sucesso!");
                } catch (Exception ex) {
                    Notification.show("Erro ao deletar grupo: " + ex.getMessage());
                }
            } else {
                Notification.show("Por favor, selecione um grupo para deletar!");
            }
        });

        HorizontalLayout buttons = new HorizontalLayout(nomeGrupo, buscarGrupoButton, criar, deletar);
        buttons.setAlignItems(FlexComponent.Alignment.CENTER);

        VerticalLayout layout = new VerticalLayout(title, buttons, grid);
        layout.setAlignItems(Alignment.CENTER);
        layout.setWidthFull();
        layout.setPadding(true);
        layout.setSpacing(true);
        layout.getStyle().set("max-width", "1200px");

        return layout;
    }

    private void refreshGrid() {
        grid.setItems(grupoService.listarTodos());
        grid.getDataProvider().refreshAll();
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
            if (nomeGrupo != null && !nomeGrupo.trim().isEmpty()) {
                try {
                    Grupo novoGrupo = new Grupo(nomeGrupo);
                    novoGrupo.addUsuario(user); // Associa o criador ao grupo
                    novoGrupo.setTipo(tipoGrupo);
                    grupoService.salvar(novoGrupo);
                    refreshGrid();
                    dialog.close();
                    Notification.show("Grupo criado com sucesso!");
                } catch (Exception ex) {
                    Notification.show("Erro ao criar grupo: " + ex.getMessage());
                }
            } else {
                Notification.show("Por favor, digite o nome do grupo!");
            }
        });

        Button cancelarButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(salvarButton, cancelarButton);

        VerticalLayout layout = new VerticalLayout(nomeGrupoField, tipoGrupoSelect, botoes);
        layout.setSpacing(true);
        layout.setPadding(true);
        dialog.add(layout);

        dialog.open();
    }

    private void abrirDialogRemoverUsuario(Grupo grupo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Remover Usuários do Grupo: " + grupo.getNome());

        // Grid para exibir os usuários do grupo
        Grid<Users> usuariosGrid = new Grid<>();
        usuariosGrid.addColumn(Users::getNome).setHeader("Nome");
        usuariosGrid.addColumn(Users::getSobrenome).setHeader("Sobrenome");
        usuariosGrid.addColumn(Users::getCurso).setHeader("Curso");
        usuariosGrid.addColumn(Users::getFaculdade).setHeader("Faculdade");

        // Coluna com botão de remoção por usuário
        usuariosGrid.addComponentColumn(usuario -> {
            Button removerButton = new Button("Remover", event -> {
                try {
                    // Recarrega o grupo para garantir que está atualizado
                    Grupo grupoAtualizado = grupoService.buscarPorId(grupo.getId());
                    if (grupoAtualizado != null) {
                        grupoAtualizado.removerUsuario(usuario);
                        grupoService.salvar(grupoAtualizado);

                        Notification.show("Usuário removido: " + usuario.getNome() + " " + usuario.getSobrenome());

                        // Atualiza o grid local
                        usuariosGrid.setItems(grupoAtualizado.getUsuarios());

                        // Atualiza o grid principal
                        refreshGrid();

                        // Atualiza todas as sessões da UI conectadas
                        updateAllHomeViews();
                    }
                } catch (Exception ex) {
                    Notification.show("Erro ao remover usuário: " + ex.getMessage());
                }
            });
            return removerButton;
        }).setHeader("Ações");

        usuariosGrid.setItems(grupo.getUsuarios());
        usuariosGrid.setWidth("100%");
        usuariosGrid.setHeight("300px");

        // Botão de fechar
        Button fecharButton = new Button("Fechar", event -> dialog.close());

        VerticalLayout layout = new VerticalLayout(usuariosGrid, fecharButton);
        layout.setSpacing(true);
        layout.setPadding(true);

        dialog.add(layout);
        dialog.setWidth("800px");
        dialog.setHeight("600px");
        dialog.open();
    }

    public void abrirDialogAdicionarUsuario(Grupo grupo) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Adicionar Usuário ao Grupo: " + grupo.getNome());

        // Campo para digitar o nome do usuário
        TextField nomeUsuarioField = new TextField("Nome do Usuário");

        // Grid para exibir os resultados da busca
        Grid<Users> usuariosGrid = new Grid<>();
        usuariosGrid.addColumn(Users::getNome).setHeader("Nome");
        usuariosGrid.addColumn(Users::getSobrenome).setHeader("Sobrenome");
        usuariosGrid.addColumn(Users::getCurso).setHeader("Curso");
        usuariosGrid.addColumn(Users::getFaculdade).setHeader("Faculdade");
        usuariosGrid.setSelectionMode(Grid.SelectionMode.SINGLE);

        usuariosGrid.setWidth("100%");
        usuariosGrid.setHeight("300px");

        // Botão de busca para filtrar os usuários
        Button buscarButton = new Button("Buscar", event -> {
            String nomeUsuario = nomeUsuarioField.getValue();
            try {
                List<Users> usuarios;
                if (nomeUsuario != null && !nomeUsuario.trim().isEmpty()) {
                    // Busca por nome (assumindo que existe esse método no service)
                    usuarios = userService.listarTodos().stream()
                            .filter(u -> u.getNome().toLowerCase().contains(nomeUsuario.toLowerCase()) ||
                                    u.getSobrenome().toLowerCase().contains(nomeUsuario.toLowerCase()))
                            .collect(Collectors.toList());
                } else {
                    usuarios = userService.listarTodos();
                }

                // Filtra usuários que já estão no grupo
                Set<Integer> usuariosDoGrupo = grupo.getUsuarios().stream()
                        .map(Users::getId)
                        .collect(Collectors.toSet());

                usuarios = usuarios.stream()
                        .filter(u -> !usuariosDoGrupo.contains(u.getId()))
                        .collect(Collectors.toList());

                usuariosGrid.setItems(usuarios);
            } catch (Exception ex) {
                Notification.show("Erro ao buscar usuários: " + ex.getMessage());
            }
        });

        // Botão para adicionar o usuário selecionado ao grupo
        Button adicionarButton = new Button("Adicionar", event -> {
            Users usuarioSelecionado = usuariosGrid.getSelectedItems().stream().findFirst().orElse(null);

            if (usuarioSelecionado != null) {
                try {
                    // Recarrega o grupo para garantir que está atualizado
                    Grupo grupoAtualizado = grupoService.buscarPorId(grupo.getId());
                    if (grupoAtualizado != null) {
                        grupoAtualizado.addUsuario(usuarioSelecionado);
                        grupoService.salvar(grupoAtualizado);

                        Notification.show("Usuário adicionado: " + usuarioSelecionado.getNome() + " " + usuarioSelecionado.getSobrenome());

                        // Atualiza o grid principal
                        refreshGrid();

                        // Atualiza todas as sessões da UI conectadas
                        updateAllHomeViews();

                        dialog.close();
                    }
                } catch (Exception ex) {
                    Notification.show("Erro ao adicionar usuário: " + ex.getMessage());
                }
            } else {
                Notification.show("Por favor, selecione um usuário para adicionar ao grupo.");
            }
        });

        Button cancelarButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(adicionarButton, cancelarButton);

        VerticalLayout layout = new VerticalLayout(nomeUsuarioField, buscarButton, usuariosGrid, botoes);
        layout.setSpacing(true);
        layout.setPadding(true);

        dialog.add(layout);
        dialog.setWidth("800px");
        dialog.setHeight("700px");
        dialog.open();
    }

    private void updateAllHomeViews() {
        try {
            Set<UI> uis = UIManager.getInstance().getAllUIs();
            for (UI ui : uis) {
                ui.access(() -> {
                    try {
                        HomeView homeView = ui.getSession().getAttribute(HomeView.class);
                        Users sessionUser = ui.getSession().getAttribute(Users.class);
                        if (homeView != null && sessionUser != null) {
                            homeView.grupoGrid.setItems(grupoService.buscarPorUsuario(sessionUser.getId()));
                            homeView.grupoGrid.getDataProvider().refreshAll();
                        }
                    } catch (Exception ex) {
                        System.err.println("Erro ao atualizar HomeView: " + ex.getMessage());
                    }
                });
            }
        } catch (Exception ex) {
            System.err.println("Erro ao atualizar todas as HomeViews: " + ex.getMessage());
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Users user = VaadinSession.getCurrent().getAttribute(Users.class);

        // Verifica se não está logado ou não é admin
        if (user == null ) {
            // Redireciona para a página de login, home ou onde preferir
            event.forwardTo("");
            return;
        }

        if (!"admin".equalsIgnoreCase(user.getRole())) {
            event.forwardTo("homeview");
        }
    }
}