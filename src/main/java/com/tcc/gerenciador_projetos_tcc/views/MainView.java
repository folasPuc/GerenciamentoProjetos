package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.entity.Users;
import com.tcc.gerenciador_projetos_tcc.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import org.springframework.beans.factory.annotation.Autowired;

@Route("")
public class MainView extends VerticalLayout {

    private H1 title;
    private IntegerField username;
    private PasswordField password;
    private Button loginButton;
    private Button registerButton;
    private Select<String> collegeSelect;



    @Autowired
    private AlunoService alunoService;
    @Autowired
    private UserService userService;


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
        title = new H1("UniWorks - Login");
        title.getStyle()
                .set("color", "#002D72")
                .set("font-family", "Segoe UI, Roboto, Arial, sans-serif")
                .set("font-size", "28px")
                .set("font-weight", "bold");
    }

    private void formConfig() {
        username = new IntegerField("RA");
        username.getStyle().set("width", "300px");

        // Definindo uma mensagem de erro personalizada
        username.setErrorMessage("Por favor, insira seu nome.");

        // Tornando o campo obrigatório
        username.setRequired(true);

        // Adicionando listener de foco perdido (blur) para validação imediata
        username.addBlurListener(event -> {
            if (username.isEmpty()) {
                username.setInvalid(true);  // Marca o campo como inválido
            } else {
                username.setInvalid(false); // Marca o campo como válido
            }
        });

        password = new PasswordField("Senha");
        password.getStyle().set("width", "300px");


        // Tornando o campo obrigatório
        password.setRequired(true);

        // Definindo a mensagem de erro
        password.setErrorMessage("A senha não pode estar vazia.");

        // Adicionando listener de foco perdido
        password.addBlurListener(event -> {
            if (password.isEmpty()) {
                password.setInvalid(true);
            } else {
                password.setInvalid(false);
            }
        });

        // Validando o campo
        password.addValueChangeListener(event -> {
            if (password.isEmpty()) {
                password.setInvalid(true); // Marca o campo como inválido
            } else {
                password.setInvalid(false); // Marca o campo como válido
            }
        });

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
            Integer user = username.getValue();
            String pass = password.getValue();
            String collegeValue = collegeSelect.getValue();
            // Lógica de login (adicionar backend depois)

            if (verifyFields(user, pass, collegeValue)) {


            if (alunoService.existsRAAndFaculdade(user, collegeValue)) {
                try {
                    if (userService.autenticarUsuario(user, pass, collegeValue)) {
                        Notification notification = new Notification("Usuário autenticado com sucesso!", 3000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                        notification.open();


                        Users userSave = userService.getUserByRaAndFaculdade(user, collegeValue)
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado!"));


                        VaadinSession.getCurrent().setAttribute(Users.class, userSave);

                        if (userSave.getRole().equals("admin")) {
                            getUI().ifPresent(ui -> ui.navigate("/admin"));
                        } else {
                            getUI().ifPresent(ui -> ui.navigate("/homeview"));
                        }



                    } else {
                        Notification notification = new Notification("Credenciais incorretas!", 3000, Notification.Position.TOP_CENTER);
                        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                        notification.open();

                    }
                } catch (IllegalArgumentException e) {
                    Notification notification = new Notification(e.getMessage(), 3000, Notification.Position.TOP_CENTER);
                    notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                    notification.open();


                }
            } else {
                Notification notification = new Notification("RA inválido para " + collegeValue, 3000, Notification.Position.TOP_CENTER);
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                notification.open();

            }

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

    private boolean verifyFields(Integer ra, String password, String college) {
        // Verificando se algum campo está vazio ou inválido
        if (ra == null || ra <= 0) {

            Notification notification = new Notification("O campo 'RA' é obrigatório e deve ser um valor válido.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();

            return false;
        }
        if (password == null || password.trim().isEmpty()) {

            Notification notification = new Notification("O campo 'Senha' é obrigatório.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
        if (college == null || college.trim().isEmpty()) {

            Notification notification = new Notification("O campo 'Faculdade' é obrigatório.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }

        // Se todos os campos estiverem preenchidos corretamente
        return true;
    }
}
