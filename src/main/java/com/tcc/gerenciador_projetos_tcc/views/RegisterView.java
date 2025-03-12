package com.tcc.gerenciador_projetos_tcc.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("register")
public class RegisterView extends VerticalLayout {

    private TextField firstName;
    private TextField lastName;
    private EmailField email;
    private IntegerField ra;
    private PasswordField password;
    private H1 title;
    private Button registerButton;
    private Button backButton;

    public RegisterView() {

        setLayout();
        titleConfig();
        formConfig();
        registerButtonConfig();
        backButtonConfig();

        add(title, firstName, lastName, email, ra, password, registerButton, backButton);
    }

    private void backButtonConfig() {

        // Botão para voltar ao login
        backButton = new Button("Voltar ao Login", event -> {
            getUI().ifPresent(ui -> ui.navigate(""));
        });

        backButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
    }

    private void registerButtonConfig() {
        // Botão de Cadastro
        registerButton = new Button("Cadastrar", event -> {
            // Lógica de cadastro (adicionar backend depois)
        });

        registerButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
    }

    private void formConfig() {

        firstName = new TextField("Nome");
        firstName.getStyle().set("width", "300px");

        lastName = new TextField("Sobrenome");
        lastName.getStyle().set("width", "300px");

        email = new EmailField("Email");
        email.getStyle().set("width", "300px");

        ra = new IntegerField("RA");
        ra.getStyle().set("width", "300px");

        password = new PasswordField("Senha");
        password.getStyle().set("width", "300px");

    }

    private void titleConfig() {
        title = new H1("PUC-Campinas - Cadastro");

        title.getStyle()
                .set("color", "#002D72")
                .set("font-family", "Segoe UI, Roboto, Arial, sans-serif") // Fonte mais clean
                .set("font-size", "28px")
                .set("font-weight", "bold");
    }

    private void setLayout() {
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setSizeFull();
    }
}
