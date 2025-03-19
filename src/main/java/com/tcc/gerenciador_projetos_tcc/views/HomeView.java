package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.Grupo;
import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.service.AlunoService;
import com.tcc.gerenciador_projetos_tcc.service.GrupoService;
import com.tcc.gerenciador_projetos_tcc.service.UserService;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;

@PageTitle("Home")
@Route("homeview")
public class HomeView extends VerticalLayout {

    private final UserService userService;
    private final AlunoService alunoService;
    private  final GrupoService grupoService;
    private VerticalLayout sidebar;
    private Users user;
    private Grid<Grupo> grupoGrid;


    public HomeView(UserService userService, AlunoService alunoService, GrupoService grupoService) {
        this.userService = userService;
        this.alunoService = alunoService;
        this.grupoService = grupoService;
        // Recupera o usuário da sessão

        user = VaadinSession.getCurrent().getAttribute(Users.class);

        setSizeFull(); // Ocupa toda a tela
        setSpacing(true);

        initSidebar();
        add(sidebar);  // Adiciona o painel lateral à tela

    }

    private void initSidebar() {
        sidebar = new VerticalLayout();
        sidebar.setWidth("300px");
        sidebar.setPadding(true);
        sidebar.setSpacing(true);


        if (user != null) {
            addUserInfo(user); // Exibe as informações do usuário
            addGroupSection();  // Exibe a seção de grupos
        } else {
            sidebar.add(new Text("Usuário não autenticado."));
        }
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
        Button criarGrupoButton = new Button("Criar Grupo", event -> {
            // Lógica para criação de grupo (futura implementação)
            abrirCriacaoGrupo();
        });

        // Grid com os grupos do usuário
        grupoGrid = new Grid<>();
        grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId()));
        grupoGrid.addColumn(Grupo::getNome).setHeader("Grupos").setFlexGrow(1);
        // Coluna para o botão "+"
        grupoGrid.addComponentColumn(grupo -> {
            Button adicionarUsuarioButton = new Button("+", e -> abrirDialogAdicionarUsuario(grupo));
            return adicionarUsuarioButton;
        }).setHeader("Adicionar Usuários");
        grupoGrid.setHeight("600px");

        // Adiciona o botão e o grid à sidebar
        sidebar.add(criarGrupoButton, grupoGrid);
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
                grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId())); // Atualiza o grid de grupos
                Notification.show("Adicionado : " + usuarioSelecionado.getNome() + usuarioSelecionado.getSobrenome());
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


    private void abrirCriacaoGrupo() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Criar Novo Grupo");

        TextField nomeGrupoField = new TextField("Nome do Grupo");

        Button salvarButton = new Button("Criar", event -> {
            String nomeGrupo = nomeGrupoField.getValue();
            if (!nomeGrupo.isEmpty()) {
                Grupo novoGrupo = new Grupo(nomeGrupo);
                novoGrupo.addUsuario(user); // Associa o criador ao grupo
                grupoService.salvar(novoGrupo);
                grupoGrid.setItems(grupoService.buscarPorUsuario(user.getId())); // Atualiza o grid
                dialog.close();
            }
        });

        Button cancelarButton = new Button("Cancelar", event -> dialog.close());

        HorizontalLayout botoes = new HorizontalLayout(salvarButton, cancelarButton);
        dialog.add(nomeGrupoField, botoes);

        dialog.open(); // Abre o modal
    }
}
