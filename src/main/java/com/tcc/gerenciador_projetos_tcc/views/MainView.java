package com.tcc.gerenciador_projetos_tcc.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends VerticalLayout {

    private H1 title;
    private IntegerField username;
    private PasswordField password;
    private Button loginButton;
    private Button registerButton;

    public MainView() {
        setLayout();
        titleConfig();
        formConfig();
        loginButtonConfig();
        registerButtonConfig();

        add(title, username, password, loginButton, registerButton);
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
    }

    private void loginButtonConfig() {
        loginButton = new Button("Entrar", event -> {
            int user = username.getValue();
            String pass = password.getValue();
            // Lógica de login (adicionar backend depois)
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
