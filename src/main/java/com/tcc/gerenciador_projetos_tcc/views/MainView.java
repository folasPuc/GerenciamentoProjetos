package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.UsersPuc;
import com.tcc.gerenciador_projetos_tcc.entity.UsersUnicamp;
import com.tcc.gerenciador_projetos_tcc.model.User;
import com.tcc.gerenciador_projetos_tcc.service.AlunoPucService;
import com.tcc.gerenciador_projetos_tcc.service.AlunoUnicampService;
import com.tcc.gerenciador_projetos_tcc.service.UsersPucService;
import com.tcc.gerenciador_projetos_tcc.service.UsersUnicampService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

@Route("")
public class MainView extends VerticalLayout {

    private H1 title;
    private IntegerField username;
    private PasswordField password;
    private Button loginButton;
    private Button registerButton;
    private Select<String> collegeSelect;

    @Autowired
    private AlunoPucService alunoPucService;
    @Autowired
    private UsersPucService usersPucService;
    @Autowired
    private AlunoUnicampService alunoUnicampService;
    @Autowired
    private UsersUnicampService usersUnicampService;


    public MainView() {
        setLayout();
        titleConfig();
        formConfig();
        loginButtonConfig();
        registerButtonConfig();

        add(title, username, password, collegeSelect, loginButton, registerButton);
    }

    private void setLayout() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }

    private void titleConfig() {
        title = new H1("PUC-Campinas - Login");
        title.getStyle()
                .set("color", "#002D72")
                .set("font-family", "Segoe UI, Roboto, Arial, sans-serif")
                .set("font-size", "28px")
                .set("font-weight", "bold");
    }

    private void formConfig() {
        username = new IntegerField("RA");
        username.getStyle().set("width", "300px");

        password = new PasswordField("Senha");
        password.getStyle().set("width", "300px");

        configSelect();
    }

    private void configSelect() {
        collegeSelect = new Select<>();
        collegeSelect.setLabel("Faculdade");
        collegeSelect.setItems("PUCCAMPINAS", "UNICAMP");
        collegeSelect.setPlaceholder("Selecione a faculdade");
        collegeSelect.getStyle().set("width", "300px");
    }

    private void loginButtonConfig() {
        loginButton = new Button("Entrar", event -> {
            int user = username.getValue();
            String pass = password.getValue();
            String collegeValue = collegeSelect.getValue();
            // Lógica de login (adicionar backend depois)

            switch (collegeValue) {
                case "PUCCAMPINAS":
                    if (alunoPucService.existsRA(user)) {
                        try {
                            if (usersPucService.autenticarUsuario(user, pass)) {
                                Notification.show("Usuário Autenticado com sucesso!");

                                UsersPuc userPuc = usersPucService.getUserByRa(user)
                                        .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

                                User userSave = new User(userPuc.getRa(), userPuc.getNome(), userPuc.getSobrenome(), userPuc.getEmail(), collegeValue, userPuc.getCurso());

                                VaadinSession.getCurrent().setAttribute(User.class, userSave);

                                getUI().ifPresent(ui -> ui.navigate("/homeview"));



                            } else {
                                Notification.show("Credenciais incorretas");
                            }
                        } catch (IllegalArgumentException e) {
                            Notification.show(e.getMessage());
                        }
                    } else {
                        Notification.show("RA inválido para PUCCAMP!");
                    }
                    break;
                case "UNICAMP":
                    if (alunoUnicampService.existsRA(user)) {
                        try {
                           if (usersUnicampService.autenticarUsuario(user, pass)) {
                               Notification.show("Usuário Autenticado com sucesso!");

                               UsersUnicamp userUnicamp = usersUnicampService.getUserByRa(user)
                                       .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));

                               User userSave = new User(userUnicamp.getRa(), userUnicamp.getNome(), userUnicamp.getSobrenome(), userUnicamp.getEmail(), collegeValue, userUnicamp.getCurso());

                               VaadinSession.getCurrent().setAttribute(User.class, userSave);

                               getUI().ifPresent(ui -> ui.navigate("/homeview"));
                           } else {
                               Notification.show("Credenciais incorretas");
                           }

                        } catch (IllegalArgumentException e) {
                            Notification.show(e.getMessage());
                        }
                    } else {
                        Notification.show("RA inválido para UNICAMP!");
                    }
                    break;
                default:
                    Notification.show("Faculdade inválida!");
                    break;
            }
        });

        loginButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
    }

    private void registerButtonConfig() {
        registerButton = new Button("Criar Conta", event ->
                getUI().ifPresent(ui -> ui.navigate("register"))
        );

        registerButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
    }
}
