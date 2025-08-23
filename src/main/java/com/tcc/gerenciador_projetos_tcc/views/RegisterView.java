package com.tcc.gerenciador_projetos_tcc.views;

import com.tcc.gerenciador_projetos_tcc.service.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;

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
    private Select<String> collegeSelect;
    private Select<String> cursoSelect;


    @Autowired
    private AlunoService alunoService;
    @Autowired
    private UserService userService;


    public RegisterView() {

        setLayout();
        titleConfig();
        formConfig();
        registerButtonConfig();
        backButtonConfig();

        add(title, firstName, lastName, email, ra, password, collegeSelect, cursoSelect,registerButton, backButton);
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
            String firstNameValue = firstName.getValue();
            String lastNameValue = lastName.getValue();
            String emailValue = email.getValue();
            Integer raValue = ra.getValue();
            String passwordValue = password.getValue();
            String collegeValue = collegeSelect.getValue();
            String curso = cursoSelect.getValue();

            if (verifyFields(firstNameValue, lastNameValue, emailValue, raValue, passwordValue, collegeValue)) {


                        if (alunoService.existsRAAndFaculdade(raValue, collegeValue)) {
                            try {
                                userService.salvarUsuario(raValue, firstNameValue, lastNameValue, emailValue, passwordValue, curso, collegeValue);

                                Notification notification = new Notification("Usuário cadastrado com sucesso!", 3000, Notification.Position.TOP_CENTER);
                                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                                notification.open();

                                getUI().ifPresent(ui -> ui.navigate(""));
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

        registerButton.getStyle()
                .set("background", "#002D72")
                .set("color", "white")
                .set("border-radius", "8px")
                .set("padding", "10px 20px");
    }

    private void formConfig() {

        configFirstName();
        configLastName();
        configEmail();
        configRA();
        configPassword();
        cursoSelectConfig();
        selectConfig();

    }

    private void cursoSelectConfig() {
        cursoSelect = new Select<>();
        cursoSelect.setLabel("Curso");
        cursoSelect.setItems("Eng Comp", "Direito", "Arquitetura", "Design", "Ed Fisica");
        cursoSelect.setPlaceholder("Selecione o curso");
        cursoSelect.getStyle().set("width", "300px");
    }

    private void configPassword() {
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

    }

    private void configRA() {
        ra = new IntegerField("RA");
        ra.getStyle().set("width", "300px");

        // Tornando o campo obrigatório
        ra.setRequired(true);

        // Definindo a mensagem de erro
        ra.setErrorMessage("Por favor, insira um número válido para o RA.");

        // Adicionando listener de foco perdido
        ra.addBlurListener(event -> {
            if (ra.isEmpty() || ra.getValue() == null) {
                ra.setInvalid(true);
            } else {
                ra.setInvalid(false);
            }
        });

        // Validando o campo
        ra.addValueChangeListener(event -> {
            if (ra.isEmpty() || ra.getValue() == null) {
                ra.setInvalid(true); // Marca o campo como inválido
            } else {
                ra.setInvalid(false); // Marca o campo como válido
            }
        });
    }

    private void configEmail() {
        email = new EmailField("Email");
        email.getStyle().set("width", "300px");
        // Definindo uma mensagem de erro personalizada
        email.setErrorMessage("Por favor, insira um e-mail válido.");

        // Tornando o campo obrigatório
        email.setRequired(true);

        // Adicionando listener de foco perdido
        email.addBlurListener(event -> {
            if (email.isEmpty() || !email.getValue().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                email.setInvalid(true);
            } else {
                email.setInvalid(false);
            }
        });

        // Validando o campo
        email.addValueChangeListener(event -> {
            if (email.isEmpty() || !email.getValue().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
                email.setInvalid(true); // Marca o campo como inválido
            } else {
                email.setInvalid(false); // Marca o campo como válido
            }
        });

    }

    private void configLastName() {
        lastName = new TextField("Sobrenome");
        lastName.getStyle().set("width", "300px");

        // Tornando o campo obrigatório
        lastName.setRequired(true);

        // Definindo a mensagem de erro
        lastName.setErrorMessage("Sobrenome não pode estar vazio.");

        // Adicionando listener de foco perdido para validação imediata
        lastName.addBlurListener(event -> {
            if (lastName.isEmpty()) {
                lastName.setInvalid(true);  // Marca o campo como inválido
            } else {
                lastName.setInvalid(false); // Marca o campo como válido
            }
        });

        // Validando o campo
        lastName.addValueChangeListener(event -> {
            if (lastName.isEmpty()) {
                lastName.setInvalid(true); // Marca o campo como inválido
            } else {
                lastName.setInvalid(false); // Marca o campo como válido
            }
        });

    }

    private void configFirstName() {
        firstName = new TextField("Nome");
        firstName.getStyle().set("width", "300px");
        // Definindo uma mensagem de erro personalizada
        firstName.setErrorMessage("Por favor, insira seu nome.");

        // Tornando o campo obrigatório
        firstName.setRequired(true);

        // Adicionando listener de foco perdido (blur) para validação imediata
        firstName.addBlurListener(event -> {
            if (firstName.isEmpty()) {
                firstName.setInvalid(true);  // Marca o campo como inválido
            } else {
                firstName.setInvalid(false); // Marca o campo como válido
            }
        });

        // Validando
        firstName.addValueChangeListener(event -> {
            if (firstName.isEmpty()) {
                firstName.setInvalid(true); // Marca o campo como inválido
            } else {
                firstName.setInvalid(false); // Marca o campo como válido
            }
        });
    }

    private void selectConfig() {
        collegeSelect = new Select<>();
        collegeSelect.setLabel("Faculdade");
        collegeSelect.setItems("PUCCAMPINAS", "UNICAMP");
        collegeSelect.setPlaceholder("Selecione a faculdade");
        collegeSelect.getStyle().set("width", "300px");
    }

    private void titleConfig() {
        title = new H1("UniWorks - Cadastro");

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

    private boolean verifyFields(String firstName, String lastName, String email, Integer ra, String password, String college) {
        // Verificando se algum campo está vazio ou inválido
        if (firstName == null || firstName.trim().isEmpty()) {

            Notification notification = new Notification("O campo 'Nome' é obrigatório.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();

            return false;
        }
        if (lastName == null || lastName.trim().isEmpty()) {

            Notification notification = new Notification("O campo 'Sobrenome' é obrigatório.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
        if (email == null || email.trim().isEmpty()) {

            Notification notification = new Notification("O campo 'Email' é obrigatório.", 3000, Notification.Position.TOP_CENTER);
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            notification.open();
            return false;
        }
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
